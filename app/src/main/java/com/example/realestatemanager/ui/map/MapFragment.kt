package com.example.realestatemanager.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.realestatemanager.R
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.databinding.FragmentMapBinding
import com.example.realestatemanager.utils.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.realestatemanager.utils.PermissionsUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MapFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    lateinit var mapBinding: FragmentMapBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var bitmapMarker: Bitmap
    private lateinit var locationCallback: LocationCallback
    private var requestLocationUpdate: Boolean = true
    private val viewModelMap: MapViewModel by activityViewModels() {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            photoRepository = (requireActivity().application as RealEstateApplication).photoRepository
        )
    }
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        onLocationCallBack()
    }

    private fun requestPermissions() {
        if (PermissionsUtils.hasLocationPermissions(requireContext())) {
            setupMap()
        }
        EasyPermissions.requestPermissions(
            this,
            "You need to accept location permisssions to use this app.",
            REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        setupMap()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    private fun setupMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_entire_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            this.googleMap = googleMap
            addMarker(googleMap)
            googleMap.setOnMapLoadedCallback {
                startLocationUpdates()
            }
            googleMap.setOnMarkerClickListener {
                viewModelMap.setId(it.tag as Long)
                true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("MissingPermission")
    private fun onLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (location != null) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(// Update UI with location data
                                com.google.android.gms.maps.model.LatLng(
                                    location.latitude,
                                    location.longitude
                                ),
                                13.toFloat()
                            )
                        )
                        stopLocationUpdates()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        requestLocationUpdate = false
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapBinding = FragmentMapBinding.inflate(inflater, container, false)
        requestPermissions()
        onTouchImgView()
        return mapBinding.root
    }

    private fun addMarker(googleMap: GoogleMap) {
        bitmapMarker =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_marker_house)!!
                .toBitmap()
        viewModelMap.livedataListRealEstate.observe(
            viewLifecycleOwner,
            Observer { listRealEstate ->
                listRealEstate.forEach { realEstate ->
                    if (realEstate.latitude != null && realEstate.longitude != null) {
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(
                                    com.google.android.gms.maps.model.LatLng(
                                        realEstate.latitude!!.toDouble(),
                                        realEstate.longitude!!.toDouble()
                                    )
                                ).icon(BitmapDescriptorFactory.fromBitmap(bitmapMarker))
                        )
                        marker?.tag = realEstate.idRealEstate

                    }
                }
            })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchImgView() {
        mapBinding.transparentImageMap.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    mapBinding.scrollViewDetails.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    mapBinding.scrollViewDetails.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    mapBinding.scrollViewDetails.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> true
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (requestLocationUpdate) {
            startLocationUpdates()
        }
    }
}
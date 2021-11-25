package com.example.realestatemanager.ui.details

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestatemanager.R
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.databinding.FragmentDetailsBinding
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.utils.Constants.CODE_DOLLAR
import com.example.realestatemanager.utils.Constants.CODE_EURO
import com.example.realestatemanager.utils.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.realestatemanager.utils.PermissionsUtils
import com.example.realestatemanager.utils.Utils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.DateFormat.getDateInstance
import java.util.*


class DetailsFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    lateinit var detailsBinding: FragmentDetailsBinding
    lateinit var recyclerView: RecyclerView
    private var adapter = PhotoAdapter()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var bitmapMarker: Bitmap
    private lateinit var locationCallback: LocationCallback
    private var requestLocationUpdate: Boolean = true
    private val viewModelDetails: DetailsViewModel by activityViewModels() {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            photoRepository = (requireActivity().application as RealEstateApplication).photoRepository,
            (requireActivity().application as RealEstateApplication).geocoderRepository
        )
    }
    private lateinit var googleMap: GoogleMap
    private var currencyCode = 0
    private var realEstate: RealEstate? = null

    companion object {
        fun newInstance() = DetailsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            viewModelDetails.setId(requireArguments().getLong("idRealEstate"))
            Log.d("DEBUG", "onCreate: DetailFragment " + requireArguments().getLong("idRealEstate"))
        }
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
            childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            this.googleMap = googleMap
            addMarker(googleMap)
            googleMap.setOnMapLoadedCallback {
                startLocationUpdates()
            }
            googleMap.setOnMarkerClickListener {
                viewModelDetails.setId(it.tag as Long)
                true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        detailsBinding = FragmentDetailsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        updateUi()
        requestPermissions()
        observeCurrency()
        onTouchImgView()
        return detailsBinding.root
    }

    private fun updateUi() {
        viewModelDetails.liveDataRealEstate.observe(
            viewLifecycleOwner,
            Observer { realEstateWithPhoto ->
                this.realEstate = realEstateWithPhoto.realEstate
                detailsBinding.textViewDescription.text = realEstateWithPhoto.realEstate.description
                detailsBinding.nbSurface.text =
                    realEstateWithPhoto.realEstate.surface.toString().plus(" m²")
                detailsBinding.textViewAdresse.text = realEstateWithPhoto.realEstate.address
                detailsBinding.nbRooms.text = realEstateWithPhoto.realEstate.nbRooms.toString()
                detailsBinding.nbBathrooms.text =
                    realEstateWithPhoto.realEstate.nbBathrooms.toString()
                detailsBinding.nbBedroooms.text =
                    realEstateWithPhoto.realEstate.nbBedrooms.toString()
                currencySwitchAndDisplay()
                detailsBinding.textViewAgent.text = realEstateWithPhoto.realEstate.realEstateAgent
                detailsBinding.twPropertyPublicationDate.text =
                    updateDateEntry(realEstate = realEstateWithPhoto.realEstate)
                if (!realEstateWithPhoto.realEstate.propertyStatus) {
                    detailsBinding.sold.isVisible = false
                } else {
                    detailsBinding.sold.isVisible = true
                    if (realEstateWithPhoto.realEstate.dateSold != null) {
                        detailsBinding.twPropertySoldDate.text =
                            updateDateSold(realEstate = realEstateWithPhoto.realEstate)
                    }
                }
                adapter.data = realEstateWithPhoto.photos!!
            })
    }

    private fun observeCurrency() {
        viewModelDetails.liveDataCurrencyCode.observe(viewLifecycleOwner, Observer {
            currencyCode = it
            if (realEstate != null) {
                currencySwitchAndDisplay()
            }
        })
    }

    private fun currencySwitchAndDisplay() {
        when (currencyCode) {
            CODE_DOLLAR -> {
                detailsBinding.twPrice.text = realEstate!!.price.toString()
                detailsBinding.currency.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_currency_dollar_black_24dp
                    )
                )
            }
            CODE_EURO -> {
                detailsBinding.twPrice.text =
                    Utils.convertDollarToEuro(realEstate!!.price).toString()
                detailsBinding.currency.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_currency_euro_black_24dp
                    )
                )
            }
        }
    }


    private fun updateDateEntry(realEstate: RealEstate): String {
        val dateFormat = getDateInstance()
        val date = Date(realEstate.dateEntry)
        return dateFormat.format(date)
    }

    private fun updateDateSold(realEstate: RealEstate): String {
        val dateFormat = getDateInstance()
        if (realEstate.dateSold != null) {
            val date = Date(realEstate.dateSold!!)
            return dateFormat.format(date)
        }
        return ""
    }

    private fun setupRecyclerView() {
        recyclerView = detailsBinding.recyclerviewPhotoDetails
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager
        detailsBinding.recyclerviewPhotoDetails.adapter = adapter
    }

    private fun addMarker(googleMap: GoogleMap) {
        bitmapMarker =
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.baseline_real_estate_agent_24
            )!!
                .toBitmap()
        viewModelDetails.livedataListRealEstate.observe(
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
        detailsBinding.transparentImage.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    detailsBinding.scrollViewDetails.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    detailsBinding.scrollViewDetails.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    detailsBinding.scrollViewDetails.requestDisallowInterceptTouchEvent(true)
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
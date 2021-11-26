package com.example.realestatemanager.ui.update

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Address
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestatemanager.BuildConfig
import com.example.realestatemanager.R
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.databinding.FragmentUpdateBinding
import com.example.realestatemanager.domain.models.NearbyPOI
import com.example.realestatemanager.domain.models.Photo
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.domain.relation.RealEstateWithPhoto
import com.example.realestatemanager.ui.home.MainActivity
import com.example.realestatemanager.utils.PhotoFileUtils
import com.example.realestatemanager.utils.TextFieldUtils.Companion.hasText
import com.example.realestatemanager.utils.TextFieldUtils.Companion.isNumber
import com.example.realestatemanager.utils.Utils
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class UpdateFragment : Fragment() {

    private lateinit var updateBinding: FragmentUpdateBinding
    private val updateViewModel: UpdateViewModel by activityViewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            photoRepository = (requireActivity().application as RealEstateApplication).photoRepository,
            (requireActivity().application as RealEstateApplication).geocoderRepository
        )
    }
    private lateinit var realEstateActual: RealEstateWithPhoto
    private lateinit var adapter: PhotoUpdateAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var latLngAddress: Address = Address(null)
    private val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select date of sale")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()
    private var dateSelectedSold: Long? = null
    private lateinit var uri: Uri
    private var photo = Photo()
    private var changePhoto = false
    private var showDialog = true
    private var nearbyPOI = NearbyPOI()
    private var alertDialogNoNetworkSaw = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            Log.d("DEBUG", "onCreate: DetailFragment " + requireArguments().getLong("idRealEstate"))
            updateViewModel.setId(requireArguments().getLong("idRealEstate"))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        updateBinding = FragmentUpdateBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeRealEstate()
        onDateClicked()
        onClickPhoto()
        onClickPhotoFromFile()
        onValidationClick()

        return updateBinding.root
    }

    private fun observeRealEstate() {
        updateViewModel.liveDataRealEstate.observe(viewLifecycleOwner, {
            realEstateActual = it
            adapter.data = it.photos!!
            updateUi()
        })
    }

    private fun updateUi() {
        updateBinding.textFieldAddress.editText?.setText(realEstateActual.realEstate.address)
        updateBinding.textFieldPrice.editText?.setText(realEstateActual.realEstate.price.toString())
        updateBinding.textFieldDescription.editText?.setText(realEstateActual.realEstate.description)
        updateBinding.textFieldNbRooms.editText?.setText(realEstateActual.realEstate.nbRooms.toString())
        updateBinding.textFieldNbBedrooms.editText?.setText(realEstateActual.realEstate.nbBedrooms.toString())
        updateBinding.textFieldNbBathrooms.editText?.setText(realEstateActual.realEstate.nbBathrooms.toString())
        updateBinding.textFieldSurface.editText?.setText(realEstateActual.realEstate.surface.toString())
        updateBinding.textFieldType.editText?.setText(realEstateActual.realEstate.type)
    }


    private fun setupRecyclerView() {
        recyclerView = updateBinding.recyclerviewPhotoUpdate
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, linearLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.layoutManager = linearLayoutManager
        setupAdapter()
        updateBinding.recyclerviewPhotoUpdate.adapter = adapter
    }

    private fun setupAdapter() {
        adapter = PhotoUpdateAdapter {
            alertDialogUpdateOrDelete(it)
        }

    }

    private fun alertDialogUpdateOrDelete(photo: Photo) {
        val editText = EditText(requireContext())
        editText.setTextColor(
            AppCompatResources.getColorStateList(
                requireContext(),
                R.color.white
            )
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Enter Photo Name")
            .setView(editText)
            .setPositiveButton("Update") { dialog, _ ->
                if (!editText.text.isNullOrEmpty()) {
                    photo.label = editText.text.toString()
                    photo.idProperty = realEstateActual.realEstate.idRealEstate
                    updateViewModel.updatePhoto(photo)
                    changePhoto = true
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Delete") { dialog, _ ->
                if (realEstateActual.photos?.size!! > 1) {
                    updateViewModel.deletePhoto(photo)
                    changePhoto = true
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "At least one photo must be present",
                        Toast.LENGTH_LONG
                    ).show()
                    dialog.dismiss()
                }

            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private val takePicture =
        registerForActivityResult(object : ActivityResultContracts.TakePicture() {
            override fun createIntent(
                context: Context,
                input: Uri
            ): Intent {
                val intent = super.createIntent(context, input)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    intent.clipData = ClipData.newRawUri("", input)
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                return intent
            }
        }) { success ->
            if (success) {
                val nameFile: Int
                uri.let {
                    requireContext().contentResolver.query(uri, null, null, null, null)
                }?.use {
                    nameFile = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    it.moveToFirst()
                    photo.path = it.getString(nameFile)
                }
                alertDialogPhoto()
            }
        }


    private fun onClickPhotoFromFile() {
        updateBinding.ButtonAddPhotoFromFolder.setOnClickListener {
            getPicture.launch("image/*")
        }
    }

    private val getPicture =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val filename =
                    PhotoFileUtils.createImageFile(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                photo.path = filename.name
                val fileOutputStream = FileOutputStream(filename)
                fileOutputStream.write(readBytes(uri))
                alertDialogPhoto()
            }
        }

    @Throws(IOException::class)
    private fun readBytes(uri: Uri): ByteArray? =
        requireContext().contentResolver.openInputStream(uri)?.buffered().use {
            it?.readBytes()
        }

    private fun onClickPhoto() {
        updateBinding.ButtonAddPhoto.setOnClickListener {
            uri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                PhotoFileUtils.createImageFile(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
            )
            takePicture.launch(uri)
        }
    }

    private fun alertDialogPhoto() {
        val editText = EditText(requireContext())
        editText.setTextColor(
            AppCompatResources.getColorStateList(
                requireContext(),
                isDark()
            )
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Enter Photo Name")
            .setView(editText)
            .setPositiveButton(requireContext().resources.getString(R.string.validate)) { dialog, _ ->
                if (!editText.text.isNullOrEmpty()) {
                    photo.label = editText.text.toString()
                    photo.idProperty = realEstateActual.realEstate.idRealEstate
                    updateViewModel.insertPhoto(photo)
                    photo = Photo()
                    changePhoto = true
                    dialog.dismiss()
                }
            }
            .show()
    }

    private fun alertDialogBadAddressLocation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("The address is invalid")
            .setMessage("Some functionality such as the display of marker on the map and nearby points of interest will therefore not be available for this property.")
            .setNeutralButton("Ok") { dialog, _ ->
                intentMainActivity()
                dialog.dismiss()
            }
            .setPositiveButton("Stay and update") { dialog, _ ->
                dialog.dismiss()
                showDialog = true
            }
            .show()
    }

    private fun onValidationClick() {
        updateBinding.ButtonUpdate.setOnClickListener {
            if (validateTextOrNumber()) {
                if (Utils.checkInternetConnection(requireContext())) {
                    if (updateBinding.textFieldAddress.editText?.text.toString() != realEstateActual.realEstate.address) {
                        updateViewModel.getLatLng(updateBinding.textFieldAddress.editText?.text.toString())
                        updateViewModel.liveDataAddress.observe(
                            viewLifecycleOwner,
                            Observer { location ->
                                if (location.isNullOrEmpty()) {
                                    showDialog = false
                                    alertDialogBadAddressLocation()
                                    updateViewModel.getNearbyPoi()
                                } else {

                                    latLngAddress = location[0]
                                    updateViewModel.getNearbyPoi(
                                        LatLng(
                                            latLngAddress.latitude,
                                            latLngAddress.longitude
                                        )
                                    )
                                }
                                updateViewModel.liveDataNearbyPOI.observe(
                                    viewLifecycleOwner,
                                    Observer { liveDataNearbyPOI ->
                                        nearbyPOI = liveDataNearbyPOI
                                        updateRealEstate()
                                    })

                            })
                    } else {
                        noUpdatePoiAndLocation()
                    }
                } else {
                    if (!alertDialogNoNetworkSaw) {
                        alertDialogNoNetwork()
                    } else {
                        noUpdatePoiAndLocation()
                    }
                }
            }
        }
        changePhoto = false
    }


    private fun noUpdatePoiAndLocation() {
        if (realEstateActual.realEstate.latitude != null && realEstateActual.realEstate.longitude != null) {
            latLngAddress.latitude =
                realEstateActual.realEstate.latitude!!.toDouble()
            latLngAddress.longitude =
                realEstateActual.realEstate.longitude!!.toDouble()
        }
        nearbyPOI.nearbyPark = realEstateActual.realEstate.nearbyPark
        nearbyPOI.nearbyRestaurant = realEstateActual.realEstate.nearbyRestaurant
        nearbyPOI.nearbySchool = realEstateActual.realEstate.nearbySchool
        nearbyPOI.nearbyStore = realEstateActual.realEstate.nearbyStore
        updateRealEstate()
    }

    private fun updateRealEstate() {
        val realEstateToUpdate = RealEstate(
            idRealEstate = realEstateActual.realEstate.idRealEstate,
            type = updateBinding.textFieldType.editText?.text.toString(),
            price = updateBinding.textFieldPrice.editText?.text.toString()
                .toInt(),
            surface = updateBinding.textFieldSurface.editText?.text.toString()
                .toInt(),
            nbRooms = updateBinding.textFieldNbRooms.editText?.text.toString()
                .toInt(),
            nbBathrooms = updateBinding.textFieldNbBathrooms.editText?.text.toString()
                .toInt(),
            nbBedrooms = updateBinding.textFieldNbBedrooms.editText?.text.toString()
                .toInt(),
            description = updateBinding.textFieldDescription.editText?.text.toString(),
            address = updateBinding.textFieldAddress.editText?.text.toString(),
            propertyStatus = false,
            dateEntry = realEstateActual.realEstate.dateEntry,
            dateSold = dateSold(),
            realEstateAgent = realEstateActual.realEstate.realEstateAgent,
            latitude = latLngAddress.latitude.toFloat(),
            longitude = latLngAddress.longitude.toFloat(),
            nearbyStore = nearbyPOI.nearbyStore,
            nearbyPark = nearbyPOI.nearbyPark,
            nearbyRestaurant = nearbyPOI.nearbyRestaurant,
            nearbySchool = nearbyPOI.nearbySchool
        )

        //Changement de la bannière sous la photo : sold / to sale
        if (dateSelectedSold != null && !realEstateActual.realEstate.propertyStatus) {
            realEstateToUpdate.propertyStatus = true

        } else {
            realEstateToUpdate.propertyStatus = realEstateActual.realEstate.propertyStatus
        }

        //Ne pas update à vide la date de vente si une modif est faite sur autre chose ---> garder la date de vente actuelle
        if (realEstateActual.realEstate.propertyStatus && realEstateActual.realEstate.dateSold != null) {
            realEstateToUpdate.dateSold = realEstateActual.realEstate.dateSold
        }


        //Si aucune modif n'a été faite ---> message
        if (realEstateActual.realEstate == realEstateToUpdate && !changePhoto) {
            Toast.makeText(requireContext(), "No modification", Toast.LENGTH_LONG)
                .show()
        } else {
            //Sinon on update le realEstate et on revient sur MainActivity
            updateViewModel.updateRealEstate(realEstateToUpdate)
            if (showDialog) {
                intentMainActivity()
            }
        }
    }

    private fun intentMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
    }

    private fun onDateClicked() {
        updateBinding.ButtonDatePicker.setOnClickListener {

            if (!realEstateActual.realEstate.propertyStatus) {
                val fragmentManager = parentFragmentManager
                datePicker.show(fragmentManager, "DatePicker")
                datePicker.addOnPositiveButtonClickListener { selection: Long ->
                    dateSelectedSold = Date(selection).time
                }
            } else {
                Toast.makeText(requireContext(), "The property is already sold", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun validateTextOrNumber(): Boolean {
        var check = true
        if (!hasText(updateBinding.textFieldAddress, "This field must be completed")) check = false
        if (!hasText(updateBinding.textFieldType, "This field must be completed")) check = false
        if (!hasText(
                updateBinding.textFieldNbBathrooms,
                "This field must be completed"
            ) || !isNumber(
                updateBinding.textFieldNbBathrooms,
                "This field must only contain numbers"
            )
        ) check = false
        if (!hasText(
                updateBinding.textFieldNbBedrooms,
                "This field must be completed"
            ) || !isNumber(
                updateBinding.textFieldNbBedrooms,
                "This field must only contain numbers"
            )
        ) check = false
        if (!hasText(updateBinding.textFieldNbRooms, "This field must be completed") || !isNumber(
                updateBinding.textFieldNbRooms,
                "This field must only contain numbers"
            )
        ) check = false
        if (!hasText(updateBinding.textFieldPrice, "This field must be completed") || !isNumber(
                updateBinding.textFieldPrice,
                "This field must only contain numbers"
            )
        ) check = false
        if (!hasText(updateBinding.textFieldSurface, "This field must be completed") || !isNumber(
                updateBinding.textFieldSurface,
                "This field must only contain numbers"
            )
        ) check = false
        return check
    }

    private fun alertDialogNoNetwork() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("The marker for the location and nearby points of interest will not be available for this property")
            .setTitle("Network not available")
            .setPositiveButton("Ok") { dialog, _ ->
                alertDialogNoNetworkSaw = true
                dialog.dismiss()

            }
            .show()
    }

    private fun isDark(): Int {
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                return R.color.white
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                return R.color.black
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                return R.color.black
            }
        }
        return 1
    }

    private fun dateSold(): Long? = dateSelectedSold

}
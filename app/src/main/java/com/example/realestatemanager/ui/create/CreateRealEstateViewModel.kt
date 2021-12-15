package com.example.realestatemanager.ui.create

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestatemanager.data.repository.GeocoderRepository
import com.example.realestatemanager.data.repository.PhotoRepository
import com.example.realestatemanager.data.repository.RealEstateRepository
import com.example.realestatemanager.domain.models.NearbyPOI
import com.example.realestatemanager.domain.models.Photo
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.data.retrofit.Example
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class CreateRealEstateViewModel(
    private val realEstateRepository: RealEstateRepository,
    private val photoRepository: PhotoRepository,
    private val geocoderRepository: GeocoderRepository

) : ViewModel() {

    val liveData: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }

    val liveDataAddress by lazy {
        MutableLiveData<List<Address>?>()
    }

    val liveDataValidation by lazy {
        MutableLiveData<Long>()
    }

    val liveDataNearbyPOI by lazy {
        MutableLiveData<NearbyPOI>()
    }

    fun setCurrencyCode(currencyId: Int) {
        realEstateRepository.setCurrencyCode(currencyId = currencyId)
    }

    val liveDataCurrencyCode: LiveData<Int> = realEstateRepository.getCurrencyCode()


    val liveDataListPhoto by lazy {
        MutableLiveData<ArrayList<Photo>>()
    }

    // var PlacesSearchResult: Array<PlacesSearchResult>? = null

    private val listPlaceType = listOf(
        "restaurant",
        "park",
        "school",
        "store"
    )

    fun getLatLng(textAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            liveDataAddress.postValue(geocoderRepository.getLatLng(textAddress))
        }
    }

    fun getNearbyPoi(location: LatLng? = null) {
        viewModelScope.launch {
            val nearbyPoi = NearbyPOI()
            if (location != null) {
                nearbyPoi.nearbyRestaurant = false
                nearbyPoi.nearbyPark = false
                nearbyPoi.nearbySchool = false
                nearbyPoi.nearbyStore = false
                for (type in listPlaceType) {
                    val response: Response<Example>? = withContext(Dispatchers.IO) {
                        geocoderRepository.getNearbyPoi(location = location, type)
                    }
                    for (result in response?.body()?.results!!) {
                        for (element in result.types) {
                            when (element) {
                                "restaurant" -> nearbyPoi.nearbyRestaurant =
                                    true
                                "park" -> nearbyPoi.nearbyPark = true
                                "school" -> nearbyPoi.nearbySchool =
                                    true
                                "store" -> nearbyPoi.nearbyStore = true
                            }
                        }
                    }
                }
            }
            liveDataNearbyPOI.postValue(nearbyPoi)
        }
    }

    fun insertRealEstate(realEstate: RealEstate?) {
        viewModelScope.launch {
            liveData.value =
                realEstateRepository.insertRealEstate(realEstate!!)
        }
    }


    fun insertPhoto(photo: Photo) {
        viewModelScope.launch {
            liveDataValidation.value = photoRepository.insertPhoto(photo)
        }
    }

    fun setPhoto(listPhoto: ArrayList<Photo>) {
        liveDataListPhoto.value = listPhoto
    }
}
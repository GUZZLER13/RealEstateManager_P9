package com.example.realestatemanager.ui.create

import android.location.Address
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestatemanager.data.repository.PhotoRepository
import com.example.realestatemanager.data.repository.RealEstateRepository
import com.example.realestatemanager.domain.models.Photo
import com.example.realestatemanager.domain.models.RealEstate
import kotlinx.coroutines.launch

class CreateRealEstateViewModel(
    private val realEstateRepository: RealEstateRepository,
    private val photoRepository: PhotoRepository,
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

    val liveDataListPhoto by lazy {
        MutableLiveData<ArrayList<Photo>>()
    }

    // var PlacesSearchResult: Array<PlacesSearchResult>? = null

    val listPlaceType = listOf<String>(
        "restaurant",
        "park",
        "school",
        "store"
    )

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
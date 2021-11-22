package com.example.realestatemanager.ui.update

import android.location.Address
import androidx.lifecycle.*
import com.example.realestatemanager.data.repository.PhotoRepository
import com.example.realestatemanager.data.repository.RealEstateRepository
import com.example.realestatemanager.domain.models.Photo
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.domain.relation.RealEstateWithPhoto
import kotlinx.coroutines.launch

class UpdateViewModel(
    private val realEstateRepository: RealEstateRepository,
    private val photoRepository: PhotoRepository,
) : ViewModel() {
    val liveDataAddress by lazy {
        MutableLiveData<List<Address>?>()
    }

    val liveDataIdRealEstate: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }


    lateinit var liveDataRealEstate: LiveData<RealEstateWithPhoto>

    fun setId(id: Long) {
        liveDataRealEstate = realEstateRepository.getRealEstateWithId(id).asLiveData()
    }

    fun getId() {
        liveDataIdRealEstate.value = realEstateRepository.getIdRealEstate().value
    }


    fun updateRealEstate(realEstate: RealEstate) {
        viewModelScope.launch {
            realEstateRepository.updateRealEstate(realEstate)
        }
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch {
            photoRepository.deletePhoto(photo)
        }
    }

    fun insertPhoto(photo: Photo) {
        viewModelScope.launch {
            photoRepository.insertPhoto(photo)
        }
    }

    fun updatePhoto(photo: Photo) {
        viewModelScope.launch {
            photoRepository.updatePhoto(photo)
        }
    }
}
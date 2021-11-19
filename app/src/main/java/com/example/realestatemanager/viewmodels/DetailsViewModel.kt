package com.example.realestatemanager.viewmodels

import androidx.lifecycle.*
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.domain.relation.RealEstateWithPhoto
import com.example.realestatemanager.domain.repository.RealEstateRepository

class DetailsViewModel(private val realEstateRepository: RealEstateRepository) : ViewModel() {

    lateinit var liveDataRealEstate: LiveData<RealEstateWithPhoto>

    var livedataListRealEstate: LiveData<List<RealEstate>> =
        realEstateRepository.getFlowListRealEstate().asLiveData()

    val liveDataCurrencyCode: LiveData<Int> = realEstateRepository.getCurrencyCode()

    fun setCurrencyCode(currencyId: Int) {
        realEstateRepository.setCurrencyCode(currencyId = currencyId)
    }

    val liveDataIdRealEstate: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }


    fun setId(id: Long) {
        liveDataIdRealEstate.value = id
        realEstateRepository.setIdRealEstate(id)  // For modification
        liveDataRealEstate = Transformations.switchMap(liveDataIdRealEstate) { it ->
            realEstateRepository.getRealEstateWithId(it).asLiveData()
        }
    }

}
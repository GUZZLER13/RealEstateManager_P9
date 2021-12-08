package com.example.realestatemanager.ui.details

import androidx.lifecycle.*
import com.example.realestatemanager.data.repository.RealEstateRepository
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.domain.relation.RealEstateWithPhoto

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
        realEstateRepository.setIdRealEstate(id)
        liveDataRealEstate = Transformations.switchMap(liveDataIdRealEstate) {
            realEstateRepository.getRealEstateWithId(it).asLiveData()
        }
    }
}
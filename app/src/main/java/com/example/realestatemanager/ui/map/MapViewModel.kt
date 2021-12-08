package com.example.realestatemanager.ui.map

import androidx.lifecycle.*
import com.example.realestatemanager.data.repository.RealEstateRepository
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.domain.relation.RealEstateWithPhoto

class MapViewModel(private val realEstateRepository: RealEstateRepository) : ViewModel() {

    private lateinit var liveDataRealEstate: LiveData<RealEstateWithPhoto>

    var livedataListRealEstate: LiveData<List<RealEstate>> =
        realEstateRepository.getFlowListRealEstate().asLiveData()

    val liveDataCurrencyCode: LiveData<Int> = realEstateRepository.getCurrencyCode()

    fun setCurrencyCode(currencyId: Int) {
        realEstateRepository.setCurrencyCode(currencyId = currencyId)
    }

    private val liveDataIdRealEstate: MutableLiveData<Long> by lazy {
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
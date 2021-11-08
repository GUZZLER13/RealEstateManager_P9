package com.example.realestatemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.domain.RealEstateRepository
import com.example.realestatemanager.domain.models.RealEstate

class
RealEstateViewModel(
    private val realEstateRepository: RealEstateRepository,
) : ViewModel() {

    val liveDataIdRealEstate: LiveData<Long> = realEstateRepository.getIdRealEstate()

    val liveDataCurrencyCode: LiveData<Int> = realEstateRepository.getCurrencyCode()

    var realEstate = MutableLiveData<RealEstate>()


    fun setCurrencyCode(currencyId: Int) {
        realEstateRepository.setCurrencyCode(currencyId = currencyId)
    }
}

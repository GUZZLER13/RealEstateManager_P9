package com.example.realestatemanager.ui.simulator

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.data.repository.RealEstateRepository

class SimulatorViewModel(private val realEstateRepository: RealEstateRepository) : ViewModel() {

    val liveDataCurrencyCode: LiveData<Int> = realEstateRepository.getCurrencyCode()

    fun setCurrencyCode(currencyId: Int) {
        realEstateRepository.setCurrencyCode(currencyId = currencyId)
    }
}

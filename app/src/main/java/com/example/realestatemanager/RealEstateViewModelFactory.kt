package com.example.realestatemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.realestatemanager.domain.repository.PhotoRepository
import com.example.realestatemanager.domain.repository.RealEstateRepository
import com.example.realestatemanager.viewmodels.RealEstateViewModel

class RealEstateViewModelFactory(
    private val realEstateRepository: RealEstateRepository,
    private val photoRepository: PhotoRepository

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RealEstateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RealEstateViewModel(realEstateRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
package com.example.realestatemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.realestatemanager.domain.repository.PhotoRepository
import com.example.realestatemanager.domain.repository.RealEstateRepository
import com.example.realestatemanager.viewmodels.CreateRealEstateViewModel
import com.example.realestatemanager.viewmodels.DetailsViewModel
import com.example.realestatemanager.viewmodels.RealEstateViewModel
import com.example.realestatemanager.viewmodels.SimulatorViewModel

class RealEstateViewModelFactory(
    private val realEstateRepository: RealEstateRepository,
    private val photoRepository: PhotoRepository

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(RealEstateViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return RealEstateViewModel(realEstateRepository) as T
            }
            modelClass.isAssignableFrom(CreateRealEstateViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return CreateRealEstateViewModel(realEstateRepository, photoRepository) as T
            }
            modelClass.isAssignableFrom(SimulatorViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return SimulatorViewModel(realEstateRepository) as T
            }
            modelClass.isAssignableFrom(DetailsViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return DetailsViewModel(realEstateRepository) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
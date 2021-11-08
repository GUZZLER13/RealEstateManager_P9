package com.example.realestatemanager.ui.home

import androidx.lifecycle.*
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.domain.models.RealEstateRequest
import com.example.realestatemanager.domain.relation.RealEstateWithPhoto
import com.example.realestatemanager.domain.repository.RealEstateRepository

class
RealEstateViewModel(
    private val realEstateRepository: RealEstateRepository,
) : ViewModel() {

    val liveDataIdRealEstate: LiveData<Long> = realEstateRepository.getIdRealEstate()

    private val liveDataRealEstateRequest = MutableLiveData<RealEstateRequest>(RealEstateRequest())

    val liveDataCurrencyCode: LiveData<Int> = realEstateRepository.getCurrencyCode()

    var realEstate = MutableLiveData<RealEstate>()

    var listRealEstateWithPhoto: LiveData<List<RealEstateWithPhoto>> =
        Transformations.switchMap(liveDataRealEstateRequest) { realEstateRequest ->
            realEstateRepository.customQueryOrGetSimpleFlow(
                realEstateRequest.minSurface,
                realEstateRequest.maxSurface,
                realEstateRequest.minPrice,
                realEstateRequest.maxPrice,
                realEstateRequest.nearbyStore,
                realEstateRequest.nearbyPark,
                realEstateRequest.nearbyRestaurant,
                realEstateRequest.nearbySchool,
                realEstateRequest.minDateInLong,
                realEstateRequest.minDateSoldInLong,
                realEstateRequest.city,
                realEstateRequest.nbPhoto
            ).asLiveData()
        }


    fun setCurrencyCode(currencyId: Int) {
        realEstateRepository.setCurrencyCode(currencyId = currencyId)
    }
}

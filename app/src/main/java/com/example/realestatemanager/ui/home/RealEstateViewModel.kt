package com.example.realestatemanager.ui.home

import androidx.lifecycle.*
import com.example.realestatemanager.data.repository.RealEstateRepository
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.domain.models.RealEstateRequest
import com.example.realestatemanager.domain.relation.RealEstateWithPhoto

class
RealEstateViewModel(
    private val realEstateRepository: RealEstateRepository
) : ViewModel() {

    private val liveDataRealEstateRequest = MutableLiveData(RealEstateRequest())

    val liveDataIdRealEstate: LiveData<Long> = realEstateRepository.getIdRealEstate()

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

    fun setMinSurface(minSurface: Float?) {
        liveDataRealEstateRequest.value?.minSurface = minSurface
    }

    fun setMaxSurface(maxSurface: Float?) {
        liveDataRealEstateRequest.value?.maxSurface = maxSurface
    }

    fun setMinPrice(minPrice: Int?) {
        liveDataRealEstateRequest.value?.minPrice = minPrice
    }

    fun setMaxPrice(maxPrice: Int?) {
        liveDataRealEstateRequest.value?.maxPrice = maxPrice
    }

    fun setNearbyStore(nearbyStore: Boolean) {
        if (!nearbyStore) {
            liveDataRealEstateRequest.value?.nearbyStore = null
        } else {
            liveDataRealEstateRequest.value?.nearbyStore = nearbyStore
        }
    }

    fun setNearbyPark(nearbyPark: Boolean) {
        if (!nearbyPark) {
            liveDataRealEstateRequest.value?.nearbyPark = null
        } else {
            liveDataRealEstateRequest.value?.nearbyPark = nearbyPark
        }
    }

    fun setNearbySchool(nearbySchool: Boolean) {
        if (!nearbySchool) {
            liveDataRealEstateRequest.value?.nearbySchool = null
        } else {
            liveDataRealEstateRequest.value?.nearbySchool = nearbySchool
        }
    }

    fun setNearbyRestaurant(nearbyRestaurant: Boolean) {
        if (!nearbyRestaurant) {
            liveDataRealEstateRequest.value?.nearbyRestaurant = null
        } else {
            liveDataRealEstateRequest.value?.nearbyRestaurant = nearbyRestaurant
        }
    }

    fun setMinDate(date: Long?) {
        liveDataRealEstateRequest.value?.minDateInLong = date
    }

    fun setMinDateSold(date: Long?) {
        liveDataRealEstateRequest.value?.minDateSoldInLong = date
    }

    fun setNbPhoto(nbPhoto: Int?) {
        liveDataRealEstateRequest.value?.nbPhoto = nbPhoto
    }

    fun setCityName(cityName: String?) {
        liveDataRealEstateRequest.value?.city = cityName
    }

    fun validationUpdateQuery() {
        liveDataRealEstateRequest.value = liveDataRealEstateRequest.value
    }
}

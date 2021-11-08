package com.example.realestatemanager.domain.models

data class RealEstateRequest(
    var minSurface: Float? = null,
    var maxSurface: Float? = null,
    var minPrice: Int? = null,
    var maxPrice: Int? = null,
    var nearbyStore: Boolean? = null,
    var nearbyPark: Boolean? = null,
    var nearbyRestaurant: Boolean? = null,
    var nearbySchool: Boolean? = null,
    var minDateInLong: Long? = null,
    var minDateSoldInLong: Long? = null,
    var sold: Boolean? = null,
    var city: String? = null,
    var nbPhoto: Int? = null
)
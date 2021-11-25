package com.example.realestatemanager.domain.models

data class NearbyPOI(

    var nearbySchool: Boolean? = null,
    var nearbyRestaurant: Boolean? = null,
    var nearbyPark: Boolean? = null,
    var nearbyStore: Boolean? = null
)
package com.example.realestatemanager.domain.retrofit.`interface`

import com.example.realestatemanager.domain.retrofit.Example
import com.example.realestatemanager.utils.Constants.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitMaps {

    @GET("api/place/nearbysearch/json?sensor=true&key=$API_KEY")
    fun getNearbyPlaces(

        @Query("type") type: String?,
        @Query("location") location: String?,
        @Query("radius") radius: Int

    ): Call<Example>
}
package com.example.realestatemanager.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "real_estate_table")
data class RealEstate(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_realestate")
    var idRealEstate: Long = 0,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "price")
    var price: Int,

    @ColumnInfo(name = "surface")
    var surface: Int,

    @ColumnInfo(name = "nb_rooms")
    var nbRooms: Int,

    @ColumnInfo(name = "nb_bedrooms")
    var nbBedrooms: Int,

    @ColumnInfo(name = "nb_bathrooms")
    var nbBathrooms: Int,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "address")
    var address: String,

    @ColumnInfo(name = "property_status")
    var propertyStatus: Boolean,

    @ColumnInfo(name = "date_entry")
    var dateEntry: Long,

    @ColumnInfo(name = "date_sold")
    var dateSold: Long?,

    @ColumnInfo(name = "real_estate_agent")
    var realEstateAgent: String,

    @ColumnInfo(name = "latitude")
    var latitude: Float?,

    @ColumnInfo(name = "longitude")
    var longitude: Float?,

//    @ColumnInfo(name = "nearby_store")
//    var nearbyStore: Boolean?,
//
//    @ColumnInfo(name = "nearby_park")
//    var nearbyPark: Boolean?,
//
//    @ColumnInfo(name = "nearby_restaurant")
//    var nearbyRestaurant: Boolean?,
//
//    @ColumnInfo(name = "nearby_school")
//    var nearbySchool: Boolean?
)




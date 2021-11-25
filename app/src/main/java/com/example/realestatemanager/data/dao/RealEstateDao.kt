package com.example.realestatemanager.data.dao

import android.database.Cursor
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.domain.relation.RealEstateWithPhoto
import kotlinx.coroutines.flow.Flow


@Dao
interface RealEstateDao {


    @Query("SELECT * FROM real_estate_table")
    fun getAll(): Flow<List<RealEstate>>

    @Query("SELECT * FROM real_estate_table WHERE id_realestate = :idRealEstate")
    fun getRealEstateWithCursor(vararg idRealEstate: Long): Cursor

    @Query("SELECT * FROM real_estate_table WHERE id_realestate= :idRealEstate")
    fun getRealEstateWithId(vararg idRealEstate: Long): Flow<RealEstateWithPhoto>

    @Query("SELECT * FROM REAL_ESTATE_TABLE")
    fun getRealEstate(): Flow<List<RealEstate>>

    @Transaction
    @Query("SELECT * FROM real_estate_table")
    fun getRealEstateWithPhoto(): Flow<List<RealEstateWithPhoto>>

    @Transaction
    @Query("SELECT * FROM REAL_ESTATE_TABLE WHERE surface BETWEEN :minSurface AND :maxSurface")
    fun getRealEstateBetweenTwoSurface(
        minSurface: Float,
        maxSurface: Float
    ): Flow<List<RealEstateWithPhoto>>

    @Transaction
    @Query("SELECT * FROM REAL_ESTATE_TABLE WHERE price BETWEEN :minPrice AND :maxPrice")
    fun getRealEstateBetweenTwoPrice(
        minPrice: Float,
        maxPrice: Float
    ): Flow<List<RealEstateWithPhoto>>

    @Insert
    suspend fun insert(realEstate: RealEstate): Long

    @Delete
    suspend fun delete(vararg realEstate: RealEstate)


    @Query("DELETE FROM REAL_ESTATE_TABLE")
    suspend fun allDelete()

    @Update
    suspend fun updateRealEstate(vararg realEstate: RealEstate)

    @RawQuery(observedEntities = [RealEstate::class])
    fun getRealEstateWithQuery(query: SupportSQLiteQuery): Flow<List<RealEstateWithPhoto>>

}
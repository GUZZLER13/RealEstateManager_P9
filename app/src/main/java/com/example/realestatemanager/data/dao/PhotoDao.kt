package com.example.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.example.realestatemanager.domain.models.Photo

@Dao
interface PhotoDao {

    @Insert
    suspend fun insert(photo: Photo): Long

    @Delete
    suspend fun delete(photo: Photo)

    @Update
    suspend fun update(vararg photo: Photo)
}
package com.example.realestatemanager.data.repository

import androidx.annotation.WorkerThread
import com.example.realestatemanager.data.dao.PhotoDao
import com.example.realestatemanager.domain.models.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepository(private val photoDao: PhotoDao) {

    @WorkerThread
    suspend fun insertPhoto(photo: Photo) = withContext(Dispatchers.IO) {
        photoDao.insert(photo)
    }

    suspend fun deletePhoto(photo: Photo) {
        photoDao.delete(photo)
    }

    suspend fun updatePhoto(photo: Photo) {
        photoDao.update(photo)
    }
}
package com.example.realestatemanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.realestatemanager.data.dao.PhotoDao
import com.example.realestatemanager.data.dao.RealEstateDao
import com.example.realestatemanager.domain.models.Photo
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.utils.Constants.DATABASE_NAME
import kotlinx.coroutines.CoroutineScope


@Database(entities = [RealEstate::class, Photo::class], version = 100, exportSchema = false)
abstract class RealEstateDatabase : RoomDatabase() {
    abstract fun RealEstateDao(): RealEstateDao

    abstract fun PhotoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: RealEstateDatabase? = null


        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): RealEstateDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RealEstateDatabase::class.java,
                    DATABASE_NAME
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    //.addCallback(RealEstateDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun deleteDatabase(realEstateDao: RealEstateDao) {
        realEstateDao.allDelete()
    }
}


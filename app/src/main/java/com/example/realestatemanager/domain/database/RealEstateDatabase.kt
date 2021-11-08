package com.example.realestatemanager.domain.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.realestatemanager.domain.dao.PhotoDao
import com.example.realestatemanager.domain.dao.RealEstateDao
import com.example.realestatemanager.domain.models.Photo
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.utils.Constants.DATABASE_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
                    .fallbackToDestructiveMigration() // Wipes and rebuilds instead of migrating if no Migration object.
                    //.addCallback(RealEstateDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


    //* NOT ACTUALY USED //
    private class RealEstateDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        /**
         * Override the onCreate method to populate the database.
         */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // If you want to keep the data through app restarts,
            // comment out the following line.
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    //deleteDatabase(database.RealEstateDao())  // Clean db for more ez dev
                }
            }
        }

        suspend fun deleteDatabase(realEstateDao: RealEstateDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            realEstateDao.allDelete()

        }


        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */


    }
}


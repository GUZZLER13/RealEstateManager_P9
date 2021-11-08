package com.example.realestatemanager

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.example.realestatemanager.domain.database.RealEstateDatabase
import com.example.realestatemanager.domain.repository.PhotoRepository
import com.example.realestatemanager.domain.repository.RealEstateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateApplication : MultiDexApplication() {
    val realEstateRepository by lazy { RealEstateRepository(database.RealEstateDao()) }

    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    companion object {
        lateinit var instance: Application
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    val database by lazy {
        RealEstateDatabase.getDatabase(this)
    }

    val photoRepository by lazy { PhotoRepository(database.PhotoDao()) }


}

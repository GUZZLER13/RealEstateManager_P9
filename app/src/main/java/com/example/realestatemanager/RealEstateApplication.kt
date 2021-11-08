package com.example.realestatemanager

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.example.realestatemanager.domain.RealEstateRepository
import com.example.realestatemanager.domain.database.RealEstateDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateApplication : MultiDexApplication() {
    val realEstateRepository by lazy { RealEstateRepository(database.RealEstateDao()) }

    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    companion object {
        lateinit var instance: Application
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private val database by lazy {
        RealEstateDatabase.getDatabase(this, applicationScope)
    }

}

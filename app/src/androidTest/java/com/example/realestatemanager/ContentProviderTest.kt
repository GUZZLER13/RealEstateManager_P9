package com.example.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.database.DatabaseUtils
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.realestatemanager.data.database.RealEstateDatabase
import com.example.realestatemanager.domain.models.RealEstate
import com.example.realestatemanager.domain.provider.RealEstateContentProvider
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentProviderTest {

    private lateinit var contentResolver: ContentResolver
    private var id: Long = 0
    private lateinit var realEstateDatabase: RealEstateDatabase

    @Before
    fun setup() {
        realEstateDatabase = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RealEstateDatabase::class.java, "real_estate_database"
        )
            .allowMainThreadQueries()
            .build()
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
        cleanDataBase()
    }

    @Test
    fun getRealEstateWhenNoItemInserted() {
        val cursor = contentResolver.query(
            ContentUris.withAppendedId(RealEstateContentProvider.URI_REAL_ESTATE, id),
            null,
            null,
            null,
            null
        )
        assertThat(cursor?.count, `is`(0))
    }

    @Test
    fun getRealEstateWhenOneItemInserted() {
        insertRealEstate()
        val cursor = contentResolver.query(
            ContentUris.withAppendedId(RealEstateContentProvider.URI_REAL_ESTATE, id),
            null,
            null,
            null,
            null
        )
        assertThat(cursor, notNullValue())
        Log.d(
            "Content Provider",
            "getRealEstateWhenOneItemInserted: " + DatabaseUtils.dumpCursorToString(cursor)
        )
        assertThat(cursor!!.count, `is`(1))
        assert(cursor.moveToFirst())
        assert(cursor.getString(cursor.getColumnIndexOrThrow("type")) == "T4")
        assert(cursor.getString(cursor.getColumnIndexOrThrow("address")) == "2 rue du petit ponney, Civrac de Blaye")
        cursor.close()
    }

    private fun insertRealEstate() = runBlocking {
        id = realEstateDatabase.RealEstateDao().insert(
            realEstate = RealEstate(
                type = "T4",
                price = 150000,
                surface = 130,
                nbRooms = 5,
                nbBathrooms = 1,
                nbBedrooms = 3,
                description = "Mon appartement se trouve au troisième et dernier étage d’un immeuble à Acigné, près de\n" +
                        "Rennes.  Il n’y a pas d’ascenseur, et il y a deux appartements par palier.  J’ai un paillasson devant\n" +
                        "la porte, avec une vache dessinée dessus pour accueillir les visiteurs",
                address = "2 rue du petit ponney, Civrac de Blaye",
                propertyStatus = false,
                dateEntry = 1.toLong(),
                dateSold = null,
                realEstateAgent = "Math",
                latitude = null,
                longitude = null,
                nearbyStore = true,
                nearbyPark = true,
                nearbyRestaurant = false,
                nearbySchool = true
            )
        )
    }

    private fun cleanDataBase() = runBlocking {
        realEstateDatabase.RealEstateDao().allDelete()
    }
}
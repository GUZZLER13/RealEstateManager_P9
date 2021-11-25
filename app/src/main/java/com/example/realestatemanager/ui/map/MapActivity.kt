package com.example.realestatemanager.ui.map

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.example.realestatemanager.R
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.data.repository.GeocoderRepository
import com.example.realestatemanager.databinding.ActivityMapBinding
import com.example.realestatemanager.ui.create.CreateRealEstateActivity
import com.example.realestatemanager.ui.home.MainActivity
import com.example.realestatemanager.ui.simulator.SimulatorActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MapActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var mapbinding: ActivityMapBinding
    private val mapViewModel: MapViewModel by viewModels() {
        RealEstateViewModelFactory(
            (application as RealEstateApplication).realEstateRepository,
            photoRepository = (application as RealEstateApplication).photoRepository,
            GeocoderRepository(context = applicationContext)
        )
    }
    private var checkedItem = 0
    private var idForUpdateIntent: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapbinding = ActivityMapBinding.inflate(layoutInflater)
        val view = mapbinding.root
        setContentView(view)
        setupToolbar()
//        idObserver()
//        observerCurrencyId()
//        setOnMenuItemClick()
        if (savedInstanceState == null) {
            val bundle = Bundle()
            bundle.putLong("idRealEstate", intent.getLongExtra("idRealEstate", 0))
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_map, MapFragment::class.java, bundle)
                .commit()
        }
    }

    private fun setupToolbar() {
        mToolbar = mapbinding.materialToolbar
        mToolbar.title = "Map"
        setSupportActionBar(mToolbar)
        setOnMenuItemClick()
        setupBackButton()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_app_bar, menu)
        menu.findItem(R.id.realestate_filters)?.isVisible = false
        menu.findItem(R.id.realestate_update)?.isVisible = false
        menu.findItem(R.id.currency)?.isVisible = false
        menu.findItem(R.id.realestate_map)?.isVisible = false

        return true
    }


    private fun setupBackButton() {
        mToolbar.navigationIcon =
            AppCompatResources.getDrawable(this, R.drawable.ic_baseline_arrow_back_24)
        mToolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setOnMenuItemClick() {
        mToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.realestate_add -> {
                    //viewModel.insert()
                    val createIntent = Intent(
                        this,
                        CreateRealEstateActivity::class.java
                    )
                    startActivity(createIntent)
                    true
                }
                R.id.simulator -> {
                    val simulatorIntent = Intent(this, SimulatorActivity::class.java)
                    startActivity(simulatorIntent)
                    true
                }

                else -> false
            }
        }
    }

    private fun alertDialogCurrency() {
        val items = arrayOf("Dollar", "Euro")
        MaterialAlertDialogBuilder(this)
            .setTitle("Select for convert currency to :")
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(items, checkedItem) { _, which ->
                mapViewModel.setCurrencyCode(which)
            }
            .show()
    }

    private fun observerCurrencyId() {
        mapViewModel.liveDataCurrencyCode.observe(this, {
            checkedItem = it
        })
    }


    private fun idObserver() {
        mapViewModel.liveDataIdRealEstate.observe(this, {
            idForUpdateIntent = it
        })
    }
}
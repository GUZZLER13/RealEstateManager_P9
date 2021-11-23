package com.example.realestatemanager.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.realestatemanager.R
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.databinding.ActivityMainBinding
import com.example.realestatemanager.ui.create.CreateRealEstateActivity
import com.example.realestatemanager.ui.map.MapActivity
import com.example.realestatemanager.ui.simulator.SimulatorActivity
import com.example.realestatemanager.ui.update.UpdateActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var mFragmentRealEstate: RealEstateFragment
    private var idForUpdateIntent: Long? = null
    private var isLargeDisplay = false
    private lateinit var mToolbar: Toolbar
    private var checkedItem = 0
    private lateinit var binding: ActivityMainBinding


    private val viewModel: RealEstateViewModel by viewModels() {
        RealEstateViewModelFactory(
            (application as RealEstateApplication).realEstateRepository,
            photoRepository = (application as RealEstateApplication).photoRepository
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLargeDisplay = resources.getBoolean(R.bool.large_layout)
        if (isLargeDisplay) {
            idObserver()
        }
        setupToolbar()
        observerCurrencyId()
        setNavigationOnClick()
        showFragments()
        setOnMenuItemClick()

    }


    private fun showFragments() {
        mFragmentRealEstate = RealEstateFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_layout_real_estate, mFragmentRealEstate)
            .commit()
    }

    private fun observerCurrencyId() {
        viewModel.liveDataCurrencyCode.observe(this, {
            checkedItem = it
        })
    }

    private fun setupToolbar() {
        mToolbar = binding.materialToolbar
        mToolbar.title = "R.E.Manager"
        setSupportActionBar(mToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_app_bar, menu)
        if (!isLargeDisplay) {
            menu.findItem(R.id.realestate_update)?.isVisible = false
        }
        return true
    }

    private fun setNavigationOnClick() {
        mToolbar.setNavigationOnClickListener {
            // Handle navigation icon press

        }
    }

    private fun setOnMenuItemClick() {

        mToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.realestate_map -> {
                    //viewModel.insert()
                    val createIntent = Intent(
                        this,
                        MapActivity::class.java
                    )
                    startActivity(createIntent)
                    true
                }
                R.id.realestate_add -> {
                    //viewModel.insert()
                    val createIntent = Intent(
                        this,
                        CreateRealEstateActivity::class.java
                    )
                    startActivity(createIntent)
                    true
                }
                R.id.realestate_filters -> {
                    showDialog()
                    true
                }
                R.id.realestate_update -> {

                    if (idForUpdateIntent != null) {
                        val updateIntent = Intent(this, UpdateActivity::class.java)
                        updateIntent.putExtra("idRealEstate", idForUpdateIntent)
                        startActivity(updateIntent)
                    } else {
                        Toast.makeText(this, "Please select a property", Toast.LENGTH_LONG).show()
                    }
                    true
                }
                R.id.simulator -> {
                    val simulatorIntent = Intent(this, SimulatorActivity::class.java)
                    startActivity(simulatorIntent)
                    true
                }
                R.id.currency -> {
                    alertDialogCurrency()
                    true
                }
                else -> false
            }
        }
    }

    private fun showDialog() {
        val fragmentManager = supportFragmentManager
        val newFragment = FilterDialogFragment()
        if (isLargeDisplay) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog")
        } else {
            FilterDialogFragment().show(fragmentManager, "test")
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
                viewModel.setCurrencyCode(which)
            }
            .show()
    }

    private fun idObserver() {
        viewModel.liveDataIdRealEstate.observe(this, {
            idForUpdateIntent = it
        })
    }

}
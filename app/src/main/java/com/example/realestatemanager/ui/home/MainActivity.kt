package com.example.realestatemanager.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.realestatemanager.R
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.databinding.ActivityMainBinding
import com.example.realestatemanager.ui.simulator.SimulatorActivity
import com.example.realestatemanager.ui.update.UpdateRealEstateActivity
import com.example.realestatemanager.viewmodels.RealEstateViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private var idForUpdateIntent: Long? = null
    private var isLargeDisplay = false
    private lateinit var mToolbar: Toolbar
    private var checkedItem = 0


    private val viewModel: RealEstateViewModel by viewModels() {
        RealEstateViewModelFactory(
            (application as RealEstateApplication).realEstateRepository,
            photoRepository = (application as RealEstateApplication).photoRepository
        )
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLargeDisplay = resources.getBoolean(R.bool.large_layout)
        if (isLargeDisplay) {
            idObserver()
        }
        setupToolbar()
        setNavigationOnClick()
        showFragments()
        setOnMenuItemClick()

    }

    private fun idObserver() {
        viewModel.liveDataIdRealEstate.observe(this, Observer {
            idForUpdateIntent = it
        })
    }

    private fun showFragments() {
        val mFragmentRealEstate = RealEstateFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_layout_real_estate, mFragmentRealEstate)
            .commit()
    }

    fun observerCurrencyId() {
        viewModel.liveDataCurrencyCode.observe(this, Observer {
            checkedItem = it
        })
    }

    private fun setupToolbar() {
        mToolbar = binding.materialToolbar
        setSupportActionBar(mToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_app_bar, menu)
        if (!isLargeDisplay) {
            menu.findItem(R.id.realestate_update)?.isVisible = false
        }
        return true;
    }

    private fun setNavigationOnClick() {
        mToolbar.setNavigationOnClickListener {
            // Handle navigation icon press

        }
    }


    private fun setOnMenuItemClick() {

        mToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.realestate_add -> {
                    //viewModel.insert()
                    val createIntent = Intent(
                        this,
                        NewRealEstateActivity::class.java
                    )
                    startActivity(createIntent)
                    true
                }
                R.id.realestate_filters -> {
                    true
                }
                R.id.realestate_update -> {

                    if (idForUpdateIntent != null) {
                        val updateIntent = Intent(this, UpdateRealEstateActivity::class.java)
                        updateIntent.putExtra("idRealEstate", idForUpdateIntent)
                        startActivity(updateIntent)
                    } else {
                        Toast.makeText(this, "Please select a property", Toast.LENGTH_LONG).show()
                    }
                    true
                }
                R.id.loan -> {
                    val loanIntent = Intent(this, SimulatorActivity::class.java)
                    startActivity(loanIntent)
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
}
package com.example.realestatemanager.ui.details

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
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
import com.example.realestatemanager.databinding.ActivityDetailsBinding
import com.example.realestatemanager.ui.create.CreateRealEstateActivity
import com.example.realestatemanager.ui.home.MainActivity
import com.example.realestatemanager.ui.map.MapActivity
import com.example.realestatemanager.ui.simulator.SimulatorActivity
import com.example.realestatemanager.ui.update.UpdateActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class DetailsActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var detailBinding: ActivityDetailsBinding

    private val detailsViewModel: DetailsViewModel by viewModels {
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
        detailBinding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = detailBinding.root
        setContentView(view)
        setupToolbar()
        idObserver()
        observerCurrencyId()
        setOnMenuItemClick()
        isDark()
        if (savedInstanceState == null) {
            val bundle = Bundle()
            bundle.putLong("idRealEstate", intent.getLongExtra("idRealEstate", 0))
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_details, DetailsFragment::class.java, bundle)
                .commit()
        }
    }

    private fun setupToolbar() {
        mToolbar = detailBinding.materialToolbar
        mToolbar.title = "Details"
        setSupportActionBar(mToolbar)
        setupBackButton()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_app_bar, menu)
        menu.findItem(R.id.realestate_filters)?.isVisible = false
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
                R.id.simulator -> {
                    val simulatorIntent = Intent(this, SimulatorActivity::class.java)
                    startActivity(simulatorIntent)
                    true
                }
                R.id.realestate_map -> {
                    val createIntent = Intent(
                        this,
                        MapActivity::class.java
                    )
                    startActivity(createIntent)
                    true
                }
                R.id.realestate_add -> {
                    val createIntent = Intent(
                        this,
                        CreateRealEstateActivity::class.java
                    )
                    startActivity(createIntent)
                    true
                }
                R.id.realestate_update -> {
                    if (idForUpdateIntent != null) {
                        val updateIntent = Intent(this, UpdateActivity::class.java)
                        updateIntent.putExtra("idRealEstate", idForUpdateIntent)
                        startActivity(updateIntent)
                    }
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
                detailsViewModel.setCurrencyCode(which)
            }
            .show()
    }

    private fun observerCurrencyId() {
        detailsViewModel.liveDataCurrencyCode.observe(this, {
            checkedItem = it
        })
    }

    private fun idObserver() {
        detailsViewModel.liveDataIdRealEstate.observe(this, {
            idForUpdateIntent = it
        })
    }

    private fun isDark() {
        when (applicationContext?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                detailBinding.fragmentContainerDetails.setBackgroundColor(Color.parseColor("#C0C0C0"))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                detailBinding.fragmentContainerDetails.setBackgroundColor(Color.parseColor("#ededed"))
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
            }
        }
    }
}
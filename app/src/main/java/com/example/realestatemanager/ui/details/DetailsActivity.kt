package com.example.realestatemanager.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.realestatemanager.R
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.databinding.ActivityDetailsBinding
import com.example.realestatemanager.ui.create.CreateRealEstateActivity
import com.example.realestatemanager.ui.home.MainActivity
import com.example.realestatemanager.ui.update.UpdateActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class DetailsActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var detailbinding: ActivityDetailsBinding
    private val detailsViewModel: DetailsViewModel by viewModels() {
        RealEstateViewModelFactory(
            (application as RealEstateApplication).realEstateRepository,
            photoRepository = (application as RealEstateApplication).photoRepository
        )
    }
    private var checkedItem = 0
    private var idForUpdateIntent: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailbinding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = detailbinding.root
        setContentView(view)
        setupToolbar()
        idObserver()
        observerCurrencyId()
        setOnMenuItemClick()
        if (savedInstanceState == null) {
            val bundle = Bundle()
            bundle.putLong("idRealEstate", intent.getLongExtra("idRealEstate", 0))
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_details, DetailsFragment::class.java, bundle)
                .commit()
        }
    }

    private fun setupToolbar() {
        mToolbar = detailbinding.materialToolbar
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
                R.id.realestate_add -> {
                    //viewModel.insert()
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
}
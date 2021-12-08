package com.example.realestatemanager.ui.simulator

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
import com.example.realestatemanager.data.repository.GeocoderRepository
import com.example.realestatemanager.databinding.ActivitySimulatorBinding
import com.example.realestatemanager.ui.home.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SimulatorActivity : AppCompatActivity() {

    private lateinit var mToolbar: Toolbar
    private lateinit var loanBinding: ActivitySimulatorBinding
    private val viewModel: SimulatorViewModel by viewModels {
        RealEstateViewModelFactory(
            (application as RealEstateApplication).realEstateRepository,
            photoRepository = (application as RealEstateApplication).photoRepository,
            GeocoderRepository(context = applicationContext)
        )
    }
    private var checkedItem = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loanBinding = ActivitySimulatorBinding.inflate(layoutInflater)
        val view = loanBinding.root
        setContentView(view)
        setupToolbar()
        observerCurrencyId()
        setOnMenuItemClick()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_simulator, SimulatorFragment.newInstance())
                .commit()
        }
    }


    private fun setupToolbar() {
        mToolbar = loanBinding.materialToolbar
        mToolbar.title = "Loan simulator"
        setSupportActionBar(mToolbar)
        setupBackButton()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_app_bar, menu)
        menu.findItem(R.id.realestate_update)?.isVisible = false
        menu.findItem(R.id.realestate_filters)?.isVisible = false
        menu.findItem(R.id.realestate_add)?.isVisible = false
        menu.findItem(R.id.simulator)?.isVisible = false
        return true
    }

    private fun setOnMenuItemClick() {
        loanBinding.materialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
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

    private fun observerCurrencyId() {
        viewModel.liveDataCurrencyCode.observe(this, Observer {
            checkedItem = it
        })
    }


    private fun setupBackButton() {
        mToolbar.navigationIcon = AppCompatResources.getDrawable(
            this,
            R.drawable.ic_baseline_arrow_back_24
        )
        mToolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
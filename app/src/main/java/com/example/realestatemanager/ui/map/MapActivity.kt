package com.example.realestatemanager.ui.map

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityMapBinding
import com.example.realestatemanager.ui.create.CreateRealEstateActivity
import com.example.realestatemanager.ui.home.MainActivity
import com.example.realestatemanager.ui.simulator.SimulatorActivity


class MapActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var mapBinding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapBinding = ActivityMapBinding.inflate(layoutInflater)
        val view = mapBinding.root
        setContentView(view)
        setupToolbar()

        if (savedInstanceState == null) {
            val bundle = Bundle()
            bundle.putLong("idRealEstate", intent.getLongExtra("idRealEstate", 0))
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_map, MapFragment::class.java, bundle)
                .commit()
        }
    }

    private fun setupToolbar() {
        mToolbar = mapBinding.materialToolbar
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
}
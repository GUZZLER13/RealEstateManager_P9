package com.example.realestatemanager.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.realestatemanager.R
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var idForUpdateIntent: Long? = null
    private var isLargeDisplay = false
    private lateinit var mToolbar: Toolbar

    private val viewModel: RealEstateViewModel by viewModels() {
        RealEstateViewModelFactory(
            (application as RealEstateApplication).realEstateRepository
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
    }

    private fun idObserver() {
        viewModel.liveDataIdRealEstate.observe(this, Observer {
            idForUpdateIntent = it
        })
    }

    fun showFragments() {
        val mFragmentRealEstate = RealEstateFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_layout_real_estate, mFragmentRealEstate)
            .commit()
    }
}
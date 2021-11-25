package com.example.realestatemanager.ui.update

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.example.realestatemanager.R
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.data.repository.GeocoderRepository
import com.example.realestatemanager.databinding.ActivityUpdateBinding
import com.example.realestatemanager.ui.details.DetailsActivity
import kotlin.properties.Delegates


class UpdateActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var updateBinding: ActivityUpdateBinding
    private val viewModel: UpdateViewModel by viewModels() {
        RealEstateViewModelFactory(
            (application as RealEstateApplication).realEstateRepository,
            photoRepository = (application as RealEstateApplication).photoRepository,
            GeocoderRepository(context = applicationContext)
        )
    }
    private var idRealEstate by Delegates.notNull<Long>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBinding = ActivityUpdateBinding.inflate(layoutInflater)
        val view = updateBinding.root
        setContentView(view)
        setupToolbar()
        if (savedInstanceState == null) {
            val bundle = Bundle()

            bundle.putLong("idRealEstate", intent.getLongExtra("idRealEstate", 0))
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_update, UpdateFragment::class.java, bundle)
                .commit()
        }
    }

    private fun observeId() {
        viewModel.liveDataIdRealEstate.observe(this, {
            idRealEstate = it
        })
    }

    private fun setupToolbar() {
        mToolbar = updateBinding.materialToolbar
        mToolbar.title = "Update"
        setSupportActionBar(mToolbar)
        setupBackButton()
    }

    private fun setupBackButton() {
        mToolbar.navigationIcon = AppCompatResources.getDrawable(
            this,
            R.drawable.ic_baseline_arrow_back_24
        )
        mToolbar.setNavigationOnClickListener {
            viewModel.getId()
            observeId()
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("idRealEstate", idRealEstate)
            startActivity(intent)
            finish()
        }
    }
}

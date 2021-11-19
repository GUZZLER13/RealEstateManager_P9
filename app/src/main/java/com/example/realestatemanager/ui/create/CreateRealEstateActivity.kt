package com.example.realestatemanager.ui.create

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.CreateRealEstateBinding
import com.example.realestatemanager.ui.home.MainActivity


class CreateRealEstateActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var createBinding: CreateRealEstateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createBinding = CreateRealEstateBinding.inflate(layoutInflater)
        val view = createBinding.root
        setContentView(view)
        setupToolbar()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, CreateRealEstateFragment.newInstance())
                .commit()
        }
    }

    private fun setupToolbar() {
        mToolbar = createBinding.materialToolbar
        mToolbar.title = "Create"
        setSupportActionBar(mToolbar)
        setupBackButton()
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
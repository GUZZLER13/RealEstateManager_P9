package com.example.realestatemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.databinding.FragmentRealEstateBinding

class RealEstateFragment : Fragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private val adapter = RealEstateAdapter()
    private lateinit var realEstateBinding: FragmentRealEstateBinding
    private val viewModelFrag: RealEstateViewModel by activityViewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            photoRepository = (requireActivity().application as RealEstateApplication).photoRepository,
            (requireActivity().application as RealEstateApplication).geocoderRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        realEstateBinding = FragmentRealEstateBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return realEstateBinding.root
    }

    private fun setupRecyclerView() {
        recyclerView = realEstateBinding.recyclerviewRealEstate
        linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, linearLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        realEstateBinding.recyclerviewRealEstate.adapter = adapter
        viewModelFrag.listRealEstateWithPhoto.observe(
            viewLifecycleOwner,
            Observer { listRealEstatesWithPhoto ->
                listRealEstatesWithPhoto.let { adapter.data = it }
            })

        observeCurrencyCode()
    }

    private fun observeCurrencyCode() {
        viewModelFrag.liveDataCurrencyCode.observe(viewLifecycleOwner, Observer {
            adapter.currency = it
        })
    }
}
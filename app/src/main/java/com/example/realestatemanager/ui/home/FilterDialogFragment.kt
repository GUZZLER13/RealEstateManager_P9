package com.example.realestatemanager.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.realestatemanager.databinding.FilterDialogBinding
import com.example.realestatemanager.utils.TextFieldUtils.Companion.isNumber
import com.google.android.material.datepicker.MaterialDatePicker

import java.util.*

class FilterDialogFragment : DialogFragment() {

    private var datePublication: Long? = null
    private var dateSold: Long? = null
    lateinit var viewModel: RealEstateViewModel
    private lateinit var filterBinding: FilterDialogBinding
    private val datePickerPublication = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Selected the minimum publication date")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()
    private val datePickerSold = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Selected the minimum sold date")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()

    /** The system calls this to get the DialogFragment's layout, regardless
    of whether it's being displayed as a dialog or an embedded fragment. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        filterBinding = FilterDialogBinding.inflate(inflater, container, false)
        // Inflate the layout to use as dialog or embedded fragment
        //return inflater.inflate(R.layout.filter_dialog, container, false)  // Need to check on large screen
        onClickDatePicker()
        onClickDatePickerSold()
        onClickValidation()
        viewModel = ViewModelProvider(requireActivity())[RealEstateViewModel::class.java]
        return filterBinding.root
    }

    /** The system calls this only when creating the layout in a dialog. */

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun onClickDatePicker() {
        filterBinding.ButtonDatePicker.setOnClickListener {
            val fragmentManager = parentFragmentManager
            datePickerPublication.show(fragmentManager, "DatePicker")
            datePickerPublication.addOnPositiveButtonClickListener { selection: Long ->
                datePublication = Date(selection).time
            }
        }
    }

    private fun onClickDatePickerSold() {
        filterBinding.ButtonDatePickerSold.setOnClickListener {
            val fragmentManager = parentFragmentManager
            datePickerSold.show(fragmentManager, "DatePickerSold")
            datePickerSold.addOnPositiveButtonClickListener { selection: Long ->
                dateSold = Date(selection).time
            }
        }
    }

    private fun onClickValidation() {
        filterBinding.ButtonValidationFilter.setOnClickListener {
            if (validate()) {
                viewModel.setMinPrice(
                    filterBinding.textFieldPriceMin.editText?.text.toString()
                        .toIntOrNull()
                )
                viewModel.setMaxPrice(
                    filterBinding.textFieldPriceMax.editText?.text.toString()
                        .toIntOrNull()
                )
                viewModel.setMinSurface(
                    filterBinding.textFieldSurfaceMin.editText?.text.toString().toFloatOrNull()
                )
                viewModel.setMaxSurface(
                    filterBinding.textFieldSurfaceMax.editText?.text.toString()
                        .toFloatOrNull()
                )
                viewModel.setCityName(filterBinding.textFieldCity.editText?.text.toString())
                viewModel.setNearbyPark(filterBinding.checkNearbyPark.isChecked)
                viewModel.setNearbyStore(filterBinding.checkNearbyStore.isChecked)
                viewModel.setNearbySchool(filterBinding.checkNearbySchool.isChecked)
                viewModel.setNearbyRestaurant(filterBinding.checkNearbyRestaurant.isChecked)
                viewModel.setMinDateSold(dateSold)
                viewModel.setNbPhoto(
                    filterBinding.textFieldPhotoMin.editText?.text.toString().toIntOrNull()
                )
                viewModel.setMinDate(datePublication)
                viewModel.validationUpdateQuery()
                dismiss()
            }
        }
    }

    private fun validate(): Boolean {
        var check = true
        if (!isNumber(
                filterBinding.textFieldPriceMin,
                "This field must only contain numbers"
            )
        ) check = false
        if (!isNumber(
                filterBinding.textFieldSurfaceMin,
                "This field must only contain numbers"
            )
        ) check = false
        if (!isNumber(
                filterBinding.textFieldPriceMin,
                "This field must only contain numbers"
            )
        ) check = false
        if (!isNumber(
                filterBinding.textFieldSurfaceMax,
                "This field must only contain numbers"
            )
        ) check = false
        if (!isNumber(
                filterBinding.textFieldPhotoMin,
                "This field must only contain numbers"
            )
        ) check = false
        return check
    }
}
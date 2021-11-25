package com.example.realestatemanager.ui.simulator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.realestatemanager.R
import com.example.realestatemanager.RealEstateApplication
import com.example.realestatemanager.RealEstateViewModelFactory
import com.example.realestatemanager.databinding.FragmentSimulatorBinding
import com.example.realestatemanager.utils.Constants.CODE_DOLLAR
import com.example.realestatemanager.utils.Constants.CODE_EURO
import com.example.realestatemanager.utils.SimulatorUtils
import com.example.realestatemanager.utils.TextFieldUtils
import com.example.realestatemanager.utils.TextFieldUtils.Companion.hasText
import com.example.realestatemanager.utils.Utils
import com.google.android.material.slider.Slider

class SimulatorFragment : Fragment() {

    lateinit var simulatorBinding: FragmentSimulatorBinding
    var durationYear: Int = 1
    private var currencyCode = 0
    private var monthlyPayment: Double? = null
    private var monthlyPaymentInsurance: Double? = null
    private var totalLoan: Double? = null

    private val viewModelLoan: SimulatorViewModel by activityViewModels() {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            photoRepository = (requireActivity().application as RealEstateApplication).photoRepository,
            (requireActivity().application as RealEstateApplication).geocoderRepository
        )
    }

    companion object {
        fun newInstance() = SimulatorFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        simulatorBinding = FragmentSimulatorBinding.inflate(inflater, container, false)
        onValidation()
        sliderTracking()
        hideShow(false)
        observeCurrency()
        return simulatorBinding.root
    }

    fun onValidation() {
        simulatorBinding.buttonValidation.setOnClickListener {
            if (validation()) {
                hideShow(true)
                val contribution =
                    simulatorBinding.textFieldContribution.editText?.text.toString().toIntOrNull()
                var amount = simulatorBinding.textFieldAmount.editText?.text.toString().toInt()
                if (contribution != null) {
                    amount -= contribution
                }
                val interestRate =
                    simulatorBinding.textFieldInterestRate.editText?.text.toString().toDouble()
                val insuranceRate =
                    simulatorBinding.textFieldInsuranceRate.editText?.text.toString().toDouble()
                monthlyPayment =
                    SimulatorUtils.calculateMonthlyPayment(amount, interestRate, durationYear)
                monthlyPaymentInsurance =
                    SimulatorUtils.calculateMonthlyInsurance(insuranceRate, amount)
                totalLoan = SimulatorUtils.totalLoan(
                    monthlyPayment!!,
                    monthlyPaymentInsurance!!,
                    durationYear
                )
                currencySwitchAndDisplay()
            }
        }
    }

    private fun currencySwitchAndDisplay() {
        when (currencyCode) {
            CODE_DOLLAR -> {
                if (monthlyPayment != null && monthlyPaymentInsurance != null) {
                    simulatorBinding.textviewMonthlyPayment.text =
                        monthlyPayment!!.toInt().toString()
                    simulatorBinding.currency1.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_currency_dollar_black_24dp
                        )
                    )
                    simulatorBinding.textviewTotalLoan.text = totalLoan!!.toInt().toString()
                    simulatorBinding.currency3.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_currency_dollar_black_24dp
                        )
                    )
                }
                if (monthlyPaymentInsurance != null) {
                    simulatorBinding.textviewMonthlyInsurance.text =
                        monthlyPaymentInsurance!!.toInt().toString()
                    simulatorBinding.currency2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_currency_dollar_black_24dp
                        )
                    )
                }
            }
            CODE_EURO -> {
                if (monthlyPayment != null && totalLoan != null) {
                    simulatorBinding.textviewMonthlyPayment.text =
                        Utils.convertDollarToEuro(monthlyPayment!!.toInt()).toString()
                    simulatorBinding.currency1.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_currency_euro_black_24dp
                        )
                    )
                    simulatorBinding.textviewTotalLoan.text =
                        Utils.convertDollarToEuro(totalLoan!!.toInt()).toString()
                    simulatorBinding.currency3.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_currency_euro_black_24dp
                        )
                    )
                }
                if (monthlyPaymentInsurance != null) {
                    simulatorBinding.textviewMonthlyInsurance.text =
                        Utils.convertDollarToEuro(monthlyPaymentInsurance!!.toInt()).toString()
                    simulatorBinding.currency2.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_currency_euro_black_24dp
                        )
                    )
                }
            }
        }
    }

    private fun observeCurrency() {
        viewModelLoan.liveDataCurrencyCode.observe(viewLifecycleOwner, Observer {
            currencyCode = it
            currencySwitchAndDisplay()
        })
    }

    private fun sliderTracking() {
        simulatorBinding.sliderDuration.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
                durationYear = slider.value.toInt()
                simulatorBinding.textviewDuration.text = durationYear.toString()
            }

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped
                durationYear = slider.value.toInt()
                simulatorBinding.textviewDuration.text = durationYear.toString()
            }
        })
    }

    private fun validation(): Boolean {
        var check = true
        if (!hasText(
                simulatorBinding.textFieldAmount,
                "This field must be completed"
            ) || !TextFieldUtils.isNumber(
                simulatorBinding.textFieldAmount,
                "This field must only contain numbers"
            )
        ) check = false
        if (!hasText(
                simulatorBinding.textFieldInsuranceRate,
                "This field must be completed"
            ) || !TextFieldUtils.isNumber(
                simulatorBinding.textFieldInsuranceRate,
                "This field must only contain numbers"
            )
        ) check = false
        if (!hasText(
                simulatorBinding.textFieldInterestRate,
                "This field must be completed"
            ) || !TextFieldUtils.isNumber(
                simulatorBinding.textFieldInterestRate,
                "This field must only contain numbers"
            )
        ) check = false
        if (!TextFieldUtils.isNumber(
                simulatorBinding.textFieldContribution,
                "This field must only contain numbers"
            )
        ) check = false
        return check
    }

    fun hideShow(visibilty: Boolean) {
        if (visibilty) {
            simulatorBinding.cardviewResult.visibility = View.VISIBLE
        } else if (!visibilty) {
            simulatorBinding.cardviewResult.visibility = View.GONE
        }

    }
}
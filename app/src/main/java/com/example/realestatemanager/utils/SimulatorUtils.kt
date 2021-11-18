package com.example.realestatemanager.utils

import kotlin.math.pow

class SimulatorUtils {

    companion object {
        fun calculateMonthlyPayment(
            amount: Int,
            interestRate: Double,
            durationYear: Int
        ): Double {
            val monthlyInterest: Double = (interestRate / 100) / 12
            val durationMonth: Double = (durationYear.toDouble() * 12)
            return (amount * monthlyInterest) / (1 - (1 + monthlyInterest).pow(-durationMonth))
        }

        fun calculateMonthlyInsurance(
            insuranceRate: Double,
            amount: Int,
        ): Double {
            val insuranceRatePourcent = insuranceRate / 100
            return insuranceRatePourcent * amount / 12
        }

        fun totalLoan(
            monthlyPayment: Double,
            monthlyPaymentInsurance: Double,
            durationYear: Int
        ): Double {
            val durationMonth = (durationYear * 12).toDouble()
            return (monthlyPayment + monthlyPaymentInsurance) * durationMonth
        }


    }
}
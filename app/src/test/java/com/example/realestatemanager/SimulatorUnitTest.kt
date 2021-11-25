package com.example.realestatemanager

import com.example.realestatemanager.utils.SimulatorUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.roundToInt

class SimulatorUnitTest {

    @Test
    fun checkTotalLoan() {
        val totalLoan = SimulatorUtils.totalLoan(1060.66, 416.67, 10)
        assertEquals(177280, totalLoan.roundToInt())
    }

    @Test
    fun checkMonthlyPayment() {
        val monthlyPayment = SimulatorUtils.calculateMonthlyPayment(100000, 5.toDouble(), 10)
        assertEquals(1061, monthlyPayment.toFloat().roundToInt())
    }

    @Test
    fun checkMonthlyInsurance() {
        val monthlyPaymentInsurance = SimulatorUtils.calculateMonthlyInsurance(5.toDouble(), 100000)
        assertEquals(417, monthlyPaymentInsurance.roundToInt())
    }
}
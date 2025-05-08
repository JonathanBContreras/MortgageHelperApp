package com.example.mortgagehelperapp

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MortgageViewModelTest {
    private lateinit var viewModel: MortgageViewModel

    @Before
    fun setup() {
        viewModel = MortgageViewModel()
    }

    @Test
    fun `test mortgage calculation with 30 year loan (percentage down)`() {
        val result = viewModel.calculateMortgage(
            homePrice = 300000.0,
            squareFootage = 2000.0,
            downPayment = 20.0, // 20% down
            isDownPaymentPercentage = true,
            interestRate = 4.5,
            loanTermYears = 30,
            hoaFees = 100.0
        )
        assertEquals(1520.0, result.monthlyPayment, 50.0)
        assertEquals(547200.0, result.totalCost, 1000.0)
        assertEquals(273.6, result.costPerSqFt ?: 0.0, 5.0)
        assertEquals(1216.0, result.monthlyBreakdown.principalAndInterest, 50.0)
        assertEquals(300.0, result.monthlyBreakdown.propertyTax, 1.0)
        assertEquals(125.0, result.monthlyBreakdown.homeInsurance, 1.0)
        assertEquals(100.0, result.monthlyBreakdown.hoaFees, 0.0)
    }

    @Test
    fun `test mortgage calculation with 15 year loan (percentage down)`() {
        val result = viewModel.calculateMortgage(
            homePrice = 300000.0,
            squareFootage = 2000.0,
            downPayment = 20.0, // 20% down
            isDownPaymentPercentage = true,
            interestRate = 4.5,
            loanTermYears = 15,
            hoaFees = 100.0
        )
        assertEquals(1830.0, result.monthlyPayment, 50.0)
        assertEquals(329400.0, result.totalCost, 1000.0)
        assertEquals(164.7, result.costPerSqFt ?: 0.0, 5.0)
    }

    @Test
    fun testCalculateMortgage_30Year() {
        val result = viewModel.calculateMortgage(
            homePrice = 300000.0,
            squareFootage = 2000.0,
            downPayment = 60000.0,
            isDownPaymentPercentage = false,
            interestRate = 4.0,
            loanTermYears = 30,
            hoaFees = 100.0
        )
        assertEquals(240000.0, result.totalPrincipal, 1.0)
        assertEquals(171.43, result.monthlyBreakdown.propertyTax, 0.1)
        assertEquals(125.0, result.monthlyBreakdown.homeInsurance, 0.1)
        assertEquals(100.0, result.monthlyBreakdown.hoaFees, 0.1)
        assertEquals(30, result.monthlyBreakdown.loanTermYears)
    }

    @Test
    fun testCalculateMortgage_15Year() {
        val result = viewModel.calculateMortgage(
            homePrice = 300000.0,
            squareFootage = 2000.0,
            downPayment = 60000.0,
            isDownPaymentPercentage = false,
            interestRate = 4.0,
            loanTermYears = 15,
            hoaFees = 100.0
        )
        assertEquals(240000.0, result.totalPrincipal, 1.0)
        assertEquals(15, result.monthlyBreakdown.loanTermYears)
    }

    @Test
    fun testCompareLoans() {
        val comparison = viewModel.compareLoans(
            homePrice = 300000.0,
            squareFootage = 2000.0,
            downPayment = 60000.0,
            isDownPaymentPercentage = false,
            interestRate = 4.0,
            hoaFees = 100.0
        )
        assertEquals(15, comparison.loan15Year.monthlyBreakdown.loanTermYears)
        assertEquals(30, comparison.loan30Year.monthlyBreakdown.loanTermYears)
        assertEquals(240000.0, comparison.loan15Year.totalPrincipal, 1.0)
        assertEquals(240000.0, comparison.loan30Year.totalPrincipal, 1.0)
    }
} 
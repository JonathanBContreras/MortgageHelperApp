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
    fun `test mortgage calculation with 30 year loan`() {
        val result = viewModel.calculateMortgage(
            homePrice = 300000.0,
            squareFootage = 2000.0,
            downPaymentPercent = 20.0,
            interestRate = 4.5,
            loanTermYears = 30,
            hoaFees = 100.0
        )

        // Test monthly payment (approximate)
        assertEquals(1520.0, result.monthlyPayment, 50.0)
        
        // Test total cost (approximate)
        assertEquals(547200.0, result.totalCost, 1000.0)
        
        // Test cost per square foot (approximate)
        assertEquals(273.6, result.costPerSqFt, 5.0)
        
        // Test monthly breakdown
        assertEquals(1216.0, result.monthlyBreakdown.principalAndInterest, 50.0)
        assertEquals(300.0, result.monthlyBreakdown.propertyTax, 1.0)
        assertEquals(125.0, result.monthlyBreakdown.homeInsurance, 1.0)
        assertEquals(100.0, result.monthlyBreakdown.hoaFees, 0.0)
    }

    @Test
    fun `test mortgage calculation with 15 year loan`() {
        val result = viewModel.calculateMortgage(
            homePrice = 300000.0,
            squareFootage = 2000.0,
            downPaymentPercent = 20.0,
            interestRate = 4.5,
            loanTermYears = 15,
            hoaFees = 100.0
        )

        // Test monthly payment (approximate)
        assertEquals(1830.0, result.monthlyPayment, 50.0)
        
        // Test total cost (approximate)
        assertEquals(329400.0, result.totalCost, 1000.0)
        
        // Test cost per square foot (approximate)
        assertEquals(164.7, result.costPerSqFt, 5.0)
    }
} 
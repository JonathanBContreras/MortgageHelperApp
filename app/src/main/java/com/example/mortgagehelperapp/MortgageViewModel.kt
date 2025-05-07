package com.example.mortgagehelperapp

import androidx.lifecycle.ViewModel
import kotlin.math.pow

class MortgageViewModel : ViewModel() {
    
    fun calculateMortgage(
        homePrice: Double,
        squareFootage: Double,
        downPaymentPercent: Double,
        interestRate: Double,
        loanTermYears: Int,
        hoaFees: Double
    ): MortgageCalculation {
        // Convert percentage to decimal
        val downPaymentDecimal = downPaymentPercent / 100
        val interestRateDecimal = interestRate / 100
        
        // Calculate loan amount
        val loanAmount = homePrice * (1 - downPaymentDecimal)
        
        // Calculate monthly interest rate
        val monthlyInterestRate = interestRateDecimal / 12
        
        // Calculate number of payments
        val numberOfPayments = loanTermYears * 12
        
        // Calculate monthly principal and interest payment
        val monthlyPayment = loanAmount * 
            (monthlyInterestRate * (1 + monthlyInterestRate).pow(numberOfPayments)) /
            ((1 + monthlyInterestRate).pow(numberOfPayments) - 1)
        
        // Calculate property tax (assuming 1.2% of home value annually)
        val annualPropertyTax = homePrice * 0.012
        val monthlyPropertyTax = annualPropertyTax / 12
        
        // Calculate home insurance (assuming 0.5% of home value annually)
        val annualHomeInsurance = homePrice * 0.005
        val monthlyHomeInsurance = annualHomeInsurance / 12
        
        // Calculate total monthly payment
        val totalMonthlyPayment = monthlyPayment + monthlyPropertyTax + monthlyHomeInsurance + hoaFees
        
        // Calculate total cost over loan period
        val totalCost = totalMonthlyPayment * numberOfPayments + (homePrice * downPaymentDecimal)
        
        // Calculate cost per square foot
        val costPerSqFt = totalCost / squareFootage
        
        // Create monthly breakdown
        val monthlyBreakdown = MonthlyBreakdown(
            principalAndInterest = monthlyPayment,
            propertyTax = monthlyPropertyTax,
            homeInsurance = monthlyHomeInsurance,
            hoaFees = hoaFees
        )
        
        return MortgageCalculation(
            monthlyPayment = totalMonthlyPayment,
            totalCost = totalCost,
            costPerSqFt = costPerSqFt,
            monthlyBreakdown = monthlyBreakdown
        )
    }
} 
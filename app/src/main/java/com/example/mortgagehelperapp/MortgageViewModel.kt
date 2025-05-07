package com.example.mortgagehelperapp

import androidx.lifecycle.ViewModel
import kotlin.math.pow

class MortgageViewModel : ViewModel() {
    
    fun calculateMortgage(
        homePrice: Double,
        squareFootage: Double?,
        downPayment: Double,
        isDownPaymentPercentage: Boolean,
        interestRate: Double,
        loanTermYears: Int,
        hoaFees: Double?
    ): MortgageCalculation {
        // Convert percentage to decimal
        val downPaymentDecimal = if (isDownPaymentPercentage) downPayment / 100 else downPayment / homePrice
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
        
        // Calculate total interest and principal
        val totalPayment = monthlyPayment * numberOfPayments
        val totalInterest = totalPayment - loanAmount
        val totalPrincipal = loanAmount
        
        // Calculate property tax (assuming 1.2% of home value annually)
        val annualPropertyTax = homePrice * 0.012
        val monthlyPropertyTax = annualPropertyTax / 12
        
        // Calculate home insurance (assuming 0.5% of home value annually)
        val annualHomeInsurance = homePrice * 0.005
        val monthlyHomeInsurance = annualHomeInsurance / 12
        
        // Calculate total monthly payment
        val totalMonthlyPayment = monthlyPayment + monthlyPropertyTax + monthlyHomeInsurance + (hoaFees ?: 0.0)
        
        // Calculate total cost over loan period
        val totalCost = totalMonthlyPayment * numberOfPayments + (homePrice * downPaymentDecimal)
        
        // Calculate cost per square foot if square footage is provided
        val costPerSqFt = if (squareFootage != null && squareFootage > 0) totalCost / squareFootage else 0.0
        
        // Create monthly breakdown
        val monthlyBreakdown = MonthlyBreakdown(
            principalAndInterest = monthlyPayment,
            propertyTax = monthlyPropertyTax,
            homeInsurance = monthlyHomeInsurance,
            hoaFees = hoaFees ?: 0.0
        )
        
        return MortgageCalculation(
            monthlyPayment = totalMonthlyPayment,
            totalCost = totalCost,
            costPerSqFt = costPerSqFt,
            monthlyBreakdown = monthlyBreakdown,
            totalPrincipal = totalPrincipal,
            totalInterest = totalInterest
        )
    }

    fun compareLoans(
        homePrice: Double,
        squareFootage: Double?,
        downPayment: Double,
        isDownPaymentPercentage: Boolean,
        interestRate: Double,
        hoaFees: Double?
    ): LoanComparison {
        val loan15Year = calculateMortgage(
            homePrice = homePrice,
            squareFootage = squareFootage,
            downPayment = downPayment,
            isDownPaymentPercentage = isDownPaymentPercentage,
            interestRate = interestRate,
            loanTermYears = 15,
            hoaFees = hoaFees
        )

        val loan30Year = calculateMortgage(
            homePrice = homePrice,
            squareFootage = squareFootage,
            downPayment = downPayment,
            isDownPaymentPercentage = isDownPaymentPercentage,
            interestRate = interestRate,
            loanTermYears = 30,
            hoaFees = hoaFees
        )

        return LoanComparison(loan15Year, loan30Year)
    }
} 
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
        // Calculate loan amount
        val actualDownPayment = if (isDownPaymentPercentage) {
            homePrice * (downPayment / 100)
        } else {
            downPayment
        }
        val loanAmount = homePrice - actualDownPayment

        // Calculate monthly interest rate and number of payments
        val monthlyRate = interestRate / 100 / 12
        val numberOfPayments = loanTermYears * 12

        // Calculate monthly principal and interest payment
        val monthlyPrincipalAndInterest = loanAmount * (monthlyRate * Math.pow(1 + monthlyRate, numberOfPayments.toDouble())) /
                (Math.pow(1 + monthlyRate, numberOfPayments.toDouble()) - 1)

        // Calculate property tax (1% of home price annually)
        val annualPropertyTax = homePrice * 0.01
        val monthlyPropertyTax = annualPropertyTax / 12

        // Calculate home insurance (0.5% of home price annually)
        val annualHomeInsurance = homePrice * 0.005
        val monthlyHomeInsurance = annualHomeInsurance / 12

        // Calculate total monthly payment
        val monthlyPayment = monthlyPrincipalAndInterest + monthlyPropertyTax + monthlyHomeInsurance + (hoaFees ?: 0.0)

        // Calculate total cost
        val totalCost = monthlyPayment * numberOfPayments

        // Calculate total principal and interest
        val totalPrincipal = loanAmount
        val totalInterest = (monthlyPrincipalAndInterest * numberOfPayments) - loanAmount

        // Calculate cost per square foot if square footage is provided
        val costPerSqFt = squareFootage?.let { homePrice / it }

        // Create monthly breakdown
        val monthlyBreakdown = MonthlyBreakdown(
            principalAndInterest = monthlyPrincipalAndInterest,
            propertyTax = monthlyPropertyTax,
            homeInsurance = monthlyHomeInsurance,
            hoaFees = hoaFees ?: 0.0,
            interestRate = interestRate,
            loanTermYears = loanTermYears,
            loanAmount = loanAmount
        )

        return MortgageCalculation(
            monthlyPayment = monthlyPayment,
            totalCost = totalCost,
            totalPrincipal = totalPrincipal,
            totalInterest = totalInterest,
            costPerSqFt = costPerSqFt,
            monthlyBreakdown = monthlyBreakdown
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
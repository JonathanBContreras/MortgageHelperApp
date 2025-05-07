package com.example.mortgagehelperapp

data class MortgageCalculation(
    val monthlyPayment: Double,
    val totalCost: Double,
    val costPerSqFt: Double,
    val monthlyBreakdown: MonthlyBreakdown
)

data class MonthlyBreakdown(
    val principalAndInterest: Double,
    val propertyTax: Double,
    val homeInsurance: Double,
    val hoaFees: Double
) 
package com.example.mortgagehelperapp

data class MortgageCalculation(
    val monthlyPayment: Double,
    val totalCost: Double,
    val costPerSqFt: Double,
    val monthlyBreakdown: MonthlyBreakdown,
    val totalPrincipal: Double,
    val totalInterest: Double
)

data class MonthlyBreakdown(
    val principalAndInterest: Double,
    val propertyTax: Double,
    val homeInsurance: Double,
    val hoaFees: Double
)

data class LoanComparison(
    val loan15Year: MortgageCalculation,
    val loan30Year: MortgageCalculation
) 
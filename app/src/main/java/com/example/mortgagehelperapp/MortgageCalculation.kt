package com.example.mortgagehelperapp

data class MortgageCalculation(
    val monthlyPayment: Double,
    val totalCost: Double,
    val totalPrincipal: Double,
    val totalInterest: Double,
    val costPerSqFt: Double?,
    val monthlyBreakdown: MonthlyBreakdown
)

data class MonthlyBreakdown(
    val principalAndInterest: Double,
    val propertyTax: Double,
    val homeInsurance: Double,
    val hoaFees: Double,
    val interestRate: Double,
    val loanTermYears: Int,
    val loanAmount: Double
)

data class LoanComparison(
    val loan15Year: MortgageCalculation,
    val loan30Year: MortgageCalculation
) 
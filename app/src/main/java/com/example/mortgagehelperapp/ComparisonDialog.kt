package com.example.mortgagehelperapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

class ComparisonDialog(
    context: Context,
    private val comparison: LoanComparison
) : Dialog(context) {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loan_comparison)

        val loan15Year = comparison.loan15Year
        val loan30Year = comparison.loan30Year

        // 15 Year Loan Details
        findViewById<TextView>(R.id.loan15MonthlyPayment).text = 
            "Monthly Payment: ${currencyFormat.format(loan15Year.monthlyPayment)}"
        findViewById<TextView>(R.id.loan15TotalCost).text = 
            "Total Cost: ${currencyFormat.format(loan15Year.totalCost)}"
        findViewById<TextView>(R.id.loan15PrincipalInterest).text = """
            Principal: ${currencyFormat.format(loan15Year.totalPrincipal)}
            Interest: ${currencyFormat.format(loan15Year.totalInterest)}
        """.trimIndent()

        // 30 Year Loan Details
        findViewById<TextView>(R.id.loan30MonthlyPayment).text = 
            "Monthly Payment: ${currencyFormat.format(loan30Year.monthlyPayment)}"
        findViewById<TextView>(R.id.loan30TotalCost).text = 
            "Total Cost: ${currencyFormat.format(loan30Year.totalCost)}"
        findViewById<TextView>(R.id.loan30PrincipalInterest).text = """
            Principal: ${currencyFormat.format(loan30Year.totalPrincipal)}
            Interest: ${currencyFormat.format(loan30Year.totalInterest)}
        """.trimIndent()

        // Calculate and display savings
        val monthlySavings = loan30Year.monthlyPayment - loan15Year.monthlyPayment
        val totalSavings = loan30Year.totalCost - loan15Year.totalCost

        findViewById<TextView>(R.id.savingsDetails).text = """
            Monthly Payment Difference: ${currencyFormat.format(monthlySavings)}
            Total Cost Difference: ${currencyFormat.format(totalSavings)}
        """.trimIndent()

        // Close button
        findViewById<MaterialButton>(R.id.closeButton).setOnClickListener {
            dismiss()
        }
    }
} 
package com.example.mortgagehelperapp

import android.os.Bundle
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MortgageViewModel
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val decimalFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MortgageViewModel::class.java]

        val homePriceInput = findViewById<TextInputEditText>(R.id.homePriceInput)
        val squareFootageInput = findViewById<TextInputEditText>(R.id.squareFootageInput)
        val downPaymentPercentInput = findViewById<TextInputEditText>(R.id.downPaymentPercentInput)
        val interestRateInput = findViewById<TextInputEditText>(R.id.interestRateInput)
        val hoaFeesInput = findViewById<TextInputEditText>(R.id.hoaFeesInput)
        val loanTerm15 = findViewById<RadioButton>(R.id.loanTerm15)
        val calculateButton = findViewById<MaterialButton>(R.id.calculateButton)

        val monthlyPaymentResult = findViewById<TextView>(R.id.monthlyPaymentResult)
        val totalCostResult = findViewById<TextView>(R.id.totalCostResult)
        val costPerSqFtResult = findViewById<TextView>(R.id.costPerSqFtResult)
        val breakdownResult = findViewById<TextView>(R.id.breakdownResult)

        calculateButton.setOnClickListener {
            try {
                val homePrice = homePriceInput.text.toString().toDoubleOrNull() ?: 0.0
                val squareFootage = squareFootageInput.text.toString().toDoubleOrNull() ?: 0.0
                val downPaymentPercent = downPaymentPercentInput.text.toString().toDoubleOrNull() ?: 0.0
                val interestRate = interestRateInput.text.toString().toDoubleOrNull() ?: 0.0
                val hoaFees = hoaFeesInput.text.toString().toDoubleOrNull() ?: 0.0
                val loanTermYears = if (loanTerm15.isChecked) 15 else 30

                if (homePrice <= 0 || squareFootage <= 0 || downPaymentPercent < 0 || 
                    interestRate <= 0 || hoaFees < 0) {
                    Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val calculation = viewModel.calculateMortgage(
                    homePrice,
                    squareFootage,
                    downPaymentPercent,
                    interestRate,
                    loanTermYears,
                    hoaFees
                )

                // Display results
                monthlyPaymentResult.text = "Monthly Payment: ${currencyFormat.format(calculation.monthlyPayment)}"
                totalCostResult.text = "Total Cost: ${currencyFormat.format(calculation.totalCost)}"
                costPerSqFtResult.text = "Cost per Square Foot: ${currencyFormat.format(calculation.costPerSqFt)}"

                val breakdown = calculation.monthlyBreakdown
                breakdownResult.text = """
                    Monthly Breakdown:
                    Principal & Interest: ${currencyFormat.format(breakdown.principalAndInterest)}
                    Property Tax: ${currencyFormat.format(breakdown.propertyTax)}
                    Home Insurance: ${currencyFormat.format(breakdown.homeInsurance)}
                    HOA Fees: ${currencyFormat.format(breakdown.hoaFees)}
                """.trimIndent()

            } catch (e: Exception) {
                Toast.makeText(this, "Error calculating mortgage: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 
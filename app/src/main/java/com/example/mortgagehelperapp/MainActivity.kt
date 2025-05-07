package com.example.mortgagehelperapp

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mortgagehelperapp.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel = MortgageViewModel()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNumberFormatting()
        setupChart()
        setupListeners()
    }

    private fun setupNumberFormatting() {
        val textWatcher = object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val input = s.toString().replace(Regex("[^\\d.]"), "")
                if (input.isNotEmpty()) {
                    val number = input.toDoubleOrNull()
                    if (number != null) {
                        val formatted = numberFormat.format(number)
                        s?.replace(0, s.length, formatted)
                    }
                }

                isUpdating = false
            }
        }

        binding.homePriceInput.addTextChangedListener(textWatcher)
        binding.squareFootageInput.addTextChangedListener(textWatcher)
        binding.downPaymentInput.addTextChangedListener(textWatcher)
        binding.interestRateInput.addTextChangedListener(textWatcher)
        binding.hoaFeesInput.addTextChangedListener(textWatcher)
    }

    private fun setupChart() {
        binding.paymentBreakdownChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleRadius(61f)
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
        }
    }

    private fun setupListeners() {
        binding.calculateButton.setOnClickListener {
            try {
                val homePrice = binding.homePriceInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val squareFootage = binding.squareFootageInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val downPayment = binding.downPaymentInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val isDownPaymentPercentage = binding.downPaymentPercent.isChecked
                val interestRate = binding.interestRateInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val hoaFees = binding.hoaFeesInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()

                if (homePrice == null || downPayment == null || interestRate == null) {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val result = viewModel.calculateMortgage(
                    homePrice = homePrice,
                    squareFootage = squareFootage,
                    downPayment = downPayment,
                    isDownPaymentPercentage = isDownPaymentPercentage,
                    interestRate = interestRate,
                    loanTermYears = 30,
                    hoaFees = hoaFees
                )
                displayResults(result)
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.compareButton.setOnClickListener {
            try {
                val homePrice = binding.homePriceInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val squareFootage = binding.squareFootageInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val downPayment = binding.downPaymentInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val isDownPaymentPercentage = binding.downPaymentPercent.isChecked
                val interestRate = binding.interestRateInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val hoaFees = binding.hoaFeesInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()

                if (homePrice == null || downPayment == null || interestRate == null) {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val comparison = viewModel.compareLoans(
                    homePrice = homePrice,
                    squareFootage = squareFootage,
                    downPayment = downPayment,
                    isDownPaymentPercentage = isDownPaymentPercentage,
                    interestRate = interestRate,
                    hoaFees = hoaFees
                )
                displayComparison(comparison)
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayResults(result: MortgageCalculation) {
        binding.monthlyPaymentResult.text = "Monthly Payment: ${currencyFormat.format(result.monthlyPayment)}"
        binding.totalCostResult.text = "Total Cost: ${currencyFormat.format(result.totalCost)}"
        binding.principalInterestBreakdown.text = "Principal: ${currencyFormat.format(result.totalPrincipal)}\nInterest: ${currencyFormat.format(result.totalInterest)}"

        if (result.costPerSqFt != null) {
            binding.costPerSqFtResult.text = "Cost per Square Foot: ${currencyFormat.format(result.costPerSqFt)}"
        } else {
            binding.costPerSqFtResult.text = ""
        }

        binding.breakdownResult.text = """
            Monthly Breakdown:
            Principal & Interest: ${currencyFormat.format(result.monthlyBreakdown.principalAndInterest)}
            Property Tax: ${currencyFormat.format(result.monthlyBreakdown.propertyTax)}
            Home Insurance: ${currencyFormat.format(result.monthlyBreakdown.homeInsurance)}
            HOA Fees: ${currencyFormat.format(result.monthlyBreakdown.hoaFees)}
        """.trimIndent()

        updateChart(result.monthlyBreakdown)
    }

    private fun displayComparison(comparison: LoanComparison) {
        ComparisonDialog(this, comparison).show()
    }

    private fun updateChart(breakdown: MonthlyBreakdown) {
        val entries = listOf(
            PieEntry(breakdown.principalAndInterest.toFloat(), "Principal & Interest"),
            PieEntry(breakdown.propertyTax.toFloat(), "Property Tax"),
            PieEntry(breakdown.homeInsurance.toFloat(), "Home Insurance"),
            PieEntry(breakdown.hoaFees.toFloat(), "HOA Fees")
        )

        val dataSet = PieDataSet(entries, "Monthly Payment Breakdown").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        binding.paymentBreakdownChart.data = PieData(dataSet)
        binding.paymentBreakdownChart.invalidate()
    }
} 
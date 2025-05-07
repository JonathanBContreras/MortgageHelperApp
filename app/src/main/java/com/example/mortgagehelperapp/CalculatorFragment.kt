package com.example.mortgagehelperapp

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mortgagehelperapp.databinding.FragmentCalculatorBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.NumberFormat
import java.util.Locale

class CalculatorFragment : Fragment() {
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private val viewModel = MortgageViewModel()
    private val sharedViewModel: SharedMortgageViewModel by activityViewModels()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                        val formatted = if (input.contains(".")) {
                            String.format("%.2f", number)
                        } else {
                            numberFormat.format(number)
                        }
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
            legend.apply {
                isEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                textSize = 12f
                formSize = 12f
                formToTextSpace = 5f
                xEntrySpace = 10f
            }
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
                val loanTermYears = if (binding.loanTerm15.isChecked) 15 else 30

                if (homePrice == null || downPayment == null || interestRate == null) {
                    Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val result = viewModel.calculateMortgage(
                    homePrice = homePrice,
                    squareFootage = squareFootage,
                    downPayment = downPayment,
                    isDownPaymentPercentage = isDownPaymentPercentage,
                    interestRate = interestRate,
                    loanTermYears = loanTermYears,
                    hoaFees = hoaFees
                )
                sharedViewModel.setCalculation(result)
                // Also update comparison automatically
                val comparison = viewModel.compareLoans(
                    homePrice = homePrice,
                    squareFootage = squareFootage,
                    downPayment = downPayment,
                    isDownPaymentPercentage = isDownPaymentPercentage,
                    interestRate = interestRate,
                    hoaFees = hoaFees
                )
                sharedViewModel.setComparison(comparison)
                displayResults(result)
            } catch (e: Exception) {
                val msg = e.message ?: "An unexpected error occurred."
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
        binding.compareButton?.setOnClickListener {
            try {
                val homePrice = binding.homePriceInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val squareFootage = binding.squareFootageInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val downPayment = binding.downPaymentInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val isDownPaymentPercentage = binding.downPaymentPercent.isChecked
                val interestRate = binding.interestRateInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()
                val hoaFees = binding.hoaFeesInput.text.toString().replace(Regex("[^\\d.]"), "").toDoubleOrNull()

                if (homePrice == null || downPayment == null || interestRate == null) {
                    Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
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
                sharedViewModel.setComparison(comparison)
                Toast.makeText(context, "Comparison data updated! Switch to the Comparison tab.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
        
        // Update amortization chart
        (activity as? MainActivity)?.updateAmortizationChart(result)
    }

    private fun updateChart(breakdown: MonthlyBreakdown) {
        val entries = mutableListOf<PieEntry>()
        val labels = mutableListOf<String>()

        // Calculate principal and interest split
        val monthlyRate = breakdown.interestRate / 100 / 12
        val interest = breakdown.loanAmount * monthlyRate
        val principal = breakdown.principalAndInterest - interest

        if (principal > 0) {
            entries.add(PieEntry(principal.toFloat(), getString(R.string.principal)))
            labels.add(getString(R.string.principal))
        }
        if (interest > 0) {
            entries.add(PieEntry(interest.toFloat(), getString(R.string.interest)))
            labels.add(getString(R.string.interest))
        }
        if (breakdown.propertyTax > 0) {
            entries.add(PieEntry(breakdown.propertyTax.toFloat(), getString(R.string.property_tax)))
            labels.add(getString(R.string.property_tax))
        }
        if (breakdown.homeInsurance > 0) {
            entries.add(PieEntry(breakdown.homeInsurance.toFloat(), getString(R.string.home_insurance)))
            labels.add(getString(R.string.home_insurance))
        }
        if (breakdown.hoaFees > 0) {
            entries.add(PieEntry(breakdown.hoaFees.toFloat(), getString(R.string.hoa_fees)))
            labels.add(getString(R.string.hoa_fees))
        }

        if (entries.isNotEmpty()) {
            val dataSet = PieDataSet(entries, getString(R.string.monthly_payment_breakdown)).apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextColor = Color.BLACK
                valueTextSize = 12f
                valueFormatter = DefaultValueFormatter(0)
            }

            binding.paymentBreakdownChart.apply {
                data = PieData(dataSet)
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.isWordWrapEnabled = true
                legend.setCustom(labels.mapIndexed { index, label ->
                    LegendEntry(label, Legend.LegendForm.CIRCLE, 12f, 12f, null, ColorTemplate.MATERIAL_COLORS[index % ColorTemplate.MATERIAL_COLORS.size])
                })
                invalidate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
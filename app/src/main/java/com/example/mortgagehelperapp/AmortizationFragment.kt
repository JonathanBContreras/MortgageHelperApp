package com.example.mortgagehelperapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mortgagehelperapp.databinding.FragmentAmortizationBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.NumberFormat
import java.util.Locale

class AmortizationFragment : Fragment() {
    private var _binding: FragmentAmortizationBinding? = null
    private val binding get() = _binding!!
    private val viewModel = MortgageViewModel()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAmortizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
    }

    private fun setupChart() {
        binding.amortizationChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = -45f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
            }

            axisRight.isEnabled = false

            legend.apply {
                isEnabled = true
                textSize = 12f
                formSize = 12f
                formToTextSpace = 5f
                xEntrySpace = 10f
            }
        }
    }

    fun updateAmortizationSchedule(calculation: MortgageCalculation) {
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()
        var totalPrincipal = 0.0
        var totalInterest = 0.0

        // Calculate amortization schedule for each year
        val monthlyRate = calculation.monthlyBreakdown.interestRate / 100 / 12
        val numberOfPayments = calculation.monthlyBreakdown.loanTermYears * 12
        var remainingBalance = calculation.monthlyBreakdown.loanAmount

        for (year in 1..calculation.monthlyBreakdown.loanTermYears) {
            var yearPrincipal = 0.0
            var yearInterest = 0.0

            // Calculate monthly payments for this year
            for (month in 1..12) {
                val payment = calculation.monthlyBreakdown.principalAndInterest
                val interest = remainingBalance * monthlyRate
                val principal = payment - interest

                yearPrincipal += principal
                yearInterest += interest
                remainingBalance -= principal
            }

            totalPrincipal += yearPrincipal
            totalInterest += yearInterest

            entries.add(Entry(year.toFloat(), yearPrincipal.toFloat()))
            labels.add("Year $year")
        }

        // Create principal dataset
        val principalDataSet = LineDataSet(entries, getString(R.string.principal)).apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(false)
        }

        // Create interest dataset
        val interestEntries = entries.mapIndexed { index, entry ->
            Entry(entry.x, (calculation.monthlyBreakdown.principalAndInterest * 12 - entry.y).toFloat())
        }
        val interestDataSet = LineDataSet(interestEntries, getString(R.string.interest)).apply {
            color = Color.RED
            setCircleColor(Color.RED)
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(false)
        }

        // Update chart
        binding.amortizationChart.apply {
            data = LineData(principalDataSet, interestDataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            invalidate()
        }

        // Update summary text
        binding.amortizationSummary.text = """
            Total Principal: ${currencyFormat.format(totalPrincipal)}
            Total Interest: ${currencyFormat.format(totalInterest)}
            Total Cost: ${currencyFormat.format(totalPrincipal + totalInterest)}
        """.trimIndent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
package com.example.mortgagehelperapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.mortgagehelperapp.databinding.FragmentAmortizationBinding
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.NumberFormat
import java.util.*

class AmortizationFragment : Fragment() {
    private var _binding: FragmentAmortizationBinding? = null
    private val binding get() = _binding!!
    private val viewModel = MortgageViewModel()
    private val sharedViewModel: SharedMortgageViewModel by activityViewModels()
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
        sharedViewModel.calculation.observe(viewLifecycleOwner, Observer { calc ->
            if (calc != null) {
                updateAmortizationSchedule(calc)
            }
        })
    }

    private fun setupChart() {
        binding.amortizationChart.apply {
            if (this is CombinedChart) {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                isHighlightFullBarEnabled = false
                setDrawOrder(arrayOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE))
                axisRight.isEnabled = true
                axisLeft.axisMinimum = 0f
                axisRight.axisMinimum = 0f
                legend.apply {
                    isEnabled = true
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                    textSize = 12f
                }
            }
        }
    }

    fun updateAmortizationSchedule(calculation: MortgageCalculation) {
        val years = calculation.monthlyBreakdown.loanTermYears
        val xLabels = (1..years).map { (Calendar.getInstance().get(Calendar.YEAR) + it - 1).toString() }

        val principalEntries = mutableListOf<BarEntry>()
        val balanceEntries = mutableListOf<Entry>()
        var totalPrincipal = 0.0
        var totalInterest = 0.0
        var totalTaxes = 0.0
        var remainingBalance = calculation.monthlyBreakdown.loanAmount
        val monthlyRate = calculation.monthlyBreakdown.interestRate / 100 / 12
        val payment = calculation.monthlyBreakdown.principalAndInterest
        val monthlyTax = calculation.monthlyBreakdown.propertyTax
        val monthlyFees = calculation.monthlyBreakdown.hoaFees + calculation.monthlyBreakdown.homeInsurance

        for (year in 1..years) {
            var yearPrincipal = 0.0
            var yearInterest = 0.0
            var yearTaxes = 0.0
            for (month in 1..12) {
                val interest = remainingBalance * monthlyRate
                val principal = payment - interest
                yearPrincipal += principal
                yearInterest += interest
                yearTaxes += monthlyTax + monthlyFees
                remainingBalance -= principal
            }
            totalPrincipal += yearPrincipal
            totalInterest += yearInterest
            totalTaxes += yearTaxes
            principalEntries.add(BarEntry(year.toFloat(), floatArrayOf(yearPrincipal.toFloat(), yearInterest.toFloat(), yearTaxes.toFloat())))
            balanceEntries.add(Entry(year.toFloat(), remainingBalance.toFloat()))
        }

        val barDataSet = BarDataSet(principalEntries, "").apply {
            setDrawIcons(false)
            colors = listOf(Color.rgb(33, 150, 243), Color.rgb(13, 71, 161), Color.rgb(144, 202, 249))
            stackLabels = arrayOf("Principal", "Interest", "Taxes & Fees")
            setDrawValues(false)
        }
        val barData = BarData(barDataSet)
        barData.barWidth = 0.8f

        val lineDataSet = LineDataSet(balanceEntries, "Balance").apply {
            color = Color.BLACK
            lineWidth = 2f
            setDrawCircles(true)
            setCircleColor(Color.BLACK)
            axisDependency = YAxis.AxisDependency.RIGHT
            setDrawValues(false)
        }
        val lineData = LineData(lineDataSet)

        val combinedData = CombinedData()
        combinedData.setData(barData)
        combinedData.setData(lineData)

        (binding.amortizationChart as? CombinedChart)?.apply {
            data = combinedData
            xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
            xAxis.granularity = 1f
            xAxis.labelRotationAngle = -45f
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f
            invalidate()
        }

        binding.amortizationSummary.text = "Total Principal: ${currencyFormat.format(totalPrincipal)}\nTotal Interest: ${currencyFormat.format(totalInterest)}\nTotal Taxes & Fees: ${currencyFormat.format(totalTaxes)}\nTotal Cost: ${currencyFormat.format(totalPrincipal + totalInterest + totalTaxes)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
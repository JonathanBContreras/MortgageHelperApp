package com.example.mortgagehelperapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedMortgageViewModel : ViewModel() {
    private val _calculation = MutableLiveData<MortgageCalculation?>()
    val calculation: LiveData<MortgageCalculation?> = _calculation

    private val _comparison = MutableLiveData<LoanComparison?>()
    val comparison: LiveData<LoanComparison?> = _comparison

    fun setCalculation(calc: MortgageCalculation) {
        _calculation.value = calc
    }

    fun setComparison(comp: LoanComparison) {
        _comparison.value = comp
    }

    fun clear() {
        _calculation.value = null
        _comparison.value = null
    }
} 
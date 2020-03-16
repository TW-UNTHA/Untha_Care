package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.untha.utils.Constants

class CalculatorFiniquitoInputThreeViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun answerSelectedCalculatorRoute(): List<String> {
        val results = sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_CALCULATOR_HINT, "")
        return results?.split(" ") ?: listOf()
    }
}

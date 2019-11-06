package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.untha.utils.Constants

open class BaseViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun saveAnswerOption(fault: String) {
        val faultAnswers = loadAnswerOptionFromSharedPreferences() ?: ""
        sharedPreferences.edit()
            .putString(
                Constants.FAULT_ANSWER,
                "$faultAnswers $fault"

            ).apply()
    }

    private fun loadAnswerOptionFromSharedPreferences(): String? {
        return sharedPreferences.getString(Constants.FAULT_ANSWER, "")
    }

}


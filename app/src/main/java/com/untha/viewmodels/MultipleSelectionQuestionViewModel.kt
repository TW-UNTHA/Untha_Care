package com.untha.viewmodels

import android.content.SharedPreferences

class MultipleSelectionQuestionViewModel(
    sharedPreferences: SharedPreferences
) : BaseQuestionViewModel(sharedPreferences) {

    fun getFaultForQuestion(isNoneOfAbove: Boolean) {
        return if (isNoneOfAbove) {
            saveAnswerOption(question?.options?.last()?.result ?: "")
        } else {
            saveAnswerOption(question?.options?.first()?.result ?: "")
        }
    }

    fun getHintForSelectedOption(isNoneOfAbove: Boolean): String? {
        return if (isNoneOfAbove) {
            question?.options?.last()?.hint
        } else {
            question?.options?.first()?.hint
        }
    }
}


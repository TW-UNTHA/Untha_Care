package com.untha.viewmodels

import android.content.SharedPreferences
import com.untha.utils.Constants
import com.untha.utils.MultipleSelectionOption

class MultipleSelectionQuestionViewModel(
    sharedPreferences: SharedPreferences
) : BaseQuestionViewModel(sharedPreferences) {

    fun getFaultForQuestion(
        isNoneOfAbove: Boolean, typeRoute: String,
        options: MutableList<MultipleSelectionOption>
    ) {
        when (typeRoute) {
            Constants.ROUTE_LABOUR ->
                processLabourRouteAnswer(isNoneOfAbove)
            Constants.ROUTE_VIOLENCE ->
                processViolenceRouteAnswer(options)
            Constants.ROUTE_CALCULATOR ->
                processCalculatorRouteAnswer(isNoneOfAbove)
        }
    }

    private fun processCalculatorRouteAnswer(isNoneOfAbove: Boolean) {

        return if (isNoneOfAbove) {
            saveAnswerOption(
                question?.options?.last()?.result ?: "",
                Constants.FAULT_ANSWER_ROUTE_CALCULATOR
            )
            saveAnswerOption(
                question?.options?.last()?.hint ?: "",
                Constants.FAULT_ANSWER_ROUTE_CALCULATOR_HINT
            )
        } else {
            saveAnswerOption(
                question?.options?.first()?.result ?: "",
                Constants.FAULT_ANSWER_ROUTE_CALCULATOR
            )
            saveAnswerOption(
                question?.options?.first()?.hint ?: "",
                Constants.FAULT_ANSWER_ROUTE_CALCULATOR_HINT
            )
        }
    }

    private fun processLabourRouteAnswer(isNoneOfAbove: Boolean) {
        return if (isNoneOfAbove) {
            saveAnswerOption(
                question?.options?.last()?.result ?: "",
                Constants.FAULT_ANSWER_ROUTE_LABOUR
            )
        } else {
            saveAnswerOption(
                question?.options?.first()?.result ?: "",
                Constants.FAULT_ANSWER_ROUTE_LABOUR
            )
        }

    }

    private fun processViolenceRouteAnswer(options: MutableList<MultipleSelectionOption>) {
        if (options.filter { it.isSelected && it.code.equals(Constants.VIOLENCE_ROUTE_LOW) }.any()) {
            saveAnswerOption(Constants.VIOLENCE_ROUTE_LOW, Constants.FAULT_ANSWER_ROUTE_VIOLENCE)
        }
        if (options.filter { it.isSelected && it.code.equals(Constants.VIOLENCE_ROUTE_MEDIUM) }.any()) {
            saveAnswerOption(Constants.VIOLENCE_ROUTE_MEDIUM, Constants.FAULT_ANSWER_ROUTE_VIOLENCE)
        }
        if (options.filter { it.isSelected && it.code.equals(Constants.VIOLENCE_ROUTE_HIGHT) }.any()) {
            saveAnswerOption(Constants.VIOLENCE_ROUTE_HIGHT, Constants.FAULT_ANSWER_ROUTE_VIOLENCE)
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


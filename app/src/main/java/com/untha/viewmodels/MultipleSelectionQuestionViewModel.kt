package com.untha.viewmodels

import android.content.SharedPreferences
import com.untha.utils.Constants
import com.untha.utils.MultipleSelectionOption

class MultipleSelectionQuestionViewModel(
    sharedPreferences: SharedPreferences
) : BaseQuestionViewModel(sharedPreferences) {

    fun getFaultForQuestion(
        isNoneOfAbove: Boolean, isLabourRoute: Boolean,
        options: MutableList<MultipleSelectionOption>
    ) {
        if (isLabourRoute) {
            processLabourRouteAnswer(isNoneOfAbove)
        } else {
            processLabourRouteAnswer(options)
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

    private fun processLabourRouteAnswer(options: MutableList<MultipleSelectionOption>) {
        //TODO refactizar
           if( options.filter{ it.isSelected  && it.code.equals("BAJO")}.any() ){
               saveAnswerOption( "BAJO" , Constants.FAULT_ANSWER_ROUTE_VIOLENCE)
            }
            if( options.filter{ it.isSelected  && it.code.equals("MEDIO")}.any() ){
            saveAnswerOption( "MEDIO" , Constants.FAULT_ANSWER_ROUTE_VIOLENCE)
            }
            if( options.filter{ it.isSelected  && it.code.equals("ALTO")}.any() ){
                saveAnswerOption( "ALTO" , Constants.FAULT_ANSWER_ROUTE_VIOLENCE)
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


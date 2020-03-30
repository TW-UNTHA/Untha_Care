package com.untha.utils

import android.content.SharedPreferences
import com.untha.model.transactionalmodels.ConstantsWrapper
import kotlinx.serialization.json.Json

class ConstantsValues(
     val sharedPreferences : SharedPreferences
) {

    fun getSBU():String{
        val result=sharedPreferences.getString(Constants.CONSTANTS,"")
        val constantsWrapper = Json.parse(ConstantsWrapper.serializer(),result!!)
        return constantsWrapper.sbu
    }

    fun getPercentageIESSAfiliado(): String{
        val result=sharedPreferences.getString(Constants.CONSTANTS,"")
        val constantsWrapper = Json.parse(ConstantsWrapper.serializer(),result!!)
        return constantsWrapper.percentageIessAfiliado
    }

    fun getPercentageFondosReserva(): String{
        val result=sharedPreferences.getString(Constants.CONSTANTS,"")
        val constantsWrapper = Json.parse(ConstantsWrapper.serializer(),result!!)
        return constantsWrapper.percentageFondosReserva
    }

    fun getCompleteTimeHours(): Int{
        val result=sharedPreferences.getString(Constants.CONSTANTS,"")
        val constantsWrapper = Json.parse(ConstantsWrapper.serializer(),result!!)
        return constantsWrapper.hoursCompleteTime
    }

}

package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.untha.model.transactionalmodels.Route
import com.untha.utils.Constants
import kotlinx.serialization.json.Json

class RoutesViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    fun loadRouteFromSharedPreferences(typeRoute: String): Route {
        val jsonRoute = sharedPreferences.getString(typeRoute, null)
        return if (jsonRoute == null) {
            Route(0, listOf())
        } else {
            Json.parse(Route.serializer(), jsonRoute)
        }
    }

    fun deleteAnswersOptionFromSharedPreferences(typeRoute: String) {
        val nameRoute = when (typeRoute) {
            Constants.ROUTE_LABOUR   -> Constants.FAULT_ANSWER_ROUTE_LABOUR
            Constants.ROUTE_VIOLENCE -> Constants.FAULT_ANSWER_ROUTE_VIOLENCE
            else                     -> Constants.FAULT_ANSWER_ROUTE_CALCULATOR
        }
        sharedPreferences.edit().remove(nameRoute).apply()
    }

    fun loadResultFaultAnswersFromSharedPreferences(isLabourRoute: Boolean): String? {
        val typeRoute =
            if (isLabourRoute) Constants.FAULT_ANSWER_ROUTE_LABOUR else Constants.FAULT_ANSWER_ROUTE_VIOLENCE

        return sharedPreferences.getString(typeRoute, "")
    }

    fun isThereLastResultForRoute(isLabourRoute: Boolean): Boolean {
        val isThereLastResult: String?
        val typeRoute =
            if (isLabourRoute) Constants.COMPLETE_LABOUR_ROUTE else Constants.COMPLETE_VIOLENCE_ROUTE

        isThereLastResult = sharedPreferences.getString(typeRoute, null)
        return isThereLastResult != null
    }


}

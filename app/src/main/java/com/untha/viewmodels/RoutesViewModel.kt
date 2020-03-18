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
        when (typeRoute) {
            Constants.ROUTE_LABOUR     -> sharedPreferences.edit().remove(Constants.FAULT_ANSWER_ROUTE_LABOUR).apply()
            Constants.ROUTE_VIOLENCE   -> sharedPreferences.edit().remove(Constants.FAULT_ANSWER_ROUTE_VIOLENCE).apply()
            Constants.ROUTE_CALCULATOR -> {
                sharedPreferences.edit().remove(Constants.FAULT_ANSWER_ROUTE_CALCULATOR_HINT)
                    .apply()
                sharedPreferences.edit().remove(Constants.FAULT_ANSWER_ROUTE_CALCULATOR)
                    .apply()
            }
        }
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

package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.untha.model.transactionalmodels.Route
import com.untha.utils.Constants
import kotlinx.serialization.json.Json

class RoutesViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    fun loadViolenceRouteFromSharedPreferences(): Route {
        val jsonRoute = sharedPreferences.getString(Constants.VIOLENCE_ROUTE, null)
        return if (jsonRoute == null) {
            Route(0, listOf())
        } else {
            Json.parse(Route.serializer(), jsonRoute)
        }
    }

    fun loadLabourRouteFromSharedPreferences(): Route {
        val jsonRoute = sharedPreferences.getString(Constants.LABOUR_ROUTE, null)
        return if (jsonRoute == null) {
            Route(0, listOf())
        } else {
            Json.parse(Route.serializer(), jsonRoute)
        }
    }

    fun deleteAnswersOptionFromSharedPreferences(isLabourRoute: Boolean) {
        if (isLabourRoute) {
            sharedPreferences.edit().remove(Constants.FAULT_ANSWER_ROUTE_LABOUR).apply()
        } else {
            sharedPreferences.edit().remove(Constants.FAULT_ANSWER_ROUTE_VIOLENCE).apply()
        }
    }

    fun loadResultFaultAnswersFromSharedPreferences(isLabourRoute: Boolean): String? {
        if (isLabourRoute) {
            return sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_LABOUR, "")
        }
        return sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_VIOLENCE, "")
    }

    fun isThereLastResultForRoute(isLabourRoute: Boolean): Boolean {
        val isThereLastResult: String?
        return if (isLabourRoute) {
            isThereLastResult = sharedPreferences.getString(Constants.COMPLETE_LABOUR_ROUTE, null)
            isThereLastResult != null
        } else {
            isThereLastResult = sharedPreferences.getString(Constants.COMPLETE_VIOLENCE_ROUTE, null)
            isThereLastResult != null
        }
    }


}

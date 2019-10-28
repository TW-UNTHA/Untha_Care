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
}
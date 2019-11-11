package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.untha.model.transactionalmodels.ResultWrapper
import com.untha.model.transactionalmodels.RouteResult
import com.untha.utils.Constants
import kotlinx.serialization.json.Json

class RouteResultsViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    var routeResults: List<RouteResult>? = null
        private set

    private fun getRouteResultsIds(): List<String> {
        val results = sharedPreferences.getString(Constants.FAULT_ANSWER, "")
        return results?.split(" ") ?: listOf()
    }

    fun retrieveRouteResults() {
        val answers = getRouteResultsIds()
        if (!answers.isNullOrEmpty()) {
            val result = sharedPreferences.getString(Constants.ROUTE_RESULT, "")
            if (!result.isNullOrEmpty()) {
                val resultWrapper = Json.parse(ResultWrapper.serializer(), result)
                val matchingRouteResults =
                    resultWrapper.routeResults.filter { routeResult -> routeResult.id in answers }
                routeResults = if (matchingRouteResults.isEmpty()) null else matchingRouteResults
            }
        }
    }
}


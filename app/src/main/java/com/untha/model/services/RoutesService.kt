package com.untha.model.services

import androidx.lifecycle.LiveData
import com.untha.model.transactionalmodels.Route
import me.linshen.retrofit2.adapter.ApiResponse
import retrofit2.http.GET

interface RoutesService {
    @GET("labour_route.json")
    fun getLabourRoute(): LiveData<ApiResponse<Route>>

    @GET("violence_route.json")
    fun getViolenceRoute(): LiveData<ApiResponse<Route>>

    @GET("calculator_route.json")
    fun getCalculatorRoute(): LiveData<ApiResponse<Route>>


}

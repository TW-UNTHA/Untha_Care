package com.untha.model.services

import androidx.lifecycle.LiveData
import com.untha.model.transactionalmodels.ResultCalculatorWrapper
import com.untha.model.transactionalmodels.ResultWrapper
import me.linshen.retrofit2.adapter.ApiResponse
import retrofit2.http.GET

interface ResultService {
    @GET("result.json")
    fun getResult(): LiveData<ApiResponse<ResultWrapper>>

    @GET("result_calculator.json")
    fun getResultCalculator(): LiveData<ApiResponse<ResultWrapper>>

    @GET("recommend_calculator.json")
    fun getRecommendCalculator(): LiveData<ApiResponse<ResultCalculatorWrapper>>
}

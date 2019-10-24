package com.untha.model.services

import androidx.lifecycle.LiveData
import com.untha.model.transactionalmodels.RoutesWrapper
import me.linshen.retrofit2.adapter.ApiResponse
import retrofit2.http.GET

interface RoutesService {
    @GET("labour_route.json")
    fun getCategories(): LiveData<ApiResponse<RoutesWrapper>>
}

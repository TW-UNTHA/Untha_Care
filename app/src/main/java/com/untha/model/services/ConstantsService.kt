package com.untha.model.services

import androidx.lifecycle.LiveData
import com.untha.model.transactionalmodels.ConstantsWrapper
import me.linshen.retrofit2.adapter.ApiResponse
import retrofit2.http.GET

interface ConstantsService {

    @GET("constants.json")
    fun getConstants(): LiveData<ApiResponse<ConstantsWrapper>>
}

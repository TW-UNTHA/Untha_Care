package com.untha.model.services

import androidx.lifecycle.LiveData
import com.untha.model.transactionalmodels.UpdateWrapper
import me.linshen.retrofit2.adapter.ApiResponse
import retrofit2.http.GET

interface UpdateService {

    @GET("version.json")
    fun getUpdateConfig(): LiveData<ApiResponse<UpdateWrapper>>
}

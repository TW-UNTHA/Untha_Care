package com.untha.model.services

import androidx.lifecycle.LiveData
import com.untha.BuildConfig
import com.untha.model.transactionalmodels.CategoriesWrapper
import me.linshen.retrofit2.adapter.ApiResponse
import retrofit2.http.GET

interface CategoriesService {

    @GET(BuildConfig.CATEGORIES_SOURCE + ".json")
    fun getCategories(): LiveData<ApiResponse<CategoriesWrapper>>
}

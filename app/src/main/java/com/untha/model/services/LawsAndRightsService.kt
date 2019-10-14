package com.untha.model.services

import androidx.lifecycle.LiveData
import com.untha.model.transactionalmodels.CategoriesWrapper
import me.linshen.retrofit2.adapter.ApiResponse
import retrofit2.http.GET

interface LawsAndRightsService {

    @GET("categoriesPrueba.json")
    fun getCategories(): LiveData<ApiResponse<CategoriesWrapper>>
}

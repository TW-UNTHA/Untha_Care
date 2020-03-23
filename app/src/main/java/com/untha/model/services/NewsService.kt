package com.untha.model.services

import androidx.lifecycle.LiveData
import com.untha.model.transactionalmodels.NewsWrapper
import me.linshen.retrofit2.adapter.ApiResponse
import retrofit2.http.GET

interface NewsService {

    @GET("news.json")
    fun getNews(): LiveData<ApiResponse<NewsWrapper>>
}

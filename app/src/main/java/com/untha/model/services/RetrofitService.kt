package com.untha.model.services

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import me.linshen.retrofit2.adapter.LiveDataCallAdapterFactory
import okhttp3.MediaType
import retrofit2.Retrofit

class RetrofitService {
    val contentType = MediaType.get("application/json")
    val retrofit = Retrofit.Builder()
        .baseUrl("https://tw-untha.github.io/Untha_Care/")
        .addConverterFactory(Json.asConverterFactory(contentType))
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .build()

    fun getRetrofitCategoryService(): CategoriesService {
        return retrofit.create(CategoriesService::class.java)
    }

    fun getRetrofitRouteService(): RoutesService {
        return retrofit.create(RoutesService::class.java)
    }

    fun getRetrofitResultService(): ResultService {
        return retrofit.create(ResultService::class.java)
    }

    fun getRetrofitQuestionnaireRouteResult(): QuestionnaireRouteResultService {
        return retrofit.create(QuestionnaireRouteResultService::class.java)
    }
    fun getRetrofitVersionService(): UpdateService{
        return retrofit.create(UpdateService::class.java)
    }
    fun getRetrofitNewsService(): NewsService{
        return retrofit.create(NewsService::class.java)
    }
}


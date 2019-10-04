package com.untha.model.services

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import me.linshen.retrofit2.adapter.LiveDataCallAdapterFactory
import okhttp3.MediaType
import retrofit2.Retrofit

class LawsAndRightsServiceAPI {
    fun getLawsAndRightsService(): LawsAndRightsService {
        val contentType = MediaType.get("application/json")
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tw-untha.github.io/Untha_Care/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
        return retrofit.create(LawsAndRightsService::class.java)
    }
}


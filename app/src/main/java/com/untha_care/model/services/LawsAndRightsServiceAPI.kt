package com.untha_care.model.services

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

class LawsAndRightsServiceAPI {

    fun getLawsAndRightsService(): LawsAndRightsService {

        val contentType = MediaType.get("application/json")
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()

        return retrofit.create(LawsAndRightsService::class.java)
    }
}
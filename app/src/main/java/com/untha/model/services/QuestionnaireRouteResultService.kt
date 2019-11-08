package com.untha.model.services

import androidx.lifecycle.LiveData
import com.untha.model.transactionalmodels.QuestionnaireRouteResultWrapper
import me.linshen.retrofit2.adapter.ApiResponse
import retrofit2.http.GET

interface QuestionnaireRouteResultService {

    @GET("questionnaire_route_result.json")
    fun getQuestionnaireRouteResult(): LiveData<ApiResponse<QuestionnaireRouteResultWrapper>>
}

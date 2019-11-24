package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class QuestionnaireRouteResultWrapper(
    val version: Int,
    val results: List<QuestionnaireRouteResult>
) : java.io.Serializable


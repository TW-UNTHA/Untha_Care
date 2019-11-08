package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class QuestionnaireRouteResult(
    val id: Int = 0,
    val title: String,
    val code: String,
    val type: String,
    val sections: List<Section>
) : java.io.Serializable

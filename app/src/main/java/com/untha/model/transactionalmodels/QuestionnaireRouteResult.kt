package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class QuestionnaireRouteResult(
    val id: Int = 0,
    val title: String? = null,
    val code: String? = null,
    val type: String,
    val sections: List<Section>
) : java.io.Serializable

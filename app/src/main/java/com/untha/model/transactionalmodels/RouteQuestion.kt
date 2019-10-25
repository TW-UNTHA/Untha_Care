package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class RouteQuestion(
    val id: Int,
    val type: String,
    val content: String,
    val explanation: String,
    val goTo: Int?,
    val result: String?,
    val options: List<RouteOption>
)

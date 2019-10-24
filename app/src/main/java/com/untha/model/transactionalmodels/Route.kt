package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: Int,
    val type: String,
    val content: String,
    val explanation: String,
    val options: List<RouteOption>? = null
)

package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class RouteResult(
    val id: String,
    val type: String,
    val content: String,
    val categories: List<Int>? = null
) : java.io.Serializable

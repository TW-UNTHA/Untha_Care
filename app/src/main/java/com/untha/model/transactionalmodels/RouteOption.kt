package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class RouteOption(
    val value: String,
    val result: String?,
    val goTo: Int?
)



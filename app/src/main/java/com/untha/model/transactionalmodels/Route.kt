package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class Route(val version: Int, val questions: List<RouteQuestion>)

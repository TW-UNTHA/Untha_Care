package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class ResultWrapper(val version: Int, val routeResults: List<RouteResult>):java.io.Serializable

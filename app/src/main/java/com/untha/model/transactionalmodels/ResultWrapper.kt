package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class ResultWrapper(val version: Int, val results: List<RouteResult>) : java.io.Serializable

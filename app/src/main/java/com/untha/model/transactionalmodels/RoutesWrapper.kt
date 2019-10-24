package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class RoutesWrapper(val version: Int, val routes: List<Route>)

package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class CategoriesWrapper(val version: Int, val categories: List<Category>) :
    java.io.Serializable

package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class ResultCalculator(
    val id: String,
    val title: String,
    val content: String
) : java.io.Serializable

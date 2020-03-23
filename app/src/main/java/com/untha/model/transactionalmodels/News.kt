package com.untha.model.transactionalmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class News(
    val id: Long,
    @SerialName("show_new") val showNew: Boolean,
    val title: String,
    val subtitle: String,
    val steps: List<Step>? = null
) : java.io.Serializable

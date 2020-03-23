package com.untha.model.transactionalmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsWrapper(
    val version: Long,
    @SerialName("show_screen") val showScreen: Boolean,
    @SerialName("button_title") val buttonTitle: String,
    @SerialName("button_subtitle") val buttonSubtitle: String,
    val news: List<News>? = null

) :
    java.io.Serializable

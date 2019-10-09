package com.untha.model.transactionalmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryInformation(
    val id: Int = 0,
    val description: String? = null,
    val image: String? = null,
    @SerialName("screen_title") val screenTitle: String? = null,
    val sections: List<Section>? = null
) : java.io.Serializable

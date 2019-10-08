package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class CategoryInformation(
    val id: Int = 0,
    val description: String? = null,
    val image: String? = null,
    val sections: List<Section>? = null
): java.io.Serializable

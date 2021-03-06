package com.untha.model.transactionalmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val title: String,
    val subtitle: String? = null,
    @SerialName("title_next_step") val titleNextStep: String? = null,
    val image: String? = null,
    @SerialName("parent_id") val parentId: Int? = null,
    val type: String? = null,
    val information: List<CategoryInformation>? = null
) : java.io.Serializable

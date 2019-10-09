package com.untha.model.transactionalmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val title: String,
    val subtitle: String? = null,
    val image: String? = null,
    @SerialName("parent_id") val parentId: Int? = null,
    @SerialName("next_step") val nextStep: Int? = null,
    val information: CategoryInformation? = null
) : java.io.Serializable

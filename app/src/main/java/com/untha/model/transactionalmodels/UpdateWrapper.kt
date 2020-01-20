package com.untha.model.transactionalmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateWrapper(
    val version: Long, @SerialName("force_update") val forceUpdate: Boolean, @SerialName(
        "message_to_update"
    ) val messageUpdate: String
) :
    java.io.Serializable

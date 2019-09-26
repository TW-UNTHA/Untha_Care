package com.untha.model.transactionalmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Step(@SerialName("step_id") val stepId: Int, val description: String)

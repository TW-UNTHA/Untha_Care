package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class Section(val id: Int = 0, val title: String, val steps: List<Step>? = null)

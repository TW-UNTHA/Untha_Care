package com.untha.model.transactionalmodels

import kotlinx.serialization.Serializable

@Serializable
data class ResultCalculatorWrapper(val version: Int, val resultCalculators: List<ResultCalculator>) :
    java.io.Serializable

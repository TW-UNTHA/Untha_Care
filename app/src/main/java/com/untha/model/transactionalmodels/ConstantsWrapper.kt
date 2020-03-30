package com.untha.model.transactionalmodels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConstantsWrapper(
    val version: Long,
    val sbu: String,
    @SerialName("percentage_iess_afiliado") val percentageIessAfiliado: String,
    @SerialName(    "percentage_fondos_reserva") val percentageFondosReserva: String,
    @SerialName(    "hours_complete_time") val hoursCompleteTime: Int
) :
    java.io.Serializable

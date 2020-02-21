package com.untha.automation

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.*

@Serializable
@Entity
data class ExcelModel(
    @PrimaryKey var id: Double,
    var weekHours: Double,
    @ContextualSerialization
    var finalSalary: BigDecimal,
    var idArea: Int,
    var startDate: String,
    var endDate: String,
    @ContextualSerialization
    var percentageIESS: BigDecimal,
    @ContextualSerialization
    var fondosReserva: BigDecimal,
    @ContextualSerialization
    var decimoTerceroAcumulado: BigDecimal,
    @ContextualSerialization
    var decimoTerceroMensualizado: BigDecimal,
    @ContextualSerialization
    var decimoCuartoAcumulado: BigDecimal,
    @ContextualSerialization
    var decimoCuartoMensualizado: BigDecimal
)


package com.untha.automation

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
@Entity
data class ExcelModelFiniquito(
    @PrimaryKey var id: Int,
    var typeIndemnizacion: String,
    var typeCausal: String,
    var horasSemana: Int,
    @ContextualSerialization
    var salario: BigDecimal,
    var idArea: Int,
    var decimoCuartoOption: Int,
    var decimoTerceroOption: Int,
    var fondosReservaOption: Int,
    var startDate: String,
    var endDate: String,
    var birthDate: String,
    @ContextualSerialization
    var discounts: BigDecimal,
    var daysTaken: Int,
    @ContextualSerialization
    var vacationPay: BigDecimal,
    @ContextualSerialization
    var finalSalary: BigDecimal,
    @ContextualSerialization
    var decimoCuarto: BigDecimal,
    @ContextualSerialization
    var decimoTercero: BigDecimal,
    @ContextualSerialization
    var fondosReserva: BigDecimal,
    @ContextualSerialization
    var compensation: BigDecimal,
    @ContextualSerialization
    var causal: BigDecimal,
    @ContextualSerialization
    var subtotal: BigDecimal,
    @ContextualSerialization
    var total: BigDecimal
)


package com.untha.automation

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class EntriesAnnualLeaveTest(
    var startDate: String,
    var endDate: String,
    var age: Int,
    var result: Double
)


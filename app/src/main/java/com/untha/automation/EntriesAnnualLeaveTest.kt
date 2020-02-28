package com.untha.automation

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class EntriesAnnualLeaveTest(
    var age: Int,
    var startDate: String,
    var endDate: String,
    var result: Double
)


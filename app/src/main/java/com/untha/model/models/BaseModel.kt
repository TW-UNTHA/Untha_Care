package com.untha.model.models

import androidx.room.ColumnInfo
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.util.*

//All models must inherit from this class
@Serializable
abstract class BaseModel {
    @ColumnInfo(name = "created_at")
    @ContextualSerialization
    var createdAt: Date = Calendar.getInstance().time
    @ColumnInfo(name = "updated_at")
    @ContextualSerialization
    var updatedAt: Date = Calendar.getInstance().time
}

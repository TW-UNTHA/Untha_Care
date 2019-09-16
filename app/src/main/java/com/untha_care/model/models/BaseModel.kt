package com.untha_care.model.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

//All models must inherit from this class
@Serializable
@Entity
abstract class BaseModel {
     @PrimaryKey(autoGenerate = true)  var id: Int = 0
}
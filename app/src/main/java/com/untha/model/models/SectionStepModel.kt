package com.untha.model.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    foreignKeys = [ForeignKey(
        entity = SectionModel::class,
        parentColumns = ["id"],
        childColumns = ["section_id"],
        onDelete = CASCADE
    )]
)
data class SectionStepModel(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var description: String = "",
    @ColumnInfo(name = "step_id") val stepId: Int ? = null,
    @ColumnInfo(name = "section_id") val sectionId: Int
) : BaseModel()

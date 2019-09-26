package com.untha.model.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    foreignKeys = [ForeignKey(
        entity = CategoryInformationModel::class,
        parentColumns = ["id"],
        childColumns = ["category_information_id"],
        onDelete = CASCADE
    )]
)
data class SectionModel(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var title: String = "",
    @ColumnInfo(name = "category_information_id") var categoryInformationId: Int,
    @Ignore
    var steps: List<SectionStepModel>? = null
) : BaseModel() {
    constructor() : this(0, "", 0)
}

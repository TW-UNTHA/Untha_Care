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
        entity = CategoryModel::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = CASCADE
    )]
)

data class CategoryInformationModel(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var description: String = "",
    var image: String = "",
    @ColumnInfo(name = "screen_title")
    var screenTitle: String = "",
    @ColumnInfo(name = "next_step")
    var nextStep: Int? = null,
    @ColumnInfo(name = "category_id")
    var categoryId: Int = 0,
    @Ignore
    var sections: List<SectionModel>? = null
) : BaseModel() {
    constructor() : this(0, "", "","", 0)
}

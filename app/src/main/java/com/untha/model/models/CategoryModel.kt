package com.untha.model.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class CategoryModel(
    @PrimaryKey var id: Int,
    var title: String = "",
    var subtitle: String? = "",
    var titleNextStep: String? = null,
    var image: String? = "",
    @ColumnInfo(name = "parent_id")
    var parentId: Int? = null,
    var type: String? = null,
    @Ignore
    var categoryInformationModel: List<CategoryInformationModel>? = null
) : BaseModel() {
    constructor() : this(0, "")
}

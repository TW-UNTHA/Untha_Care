package com.untha.model.models

import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
class QueryingCategory {

    @Embedded
    var categoryModel: CategoryModel? = null
    @Relation(
        parentColumn = "id",
        entityColumn = "category_id",
        entity = CategoryInformationModel::class
    )
    var queryingCategoryInformation: List<QueryingCategoryInformation>? = null

}

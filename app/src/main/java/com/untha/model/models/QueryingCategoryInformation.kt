package com.untha.model.models

import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
class QueryingCategoryInformation {

    @Embedded
    var categoryInformationModel: CategoryInformationModel? = null

    @Relation(
        parentColumn = "id",
        entityColumn = "category_information_id",
        entity = SectionModel::class
    )
    var queryingSection: List<QueryingSection>? = null
}

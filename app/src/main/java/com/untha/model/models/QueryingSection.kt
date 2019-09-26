package com.untha.model.models

import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
class QueryingSection {

    @Embedded
    var section: SectionModel? = null
    @Relation(parentColumn = "id", entityColumn = "section_id", entity = SectionStepModel::class)
    var steps: List<SectionStepModel>? = null
}

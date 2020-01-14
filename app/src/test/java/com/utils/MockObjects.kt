package com.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.nhaarman.mockito_kotlin.mock
import com.untha.model.models.CategoryInformationModel
import com.untha.model.models.CategoryModel
import com.untha.model.models.QueryingCategory
import com.untha.model.models.QueryingCategoryInformation
import com.untha.model.models.QueryingSection
import com.untha.model.models.SectionModel
import com.untha.model.models.SectionStepModel
import org.mockito.Mockito

object MockObjects {

    fun mockQueryingCategory(type: String = "route"): QueryingCategory {
        var stepModel1 = SectionStepModel(1, RandomGenerator.generateRandomString(5), 1, 1)
        var sectionModel1 = SectionModel(1, RandomGenerator.generateRandomString(5), 1)
        var categoryInformationModel1 =
            CategoryInformationModel(
                1,
                RandomGenerator.generateRandomString(5),
                RandomGenerator.generateRandomString(5),
                RandomGenerator.generateRandomString(5),
                1
            )
        var categoryModel1 = CategoryModel(
            1,
            RandomGenerator.generateRandomString(5),
            RandomGenerator.generateRandomString(5),
            RandomGenerator.generateRandomString(5),
            RandomGenerator.generateRandomString(5),
            0,
            type
        )


        val queryingSection = QueryingSection().apply {
            steps = listOf(stepModel1)
            section = sectionModel1
        }

        val queryingCategoryInformation =
            QueryingCategoryInformation().apply {
                categoryInformation = categoryInformationModel1
                this.queryingSection = listOf(queryingSection)
            }

        return QueryingCategory().apply {
            categoryModel = categoryModel1
            this.queryingCategoryInformation = listOf(queryingCategoryInformation)
        }
    }

    fun mockLifecycleOwner(): LifecycleOwner {
        val owner = mock<LifecycleOwner>()
        val lifecycle = LifecycleRegistry(owner)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        Mockito.`when`<Lifecycle>(owner.lifecycle).thenReturn(lifecycle)
        return owner
    }
}
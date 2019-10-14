package com.utils

import com.untha.model.models.*

object MockObjects {

     fun mockQueryingCategory(): QueryingCategory {
        var stepModel1 = SectionStepModel(1, RandomGenerator.generateRandomString(5), 1, 1)
        var sectionModel1 = SectionModel(1, RandomGenerator.generateRandomString(5), 1)
        var categoryInformationModel1 =
            CategoryInformationModel(1, RandomGenerator.generateRandomString(5),RandomGenerator.generateRandomString(5) , RandomGenerator.generateRandomString(5) ,1)
        var categoryModel1 = CategoryModel(
            1,
            RandomGenerator.generateRandomString(5),
            RandomGenerator.generateRandomString(5),
            RandomGenerator.generateRandomString(5),
            0
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
}
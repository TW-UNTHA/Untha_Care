package com.untha.model.mappers

import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.CategoryInformation
import com.untha.model.models.CategoryInformationModel
import com.untha.model.models.CategoryModel
import com.untha.model.models.QueryingCategory
import com.untha.model.transactionalmodels.Section
import com.untha.model.models.SectionModel
import com.untha.model.models.SectionStepModel
import com.untha.model.models.QueryingSection
import com.untha.model.transactionalmodels.Step


class CategoryMapper {
    fun mapToModel(category: Category): CategoryModel {

        val stepModels: MutableList<SectionStepModel> = mutableListOf()
        val sectionModels: MutableList<SectionModel> = mutableListOf()

        val categoryInformation = CategoryInformationModel(
            category.id, category.information?.description ?: "",
            category.id
        )

        category.information?.sections?.map { section ->
            section.steps?.map { step ->
                stepModels.add(
                    SectionStepModel(
                        description = step.description,
                        stepId = step.stepId,
                        sectionId = section.id
                    )
                )
            }
        }

        category.information?.sections?.map {
            sectionModels.add(SectionModel(it.id, it.title, categoryInformation.id))
        }


        sectionModels.map { section ->
            section.steps = stepModels.filter { step -> step.sectionId == section.id }
        }

        categoryInformation.sections = sectionModels

        return CategoryModel(
            category.id,
            category.title,
            category.subtitle,
            category.image,
            category.parentId,
            category.nextStep,
            categoryInformation
        )
    }


    fun mapFromModel(queryingCategory: QueryingCategory): Category {

        val categoryInformation = CategoryInformation(
            queryingCategory.queryingCategoryInformation?.categoryInformationModel?.id ?: 0,
            queryingCategory.queryingCategoryInformation?.categoryInformationModel?.description,
            getSections(queryingCategory)
        )

        return Category(
            queryingCategory.categoryModel?.id ?: 0,
            queryingCategory.categoryModel?.title ?: "",
            queryingCategory.categoryModel?.subtitle,
            queryingCategory.categoryModel?.image,
            queryingCategory.categoryModel?.parentId,
            queryingCategory.categoryModel?.nextStep,
            categoryInformation
        )
    }

    private fun getSections(queryingCategory: QueryingCategory) =
        queryingCategory.queryingCategoryInformation?.queryingSection?.map { sectionModel ->
            Section(
                sectionModel.section?.id ?: 0, sectionModel.section?.title ?: "",
                getSteps(sectionModel)
            )
        }

    private fun getSteps(queryingSection: QueryingSection): List<Step>? {
        return queryingSection.steps?.map { stepModel ->
            Step(stepModel.stepId, stepModel.description)
        }
    }
}

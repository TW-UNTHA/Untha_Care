package com.untha.model.mappers

import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.CategoryInformation
import com.untha.model.models.CategoryInformationModel
import com.untha.model.models.CategoryModel
import com.untha.model.models.QueryingCategory
import com.untha.model.models.QueryingCategoryInformation
import com.untha.model.transactionalmodels.Section
import com.untha.model.models.SectionModel
import com.untha.model.models.SectionStepModel
import com.untha.model.transactionalmodels.Step


class CategoryMapper {
    fun mapToModel(category: Category): CategoryModel {

        val informationModels: MutableList<CategoryInformationModel> = mutableListOf()

        category.information?.map {categoryInformation ->
            informationModels.add(CategoryInformationModel(
                categoryInformation.id,
                categoryInformation.description?: "",
                categoryInformation.image ?: "",
                categoryInformation.screenTitle ?: "",
                category.id,
                sections = mapperSectionsModel(categoryInformation.sections, categoryInformation.id)
            ))

        }

        return CategoryModel(
            category.id,
            category.title,
            category.subtitle,
            category.image,
            category.parentId,
            category.nextStep,
            informationModels
        )
    }

    private fun mapperSectionsModel(sections: List<Section>?, idCategoryInformation: Int):
            List<SectionModel>? {
        val sectionModels: MutableList<SectionModel> = mutableListOf()
        sections?.map {
        sectionModels.add(SectionModel(it.id, it.title, idCategoryInformation, mapperStepsModel(it.steps, it.id)))
       }
        return sectionModels
    }

    private fun mapperStepsModel(steps: List<Step>?, sectionId:Int): List<SectionStepModel>? {
        val stepModels: MutableList<SectionStepModel> = mutableListOf()

        steps?.map { step ->
                stepModels.add(
                    SectionStepModel(
                        description = step.description,
                        stepId = step.stepId,
                        sectionId = sectionId
                    )
                )
        }
        return stepModels
    }

    fun mapFromModel(queryingCategory: QueryingCategory): Category {
        Category(
            queryingCategory.categoryModel?.id ?: 0,
            queryingCategory.categoryModel?.title ?: "",
            queryingCategory.categoryModel?.subtitle,
            queryingCategory.categoryModel?.image,
            queryingCategory.categoryModel?.parentId,
            queryingCategory.categoryModel?.nextStep,
            getInformation(queryingCategory)
        )

        return Category(
            queryingCategory.categoryModel?.id ?: 0,
            queryingCategory.categoryModel?.title ?: "",
            queryingCategory.categoryModel?.subtitle,
            queryingCategory.categoryModel?.image,
            queryingCategory.categoryModel?.parentId,
            queryingCategory.categoryModel?.nextStep,
            getInformation(queryingCategory)
        )
    }

    private fun getInformation(queryingCategory: QueryingCategory?) : List<CategoryInformation>? {
          return queryingCategory?.queryingCategoryInformation?.map { categoryInformationModel->
                 CategoryInformation(
                        categoryInformationModel.categoryInformation?.id?:0,
                     categoryInformationModel.categoryInformation?.description?:"",
                     categoryInformationModel.categoryInformation?.image?:"",
                     categoryInformationModel.categoryInformation?.screenTitle?:"",
                     getSections(categoryInformationModel)
                 )
         }
    }

    private fun getSections(queryingCategoryInformation: QueryingCategoryInformation) =
        queryingCategoryInformation.queryingSection?.map() { sectionModel ->

            Section(
                sectionModel.section?.id ?: 0, sectionModel.section?.title ?: "",
                getSteps(sectionModel.steps)
            )
        }

    private fun getSteps(queryingSection: List<SectionStepModel>?): List<Step>? {
        return queryingSection?.map { stepModel ->
            Step(stepModel.stepId, stepModel.description)
        }
    }

}

package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.untha.model.mappers.CategoryMapper
import com.untha.model.dbservices.CategoryDbService
import com.untha.model.models.CategoryModel
import com.untha.model.models.QueryingCategory
import com.untha.model.models.CategoryInformationModel
import com.untha.model.models.SectionModel
import com.untha.model.models.SectionStepModel
import com.untha.model.transactionalmodels.Category
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.services.CategoriesService
import com.untha.utils.Constants
import me.linshen.retrofit2.adapter.ApiErrorResponse
import me.linshen.retrofit2.adapter.ApiSuccessResponse

class MainViewModel(
    private val categoryDbService: CategoryDbService,
    private val categoriesService: CategoriesService,
    private val categoryMapper: CategoryMapper,
    private val categoryWithRelationsRepository: CategoryWithRelationsRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val categoryModels: MutableList<CategoryModel> = mutableListOf()

    fun retrieveUpdatedCategories(owner: LifecycleOwner) {
        categoriesService.getCategories()
            .observe(owner, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        val version = sharedPreferences.getInt(Constants.CATEGORIES_VERSION, 0)
                        if (version != response.body.version) {
                            response.body.categories.map { category ->
                                categoryModels.add(
                                    categoryMapper.mapToModel(
                                        category
                                    )
                                )
                            }
                            categoryDbService.saveCategory(
                                categoryModels, ::categoriesSavedCallback
                            )
                        }
                    }
                    is ApiErrorResponse -> {
                        println("Errror!!!!!!!!!!!!! $response.errorMessage")
                    }
                }
            })
    }

    fun retrieveAllCategories(): LiveData<List<QueryingCategory>> {
        return categoryWithRelationsRepository.getAllCategories()
    }


    private fun categoriesSavedCallback(message: String) {
        println(message)
        val categoryInformationModels = mutableListOf<CategoryInformationModel>()
        categoryModels.map { category ->
            category.categoryInformationModel?.let { categoryInformationModel ->
                categoryInformationModel.map {categoryInformation->
                    categoryInformationModels.add(categoryInformation)
                }
            }

        }
        categoryDbService.saveCategoryInformation(
            categoryInformationModels,
            ::categoriesInformationSaved
        )
    }

    private fun categoriesInformationSaved(message: String) {
        println(message)
        val informationSections = mutableListOf<SectionModel>()
        categoryModels.map { category ->
            category.categoryInformationModel?.let { categoryInformationModel ->
                categoryInformationModel.map {categoryInformation->
                    categoryInformation.sections?.let { informationSections.addAll(it) }
//                    categoryInformation.sections?.map {sections->
//                        informationSections.addAll(listOf(sections))
//                    }
                }
            }
        }
        categoryDbService.saveSections(informationSections, ::categoriesSectionsSaved)
    }

    private fun categoriesSectionsSaved(message: String) {
        println(message)
        val sectionSteps = mutableListOf<SectionStepModel>()
        categoryModels.map { category ->
            category.categoryInformationModel?.map { categoryInformationModel ->
                categoryInformationModel.sections?.map { section ->
                    section.steps?.let { sectionSteps.addAll(it) }
                }
            }
        }
        categoryDbService.saveSectionSteps(sectionSteps, ::categoriesSectionsStepsSaved)
    }

    private fun categoriesSectionsStepsSaved(message: String) {
        println(message)
    }

    fun getCategories(categoriesQuerying: List<QueryingCategory>): List<Category> {
        return categoriesQuerying.map { categoryMapper.mapFromModel(it) }
    }
}



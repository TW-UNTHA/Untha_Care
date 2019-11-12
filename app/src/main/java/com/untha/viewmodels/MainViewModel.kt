package com.untha.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.untha.R
import com.untha.model.dbservices.CategoryDbService
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.CategoryInformationModel
import com.untha.model.models.CategoryModel
import com.untha.model.models.QueryingCategory
import com.untha.model.models.SectionModel
import com.untha.model.models.SectionStepModel
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.services.CategoriesService
import com.untha.model.services.QuestionnaireRouteResultService
import com.untha.model.services.ResultService
import com.untha.model.services.RoutesService
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.QuestionnaireRouteResultWrapper
import com.untha.model.transactionalmodels.ResultWrapper
import com.untha.model.transactionalmodels.Route
import com.untha.utils.Constants
import kotlinx.serialization.json.Json
import me.linshen.retrofit2.adapter.ApiErrorResponse
import me.linshen.retrofit2.adapter.ApiSuccessResponse

class MainViewModel(
    private val categoryDbService: CategoryDbService,
    private val categoriesService: CategoriesService,
    private val categoryMapper: CategoryMapper,
    private val categoryWithRelationsRepository: CategoryWithRelationsRepository,
    private val sharedPreferences: SharedPreferences,
    private val routesService: RoutesService,
    private val resultService: ResultService,
    private val questionnaireRouteResultService: QuestionnaireRouteResultService

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
                        println("Error! $response.errorMessage")
                    }
                }
            })
    }


    fun retrieveAllCategories(): LiveData<List<QueryingCategory>> {
        return categoryWithRelationsRepository.getAllCategories()
    }


    private fun categoriesSavedCallback(message: String) {
        val categoryInformationModels = mutableListOf<CategoryInformationModel>()
        categoryModels.map { category ->
            category.categoryInformationModel?.let { categoryInformationModel ->
                categoryInformationModel.map { categoryInformation ->
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
                categoryInformationModel.map { categoryInformation ->
                    categoryInformation.sections?.let { informationSections.addAll(it) }
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

    fun loadLabourRoute(owner: LifecycleOwner) {
        routesService.getLabourRoute()
            .observe(owner, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        sharedPreferences.edit()
                            .putString(
                                Constants.LABOUR_ROUTE,
                                Json.stringify(Route.serializer(), response.body)
                            ).apply()
                    }
                }
            })
    }

    fun loadViolenceRoute(owner: LifecycleOwner) {
        routesService.getViolenceRoute()
            .observe(owner, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        sharedPreferences.edit()
                            .putString(
                                Constants.VIOLENCE_ROUTE,
                                Json.stringify(Route.serializer(), response.body)
                            ).apply()
                    }
                }
            })
    }


    fun loadRouteResults(owner: LifecycleOwner) {
        resultService.getResult()
            .observe(owner, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        sharedPreferences.edit()
                            .putString(
                                Constants.ROUTE_RESULT,
                                Json.stringify(ResultWrapper.serializer(), response.body)
                            ).apply()
                    }
                }
            })
    }

    fun loadDefaultResult(context: Context) {
        val result = context.resources.openRawResource(R.raw.result)
            .bufferedReader().use { it.readText() }
        sharedPreferences.edit()
            .putString(
                Constants.ROUTE_RESULT,
                result
            ).apply()
    }

    fun loadResultFromSharedPreferences(): ResultWrapper {
        val jsonResult = sharedPreferences.getString(Constants.ROUTE_RESULT, "N/A")
        return if (jsonResult == null) {
            ResultWrapper(0, listOf())
        } else {
            Json.parse(ResultWrapper.serializer(), jsonResult)
        }
    }

    fun loadResultFaultAnswerFromSharedPreferences(): String? {
        return sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_LABOUR, "")
    }

    fun loadResultRouteViolenceFaultAnswerFromSharedPreferences(): String? {
        return sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_VIOLENCE, "")
    }

    fun loadDefaultLabourRoute(context: Context) {
        val route = context.resources.openRawResource(R.raw.labour_route)
            .bufferedReader().use { it.readText() }
        sharedPreferences.edit()
            .putString(
                Constants.LABOUR_ROUTE,
                route
            ).apply()
    }

    fun loadDefaultViolenceRoute(context: Context) {
        val route = context.resources.openRawResource(R.raw.violence_route)
            .bufferedReader().use { it.readText() }
        sharedPreferences.edit()
            .putString(
                Constants.VIOLENCE_ROUTE,
                route
            ).apply()
    }

    fun deleteAnswersOptionFromSharedPreferences() {
        sharedPreferences.edit().remove(Constants.FAULT_ANSWER).apply()
    }


    fun loadQuestionnaireRouteResult(owner: LifecycleOwner) {
        questionnaireRouteResultService.getQuestionnaireRouteResult()
            .observe(owner, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        sharedPreferences.edit()
                            .putString(
                                Constants.QUESTIONNAIRE_ROUTE,
                                Json.stringify(
                                    QuestionnaireRouteResultWrapper.serializer(),
                                    response.body
                                )
                            ).apply()

                    }
                    is ApiErrorResponse -> {
                        println("ErrorQUESTIONNAIRE_ROUTE! $response.errorMessage")
                    }
                }
            })
    }

    fun loadDefaultQuestionnaireRouteResult(context: Context) {
        val result = context.resources.openRawResource(R.raw.questionnaire_route_result)
            .bufferedReader().use { it.readText() }
        sharedPreferences.edit()
            .putString(
                Constants.QUESTIONNAIRE_ROUTE,
                result
            ).apply()
    }
}






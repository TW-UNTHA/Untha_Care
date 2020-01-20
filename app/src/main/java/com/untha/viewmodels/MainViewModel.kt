package com.untha.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.untha.BuildConfig
import com.untha.R
import com.untha.model.dbservices.CategoryDbService
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.CategoryInformationModel
import com.untha.model.models.CategoryModel
import com.untha.model.models.QueryingCategory
import com.untha.model.models.SectionModel
import com.untha.model.models.SectionStepModel
import com.untha.model.services.CategoriesService
import com.untha.model.services.QuestionnaireRouteResultService
import com.untha.model.services.ResultService
import com.untha.model.services.RoutesService
import com.untha.model.transactionalmodels.CategoriesWrapper
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.QuestionnaireRouteResultWrapper
import com.untha.model.transactionalmodels.ResultWrapper
import com.untha.model.transactionalmodels.Route
import com.untha.utils.Constants
import kotlinx.serialization.json.Json
import me.linshen.retrofit2.adapter.ApiErrorResponse
import me.linshen.retrofit2.adapter.ApiSuccessResponse
import timber.log.Timber

class MainViewModel(
    private val categoryDbService: CategoryDbService,
    private val categoriesService: CategoriesService,
    private val categoryMapper: CategoryMapper,
    private val sharedPreferences: SharedPreferences,
    private val routesService: RoutesService,
    private val resultService: ResultService,
    private val questionnaireRouteResultService: QuestionnaireRouteResultService
) : ViewModel() {

    private val categoryModels: MutableList<CategoryModel> = mutableListOf()
    private var versionCurrently: Int = 0

    fun retrieveUpdatedCategories(owner: LifecycleOwner) {
        categoriesService.getCategories()
            .observe(owner, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        val version = sharedPreferences.getInt(Constants.CATEGORIES_VERSION, 0)
                        if (version < response.body.version) {
                            versionCurrently = response.body.version
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
                        Timber.e("Error! $response.errorMessage")
                    }
                }
            })
    }

    fun loadDefaultCategories(context: Context) {
        val fileId = context.resources.getIdentifier(
            "com.untha:raw/" + BuildConfig.CATEGORIES_SOURCE,
            null,
            null
        )
        val result = context.resources.openRawResource(fileId)
            .bufferedReader().use { it.readText() }
        val categoriesWrapper = Json.parse(CategoriesWrapper.serializer(), result)
        categoriesWrapper.categories.map { category ->
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

    private fun categoriesSavedCallback(message: String) {
        Timber.i(message)
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
        Timber.i(message)
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
        Timber.i(message)
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
        Timber.i(message)
        sharedPreferences.edit()
            .putInt(
                Constants.CATEGORIES_VERSION,
                versionCurrently
            ).apply()
    }

    fun getCategories(categoriesQuerying: List<QueryingCategory>): List<Category> {
        return categoriesQuerying.map { categoryMapper.mapFromModel(it) }
    }

    fun loadLabourRoute(owner: LifecycleOwner) {
        routesService.getLabourRoute()
            .observe(owner, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        val sharedPreferencesResult =
                            sharedPreferences.getString(Constants.LABOUR_ROUTE, "")
                        if (!sharedPreferencesResult.isNullOrEmpty()) {
                            val resultWrapperSharedPreferences =
                                Json.parse(Route.serializer(), sharedPreferencesResult)
                            if (response.body.version > resultWrapperSharedPreferences.version) {
                                sharedPreferences.edit()
                                    .putString(
                                        Constants.LABOUR_ROUTE,
                                        Json.stringify(Route.serializer(), response.body)
                                    ).apply()
                            }
                        } else {
                            sharedPreferences.edit()
                                .putString(
                                    Constants.LABOUR_ROUTE,
                                    Json.stringify(Route.serializer(), response.body)
                                ).apply()
                        }
                    }
                    else -> {
                        Timber.e("Error en la llamada de load labour $response")
                    }
                }
            })
    }

    fun loadDefaultLabourRoute(context: Context) {
        val sharedPreferencesResult = sharedPreferences.getString(Constants.LABOUR_ROUTE, "")
        if (sharedPreferencesResult.isNullOrEmpty()) {
            val route = context.resources.openRawResource(R.raw.labour_route)
                .bufferedReader().use { it.readText() }
            sharedPreferences.edit()
                .putString(
                    Constants.LABOUR_ROUTE,
                    route
                ).apply()
        }

    }

    fun loadViolenceRoute(owner: LifecycleOwner) {
        routesService.getViolenceRoute()
            .observe(owner, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        val sharedPreferencesResult =
                            sharedPreferences.getString(Constants.VIOLENCE_ROUTE, "")
                        if (!sharedPreferencesResult.isNullOrEmpty()) {
                            val resultWrapperSharedPreferences =
                                Json.parse(Route.serializer(), sharedPreferencesResult)
                            if (response.body.version > resultWrapperSharedPreferences.version) {
                                sharedPreferences.edit()
                                    .putString(
                                        Constants.VIOLENCE_ROUTE,
                                        Json.stringify(Route.serializer(), response.body)
                                    ).apply()
                            }
                        } else {
                            sharedPreferences.edit()
                                .putString(
                                    Constants.VIOLENCE_ROUTE,
                                    Json.stringify(Route.serializer(), response.body)
                                ).apply()
                        }
                    }
                    else -> {
                        Timber.e("Error en la llamada de load violence $response")
                    }
                }
            })
    }

    fun loadDefaultViolenceRoute(context: Context) {
        val sharedPreferencesResult = sharedPreferences.getString(Constants.VIOLENCE_ROUTE, "")
        if (sharedPreferencesResult.isNullOrEmpty()) {
            val route = context.resources.openRawResource(R.raw.violence_route)
                .bufferedReader().use { it.readText() }
            sharedPreferences.edit()
                .putString(
                    Constants.VIOLENCE_ROUTE,
                    route
                ).apply()
        }
    }


    fun loadRouteResults(owner: LifecycleOwner) {
        resultService.getResult()
            .observe(owner, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        val sharedPreferencesResult =
                            sharedPreferences.getString(Constants.ROUTE_RESULT, "")
                        if (!sharedPreferencesResult.isNullOrEmpty()) {
                            val resultWrapperSharedPreferences =
                                Json.parse(ResultWrapper.serializer(), sharedPreferencesResult)
                            if (response.body.version > resultWrapperSharedPreferences.version) {
                                sharedPreferences.edit()
                                    .putString(
                                        Constants.ROUTE_RESULT,
                                        Json.stringify(ResultWrapper.serializer(), response.body)
                                    ).apply()
                            }
                        } else {
                            sharedPreferences.edit()
                                .putString(
                                    Constants.ROUTE_RESULT,
                                    Json.stringify(ResultWrapper.serializer(), response.body)
                                ).apply()
                        }
                    }
                    else -> {
                        Timber.e("Error en la llamada de load route results $response")
                    }
                }
            })
    }

    fun loadDefaultResult(context: Context) {
        val sharedPreferencesResult = sharedPreferences.getString(Constants.ROUTE_RESULT, "")
        if (sharedPreferencesResult.isNullOrEmpty()) {
            val result = context.resources.openRawResource(R.raw.result)
                .bufferedReader().use { it.readText() }
            sharedPreferences.edit()
                .putString(
                    Constants.ROUTE_RESULT,
                    result
                ).apply()
        }
    }


    fun loadQuestionnaireRouteResult(owner: LifecycleOwner) {
        questionnaireRouteResultService.getQuestionnaireRouteResult()
            .observe(owner, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        val sharedPreferencesResult =
                            sharedPreferences.getString(Constants.QUESTIONNAIRE_ROUTE, "")
                        if (!sharedPreferencesResult.isNullOrEmpty()) {
                            val resultWrapperSharedPreferences =
                                Json.parse(
                                    QuestionnaireRouteResultWrapper.serializer(),
                                    sharedPreferencesResult
                                )
                            if (response.body.version > resultWrapperSharedPreferences.version) {
                                sharedPreferences.edit()
                                    .putString(
                                        Constants.QUESTIONNAIRE_ROUTE,
                                        Json.stringify(
                                            QuestionnaireRouteResultWrapper.serializer(),
                                            response.body
                                        )
                                    ).apply()
                            }
                        } else {
                            sharedPreferences.edit()
                                .putString(
                                    Constants.QUESTIONNAIRE_ROUTE,
                                    Json.stringify(
                                        QuestionnaireRouteResultWrapper.serializer(),
                                        response.body
                                    )
                                ).apply()
                        }
                    }
                    is ApiErrorResponse -> {
                        Timber.e("ErrorQUESTIONNAIRE_ROUTE! $response.errorMessage")
                    }
                }
            })
    }

    fun loadDefaultQuestionnaireRouteResult(context: Context) {
        val sharedPreferencesResult = sharedPreferences.getString(Constants.QUESTIONNAIRE_ROUTE, "")
        if (sharedPreferencesResult.isNullOrEmpty()) {
            val result = context.resources.openRawResource(R.raw.questionnaire_route_result)
                .bufferedReader().use { it.readText() }
            sharedPreferences.edit()
                .putString(
                    Constants.QUESTIONNAIRE_ROUTE,
                    result
                ).apply()
        }
    }
}

package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.QuestionnaireRouteResultWrapper
import com.untha.model.transactionalmodels.ResultWrapper
import com.untha.model.transactionalmodels.RouteResult
import com.untha.utils.Constants
import com.utils.MockObjects.mockQueryingCategory
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class RouteResultsViewModelTest : KoinTest {
    private val sharedPreferences by inject<SharedPreferences>()
    private val repository by inject<CategoryWithRelationsRepository>()
    private val mapper by inject<CategoryMapper>()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        startKoin {
            modules(
                listOf(
                    persistenceModule,
                    networkModule,
                    viewModelsModule,
                    mapperModule
                )
            )
        }
        declareMock<SharedPreferences>()
        declareMock<CategoryWithRelationsRepository>()
        declareMock<CategoryMapper>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should get route results ids from shared preferences`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.COMPLETE_LABOUR_ROUTE, "")
    }

    @Test
    fun `should get route results from shared preferences when route result ids are not null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        val answer = "R1 R2 R3"
        `when`(sharedPreferences.getString(Constants.COMPLETE_LABOUR_ROUTE, "")).thenReturn(
            answer
        )

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.ROUTE_RESULT, "")
    }

    @Test
    fun `should not get route results when route result ids are empty`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn("")

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.COMPLETE_LABOUR_ROUTE, "")
        verify(sharedPreferences, never()).getString(Constants.ROUTE_RESULT, "")
    }

    @Test
    fun `should not get route results when route result ids are null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn(null)

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.COMPLETE_LABOUR_ROUTE, "")
        verify(sharedPreferences, never()).getString(Constants.ROUTE_RESULT, "")
    }

    @Test
    fun `should return null when route results are null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        val answer = "R1 R2 R3"
        `when`(sharedPreferences.getString(Constants.COMPLETE_LABOUR_ROUTE, "")).thenReturn(
            answer
        )
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn(null)

        viewModel.retrieveRouteResults()

        assertThat(null, `is`(viewModel.routeResults))
    }

    @Test
    fun `should return a list of route results when route results ids and route results are not null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        val answer = "R1 R2 R3"
        `when`(sharedPreferences.getString(Constants.COMPLETE_LABOUR_ROUTE, "")).thenReturn(
            answer
        )
        val firstRouteResult = RouteResult("R1", "dummy", "dummy", listOf(1))
        val secondRouteResult = RouteResult("R2", "dummy", "dummy", listOf(1))
        val thirdRouteResult = RouteResult("R3", "dummy", "dummy", listOf(1))
        val fourthRouteResult = RouteResult("R4", "dummy", "dummy", listOf(1))
        val routeResults =
            listOf(firstRouteResult, secondRouteResult, thirdRouteResult, fourthRouteResult)
        val resultWrapper = ResultWrapper(1, routeResults)
        val jsonRouteResults = Json.stringify(ResultWrapper.serializer(), resultWrapper)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn(jsonRouteResults)
        val expectedRouteResults =
            listOf(firstRouteResult, secondRouteResult, thirdRouteResult)

        viewModel.retrieveRouteResults()

        assertThat(expectedRouteResults, `is`(viewModel.routeResults))
    }

    @Test
    fun `should return a list of route filtered by type`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        val answer = "R1 R2 R3 R4"
        `when`(sharedPreferences.getString(Constants.COMPLETE_LABOUR_ROUTE, "")).thenReturn(
            answer
        )
        val firstRouteResult = RouteResult("R1", "fault", "dummy", listOf(1))
        val secondRouteResult = RouteResult("R2", "recommendation", "dummy", listOf(1))
        val thirdRouteResult = RouteResult("R3", "fault", "dummy", listOf(1))
        val fourthRouteResult = RouteResult("R4", "recommendation", "dummy", listOf(1))
        val routeResults =
            listOf(firstRouteResult, secondRouteResult, thirdRouteResult, fourthRouteResult)
        val resultWrapper = ResultWrapper(1, routeResults)
        val jsonRouteResults = Json.stringify(ResultWrapper.serializer(), resultWrapper)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn(jsonRouteResults)
        val expectedRouteResults =
            listOf(secondRouteResult, fourthRouteResult)
        viewModel.retrieveRouteResults()

        val recommendationRoutes = viewModel.getRouteResultsByType("recommendation")

        assertThat(recommendationRoutes, `is`(expectedRouteResults))
    }

    @Test
    fun `should return null if route type does not exist`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        val answer = "R1 R2 R3 R4"
        `when`(sharedPreferences.getString(Constants.COMPLETE_LABOUR_ROUTE, "")).thenReturn(
            answer
        )
        val firstRouteResult = RouteResult("R1", "fault", "dummy", listOf(1))
        val secondRouteResult = RouteResult("R2", "recommendation", "dummy", listOf(1))
        val thirdRouteResult = RouteResult("R3", "fault", "dummy", listOf(1))
        val fourthRouteResult = RouteResult("R4", "recommendation", "dummy", listOf(1))
        val routeResults =
            listOf(firstRouteResult, secondRouteResult, thirdRouteResult, fourthRouteResult)
        val resultWrapper = ResultWrapper(1, routeResults)
        val jsonRouteResults = Json.stringify(ResultWrapper.serializer(), resultWrapper)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn(jsonRouteResults)

        viewModel.retrieveRouteResults()

        val recommendationRoutes = viewModel.getRouteResultsByType("dummy")

        assertThat(null, `is`(recommendationRoutes))
    }

    @Test
    fun `should return null when any route result id match with route results`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        val answer = "R7 R5 R6"
        `when`(sharedPreferences.getString(Constants.COMPLETE_LABOUR_ROUTE, "")).thenReturn(
            answer
        )
        val firstRouteResult = RouteResult("R1", "dummy", "dummy", listOf(1))
        val secondRouteResult = RouteResult("R2", "dummy", "dummy", listOf(1))
        val thirdRouteResult = RouteResult("R3", "dummy", "dummy", listOf(1))
        val fourthRouteResult = RouteResult("R4", "dummy", "dummy", listOf(1))
        val routeResults =
            listOf(firstRouteResult, secondRouteResult, thirdRouteResult, fourthRouteResult)
        val resultWrapper = ResultWrapper(1, routeResults)
        val jsonRouteResults = Json.stringify(ResultWrapper.serializer(), resultWrapper)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn(jsonRouteResults)

        viewModel.retrieveRouteResults()

        assertThat(null, `is`(viewModel.routeResults))
    }

    @Test
    fun `should get all categories`() {
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        val categoriesLiveData = MediatorLiveData<List<QueryingCategory>>()
        val categories = mutableListOf<QueryingCategory>()
        categories.add(
            mockQueryingCategory()
        )
        categoriesLiveData.value = categories
        `when`(repository.getAllCategories()).thenReturn(categoriesLiveData)

        val result = routeResultViewModel.retrieveAllCategories()

        verify(repository).getAllCategories()
        assertThat(result, `is`(categoriesLiveData as LiveData<List<QueryingCategory>>))
    }

    @Test
    fun `should call mapper as many times as querying categories exists`() {
        val queryingCategories = listOf(mockQueryingCategory(), mockQueryingCategory())
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        `when`(mapper.mapFromModel(any())).thenReturn(Category(1, ""))

        routeResultViewModel.mapCategories(queryingCategories)

        verify(
            mapper,
            times(queryingCategories.size)
        ).mapFromModel(any())
        assertThat(routeResultViewModel.categories.size, `is`(queryingCategories.size))
    }

    @Test
    fun `should get a category when category id exists`() {
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        val categoryId = 1
        val category = Category(categoryId, "dummy")
        val queryingCategories = listOf(mockQueryingCategory())
        `when`(mapper.mapFromModel(any())).thenReturn(category)
        routeResultViewModel.mapCategories(queryingCategories)

        `when`(mapper.mapFromModel(any())).thenReturn(category)

        val resultCategory = routeResultViewModel.getCategoryById(categoryId)

        assertThat(resultCategory, `is`(category))
    }

    @Test
    fun `should return null when category id does not exist`() {
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        val categoryIdNotFound = 2
        val category = Category(1, "dummy")
        val queryingCategories = listOf(mockQueryingCategory())
        `when`(mapper.mapFromModel(any())).thenReturn(category)
        routeResultViewModel.mapCategories(queryingCategories)

        `when`(mapper.mapFromModel(any())).thenReturn(category)

        val resultCategory = routeResultViewModel.getCategoryById(categoryIdNotFound)

        assertThat(null, `is`(resultCategory))
    }

    @Test
    fun `should get questionnaire from sharedPreferences`() {
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )

        routeResultViewModel.loadQuestionnaire()

        verify(sharedPreferences).getString(Constants.QUESTIONNAIRE_ROUTE, "")
    }

    @Test
    fun `should get violence questionnaire `() {
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        val searchType = "VIOLENCE"
        val savedQuestionnaire = "{\n" +
                "  \"version\": 1,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"title\": \"BAJO\",\n" +
                "      \"code\": \"BAJO\",\n" +
                "      \"type\": \"$searchType\",\n" +
                "      \"sections\": [\n" +
                "        {\n" +
                "          \"id\": 1,\n" +
                "          \"title\": \"¿QUÉ PUEDES HACER?\",\n" +
                "          \"steps\": [\n" +
                "            {\n" +
                "              \"step_id\": 1,\n" +
                "              \"description\": \"Puedes obtener " +
                "una asesoría gratuita llamando al:" +
                " 151 Defensoría Pública.\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"step_id\": 2,\n" +
                "              \"description\": \"Puedes hacer una denuncia: Unidad Judicial de " +
                "Violencia contra la Mujer y la Familia de la Fiscalía 1800 266 822 . " +
                "Ministerio del Trabajo\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"step_id\": null,\n" +
                "              \"description\": \"Link a denuncia en la Fiscalía\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }]}"
        `when`(sharedPreferences.getString(Constants.QUESTIONNAIRE_ROUTE, ""))
            .thenReturn(savedQuestionnaire)
        routeResultViewModel.loadQuestionnaire()
        val expectedQuestionnaire = Json.parse(
            QuestionnaireRouteResultWrapper.serializer(),
            savedQuestionnaire
        )

        val questionnaire = routeResultViewModel.getQuestionnairesByType(searchType)

        assertThat(questionnaire, `is`(listOf(expectedQuestionnaire.results[0])))
    }

    @Test
    fun `should get questionnaire by code and type`() {
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        val searchType = "VIOLENCE"
        val code = "BAJO"
        val savedQuestionnaire = "{\n" +
                "  \"version\": 1,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"title\": \"BAJO\",\n" +
                "      \"code\": \"$code\",\n" +
                "      \"type\": \"$searchType\",\n" +
                "      \"sections\": [\n" +
                "        {\n" +
                "          \"id\": 1,\n" +
                "          \"title\": \"¿QUÉ PUEDES HACER?\",\n" +
                "          \"steps\": [\n" +
                "            {\n" +
                "              \"step_id\": 1,\n" +
                "              \"description\": \"Puedes obtener " +
                "una asesoría gratuita llamando al:" +
                " 151 Defensoría Pública.\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"step_id\": 2,\n" +
                "              \"description\": \"Puedes hacer una denuncia: Unidad Judicial de " +
                "Violencia contra la Mujer y la Familia de la Fiscalía 1800 266 822 . " +
                "Ministerio del Trabajo\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"step_id\": null,\n" +
                "              \"description\": \"Link a denuncia en la Fiscalía\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }]}"
        `when`(sharedPreferences.getString(Constants.QUESTIONNAIRE_ROUTE, ""))
            .thenReturn(savedQuestionnaire)
        routeResultViewModel.loadQuestionnaire()
        val expectedQuestionnaire = Json.parse(
            QuestionnaireRouteResultWrapper.serializer(),
            savedQuestionnaire
        )

        val questionnaire = routeResultViewModel.getQuestionnairesByTypeAndCode(searchType, code)

        assertThat(questionnaire, `is`(listOf(expectedQuestionnaire.results[0])))
    }

    @Test
    fun `should return null when questionnaire by code and type is not found`() {
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        val searchType = "VIOLENCE"
        val code = "BAJO"
        val savedQuestionnaire = "{\n" +
                "  \"version\": 1,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"title\": \"BAJO\",\n" +
                "      \"code\": \"$code\",\n" +
                "      \"type\": \"$searchType\",\n" +
                "      \"sections\": [\n" +
                "        {\n" +
                "          \"id\": 1,\n" +
                "          \"title\": \"¿QUÉ PUEDES HACER?\",\n" +
                "          \"steps\": [\n" +
                "            {\n" +
                "              \"step_id\": 1,\n" +
                "              \"description\": \"Puedes obtener " +
                "una asesoría gratuita llamando al:" +
                " 151 Defensoría Pública.\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"step_id\": 2,\n" +
                "              \"description\": \"Puedes hacer una denuncia: Unidad Judicial de " +
                "Violencia contra la Mujer y la Familia de la Fiscalía 1800 266 822 . " +
                "Ministerio del Trabajo\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"step_id\": null,\n" +
                "              \"description\": \"Link a denuncia en la Fiscalía\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }]}"
        `when`(sharedPreferences.getString(Constants.QUESTIONNAIRE_ROUTE, ""))
            .thenReturn(savedQuestionnaire)
        routeResultViewModel.loadQuestionnaire()

        val questionnaire = routeResultViewModel.getQuestionnairesByTypeAndCode(searchType, "dummy")

        assertThat(null, `is`(questionnaire))
    }

    @Test
    fun `should get HIGH when is present in violence route result`() {
        val result = "ALTO"

        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        `when`(sharedPreferences.getString(Constants.COMPLETE_VIOLENCE_ROUTE, ""))
            .thenReturn(result)

        val resultLevel = routeResultViewModel.getHigherViolenceLevel()

        assertThat(resultLevel, `is`(result))
    }

    @Test
    fun `should get HIGH when is present in violence route result and more than one level is saved`() {
        val high = "ALTO"
        val medium = "MEDIO"
        val result = "$high $medium"

        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        `when`(sharedPreferences.getString(Constants.COMPLETE_VIOLENCE_ROUTE, ""))
            .thenReturn(result)

        val resultLevel = routeResultViewModel.getHigherViolenceLevel()

        assertThat(resultLevel, `is`(high))
    }

    @Test
    fun `should get MEDIUM when is present in violence route result alto is not present`() {
        val medium = "MEDIO"

        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        `when`(sharedPreferences.getString(Constants.COMPLETE_VIOLENCE_ROUTE, ""))
            .thenReturn(medium)

        val resultLevel = routeResultViewModel.getHigherViolenceLevel()

        assertThat(resultLevel, `is`(medium))
    }

    @Test
    fun `should get MEDIUM when is present in violence route result alto is not present and low is present`() {
        val medium = "MEDIO"
        val low = "BAJO"

        val result = "$medium $low"
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        `when`(sharedPreferences.getString(Constants.COMPLETE_VIOLENCE_ROUTE, ""))
            .thenReturn(result)

        val resultLevel = routeResultViewModel.getHigherViolenceLevel()

        assertThat(resultLevel, `is`(medium))
    }

    @Test
    fun `should get LOW when is present in violence route result high is not present and medium is not present`() {
        val low = "BAJO"

        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        `when`(sharedPreferences.getString(Constants.COMPLETE_VIOLENCE_ROUTE, ""))
            .thenReturn(low)

        val resultLevel = routeResultViewModel.getHigherViolenceLevel()

        assertThat(resultLevel, `is`(low))
    }

    @Test
    fun `should get HIGH when is present in violence route result medium is present and low is present`() {
        val low = "BAJO"
        val medium = "MEDIO"
        val high = "ALTO"
        val result = "$medium $high $low"

        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        `when`(sharedPreferences.getString(Constants.COMPLETE_VIOLENCE_ROUTE, ""))
            .thenReturn(result)

        val resultLevel = routeResultViewModel.getHigherViolenceLevel()

        assertThat(resultLevel, `is`(high))
    }
}
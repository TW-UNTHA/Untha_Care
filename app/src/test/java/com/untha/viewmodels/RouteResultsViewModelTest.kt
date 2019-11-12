package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
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
import com.utils.MockObjects
import com.utils.MockObjects.mockLifecycleOwner
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

        verify(sharedPreferences).getString(Constants.FAULT_ANSWER, "")
    }

    @Test
    fun `should get route results from shared preferences when route result ids are not null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        val answer = "R1 R2 R3"
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER, "")).thenReturn(answer)

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.ROUTE_RESULT, "")
    }

    @Test
    fun `should not get route results when route result ids are empty`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn("")

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.FAULT_ANSWER, "")
        verify(sharedPreferences, never()).getString(Constants.ROUTE_RESULT, "")
    }

    @Test
    fun `should not get route results when route result ids are null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn(null)

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.FAULT_ANSWER, "")
        verify(sharedPreferences, never()).getString(Constants.ROUTE_RESULT, "")
    }

    @Test
    fun `should return null when route results are null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        val answer = "R1 R2 R3"
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER, "")).thenReturn(answer)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn(null)

        viewModel.retrieveRouteResults()

        assertThat(null, `is`(viewModel.routeResults))
    }

    @Test
    fun `should return a list of route results when route results ids and route results are not null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        val answer = "R1 R2 R3"
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER, "")).thenReturn(answer)
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
    fun `should return null when any route result id match with route results`() {
        val viewModel = RouteResultsViewModel(sharedPreferences, repository, mapper)
        val answer = "R7 R5 R6"
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER, "")).thenReturn(answer)
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
    fun `should get a categories list`() {
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        val mockLifeCycleOwner = mockLifecycleOwner()
        val categoriesLiveData = MediatorLiveData<List<QueryingCategory>>()
        val categories = mutableListOf<QueryingCategory>()
        categories.add(
            MockObjects.mockQueryingCategory()
        )
        categoriesLiveData.value = categories
        val observer = mock<Observer<List<QueryingCategory>>>()
        `when`(repository.getAllCategories()).thenReturn(categoriesLiveData)
        repository.getAllCategories().observeForever(observer)
        val category = Category(1, "dummy")
        `when`(mapper.mapFromModel(categories[0])).thenReturn(category)

        routeResultViewModel.retrieveAllCategories(mockLifeCycleOwner)

        verify(observer).onChanged(categories)
        verify(mapper).mapFromModel(categories[0])
        assertThat(routeResultViewModel.categories, `is`(listOf(category)))
    }

    @Test
    fun `should get a category when category id exists`() {
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        val mockLifeCycleOwner = mockLifecycleOwner()
        val categoriesLiveData = MediatorLiveData<List<QueryingCategory>>()
        val categories = mutableListOf<QueryingCategory>()
        categories.add(
            MockObjects.mockQueryingCategory()
        )
        categoriesLiveData.value = categories
        val observer = mock<Observer<List<QueryingCategory>>>()
        `when`(repository.getAllCategories()).thenReturn(categoriesLiveData)
        repository.getAllCategories().observeForever(observer)
        val categoryId = 1
        val category = Category(categoryId, "dummy")
        `when`(mapper.mapFromModel(categories[0])).thenReturn(category)
        routeResultViewModel.retrieveAllCategories(mockLifeCycleOwner)

        val resultCategory = routeResultViewModel.getCategoryById(categoryId)

        assertThat(category, `is`(resultCategory))
    }

    @Test
    fun `should return null when category id does not exist`() {
        val routeResultViewModel = RouteResultsViewModel(
            sharedPreferences,
            repository, mapper
        )
        val mockLifeCycleOwner = mockLifecycleOwner()
        val categoriesLiveData = MediatorLiveData<List<QueryingCategory>>()
        val categories = mutableListOf<QueryingCategory>()
        categories.add(
            MockObjects.mockQueryingCategory()
        )
        categoriesLiveData.value = categories
        val observer = mock<Observer<List<QueryingCategory>>>()
        `when`(repository.getAllCategories()).thenReturn(categoriesLiveData)
        repository.getAllCategories().observeForever(observer)
        val categoryId = 1
        val nonExistentCategoryId = 2
        val category = Category(categoryId, "dummy")
        `when`(mapper.mapFromModel(categories[0])).thenReturn(category)
        routeResultViewModel.retrieveAllCategories(mockLifeCycleOwner)

        val resultCategory = routeResultViewModel.getCategoryById(nonExistentCategoryId)

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

}
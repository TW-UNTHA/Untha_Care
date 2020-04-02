package com.untha.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import com.untha.R
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.dbservices.CategoryDbService
import com.untha.model.mappers.CategoryMapper
import com.untha.model.services.CategoriesService
import com.untha.model.services.ConstantsService
import com.untha.model.services.NewsService
import com.untha.model.services.QuestionnaireRouteResultService
import com.untha.model.services.ResultService
import com.untha.model.services.RoutesService
import com.untha.model.transactionalmodels.CategoriesWrapper
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.ConstantsWrapper
import com.untha.model.transactionalmodels.NewsWrapper
import com.untha.model.transactionalmodels.QuestionnaireRouteResultWrapper
import com.untha.model.transactionalmodels.ResultCalculatorWrapper
import com.untha.model.transactionalmodels.ResultWrapper
import com.untha.model.transactionalmodels.Route
import com.untha.utils.Constants
import com.utils.MockObjects
import kotlinx.serialization.json.Json
import me.linshen.retrofit2.adapter.ApiResponse
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.anko.db.REAL
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
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.Response
import java.io.ByteArrayInputStream


@RunWith(JUnit4::class)
class MainViewModelTest : KoinTest {

    private val dbService by inject<CategoryDbService>()
    private val categoriesService by inject<CategoriesService>()
    private val routesService by inject<RoutesService>()
    private val resultService by inject<ResultService>()
    private val questionnaireRouteResultService by inject<QuestionnaireRouteResultService>()
    private val mapper by inject<CategoryMapper>()
    private val sharedPreferences by inject<SharedPreferences>()
    private val constantsService by inject<ConstantsService>()
    private val newsService by inject<NewsService>()

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
        declareMock<CategoryDbService>()
        declareMock<CategoriesService>()
        declareMock<CategoryMapper>()
        declareMock<SharedPreferences>()
        declareMock<RoutesService>()
        declareMock<ResultService>()
        declareMock<QuestionnaireRouteResultService>()
        declareMock<ConstantsService>()
        declareMock<NewsService>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should call transform method to get a list of Category Models`() {
        val mockLifeCycleOwner = mockLifecycleOwner()
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "mis derechos descripcion next step",
                    "imagen",
                    0,
                    null,
                    null
                )
            )
        )
        var response = Response.success(categoryWrapper)
        var apiResponse = ApiResponse.create(response)
        val updatedCategories = MutableLiveData<ApiResponse<CategoriesWrapper>>()
        updatedCategories.value = apiResponse
        `when`(categoriesService.getCategories()).thenReturn(updatedCategories)

        val observer = mock<Observer<ApiResponse<CategoriesWrapper>>>()
        categoriesService.getCategories().observeForever(observer)

        mainViewModel.retrieveUpdatedCategories(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(mapper).mapToModel(categoryWrapper.categories[0])
    }

    @Test
    fun `should call mapFromModel`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        val queryingCategory = MockObjects.mockQueryingCategory()
        var categoryResult =
            Category(0, "", parentId = 1)
        `when`(mapper.mapFromModel(queryingCategory)).thenReturn(categoryResult)
        val categories: List<Category> = mainViewModel.getCategories(listOf(queryingCategory))

        verify(mapper).mapFromModel(queryingCategory)

        assertThat(categories, `is`(listOf(categoryResult)))
    }

    @Test
    fun `should call method getInt from SharedPreferences when API response is success`() {
        val mockLifeCycleOwner = mockLifecycleOwner()
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "mis derechos descripcion next step",
                    "imagen",
                    0,
                    null,
                    null
                )
            )
        )
        var response = Response.success(categoryWrapper)
        var apiResponse = ApiResponse.create(response)
        val updatedCategories = MutableLiveData<ApiResponse<CategoriesWrapper>>()
        updatedCategories.value = apiResponse
        `when`(categoriesService.getCategories()).thenReturn(updatedCategories)

        val observer = mock<Observer<ApiResponse<CategoriesWrapper>>>()
        categoriesService.getCategories().observeForever(observer)

        mainViewModel.retrieveUpdatedCategories(mockLifeCycleOwner)

        verify(sharedPreferences).getInt(Constants.CATEGORIES_VERSION, 0)
    }

    @Test
    fun `should not call mapper when version number is equal to  api response version number`() {
        val mockLifeCycleOwner = mockLifecycleOwner()
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "mis derechos descripcion next step",
                    "imagen",
                    0,
                    null,
                    null
                )
            )
        )
        var response = Response.success(categoryWrapper)
        var apiResponse = ApiResponse.create(response)
        val updatedCategories = MutableLiveData<ApiResponse<CategoriesWrapper>>()
        updatedCategories.value = apiResponse
        `when`(categoriesService.getCategories()).thenReturn(updatedCategories)
        `when`(
            sharedPreferences.getInt(
                Constants.CATEGORIES_VERSION,
                0
            )
        ).thenReturn(categoryWrapper.version)

        val observer = mock<Observer<ApiResponse<CategoriesWrapper>>>()
        categoriesService.getCategories().observeForever(observer)

        mainViewModel.retrieveUpdatedCategories(mockLifeCycleOwner)

        verifyZeroInteractions(mapper)
    }

    @Test
    fun `should call category mapper when version number is not equal to  api response version number`() {
        val mockLifeCycleOwner = mockLifecycleOwner()
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "mis derechos descripcion next step",
                    "imagen",
                    0,
                    "route",
                    null
                )
            )
        )
        var response = Response.success(categoryWrapper)
        var apiResponse = ApiResponse.create(response)
        val updatedCategories = MutableLiveData<ApiResponse<CategoriesWrapper>>()
        updatedCategories.value = apiResponse
        `when`(categoriesService.getCategories()).thenReturn(updatedCategories)
        `when`(
            sharedPreferences.getInt(
                Constants.CATEGORIES_VERSION,
                0
            )
        ).thenReturn(0)

        val observer = mock<Observer<ApiResponse<CategoriesWrapper>>>()
        categoriesService.getCategories().observeForever(observer)

        mainViewModel.retrieveUpdatedCategories(mockLifeCycleOwner)

        verify(mapper).mapToModel(categoryWrapper.categories.first())
    }

    @Test
    fun `should save data in share preferences `() {

        val mockLifeCycleOwner = mockLifecycleOwner()

        val route = Route(1, listOf())
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        var response = Response.success(route)
        var apiResponse = ApiResponse.create(response)
        val updatedRoute = MutableLiveData<ApiResponse<Route>>()
        updatedRoute.value = apiResponse
        `when`(routesService.getLabourRoute()).thenReturn(updatedRoute)
        val observer = mock<Observer<ApiResponse<Route>>>()
        routesService.getLabourRoute().observeForever(observer)
        val editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.LABOUR_ROUTE,
                Json.stringify(Route.serializer(), route)
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadLabourRoute(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(sharedPreferences.edit())
            .putString(
                Constants.LABOUR_ROUTE,
                Json.stringify(
                    Route.serializer(),
                    route
                )
            )
        verify(editor).apply()

    }

    @Test
    fun `should save data in share preferences when violence route result is successful `() {

        val mockLifeCycleOwner = mockLifecycleOwner()

        val route = Route(1, listOf())
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        val response = Response.success(route)
        val apiResponse = ApiResponse.create(response)
        val updatedRoute = MutableLiveData<ApiResponse<Route>>()
        updatedRoute.value = apiResponse
        `when`(routesService.getViolenceRoute()).thenReturn(updatedRoute)
        val observer = mock<Observer<ApiResponse<Route>>>()
        routesService.getViolenceRoute().observeForever(observer)
        val editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.VIOLENCE_ROUTE,
                Json.stringify(Route.serializer(), route)
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadViolenceRoute(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(sharedPreferences.edit())
            .putString(
                Constants.VIOLENCE_ROUTE,
                Json.stringify(
                    Route.serializer(),
                    route
                )
            )
        verify(editor).apply()
    }

    @Test
    fun `should save data in share preferences when result is successful `() {

        val mockLifeCycleOwner = mockLifecycleOwner()

        val result = ResultWrapper(1, listOf())
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        val response = Response.success(result)
        val apiResponse = ApiResponse.create(response)
        val updatedResult = MutableLiveData<ApiResponse<ResultWrapper>>()
        updatedResult.value = apiResponse
        `when`(resultService.getResult()).thenReturn(updatedResult)
        val observer = mock<Observer<ApiResponse<ResultWrapper>>>()
        resultService.getResult().observeForever(observer)
        val editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.ROUTE_RESULT,
                Json.stringify(ResultWrapper.serializer(), result)
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadRouteResults(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(sharedPreferences.edit())
            .putString(
                Constants.ROUTE_RESULT,
                Json.stringify(
                    ResultWrapper.serializer(),
                    result
                )
            )
        verify(editor).apply()
    }

    @Test
    fun `should save labour default route to sharedPreferences `() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )
        val context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        val json = "{\n" +
                "  \"version\": 1,\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"id\": 0,\n" +
                "      \"type\": \"SingleOption\",\n" +
                "      \"content\": \"¿Actualmente trabajas como Trabajadora Remunerada del Hogar?\",\n" +
                "      \"explanation\": \"\",\n" +
                "      \"goTo\": null,\n" +
                "      \"result\": null,\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"value\": \"Si\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 1\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"No\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 18\n" +
                "        }\n" +
                "      ]\n" +
                "    }}"
        val inputStream = ByteArrayInputStream(json.toByteArray())
        `when`(context.resources).thenReturn(resources)
        `when`(resources.openRawResource(R.raw.labour_route)).thenReturn(inputStream)
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.LABOUR_ROUTE,
                json
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadDefaultBase(context, Constants.LABOUR_ROUTE, R.raw.labour_route)

        verify(sharedPreferences.edit())
            .putString(
                Constants.LABOUR_ROUTE,
                json
            )
        verify(editor).apply()
    }

    @Test
    fun `should save result default  to sharedPreferences `() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )
        val context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        val json = "{\n" +
                "  \"version\": \"1\",\n" +
                "  \"routeResults\": [\n" +
                "    {\n" +
                "      \"id\": 0,\n" +
                "      \"type\": \"recommendation\",\n" +
                "      \"identifier\": \"R1\",\n" +
                "      \"content\": \"Al ser menor de edad, te recomendamos comunicarte con UNTHA para que te asesore sobre tus derechos laborales\",\n" +
                "      \"categories\": [\n" +
                "        {\n" +
                "          \"id\": 7\n" +
                "        }\n" +
                "      ]\n" +
                "    }" +
                "}"
        val inputStream = ByteArrayInputStream(json.toByteArray())
        `when`(context.resources).thenReturn(resources)
        `when`(resources.openRawResource(R.raw.result)).thenReturn(inputStream)
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.ROUTE_RESULT,
                json
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadDefaultBase(context, Constants.ROUTE_RESULT, R.raw.result)

        verify(sharedPreferences.edit())
            .putString(
                Constants.ROUTE_RESULT,
                json
            )
        verify(editor).apply()
    }


    @Test
    fun `should save violence default route to sharedPreferences`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )
        val context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        val json = "{\n" +
                "  \"version\": 1,\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"id\": 0,\n" +
                "      \"type\": \"SingleOption\",\n" +
                "      \"content\": \"¿Actualmente trabajas como Trabajadora Remunerada del Hogar?\",\n" +
                "      \"explanation\": \"\",\n" +
                "      \"goTo\": null,\n" +
                "      \"result\": null,\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"value\": \"Si\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 1\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"No\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 18\n" +
                "        }\n" +
                "      ]\n" +
                "    }}"
        val inputStream = ByteArrayInputStream(json.toByteArray())
        `when`(context.resources).thenReturn(resources)
        `when`(resources.openRawResource(R.raw.violence_route)).thenReturn(inputStream)
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.VIOLENCE_ROUTE,
                json
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadDefaultBase(context, Constants.VIOLENCE_ROUTE, R.raw.violence_route)

        verify(sharedPreferences.edit())
            .putString(
                Constants.VIOLENCE_ROUTE,
                json
            )
        verify(editor).apply()
    }


    private fun mockLifecycleOwner(): LifecycleOwner {
        val owner = mock<LifecycleOwner>()
        val lifecycle = LifecycleRegistry(owner)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        `when`<Lifecycle>(owner.lifecycle).thenReturn(lifecycle)
        return owner
    }

    @Test
    fun `should save questionnaire routes default to sharedPreferences`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )
        val context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        val json = "{\n" +
                "  \"version\": 1,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"title\": \"BAJO\",\n" +
                "      \"result\": \"BAJO\",\n" +
                "      \"type\": \"VIOLENCE\",\n" +
                "      \"sections\": [\n" +
                "        {\n" +
                "          \"id\": 1,\n" +
                "          \"title\": \"¿QUÉ PUEDES HACER?\",\n" +
                "          \"steps\": [\n" +
                "            {\n" +
                "              \"step_id\": 1,\n" +
                "              \"description\": \"Puedes obtener una asesoría gratuita llamando al: 151 Defensoría Pública.\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"step_id\": 2,\n" +
                "              \"description\": \"Puedes hacer una denuncia: Unidad Judicial de Violencia contra la Mujer y la Familia de la Fiscalía 1800 266 822 . Ministerio del Trabajo\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"step_id\": null,\n" +
                "              \"description\": \"Link a denuncia en la Fiscalía\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }]}"
        val inputStream = ByteArrayInputStream(json.toByteArray())
        `when`(context.resources).thenReturn(resources)
        `when`(resources.openRawResource(R.raw.questionnaire_route_result)).thenReturn(inputStream)
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.QUESTIONNAIRE_ROUTE,
                json
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadDefaultBase(
            context,
            Constants.QUESTIONNAIRE_ROUTE,
            R.raw.questionnaire_route_result
        )

        verify(sharedPreferences.edit())
            .putString(
                Constants.QUESTIONNAIRE_ROUTE,
                json
            )
        verify(editor).apply()
    }

    @Test
    fun `should save data questionnaire routes in share preferences when result is successful `() {

        val mockLifeCycleOwner = mockLifecycleOwner()

        val result = QuestionnaireRouteResultWrapper(1, listOf())
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        val response = Response.success(result)
        val apiResponse = ApiResponse.create(response)
        val updatedResult = MutableLiveData<ApiResponse<QuestionnaireRouteResultWrapper>>()
        updatedResult.value = apiResponse
        `when`(questionnaireRouteResultService.getQuestionnaireRouteResult()).thenReturn(
            updatedResult
        )
        val observer = mock<Observer<ApiResponse<QuestionnaireRouteResultWrapper>>>()
        questionnaireRouteResultService.getQuestionnaireRouteResult().observeForever(observer)
        val editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.QUESTIONNAIRE_ROUTE,
                Json.stringify(QuestionnaireRouteResultWrapper.serializer(), result)
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadQuestionnaireRouteResult(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(sharedPreferences.edit())
            .putString(
                Constants.QUESTIONNAIRE_ROUTE,
                Json.stringify(
                    QuestionnaireRouteResultWrapper.serializer(),
                    result
                )
            )
        verify(editor).apply()
    }

   @Test
    fun `should save on db when loading default categories` () {
        val context = mock(Context::class.java)

        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        val resources = mock(Resources::class.java)
        val json = "{\"version\": 3,\n" +
                "  \"categories\":[{\n" +
                "      \"id\": 18,\n" +
                "      \"image\": \"home_beneficios\",\n" +
                "      \"title\": \"CALCULADORA DE HORAS\",\n" +
                "      \"subtitle\": \"Lorem ipsum dolor sit amet consectetur adipisicing elit. Nam illum, iure, asperiores natus placeat, eaque voluptatibus reprehenderit aspernatur facere non expedita vel numquam dignissimos! Possimus autem eos voluptatum voluptatem corporis.\",\n" +
                "      \"title_next_step\": \"CALCULADORA DE HORAS\",\n" +
                "      \"parent_id\": null,\n" +
                "      \"type\": \"calculator\"\n" +
                "    }]}"
        val inputStream = ByteArrayInputStream(json.toByteArray())
        `when`(context.resources).thenReturn(resources)
        `when`(context.resources.getIdentifier("com.untha:raw/categories_version_2",null, null)).thenReturn(R.raw.categories_version_2)
        `when`(resources.openRawResource(R.raw.categories_version_2)).thenReturn(inputStream)

        mainViewModel.loadDefaultCategories(context)

        verify(dbService, times(1)).saveCategory(anyList(), any())

    }

    @Test
    fun `should save category information on DB`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        val testMethod =
            mainViewModel.javaClass.getDeclaredMethod("categoriesSavedCallback", String::class.java)
        testMethod.isAccessible = true

        testMethod.invoke(mainViewModel, "")

        verify(dbService, times(1)).saveCategoryInformation(anyList(), any())
    }

    @Test
    fun `should save categories sections`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        val methodToTest = mainViewModel.javaClass.getDeclaredMethod(
            "categoriesInformationSaved",
            String::class.java
        )
        methodToTest.isAccessible = true

        methodToTest.invoke(mainViewModel, "")

        verify(dbService, times(1)).saveSections(anyList(), any())
    }

    @Test
    fun `should save category sections `() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        val methodToTest =
            mainViewModel.javaClass.getDeclaredMethod("categoriesSectionsSaved", String::class.java)
        methodToTest.isAccessible = true

        methodToTest.invoke(mainViewModel, "")

        verify(dbService, times(1)).saveSectionSteps(anyList(), any())

    }

    @Test
    fun `should save categories sections steps`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        val methodToTest = mainViewModel.javaClass.getDeclaredMethod(
            "categoriesSectionsStepsSaved",
            String::class.java
        )
        methodToTest.isAccessible = true

        val editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putInt(
                Constants.CATEGORIES_VERSION,
                0
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        methodToTest.invoke(mainViewModel, "")

        verify(sharedPreferences.edit())
            .putInt(
                Constants.CATEGORIES_VERSION,
                0
            )

        verify(editor).apply()

    }
    @Test
    fun `should save route calculator in shared preferences`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )
        val context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        val json = "{\n" +
                "  \"version\": 1,\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"type\": \"MultipleOption\",\n" +
                "      \"content\": \"¿Te encuentras en alguna de éstas situaciones?\",\n" +
                "      \"explanation\": \"Necesitamos esta información para calcular tus indemnizaciones\",\n" +
                "      \"recommend\": \"*Para ser oficialmente sustituta de una persona con discapacidad, debes efectuar el proceso pertinente en el Ministerio de Trabajo\",\n" +
                "      \"goTo\": 2,\n" +
                "      \"result\": null,\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"value\": \"Discapacidad\",\n" +
                "          \"remaining\": 2,\n" +
                "          \"hint\": \"R3P1R1\",\n" +
                "          \"result\": \"F5\",\n" +
                "          \"goTo\": 2\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"A cargo de una persona con discapacidad*\",\n" +
                "          \"remaining\": 2,\n" +
                "          \"hint\": \"R3P1R2\",\n" +
                "          \"result\": \"F5\",\n" +
                "          \"goTo\": 2\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"Embarazo\",\n" +
                "          \"remaining\": 2,\n" +
                "          \"hint\": \"R3P1R3\",\n" +
                "          \"result\": \"F3\",\n" +
                "          \"goTo\": 2\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"Ninguna de las anteriores\",\n" +
                "          \"remaining\": 2,\n" +
                "          \"hint\": \"R3P1R4\",\n" +
                "          \"result\": \"R4\",\n" +
                "          \"goTo\": 2\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 2,\n" +
                "      \"type\": \"SingleOption\",\n" +
                "      \"content\": \"¿Cuál es el motivo para la terminación del contrato?\",\n" +
                "      \"explanation\": \"\",\n" +
                "      \"recommend\": \"En caso de presiones recibidas o dudas sobre los motivos de terminacion, puedes llamar al 1800-266822  o a la UNTHA (0960639550)\",\n" +
                "      \"goTo\": -1,\n" +
                "      \"result\": \"\",\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"value\": \"Renuncia voluntaria (también llamada desahucio)\",\n" +
                "          \"remaining\": 1,\n" +
                "          \"hint\": \"R3P2R1\",\n" +
                "          \"result\": \"R1\",\n" +
                "          \"goTo\": -1\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"Terminación durante el período de prueba (15 días)\",\n" +
                "          \"remaining\": 1,\n" +
                "          \"hint\": \"R3P2R2\",\n" +
                "          \"result\": \"R2\",\n" +
                "          \"goTo\": -1\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"Muerte o incapacidad TRABAJADOR/A\",\n" +
                "          \"remaining\": 1,\n" +
                "          \"hint\": \"R3P2R3\",\n" +
                "          \"result\": \"R3\",\n" +
                "          \"goTo\": -1\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"Muerte o incapacidad EMPLEADOR/A\",\n" +
                "          \"remaining\": 1,\n" +
                "          \"hint\": \"R3P2R4\",\n" +
                "          \"result\": \"R5\",\n" +
                "          \"goTo\": -1\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"Despido intempestivo (Sin previo aviso)\",\n" +
                "          \"remaining\": 1,\n" +
                "          \"hint\": \"R3P2R5\",\n" +
                "          \"result\": \"F4\",\n" +
                "          \"goTo\": -1\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}"
        val inputStream = ByteArrayInputStream(json.toByteArray())
        `when`(context.resources).thenReturn(resources)
        `when`(resources.openRawResource(R.raw.calculator_route)).thenReturn(inputStream)
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.CALCULATOR_ROUTE,
                json
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadDefaultBase(
            context,
            Constants.CALCULATOR_ROUTE,
            R.raw.calculator_route
        )

        verify(sharedPreferences.edit())
            .putString(
                Constants.CALCULATOR_ROUTE,
                json
            )
        verify(editor).apply()
    }

    @Test
    fun `should route calculator docs share preferences `() {

        val mockLifeCycleOwner = mockLifecycleOwner()

        val route = Route(1, listOf())
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        var response = Response.success(route)
        var apiResponse = ApiResponse.create(response)
        val updatedRoute = MutableLiveData<ApiResponse<Route>>()
        updatedRoute.value = apiResponse
        `when`(routesService.getCalculatorRoute()).thenReturn(updatedRoute)
        val observer = mock<Observer<ApiResponse<Route>>>()
        routesService.getCalculatorRoute().observeForever(observer)
        val editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.CALCULATOR_ROUTE,
                Json.stringify(Route.serializer(), route)
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadCalculatorRoute(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(sharedPreferences.edit())
            .putString(
                Constants.CALCULATOR_ROUTE,
                Json.stringify(
                    Route.serializer(),
                    route
                )
            )
        verify(editor).apply()

    }

    @Test
    fun `should save result wrapper in share preferences `() {

        val mockLifeCycleOwner = mockLifecycleOwner()

        val route = ResultWrapper(1, listOf())
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        var response = Response.success(route)
        var apiResponse = ApiResponse.create(response)
        val updatedRoute = MutableLiveData<ApiResponse<ResultWrapper>>()
        updatedRoute.value = apiResponse
        `when`(resultService.getResultCalculator()).thenReturn(updatedRoute)
        val observer = mock<Observer<ApiResponse<ResultWrapper>>>()
        resultService.getResultCalculator().observeForever(observer)
        val editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.CALCULATOR_ROUTE_RESULT,
                Json.stringify(ResultWrapper.serializer(), route)
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.getResultCalculator(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(sharedPreferences.edit())
            .putString(
                Constants.CALCULATOR_ROUTE_RESULT,
                Json.stringify(
                    ResultWrapper.serializer(),
                    route
                )
            )
        verify(editor).apply()

    }

    @Test
    fun `should save result recommend in share preferences `() {

        val mockLifeCycleOwner = mockLifecycleOwner()

        val route = ResultCalculatorWrapper(1, listOf())
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        var response = Response.success(route)
        var apiResponse = ApiResponse.create(response)
        val updatedRoute = MutableLiveData<ApiResponse<ResultCalculatorWrapper>>()
        updatedRoute.value = apiResponse
        `when`(resultService.getRecommendCalculator()).thenReturn(updatedRoute)
        val observer = mock<Observer<ApiResponse<ResultCalculatorWrapper>>>()
        resultService.getRecommendCalculator().observeForever(observer)
        val editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.CALCULATOR_RECOMMEND,
                Json.stringify(ResultCalculatorWrapper.serializer(), route)
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.getRecommendCalculator(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(sharedPreferences.edit())
            .putString(
                Constants.CALCULATOR_RECOMMEND,
                Json.stringify(
                    ResultCalculatorWrapper.serializer(),
                    route
                )
            )
        verify(editor).apply()

    }

    @Test
    fun `should save constants in share preferences `() {

        val mockLifeCycleOwner = mockLifecycleOwner()

        val route = ConstantsWrapper(1, "400", "0.0945", "0.0833", 40)
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        var response = Response.success(route)
        var apiResponse = ApiResponse.create(response)
        val updatedRoute = MutableLiveData<ApiResponse<ConstantsWrapper>>()
        updatedRoute.value = apiResponse
        `when`(constantsService.getConstants()).thenReturn(updatedRoute)
        val observer = mock<Observer<ApiResponse<ConstantsWrapper>>>()
        constantsService.getConstants().observeForever(observer)
        val editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.CONSTANTS,
                Json.stringify(ConstantsWrapper.serializer(), route)
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.getConstants(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(sharedPreferences.edit())
            .putString(
                Constants.CONSTANTS,
                Json.stringify(
                    ConstantsWrapper.serializer(),
                    route
                )
            )
        verify(editor).apply()

    }

    @Test
    fun `should save news in share preferences `() {

        val mockLifeCycleOwner = mockLifecycleOwner()

        val route = NewsWrapper(1, true, "title", "subtitle", listOf())
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            sharedPreferences,
            routesService,
            resultService,
            questionnaireRouteResultService,
            constantsService,
            newsService
        )

        var response = Response.success(route)
        var apiResponse = ApiResponse.create(response)
        val updatedRoute = MutableLiveData<ApiResponse<NewsWrapper>>()
        updatedRoute.value = apiResponse
        `when`(newsService.getNews()).thenReturn(updatedRoute)
        val observer = mock<Observer<ApiResponse<NewsWrapper>>>()
        newsService.getNews().observeForever(observer)
        val editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.NEWS,
                Json.stringify(NewsWrapper.serializer(), route)
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.getNews(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(sharedPreferences.edit())
            .putString(
                Constants.NEWS,
                Json.stringify(
                    NewsWrapper.serializer(),
                    route
                )
            )
        verify(editor).apply()

    }
}



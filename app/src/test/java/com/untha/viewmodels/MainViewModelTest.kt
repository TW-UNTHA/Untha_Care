package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.dbservices.CategoryDbService
import com.untha.model.mappers.CategoryMapper
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.services.CategoriesService
import com.untha.model.services.RoutesService
import com.untha.model.transactionalmodels.CategoriesWrapper
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.Route
import com.untha.utils.Constants
import com.utils.MockObjects
import me.linshen.retrofit2.adapter.ApiResponse
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
import retrofit2.Response
import android.content.Context
import android.content.res.Resources
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.whenever
import com.untha.R
import com.untha.model.services.ResultService
import com.untha.model.transactionalmodels.Result
import com.untha.model.transactionalmodels.ResultWrapper
import kotlinx.serialization.json.Json
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream


@RunWith(JUnit4::class)
class MainViewModelTest : KoinTest {

    private val dbService by inject<CategoryDbService>()
    private val categoriesService by inject<CategoriesService>()
    private val routesService by inject<RoutesService>()
    private val resultService by inject<ResultService>()
    private val mapper by inject<CategoryMapper>()
    private val repository by inject<CategoryWithRelationsRepository>()
    private val sharedPreferences by inject<SharedPreferences>()

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
        declareMock<CategoryWithRelationsRepository>()
        declareMock<SharedPreferences>()
        declareMock<RoutesService>()
        declareMock<ResultService>()
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
            repository,
            sharedPreferences,
            routesService,
            resultService
        )
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "mis derechos descripcion next step",
                    "imagen",
                    false,
                    0,
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
    fun `should call get all categories`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            repository,
            sharedPreferences,
            routesService,
            resultService
        )

        mainViewModel.retrieveAllCategories()

        verify(repository).getAllCategories()
    }

    @Test
    fun `should call mapFromModel`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            repository,
            sharedPreferences,
            routesService,
            resultService
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
            repository,
            sharedPreferences,
            routesService,
            resultService
        )
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "mis derechos descripcion next step",
                    "imagen",
                    false,
                    0,
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
            repository,
            sharedPreferences,
            routesService,
            resultService
        )
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "mis derechos descripcion next step",
                    "imagen",
                    false,
                    0,
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
            repository,
            sharedPreferences,
            routesService,
            resultService
        )
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "mis derechos descripcion next step",
                    "imagen",
                    true,
                    0,
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
            repository,
            sharedPreferences,
            routesService,
            resultService
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
            repository,
            sharedPreferences,
            routesService,
            resultService
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
            repository,
            sharedPreferences,
            routesService,
            resultService
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
                Constants.RESULT,
                Json.stringify(ResultWrapper.serializer(), result)
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadResult(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(sharedPreferences.edit())
            .putString(
                Constants.RESULT,
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
            repository,
            sharedPreferences,
            routesService,
            resultService
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

        mainViewModel.loadDefaultLabourRoute(context)

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
            repository,
            sharedPreferences,
            routesService,
            resultService
        )
        val context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        val json = "{\n" +
                "  \"version\": \"1\",\n" +
                "  \"results\": [\n" +
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
                Constants.RESULT,
                json
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        mainViewModel.loadDefaultResult(context)

        verify(sharedPreferences.edit())
            .putString(
                Constants.RESULT,
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
            repository,
            sharedPreferences,
            routesService,
            resultService
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

        mainViewModel.loadDefaultViolenceRoute(context)

        verify(sharedPreferences.edit())
            .putString(
                Constants.VIOLENCE_ROUTE,
                json
            )
        verify(editor).apply()
    }

    @Test
    fun `should get violence route from shared preferences`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            repository,
            sharedPreferences,
            routesService,
            resultService
        )
        val jsonRoute = "{\n" +
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
                "    }]}"
        `when`(sharedPreferences.getString(Constants.VIOLENCE_ROUTE, null)).thenReturn(jsonRoute)
        val route = Json.parse(Route.serializer(), jsonRoute)

        val resultRoute = mainViewModel.loadViolenceRouteFromSharedPreferences()

        assertThat(resultRoute, `is`(route))
    }


    @Test
    fun `should get result from shared preferences`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            repository,
            sharedPreferences,
            routesService,
            resultService
        )
        val jsonResult = "{\n" +
                "  \"version\": 1,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": \"R1\",\n" +
                "      \"type\": \"recommendation\",\n" +
                "      \"content\": \"Al ser menor de edad, te recomendamos comunicarte con UNTHA para que te asesore sobre tus derechos laborales\",\n" +
                "      \"categories\":  [7]\n" +
                "    }]}"


        `when`(sharedPreferences.getString(Constants.RESULT, "N/A")).thenReturn(jsonResult)
        val result = Json.parse(ResultWrapper.serializer(), jsonResult)

        val resultFromSharePreferences = mainViewModel.loadResultFromSharedPreferences()

        assertThat(resultFromSharePreferences, `is`(result))
    }

    @Test
    fun `should return an empty violence route when shared preferences return null`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            repository,
            sharedPreferences,
            routesService,
            resultService
        )
        `when`(sharedPreferences.getString(Constants.VIOLENCE_ROUTE, null)).thenReturn(null)

        val resultRoute = mainViewModel.loadViolenceRouteFromSharedPreferences()

        assertThat(resultRoute, `is`(Route(0, listOf())))
    }

    @Test
    fun `should get labour route from shared preferences`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            repository,
            sharedPreferences,
            routesService,
            resultService
        )
        val jsonRoute = "{\n" +
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
                "    }]}"
        `when`(sharedPreferences.getString(Constants.LABOUR_ROUTE, null)).thenReturn(jsonRoute)
        val route = Json.parse(Route.serializer(), jsonRoute)

        val resultRoute = mainViewModel.loadLabourRouteFromSharedPreferences()

        assertThat(resultRoute, `is`(route))
    }

    @Test
    fun `should return an empty labour route when shared preferences return null`() {
        val mainViewModel = MainViewModel(
            dbService,
            categoriesService,
            mapper,
            repository,
            sharedPreferences,
            routesService,
            resultService
        )
        `when`(sharedPreferences.getString(Constants.LABOUR_ROUTE, null)).thenReturn(null)

        val resultRoute = mainViewModel.loadLabourRouteFromSharedPreferences()

        assertThat(resultRoute, `is`(Route(0, listOf())))
    }

    private fun mockLifecycleOwner(): LifecycleOwner {
        val owner = mock<LifecycleOwner>()
        val lifecycle = LifecycleRegistry(owner)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        `when`<Lifecycle>(owner.lifecycle).thenReturn(lifecycle)
        return owner
    }
}




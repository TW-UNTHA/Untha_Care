package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.dbservices.CategoryDbService
import com.untha.model.mappers.CategoryMapper
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.services.CategoriesService
import com.untha.model.services.RoutesService
import com.untha.model.transactionalmodels.Route
import com.untha.utils.Constants
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
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
import org.mockito.Mockito


@RunWith(JUnit4::class)
class RoutesViewModelTest : KoinTest {
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
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should return an empty violence route when shared preferences return null`() {
        val routesViewModel = RoutesViewModel(
            sharedPreferences

        )
        Mockito.`when`(sharedPreferences.getString(Constants.VIOLENCE_ROUTE, null)).thenReturn(null)

        val resultRoute = routesViewModel.loadViolenceRouteFromSharedPreferences()

        MatcherAssert.assertThat(resultRoute, CoreMatchers.`is`(Route(0, listOf())))
    }

    @Test
    fun `should get violence route from shared preferences`() {
        val routesViewModel = RoutesViewModel(
            sharedPreferences
        )
        val jsonRoute = "{\n" +
                "  \"version\": 1,\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"type\": \"SingleOption\",\n" +
                "      \"content\": \"¿Actualmente trabajas como Trabajadora Remunerada del" +
                " Hogar?\",\n" +
                "      \"explanation\": \"\",\n" +
                "      \"goTo\": null,\n" +
                "      \"result\": null,\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"value\": \"Si\",\n" +
                "          \"hint\": \"R1P1R1\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 2\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"No\",\n" +
                "          \"hint\": \"R1P1R2\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 19\n" +
                "        }\n" +
                "      ]\n" +
                "    }]}"
        Mockito.`when`(sharedPreferences.getString(Constants.VIOLENCE_ROUTE, null))
            .thenReturn(jsonRoute)
        val route = Json.parse(Route.serializer(), jsonRoute)

        val resultRoute = routesViewModel.loadViolenceRouteFromSharedPreferences()

        MatcherAssert.assertThat(resultRoute, CoreMatchers.`is`(route))
    }


    @Test
    fun `should get labour route from shared preferences`() {
        val routesViewModel = RoutesViewModel(
            sharedPreferences
        )
        val jsonRoute = "{\n" +
                "  \"version\": 1,\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"type\": \"SingleOption\",\n" +
                "      \"content\": \"¿Actualmente trabajas como Trabajadora Remunerada del" +
                " Hogar?\",\n" +
                "      \"explanation\": \"\",\n" +
                "      \"goTo\": null,\n" +
                "      \"result\": null,\n" +
                "      \"options\": [\n" +
                "        {\n" +
                "          \"value\": \"Si\",\n" +
                "          \"hint\": \"R1P1R1\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 2\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"No\",\n" +
                "          \"hint\": \"R1P1R2\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 19\n" +
                "        }\n" +
                "      ]\n" +
                "    }]}"
        Mockito.`when`(sharedPreferences.getString(Constants.LABOUR_ROUTE, null))
            .thenReturn(jsonRoute)
        val route = Json.parse(Route.serializer(), jsonRoute)

        val resultRoute = routesViewModel.loadLabourRouteFromSharedPreferences()

        MatcherAssert.assertThat(resultRoute, CoreMatchers.`is`(route))
    }

    @Test
    fun `should return an empty labour route when shared preferences return null`() {
        val routesViewModel = RoutesViewModel(
            sharedPreferences
        )
        Mockito.`when`(sharedPreferences.getString(Constants.LABOUR_ROUTE, null)).thenReturn(null)

        val resultRoute = routesViewModel.loadLabourRouteFromSharedPreferences()

        MatcherAssert.assertThat(resultRoute, CoreMatchers.`is`(Route(0, listOf())))
    }
}
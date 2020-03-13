package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
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
import com.untha.utils.Constants.LABOUR_ROUTE
import com.untha.utils.Constants.ROUTE_LABOUR
import com.untha.utils.Constants.ROUTE_VIOLENCE
import com.untha.utils.Constants.VIOLENCE_ROUTE
import kotlinx.serialization.json.Json
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
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

        val resultRoute = routesViewModel.loadRouteFromSharedPreferences(VIOLENCE_ROUTE)

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
                "          \"remaining\": 1,\n" +
                "          \"hint\": \"R1P1R1\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 2\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"No\",\n" +
                "          \"remaining\": 1,\n" +
                "          \"hint\": \"R1P1R2\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 19\n" +
                "        }\n" +
                "      ]\n" +
                "    }]}"
        Mockito.`when`(sharedPreferences.getString(VIOLENCE_ROUTE, null))
            .thenReturn(jsonRoute)
        val route = Json.parse(Route.serializer(), jsonRoute)

        val resultRoute = routesViewModel.loadRouteFromSharedPreferences(VIOLENCE_ROUTE)

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
                "          \"remaining\": 1,\n" +
                "          \"hint\": \"R1P1R1\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 2\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"No\",\n" +
                "          \"remaining\": 1,\n" +
                "          \"hint\": \"R1P1R2\",\n" +
                "          \"result\": null,\n" +
                "          \"goTo\": 19\n" +
                "        }\n" +
                "      ]\n" +
                "    }]}"
        Mockito.`when`(sharedPreferences.getString(LABOUR_ROUTE, null))
            .thenReturn(jsonRoute)
        val route = Json.parse(Route.serializer(), jsonRoute)

        val resultRoute = routesViewModel.loadRouteFromSharedPreferences(LABOUR_ROUTE)

        MatcherAssert.assertThat(resultRoute, CoreMatchers.`is`(route))
    }

    @Test
    fun `should return an empty labour route when shared preferences return null`() {
        val routesViewModel = RoutesViewModel(
            sharedPreferences
        )
        Mockito.`when`(sharedPreferences.getString(LABOUR_ROUTE, null)).thenReturn(null)

        val resultRoute = routesViewModel.loadRouteFromSharedPreferences(LABOUR_ROUTE)

        MatcherAssert.assertThat(resultRoute, CoreMatchers.`is`(Route(0, listOf())))
    }

    @Test
    fun `Should reset shared preferences when the route is Labour`() {

        val resultAnswersDefault = "F5 F9 F7 F6"
        val routesViewModel = RoutesViewModel(
            sharedPreferences
        )
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)
        Mockito.`when`(
            sharedPreferences.getString(
                Constants.FAULT_ANSWER_ROUTE_LABOUR,
                resultAnswersDefault
            )
        ).thenReturn(resultAnswersDefault)

        routesViewModel.loadResultFaultAnswersFromSharedPreferences(true)
        Mockito.`when`(
            sharedPreferences.edit().remove(
                Constants.FAULT_ANSWER_ROUTE_LABOUR
            )
        ).thenReturn(editor)
        routesViewModel.deleteAnswersOptionFromSharedPreferences(ROUTE_LABOUR)
        verify(sharedPreferences.edit()).remove(Constants.FAULT_ANSWER_ROUTE_LABOUR)
        verify(editor).apply()
    }

    @Test
    fun `should reset shared preferences when the route is Violence`() {

        val resultAnswersDefault = "F5 F9 F7 F6"
        val routesViewModel = RoutesViewModel(
            sharedPreferences
        )
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)
        Mockito.`when`(
            sharedPreferences.getString(
                Constants.FAULT_ANSWER_ROUTE_VIOLENCE,
                resultAnswersDefault
            )
        ).thenReturn(resultAnswersDefault)

        routesViewModel.loadResultFaultAnswersFromSharedPreferences(false)
        Mockito.`when`(
            sharedPreferences.edit().remove(
                Constants.FAULT_ANSWER_ROUTE_VIOLENCE
            )
        ).thenReturn(editor)
        routesViewModel.deleteAnswersOptionFromSharedPreferences(ROUTE_VIOLENCE)
        verify(sharedPreferences.edit()).remove(Constants.FAULT_ANSWER_ROUTE_VIOLENCE)
        verify(editor).apply()
    }

    @Test
    fun `should return false when no previous violence result is present`() {
        val routesViewModel = RoutesViewModel(
            sharedPreferences
        )
        Mockito.`when`(
            sharedPreferences.getString(
                Constants.COMPLETE_VIOLENCE_ROUTE,
                null
            )
        ).thenReturn(null)

        val isThereLastResult = routesViewModel.isThereLastResultForRoute(false)

        verify(sharedPreferences).getString(Constants.COMPLETE_VIOLENCE_ROUTE, null)
        assertThat(isThereLastResult, `is`(false))
    }

    @Test
    fun `should return true when previous violence result is present`() {
        val routesViewModel = RoutesViewModel(
            sharedPreferences
        )
        Mockito.`when`(
            sharedPreferences.getString(
                Constants.COMPLETE_VIOLENCE_ROUTE,
                null
            )
        ).thenReturn("")

        val isThereLastResult = routesViewModel.isThereLastResultForRoute(false)

        verify(sharedPreferences).getString(Constants.COMPLETE_VIOLENCE_ROUTE, null)
        assertThat(isThereLastResult, `is`(true))
    }

    @Test
    fun `should return true when previous labour result is present`() {
        val routesViewModel = RoutesViewModel(
            sharedPreferences
        )
        Mockito.`when`(
            sharedPreferences.getString(
                Constants.COMPLETE_LABOUR_ROUTE,
                null
            )
        ).thenReturn("")

        val isThereLastResult = routesViewModel.isThereLastResultForRoute(true)

        verify(sharedPreferences).getString(Constants.COMPLETE_LABOUR_ROUTE, null)
        verify(sharedPreferences, never()).getString(Constants.COMPLETE_VIOLENCE_ROUTE, null)
        assertThat(isThereLastResult, `is`(true))
    }

    @Test
    fun `should return false when previous labour result is present`() {
        val routesViewModel = RoutesViewModel(
            sharedPreferences
        )
        Mockito.`when`(
            sharedPreferences.getString(
                Constants.COMPLETE_LABOUR_ROUTE,
                null
            )
        ).thenReturn(null)

        val isThereLastResult = routesViewModel.isThereLastResultForRoute(true)

        verify(sharedPreferences).getString(Constants.COMPLETE_LABOUR_ROUTE, null)
        verify(sharedPreferences, never()).getString(Constants.COMPLETE_VIOLENCE_ROUTE, null)
        assertThat(isThereLastResult, `is`(false))
    }
}
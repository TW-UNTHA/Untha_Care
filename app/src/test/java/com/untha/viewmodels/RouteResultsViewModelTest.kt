package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.transactionalmodels.ResultWrapper
import com.untha.model.transactionalmodels.RouteResult
import com.untha.utils.Constants
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
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should get route results ids from shared preferences`() {
        val viewModel = RouteResultsViewModel(sharedPreferences)

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.FAULT_ANSWER, "")
    }

    @Test
    fun `should get route results from shared preferences when route result ids are not null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences)
        val answer = "R1 R2 R3"
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER, "")).thenReturn(answer)

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.ROUTE_RESULT, "")
    }

    @Test
    fun `should not get route results when route result ids are empty`() {
        val viewModel = RouteResultsViewModel(sharedPreferences)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn("")

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.FAULT_ANSWER, "")
        verify(sharedPreferences, never()).getString(Constants.ROUTE_RESULT, "")
    }

    @Test
    fun `should not get route results when route result ids are null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn(null)

        viewModel.retrieveRouteResults()

        verify(sharedPreferences).getString(Constants.FAULT_ANSWER, "")
        verify(sharedPreferences, never()).getString(Constants.ROUTE_RESULT, "")
    }

    @Test
    fun `should return null when route results are null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences)
        val answer = "R1 R2 R3"
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER, "")).thenReturn(answer)
        `when`(sharedPreferences.getString(Constants.ROUTE_RESULT, "")).thenReturn(null)

        viewModel.retrieveRouteResults()

        assertThat(null, `is`(viewModel.routeResults))
    }

    @Test
    fun `should return a list of route results when route results ids and route results are not null`() {
        val viewModel = RouteResultsViewModel(sharedPreferences)
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
        val viewModel = RouteResultsViewModel(sharedPreferences)
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


}
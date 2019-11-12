package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.dbservices.CategoryDbService
import com.untha.model.mappers.CategoryMapper
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.services.CategoriesService
import com.untha.model.services.ResultService
import com.untha.model.services.RoutesService
import com.untha.model.transactionalmodels.Route
import com.untha.model.transactionalmodels.RouteOption
import com.untha.model.transactionalmodels.RouteQuestion
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
class BaseQuestionViewModelTest : KoinTest {
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
    fun `should save single option answer when select a option with fault`() {

        val baseViewModel = BaseQuestionViewModel(sharedPreferences)
        val option = RouteOption(
            "Siempre cambia, trabajo por horas", "R1P4R2",
            "R2",
            6
        )
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)

        Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)

        Mockito.`when`(sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_LABOUR, ""))
            .thenReturn(Json.stringify(RouteOption.serializer(), option))

        val defaultAnswers = sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_LABOUR, "")

        whenever(
            editor.putString(
                Constants.FAULT_ANSWER_ROUTE_LABOUR,
                "$defaultAnswers ${option.result}"
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()
        baseViewModel.saveAnswerOption(option.result,Constants.FAULT_ANSWER_ROUTE_LABOUR)

        verify(sharedPreferences.edit())
            .putString(
                Constants.FAULT_ANSWER_ROUTE_LABOUR,
                "$defaultAnswers ${option.result}"

            )
        verify(editor).apply()
    }

    @Test
    fun `should retrieve the question with the given id`() {
        val routeOption = RouteOption("dummy", "dummy", null, null)
        val routeQuestion =
            RouteQuestion(1, "dummy", "dummy", "dummy", null, "dummy", listOf(routeOption))
        val route = Route(1, listOf(routeQuestion))

        val viewModel = BaseQuestionViewModel(sharedPreferences)

        viewModel.loadQuestion(1, route)

        MatcherAssert.assertThat(routeQuestion, CoreMatchers.`is`(viewModel.question))
    }

}
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
import com.untha.model.transactionalmodels.Route
import com.untha.model.transactionalmodels.RouteOption
import com.untha.model.transactionalmodels.RouteQuestion
import com.untha.utils.Constants
import com.untha.utils.MultipleSelectionOption
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
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class MultipleSelectionQuestionViewModelTest : KoinTest {
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
    fun `should return null when the given id does not exist`() {
        val routeOption = RouteOption("dummy", "dummy", 1,null, null)
        val routeQuestion =
            RouteQuestion(1, "dummy", "dummy", "dummy", null, "dummy", listOf(routeOption))
        val route = Route(1, listOf(routeQuestion))

        val viewModel = MultipleSelectionQuestionViewModel(sharedPreferences)

        viewModel.loadQuestion(0, route)

        assertThat(null, `is`(viewModel.question))
    }

    @Test
    fun `should save multiple option answer when select a option with fault for isNoneAboveSelected`() {
        val questionViewModel = MultipleSelectionQuestionViewModel(
            sharedPreferences
        )
        val routeOption = RouteOption("dummy", "dummy", 1,"dummy1", null)
        val routeOption2 = RouteOption("dummy", "dummy", 1,"dummy", null)
        val multipleSelectionOptions: MutableList<MultipleSelectionOption> = mutableListOf()
        multipleSelectionOptions.add(MultipleSelectionOption(0, false, null, "BAJO",1))
        multipleSelectionOptions.add(MultipleSelectionOption(1, false, null, "MEDIO",1))
        multipleSelectionOptions.add(MultipleSelectionOption(2, false, null, "ALTO",1))

        val routeQuestion =
            RouteQuestion(
                1,
                "dummy",
                "dummy",
                "dummy",
                null,
                "dummy",
                listOf(routeOption, routeOption2)
            )
        val route = Route(1, listOf(routeQuestion))
        questionViewModel.loadQuestion(1, route)
        val defaultAnswers = ""
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_LABOUR, "")).thenReturn(defaultAnswers)
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.FAULT_ANSWER_ROUTE_LABOUR,
                "$defaultAnswers ${routeOption2.result}"
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        questionViewModel.getFaultForQuestion(true,
            isLabourRoute = true,
            options = multipleSelectionOptions
        )

        verify(sharedPreferences.edit())
            .putString(
                Constants.FAULT_ANSWER_ROUTE_LABOUR,
                "$defaultAnswers ${routeOption2.result}"

            )
        verify(editor).apply()
    }

    @Test
    fun `should save multiple option answer when select a option with fault`() {
        val questionViewModel = MultipleSelectionQuestionViewModel(
            sharedPreferences
        )
        val routeOption = RouteOption("dummy", "dummy", 1,"dummy1", null)
        val routeOption2 = RouteOption("dummy", "dummy", 1,"dummy", null)
        val routeQuestion =
            RouteQuestion(
                1,
                "dummy",
                "dummy",
                "dummy",
                null,
                "dummy",
                listOf(routeOption, routeOption2)
            )

        val multipleSelectionOptions: MutableList<MultipleSelectionOption> = mutableListOf()

        multipleSelectionOptions.add(MultipleSelectionOption(0, false, null, "BAJO", 1))
        multipleSelectionOptions.add(MultipleSelectionOption(1, false, null, "MEDIO", 1))
        multipleSelectionOptions.add(MultipleSelectionOption(2, false, null, "ALTO", 1))

        val route = Route(1, listOf(routeQuestion))
        questionViewModel.loadQuestion(1, route)
        val defaultAnswers = ""
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_LABOUR, "")).thenReturn(defaultAnswers)
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.FAULT_ANSWER_ROUTE_LABOUR,
                "$defaultAnswers ${routeOption.result}"
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()

        questionViewModel.getFaultForQuestion(
            isNoneOfAbove = false,
            isLabourRoute = true,
            options = multipleSelectionOptions
        )

        verify(sharedPreferences.edit())
            .putString(
                Constants.FAULT_ANSWER_ROUTE_LABOUR,
                "$defaultAnswers ${routeOption.result}"

            )
        verify(editor).apply()
    }

    @Test
    fun `should save multiple option answer when select a option with fault MEDIO in Route Violence`() {
        val questionViewModel = MultipleSelectionQuestionViewModel(
            sharedPreferences
        )
        val routeOption = RouteOption("dummy", "dummy", 1,"dummy1", null)
        val routeOption2 = RouteOption("dummy", "dummy", 1,"dummy", null)
        val routeQuestion =
            RouteQuestion(
                1,
                "multiple_question",
                "dummy",
                "dummy",
                null,
                "dummy",
                listOf(routeOption, routeOption2)
            )

        val multipleSelectionOptions: MutableList<MultipleSelectionOption> = mutableListOf()

        multipleSelectionOptions.add(MultipleSelectionOption(0, false, null,
            Constants.VIOLENCE_ROUTE_LOW, 1))
        multipleSelectionOptions.add(MultipleSelectionOption(1, true, null,
            Constants.VIOLENCE_ROUTE_MEDIUM, 1))
        multipleSelectionOptions.add(MultipleSelectionOption(2, false, null,
            Constants.VIOLENCE_ROUTE_HIGHT, 1))

        val route = Route(1, listOf(routeQuestion))
        questionViewModel.loadQuestion(1, route)
        val fault = Constants.VIOLENCE_ROUTE_MEDIUM
        val defaultAnswers = ""
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_VIOLENCE, "")).thenReturn(defaultAnswers)
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.FAULT_ANSWER_ROUTE_VIOLENCE,
                "$defaultAnswers $fault"
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()
        questionViewModel.getFaultForQuestion(
            isNoneOfAbove = false,
            isLabourRoute = false,
            options = multipleSelectionOptions
        )
        verify(sharedPreferences.edit())
            .putString(
                Constants.FAULT_ANSWER_ROUTE_VIOLENCE,
                "$defaultAnswers $fault"
            )
        verify(editor).apply()
    }


    @Test
    fun `should save multiple option answer when select a option with fault BAJO in Route Violence`() {
        val questionViewModel = MultipleSelectionQuestionViewModel(
            sharedPreferences
        )
        val routeOption = RouteOption("dummy", "dummy", 1,"dummy1", 1)
        val routeOption2 = RouteOption("dummy", "dummy", 1,"dummy", 1)
        val routeQuestion =
            RouteQuestion(
                1,
                "multiple_question",
                "dummy",
                "dummy",
                null,
                "dummy",
                listOf(routeOption, routeOption2)
            )

        val multipleSelectionOptions: MutableList<MultipleSelectionOption> = mutableListOf()

        multipleSelectionOptions.add(MultipleSelectionOption(0, true, null,
            Constants.VIOLENCE_ROUTE_LOW, 1))
        multipleSelectionOptions.add(MultipleSelectionOption(1, false, null,
            Constants.VIOLENCE_ROUTE_MEDIUM, 1))
        multipleSelectionOptions.add(MultipleSelectionOption(2, false, null,
            Constants.VIOLENCE_ROUTE_HIGHT, 1))

        val route = Route(1, listOf(routeQuestion))
        questionViewModel.loadQuestion(1, route)
        val fault = Constants.VIOLENCE_ROUTE_LOW
        val defaultAnswers = ""
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_VIOLENCE, "")).thenReturn(defaultAnswers)
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.FAULT_ANSWER_ROUTE_VIOLENCE,
                "$defaultAnswers $fault"
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()
        questionViewModel.getFaultForQuestion(
            isNoneOfAbove = false,
            isLabourRoute = false,
            options = multipleSelectionOptions
        )
        verify(sharedPreferences.edit())
            .putString(
                Constants.FAULT_ANSWER_ROUTE_VIOLENCE,
                "$defaultAnswers $fault"
            )
        verify(editor).apply()
    }




    @Test
    fun `should save single option answer when select a option with fault`() {
        val questionViewModel = MultipleSelectionQuestionViewModel(
            sharedPreferences
        )
        val option = RouteOption(
            "Siempre cambia, trabajo por horas", "R1P4R2",
            1,
            "R2",
            6
        )
        val defaultAnswers = ""
        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
            editor.putString(
                Constants.FAULT_ANSWER_ROUTE_LABOUR,
                "$defaultAnswers ${option.result}"
            )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()
        option.result?.let { questionViewModel.saveAnswerOption(it, Constants.FAULT_ANSWER_ROUTE_LABOUR) }
        verify(sharedPreferences.edit())
            .putString(
                Constants.FAULT_ANSWER_ROUTE_LABOUR,
                "$defaultAnswers ${option.result}"

            )
        verify(editor).apply()
    }
}


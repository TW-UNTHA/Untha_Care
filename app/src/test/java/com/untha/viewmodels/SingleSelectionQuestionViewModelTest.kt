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
import com.untha.model.transactionalmodels.RouteOption
import com.untha.model.transactionalmodels.RouteQuestion
import com.untha.utils.Constants
import org.hamcrest.CoreMatchers
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
class SingleSelectionQuestionViewModelTest: KoinTest {
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
    fun `should save single option answer when select a option with fault`(){
        val questionViewModel = SingleSelectionQuestionViewModel(
        sharedPreferences
    )
        val option = RouteOption("Siempre cambia, trabajo por horas","R1P4R2",
            "R2",
            6
        )

        var defaultAnswers = questionViewModel.loadAnswerOptionFromSharedPreferences()?: ""

        val editor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        whenever(
        editor.putString(
            Constants.FAULT_ANSWER,
            "${defaultAnswers} ${option.result}"
        )
        ).thenReturn(editor)
        doNothing().whenever(editor).apply()
        option.result?.let { questionViewModel.saveAnswerOption(it) }
        verify(sharedPreferences.edit())
        .putString(
        Constants.FAULT_ANSWER,
            "$defaultAnswers ${option.result}"

        )
        verify(editor).apply()
   }

    @Test
    fun `should get answer option from shared preferences`() {

        val singleSelectionQuestionViewModel = SingleSelectionQuestionViewModel( sharedPreferences)

        val option = RouteOption("Siempre cambia, trabajo por horas","R1P4R2",
            "R2",
            6
        )
        `when`(sharedPreferences.getString(Constants.FAULT_ANSWER, ""))
            .thenReturn(option.result)
        val resultRoute = singleSelectionQuestionViewModel.loadAnswerOptionFromSharedPreferences()
        assertThat(resultRoute, CoreMatchers.`is`(option.result))
    }

    @Test
    fun `should get question labour when receive parameter goTo`() {

        val singleSelectionQuestionViewModel = SingleSelectionQuestionViewModel(sharedPreferences)

        val option = RouteOption(
            "Siempre cambia, trabajo por horas", "R1P4R2",
            "R2",
            4
        )
        val optionTwo = RouteOption(
            "15 años o menos",
            "R1P2R1", "R1",
            6
        )

        val optionSelected = optionTwo.goTo

        val questionOne = RouteQuestion(6,"SingleOption",
            "¿Cuántos años tienes",

            "", null, null, listOf(option))
        val questionTwo = RouteQuestion(1,"SingleOption",
            "¿Actualmente trabajas como Trabajadora Remunerada del Hogar?",
            "", null, null, listOf(optionTwo))

        val questionLabourRoute = optionSelected?.let {
            singleSelectionQuestionViewModel.loadQuestionLabourRoute(
                it,listOf(questionOne, questionTwo))
        }
        assertThat(questionLabourRoute,CoreMatchers.`is`(questionOne ))
    }

}


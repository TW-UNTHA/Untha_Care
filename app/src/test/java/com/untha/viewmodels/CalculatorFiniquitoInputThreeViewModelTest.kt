package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.utils.Constants
import org.hamcrest.CoreMatchers.equalTo
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
import org.hamcrest.MatcherAssert.assertThat


@RunWith(JUnit4::class)
class CalculatorFiniquitoInputThreeViewModelTest(): KoinTest{

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
    fun`should return list of hints when id FAULT_ANSWER_ROUTE_CALCULATOR_HINT in shared preferences`(){
        val calculatorViewModel = CalculatorFiniquitoInputThreeViewModel(sharedPreferences)
        val listOfHints =  listOf("R1", "R2" ,"R3" ,"R4", "R5")
        Mockito.`when`(sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_CALCULATOR_HINT, "")).thenReturn("R1 R2 R3 R4 R5")

       val result= calculatorViewModel.answerSelectedCalculatorRoute()

        assertThat(result,equalTo(listOfHints.toList()))
    }

    @Test
    fun`should return null when id FAULT_ANSWER_ROUTE_CALCULATOR_HINT is not in the shared preferences`(){
        val calculatorViewModel = CalculatorFiniquitoInputThreeViewModel(sharedPreferences)
        val listOfHints: List<String> =  emptyList()
        Mockito.`when`(sharedPreferences.getString(Constants.FAULT_ANSWER_ROUTE_CALCULATOR_HINT, "")).thenReturn(null)

        val result= calculatorViewModel.answerSelectedCalculatorRoute()

        assertThat(result,equalTo(listOfHints.toList()))
    }
}
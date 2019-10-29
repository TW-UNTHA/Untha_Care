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
import com.untha.model.services.ResultService
import com.untha.model.services.RoutesService
import com.untha.model.transactionalmodels.RouteOption
import com.untha.model.transactionalmodels.RouteQuestion
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock

@RunWith(JUnit4::class)
class SingleSelectionQuestionTest: KoinTest {

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
        val option1 = RouteOption(
            "Medio tiempo: 20",
            null,
            "RF",
            8
        )

        val option2 = RouteOption(
            "Siempre cambia, trabajo por horas",
            null,
            "R2",
            8

        )

        val question = RouteQuestion(1,
            "SingleOption",
            "¿Qué tipo de jornada de trabajo tienes?",
            "Este registro debe ser entregado al director regional de trabajo",
            null,
            null,
            listOf(option1, option2)
        )

       // val optionSelectedWithFault = question.options[1]

       // val singleSelectionQuestionViewModel = SingleSelectionQuestionViewModel()

       // MatcherAssert.assertThat(singleSelectionQuestionViewModel.loadAnswerOption(), CoreMatchers.`is`(optionSelectedWithFault))

    }

}
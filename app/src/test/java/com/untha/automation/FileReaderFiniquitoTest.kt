package com.untha.automation

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.mappers.CategoryMapper
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.viewmodels.CalculatorFiniquitoResultsViewModel
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.JUnitCore
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.Parameterized
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

@RunWith(Parameterized::class)
class FileReaderFiniquitoTest ( private val excelModel: ExcelModelFiniquito){

    companion object {
        var manageFile = FileReaderFiniquito()
        val excelData = manageFile.excelModels
        @JvmStatic
        @Parameterized.Parameters
        fun data() = excelData
    }

    @Test
    @Throws(Exception::class)
    fun subRunner() {
        JUnitCore.runClasses(CalculatorFiniquitoResultViewModelTest::class.java)

        //CalculatorFiniquitoResultViewModelTest().`should match decimo tercero value excel with result`()
    }

    @RunWith(JUnit4::class)
   inner class CalculatorFiniquitoResultViewModelTest:KoinTest{
        private val mockCategoryWithRelationsRepository by inject<CategoryWithRelationsRepository>()
        private val mockCategoryMapper by inject<CategoryMapper>()
        private val sharedPreferences by inject<SharedPreferences>()

        @get:Rule
        val rule = InstantTaskExecutorRule()

        @Before
         fun  before() {
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
            declareMock<CategoryWithRelationsRepository>()
            declareMock<CategoryMapper>()
            declareMock<SharedPreferences>()
        }

        @After
        fun after() {
            stopKoin()
        }


        @Test
        fun `should match decimo tercero value excel with result`() {
            before()
            val calculatorFiniquitoResultViewModel =CalculatorFiniquitoResultsViewModel(
                sharedPreferences, mockCategoryWithRelationsRepository, mockCategoryMapper
            )

            val expected = excelModel.decimoTercero


            val resultDecimoTercero =
                calculatorFiniquitoResultViewModel.getDecimoTercero(excelModel.decimoTerceroOption,excelModel.startDate, excelModel.endDate, excelModel.salario)
            after()
            assertThat(resultDecimoTercero, equalTo(expected))

        }

        @Test
        fun `should match decimo cuarto value excel with result`() {
            before()
            val calculatorFiniquitoResultViewModel =CalculatorFiniquitoResultsViewModel(
                sharedPreferences, mockCategoryWithRelationsRepository, mockCategoryMapper
            )
            val expected = excelModel.decimoCuarto


            val resultDecimoTercero =
                calculatorFiniquitoResultViewModel.getDecimoCuarto(excelModel.decimoCuartoOption,excelModel.startDate, excelModel.endDate, excelModel.idArea, excelModel.horasSemana)
            after()
            assertThat(resultDecimoTercero, equalTo(expected))
        }
    }




}




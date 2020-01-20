package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.verify
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.model.services.UpdateService
import com.untha.model.transactionalmodels.UpdateWrapper
import me.linshen.retrofit2.adapter.ApiResponse
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.Mockito
import retrofit2.Response

class UpdateViewModelTest : KoinTest {
    private val versionService by inject<UpdateService>()
    private val sharedPreferences by inject<SharedPreferences>()


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        startKoin {
            modules(
                listOf(
                    networkModule,
                    persistenceModule
                )
            )
        }
        declareMock<UpdateService>()
        declareMock<SharedPreferences>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should call versionService`() {
        val versionViewModel = UpdateViewModel(versionService, sharedPreferences)
        var versionWrapper = UpdateWrapper(
            5,
            true,
            "Hay una nueva versión disponible para tu aplicación."
        )
        var response = Response.success(versionWrapper)
        var apiResponse = ApiResponse.create(response)
        val updatedVersion = MutableLiveData<ApiResponse<UpdateWrapper>>()
        updatedVersion.value = apiResponse
        Mockito.`when`(versionService.getUpdateConfig()).thenReturn(updatedVersion)

        val result = versionViewModel.retrieveUpdatedConfig()

        assertThat(result as MutableLiveData, `is`(updatedVersion))
        verify(versionService).getUpdateConfig()
    }
}
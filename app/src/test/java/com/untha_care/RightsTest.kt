package com.untha_care

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.mock
import com.untha_care.dependency_injection.networkModule
import com.untha_care.dependency_injection.persistenceModule
import com.untha_care.dependency_injection.viewModelsModule
import com.untha_care.model.models.Category
import com.untha_care.model.repositories.RightsRepository
import com.untha_care.viewmodels.listCategoryDerechos.RightsViewModel
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
import org.mockito.Mockito.verify


@RunWith(JUnit4::class)
class RightsTest: KoinTest {

    private val repository by inject<RightsRepository>()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun before() {
        startKoin { modules(listOf(persistenceModule, networkModule, viewModelsModule)) }
        declareMock<RightsRepository>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should verify the observable changed when the observer get notified about changes`() {
        val rightsViewModel: RightsViewModel
        var rights = mutableListOf<Category>()
        rights.add(
            Category(
                "my title1",
                "my description1",
                "mu subtitle 1"
            )
        )
        val rightsListData = MediatorLiveData<List<Category>>()
        rightsListData.setValue(rights)
        rightsViewModel = RightsViewModel(repository)

        `when`(repository.getAll()).thenReturn(rightsListData)
        val observer = mock<Observer<List<Category>>>()
        rightsViewModel.getRightsList().observeForever(observer)

        verify(observer).onChanged(rights)
    }
}
package com.untha.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.mock
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.services.RoutesService
import com.untha.viewmodels.RightsViewModel
import com.utils.MockObjects
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
class RightsViewModelTest : KoinTest {

    private val repository by inject<CategoryWithRelationsRepository>()
    private val mapper by inject<CategoryMapper>()


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
        declareMock<CategoryMapper>()
        declareMock<CategoryWithRelationsRepository>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should call CategoriesMapper`() {
        val rightsViewModel = RightsViewModel(repository, mapper)
        val categoryQuerying = MockObjects.mockQueryingCategory()
        val categoriesQuerying = listOf(categoryQuerying)

        rightsViewModel.getRightCategories(categoriesQuerying)

        verify(mapper).mapFromModel(categoryQuerying)
    }

    @Test
    fun `should verify the observable changed when the observer get notified about changes`() {

        val rightsViewModel = RightsViewModel(repository, mapper)
        val rightsListData = MediatorLiveData<List<QueryingCategory>>()


        var rights = mutableListOf<QueryingCategory>()
        rights.add(
            MockObjects.mockQueryingCategory()
        )

        rightsListData.value = rights

        `when`(repository.getAllRightsCategories()).thenReturn(rightsListData)

        val observer = mock<Observer<List<QueryingCategory>>>()
        rightsViewModel.getRightsCategoryModels().observeForever(observer)
        verify(observer).onChanged(rights)
    }

}
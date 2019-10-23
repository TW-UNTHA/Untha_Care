package com.untha.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.utils.MockObjects
import org.junit.After
import org.junit.Assert.assertEquals
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
class CategoryViewModelTest : KoinTest {

    private val mockCategoryWithRelationsRepository by inject<CategoryWithRelationsRepository>()
    private val mockCategoryMapper by inject<CategoryMapper>()

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
        declareMock<CategoryWithRelationsRepository>()
        declareMock<CategoryMapper>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should call MainCategories`() {
        val categoryViewModel: CategoryViewModel

        categoryViewModel =
            CategoryViewModel(
                mockCategoryWithRelationsRepository,
                mockCategoryMapper
            )
        categoryViewModel.findMainCategories()

        verify(mockCategoryWithRelationsRepository).findMainCategories()
    }


    @Test
    fun `should call CategoriesRoutes`() {

        val categoryMapper = CategoryMapper()
        val categoryQueryingRoute = MockObjects.mockQueryingCategory()
        val categoryRoute = categoryQueryingRoute.categoryModel

        val categoryViewModel = CategoryViewModel(
                mockCategoryWithRelationsRepository,
                categoryMapper
            )

        categoryViewModel.getCategories(listOf(categoryQueryingRoute))

        val categoriesRoutes = categoryViewModel.getCategoryRoutes()

        assertEquals(categoryRoute?.title, categoriesRoutes[0].title)
        assertEquals(categoryRoute?.subtitle, categoriesRoutes[0].subtitle)
        assertEquals(categoryRoute?.titleNextStep, categoriesRoutes[0].titleNextStep)
        assertEquals(categoryRoute?.image, categoriesRoutes[0].image)
        assertEquals(categoryRoute?.isRoute, categoriesRoutes[0].isRoute)

    }

    @Test
    fun `should call CategoriesMapper`() {
        val categoryViewModel: CategoryViewModel
        categoryViewModel =
            CategoryViewModel(
                mockCategoryWithRelationsRepository,
                mockCategoryMapper
            )
        val categoryQuerying = MockObjects.mockQueryingCategory()
        val categoriesQuerying = listOf<QueryingCategory>(categoryQuerying)

        categoryViewModel.getCategories(categoriesQuerying)

        verify(mockCategoryMapper).mapFromModel(categoryQuerying)
    }

    @Test
    fun `should return at least one element`() {
        val categoryViewModel: CategoryViewModel
        val categoryQuerying = MockObjects.mockQueryingCategory()
        val categoriesQuerying = mutableListOf<QueryingCategory>()
        categoriesQuerying.add(categoryQuerying)
        val queryingCategories = MediatorLiveData<List<QueryingCategory>>()
        queryingCategories.value = categoriesQuerying
        categoryViewModel =
            CategoryViewModel(
                mockCategoryWithRelationsRepository,
                mockCategoryMapper
            )

        `when`(mockCategoryWithRelationsRepository.findMainCategories()).thenReturn(
            queryingCategories
        )
        val observer = mock<Observer<List<QueryingCategory>>>()
        categoryViewModel.findMainCategories().observeForever(observer)

        verify(observer).onChanged(categoriesQuerying)
    }
}

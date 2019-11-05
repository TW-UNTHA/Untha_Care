package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.Route
import com.untha.model.transactionalmodels.RouteOption
import com.untha.utils.Constants
import com.utils.MockObjects
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
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
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class CategoryViewModelTest : KoinTest {

    private val mockCategoryWithRelationsRepository by inject<CategoryWithRelationsRepository>()
    private val mockCategoryMapper by inject<CategoryMapper>()
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
        declareMock<CategoryWithRelationsRepository>()
        declareMock<CategoryMapper>()
        declareMock<SharedPreferences>()
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
                mockCategoryMapper,
                sharedPreferences
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
                categoryMapper,
                sharedPreferences
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
                mockCategoryMapper,
                sharedPreferences
            )
        val categoryQuerying = MockObjects.mockQueryingCategory()
        val categoriesQuerying = listOf<QueryingCategory>(categoryQuerying)

        categoryViewModel.getCategories(categoriesQuerying)

        verify(mockCategoryMapper).mapFromModel(categoryQuerying)
    }

    @Test
    fun `should return at least one element`() {
        val categoryViewModel = CategoryViewModel(
            mockCategoryWithRelationsRepository,
            mockCategoryMapper,
            sharedPreferences
        )
        val categoryQuerying = MockObjects.mockQueryingCategory()
        val categoriesQuerying = mutableListOf<QueryingCategory>()
        categoriesQuerying.add(categoryQuerying)
        val queryingCategories = MediatorLiveData<List<QueryingCategory>>()
        queryingCategories.value = categoriesQuerying

        `when`(mockCategoryWithRelationsRepository.findMainCategories()).thenReturn(
            queryingCategories
        )
        val observer = mock<Observer<List<QueryingCategory>>>()
        categoryViewModel.findMainCategories().observeForever(observer)

        verify(observer).onChanged(categoriesQuerying)
    }

    @Test
    fun `should save categories route`(){
        val categoryMapper = CategoryMapper()
        val categoryQueryingRoute = MockObjects.mockQueryingCategory()
        val categoryViewModel = CategoryViewModel(
            mockCategoryWithRelationsRepository,
            categoryMapper,
            sharedPreferences
        )

        categoryViewModel.getCategories(listOf(categoryQueryingRoute))

        val categoriesRoutes = categoryViewModel.getCategoryRoutes()
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(editor)

        whenever(
            editor.putString(
                Constants.CATEGORIES_ROUTES,
                Json.stringify(Category.serializer().list, categoriesRoutes)
            )
        ).thenReturn(editor)

        doNothing().whenever(editor).apply()
        categoryViewModel.saveCategoriesSharedPreferences(categoriesRoutes)

        verify(sharedPreferences.edit())
            .putString(
                Constants.CATEGORIES_ROUTES,
                Json.stringify(Category.serializer().list, categoriesRoutes)
            )
        verify(editor).apply()
    }

    @Test
    fun `should get categories routes from shared preferencecds`() {
        val categoryMapper = CategoryMapper()
        val categoryQueryingRoute = MockObjects.mockQueryingCategory()
        val categoryViewModel = CategoryViewModel(
            mockCategoryWithRelationsRepository,
            categoryMapper,
            sharedPreferences
        )

        categoryViewModel.getCategories(listOf(categoryQueryingRoute))
        val categoriesRoutes = categoryViewModel.getCategoryRoutes()
        `when`(sharedPreferences.getString(Constants.CATEGORIES_ROUTES, ""))
            .thenReturn(Json.stringify(Category.serializer().list, categoriesRoutes))
        val resultRoute = categoryViewModel.loadCategoriesRoutesFromSharedPreferences()
        MatcherAssert.assertThat(resultRoute, CoreMatchers.`is`(categoriesRoutes))


    }

}

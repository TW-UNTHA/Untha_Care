package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.untha.di.mapperModule
import com.untha.di.networkModule
import com.untha.di.persistenceModule
import com.untha.di.viewModelsModule
import com.untha.model.dbservices.CategoryDbService
import com.untha.model.mappers.CategoryMapper
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.services.LawsAndRightsService
import com.untha.model.transactionalmodels.CategoriesWrapper
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.utils.MockObjects
import me.linshen.retrofit2.adapter.ApiResponse
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
import retrofit2.Response


@RunWith(JUnit4::class)
class MainViewModelTest : KoinTest {

    private val dbService by inject<CategoryDbService>()
    private val service by inject<LawsAndRightsService>()
    private val mapper by inject<CategoryMapper>()
    private val repository by inject<CategoryWithRelationsRepository>()
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
        declareMock<LawsAndRightsService>()
        declareMock<CategoryMapper>()
        declareMock<CategoryWithRelationsRepository>()
        declareMock<SharedPreferences>()
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should call transform method to get a list of Category Models`() {
        val mockLifeCycleOwner = mockLifecycleOwner()
        val mainViewModel = MainViewModel(dbService, service, mapper, repository, sharedPreferences)
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "imagen",
                    0,
                    null
                )
            )
        )
        var response = Response.success(categoryWrapper)
        var apiResponse = ApiResponse.create(response)
        val updatedCategories = MutableLiveData<ApiResponse<CategoriesWrapper>>()
        updatedCategories.value = apiResponse
        `when`(service.getCategories()).thenReturn(updatedCategories)

        val observer = mock<Observer<ApiResponse<CategoriesWrapper>>>()
        service.getCategories().observeForever(observer)

        mainViewModel.retrieveUpdatedCategories(mockLifeCycleOwner)

        verify(observer).onChanged(apiResponse)
        verify(mapper).mapToModel(categoryWrapper.categories[0])
    }

    @Test
    fun `should call get all categories`() {
        val mainViewModel = MainViewModel(dbService, service, mapper, repository, sharedPreferences)

        mainViewModel.retrieveAllCategories()

        verify(repository).getAllCategories()
    }

    @Test
    fun `should call mapFromModel`() {
        val mainViewModel = MainViewModel(dbService, service, mapper, repository, sharedPreferences)

        val queryingCategory = MockObjects. mockQueryingCategory()
        var categoryResult =
            Category(0, "", parentId = 1)
        `when`(mapper.mapFromModel(queryingCategory)).thenReturn(categoryResult)
        val categories: List<Category> = mainViewModel.getCategories(listOf(queryingCategory))

        verify(mapper).mapFromModel(queryingCategory)

        assertThat(categories, `is`(listOf(categoryResult)))
    }

    @Test
    fun `should call method getInt from SharedPreferences when API response is success`() {
        val mockLifeCycleOwner = mockLifecycleOwner()
        val mainViewModel = MainViewModel(dbService, service, mapper, repository, sharedPreferences)
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "imagen",
                    0,
                    null
                )
            )
        )
        var response = Response.success(categoryWrapper)
        var apiResponse = ApiResponse.create(response)
        val updatedCategories = MutableLiveData<ApiResponse<CategoriesWrapper>>()
        updatedCategories.value = apiResponse
        `when`(service.getCategories()).thenReturn(updatedCategories)

        val observer = mock<Observer<ApiResponse<CategoriesWrapper>>>()
        service.getCategories().observeForever(observer)

        mainViewModel.retrieveUpdatedCategories(mockLifeCycleOwner)

        verify(sharedPreferences).getInt(Constants.CATEGORIES_VERSION, 0)
    }

    @Test
    fun `should not call mapper when version number is equal to  api response version number`() {
        val mockLifeCycleOwner = mockLifecycleOwner()
        val mainViewModel = MainViewModel(dbService, service, mapper, repository, sharedPreferences)
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "imagen",
                    0,
                    null
                )
            )
        )
        var response = Response.success(categoryWrapper)
        var apiResponse = ApiResponse.create(response)
        val updatedCategories = MutableLiveData<ApiResponse<CategoriesWrapper>>()
        updatedCategories.value = apiResponse
        `when`(service.getCategories()).thenReturn(updatedCategories)
        `when`(
            sharedPreferences.getInt(
                Constants.CATEGORIES_VERSION,
                0
            )
        ).thenReturn(categoryWrapper.version)

        val observer = mock<Observer<ApiResponse<CategoriesWrapper>>>()
        service.getCategories().observeForever(observer)

        mainViewModel.retrieveUpdatedCategories(mockLifeCycleOwner)

        verifyZeroInteractions(mapper)
    }

    @Test
    fun `should call category mapper when version number is not equal to  api response version number`() {
        val mockLifeCycleOwner = mockLifecycleOwner()
        val mainViewModel = MainViewModel(dbService, service, mapper, repository, sharedPreferences)
        var categoryWrapper = CategoriesWrapper(
            1, listOf(
                Category(
                    1,
                    "Mis derechos stub",
                    "mis derechos descripcion",
                    "imagen",
                    0,
                    null
                )
            )
        )
        var response = Response.success(categoryWrapper)
        var apiResponse = ApiResponse.create(response)
        val updatedCategories = MutableLiveData<ApiResponse<CategoriesWrapper>>()
        updatedCategories.value = apiResponse
        `when`(service.getCategories()).thenReturn(updatedCategories)
        `when`(
            sharedPreferences.getInt(
                Constants.CATEGORIES_VERSION,
                0
            )
        ).thenReturn(0)

        val observer = mock<Observer<ApiResponse<CategoriesWrapper>>>()
        service.getCategories().observeForever(observer)

        mainViewModel.retrieveUpdatedCategories(mockLifeCycleOwner)

        verify(mapper).mapToModel(categoryWrapper.categories.first())
    }


    private fun mockLifecycleOwner(): LifecycleOwner {
        val owner = mock<LifecycleOwner>()
        val lifecycle = LifecycleRegistry(owner)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        `when`<Lifecycle>(owner.lifecycle).thenReturn(lifecycle)
        return owner
    }

}




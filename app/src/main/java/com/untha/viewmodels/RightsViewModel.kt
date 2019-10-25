package com.untha.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untha.model.mappers.CategoryMapper
import com.untha.model.models.QueryingCategory
import com.untha.model.repositories.CategoryWithRelationsRepository
import com.untha.model.services.RoutesService
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.Route
import me.linshen.retrofit2.adapter.ApiResponse

class RightsViewModel(
    private val categoryWithRelationsRepository: CategoryWithRelationsRepository,
    private val mapper: CategoryMapper, private val routeService: RoutesService
) :
    ViewModel() {


    fun getRightsCategoryModels(): LiveData<List<QueryingCategory>> {
        return categoryWithRelationsRepository.getAllRightsCategories()
    }

    fun getRightCategories(queryingCategories: List<QueryingCategory>): List<Category> {
        return queryingCategories.map { queryingCategory ->
            mapper.mapFromModel(queryingCategory)
        }
    }

    fun getRoutes(): LiveData<ApiResponse<Route>> {
        return routeService.getLabourRoute()
    }

}

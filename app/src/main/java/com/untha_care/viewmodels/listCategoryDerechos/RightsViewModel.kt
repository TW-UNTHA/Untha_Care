package com.untha_care.viewmodels.listCategoryDerechos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.untha_care.model.models.Category
import com.untha_care.model.repositories.RightsRepository

class RightsViewModel(private val rightsRepository: RightsRepository): ViewModel(){
    private val rightsList = MediatorLiveData<List<Category>>()

    init {
        getAllRights()
    }

    fun getRightsList(): LiveData<List<Category>> {
        return rightsList
    }


    private fun getAllRights() {
        rightsList.addSource(rightsRepository.getAll()) { categories ->
            rightsList.postValue(categories)
        }
    }

}

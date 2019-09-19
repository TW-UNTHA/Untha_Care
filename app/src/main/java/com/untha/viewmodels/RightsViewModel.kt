package com.untha.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.untha.model.models.Category
import com.untha.model.repositories.RightsRepository

class RightsViewModel(private val rightsRepository: RightsRepository): ViewModel(){


    fun getRightsList(): LiveData<List<Category>> {
        return getAllRights()
    }


    private fun getAllRights(): LiveData<List<Category>> {
        val rightsList = MediatorLiveData<List<Category>>()
        rightsList.addSource(rightsRepository.getAll()) { categories ->
            rightsList.postValue(categories)
        }
        return rightsList
    }

}

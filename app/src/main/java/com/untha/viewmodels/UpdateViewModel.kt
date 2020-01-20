package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untha.model.services.UpdateService
import com.untha.model.transactionalmodels.UpdateWrapper
import com.untha.utils.Constants
import me.linshen.retrofit2.adapter.ApiResponse

class UpdateViewModel(
    private val updateService: UpdateService,
    private val sharedPreferences : SharedPreferences
) : ViewModel() {
    fun retrieveUpdatedConfig(): LiveData<ApiResponse<UpdateWrapper>> {
        return updateService.getUpdateConfig()
    }

    fun isThereGooglePlayError(): Boolean {
        return sharedPreferences.getBoolean(Constants.IS_THERE_GOOGLE_PLAY_ERROR, false)
    }
}

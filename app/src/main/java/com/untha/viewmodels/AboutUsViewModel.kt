package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.untha.utils.Constants

class AboutUsViewModel(private val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun saveAboutUsPreferences(isFirstTime:Boolean){
        sharedPreferences.edit()
            .putBoolean(
                Constants.ABOUS_US,
               isFirstTime
            ).apply()
    }

    fun loadAboutUsFromSharedPreferences(): Boolean {
        return sharedPreferences.getBoolean(Constants.ABOUS_US, false)
    }

}

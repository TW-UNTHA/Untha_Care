package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.untha.model.transactionalmodels.News
import com.untha.model.transactionalmodels.NewsWrapper
import com.untha.utils.Constants
import kotlinx.serialization.json.Json

class NewsViewModel(
    private val sharedPreferences : SharedPreferences
) : ViewModel() {
    var news:List<News>? = null
    var buttonTitle : String ?=null
    var buttonSubtitle : String?= null
    var showScreen : Boolean =false


    fun loadResultDynamicFromSharePreferences() {
        val jsonResultDynamic = sharedPreferences.getString(Constants.NEWS, "")
        jsonResultDynamic?.let {
            val result = Json.parse(NewsWrapper.serializer(), it)
            news=result.news
            buttonTitle = result.buttonTitle
            buttonSubtitle = result.buttonSubtitle
            showScreen = result.showScreen

        }
    }

}

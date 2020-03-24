package com.untha.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.untha.model.services.NewsService
import com.untha.model.transactionalmodels.News
import com.untha.model.transactionalmodels.NewsWrapper
import com.untha.utils.Constants
import kotlinx.serialization.json.Json
import me.linshen.retrofit2.adapter.ApiResponse

class NewsViewModel(
     val sharedPreferences: SharedPreferences, private val newService: NewsService

) : ViewModel() {
    var news: List<News>? = null
    var buttonTitle: String? = null
    var buttonSubtitle: String? = null
    var showScreen: Boolean = false



     fun isRetrievingDataFromInternet(): Boolean {
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }

    fun loadResultDynamicFromSharePreferences() {
        val jsonResultDynamic = sharedPreferences.getString(Constants.NEWS, "")
        if (!jsonResultDynamic.equals("")) {
            jsonResultDynamic?.let {
                val result = Json.parse(NewsWrapper.serializer(), it)
                news = result.news
                buttonTitle = result.buttonTitle
                buttonSubtitle = result.buttonSubtitle
                showScreen = result.showScreen

            }
        }
    }
    fun saveSharePreferences(responseBody:NewsWrapper){
        sharedPreferences.edit()
            .putString(
                Constants.NEWS,
                Json.stringify(
                    NewsWrapper.serializer(),
                    responseBody
                )
            ).apply()

    }

    fun getNews(): LiveData<ApiResponse<NewsWrapper>> {
        return newService.getNews()

    }


}

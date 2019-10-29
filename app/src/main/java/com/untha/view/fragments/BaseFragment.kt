package com.untha.view.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.untha.R
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import java.util.*

open class BaseFragment : Fragment(), TextToSpeech.OnInitListener {
    var textToSpeech: TextToSpeech? = null
    lateinit var firebaseAnalytics: FirebaseAnalytics

    val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
        .setPopEnterAnim(R.anim.slide_in_left).setExitAnim(R.anim.slide_out_left)
        .setPopExitAnim(R.anim.slide_out_right).build()


    override fun onInit(status: Int) {
        val language = "spa"
        val country = "MEX"
        if (status == TextToSpeech.SUCCESS) {
            val locSpanish = Locale(language, country)
            val result = textToSpeech!!.setLanguage(locSpanish)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeech", "The Language specified is not supported!")
            }
        } else {
            Log.e("TextToSpeech", "Initilization Failed!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            firebaseAnalytics = FirebaseAnalytics.getInstance(it)
        }
    }

    override fun onDestroy() {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        super.onDestroy()
    }

    fun logAnalyticsSelectContentWithId(name: String, contentType: ContentType) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType.description)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun logAnalyticsCustomEvent(event: FirebaseEvent) {
        firebaseAnalytics.logEvent(event.description, null)
    }

    fun logAnalyticsCustomEvent(event: String) {
        firebaseAnalytics.logEvent(event, null)
    }

    fun logAnalyticsCustomContentTypeWithId(contentType: ContentType, event: FirebaseEvent) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType.description)
        firebaseAnalytics.logEvent(event.description, bundle)
    }
}

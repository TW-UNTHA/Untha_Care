package com.untha.view.fragments

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.untha.R
import java.util.*

open class BaseFragment : Fragment(), TextToSpeech.OnInitListener {
    var textToSpeech: TextToSpeech? = null

    val navOptions =
        NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_out_right)
            .setPopEnterAnim(R.anim.slide_in_left)
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

    override fun onDestroy() {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        super.onDestroy()
    }

}

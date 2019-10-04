package com.untha.utils

import android.annotation.TargetApi
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.HashMap

object ToSpeech {
    fun speakOut(title: String, textToSpeech: TextToSpeech?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(title, textToSpeech)
        } else {
            ttsUnder20(title, textToSpeech)
        }
        return true
    }


    @Suppress("DEPRECATION")
    private fun ttsUnder20(text: String, tts: TextToSpeech?) {
        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, map)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ttsGreater21(text: String, tts: TextToSpeech?): Boolean {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        return true
    }

}

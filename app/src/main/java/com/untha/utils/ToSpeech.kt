package com.untha.utils

import android.annotation.TargetApi
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.HashMap

object ToSpeech {
    fun speakOut(title: String, textToSpeech: TextToSpeech?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreaterAndroid21Version(title, textToSpeech)
        } else {
            ttsUnderAndroid20Version(title, textToSpeech)
        }
        return true
    }


    @Suppress("DEPRECATION")
    private fun ttsUnderAndroid20Version(text: String, tts: TextToSpeech?) {
        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, map)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ttsGreaterAndroid21Version(text: String, tts: TextToSpeech?): Boolean {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        return true
    }

}

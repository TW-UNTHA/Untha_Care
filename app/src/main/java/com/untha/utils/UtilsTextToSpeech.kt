package com.untha.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

class UtilsTextToSpeech(
    val context: Context,
    val listParagraph: MutableList<String>,
    val onDone: (Int, MutableList<String>) -> String?
) :
    TextToSpeech.OnInitListener,
    UtteranceProgressListener() {
    var position: Int = 0
    private var textToSpeech: TextToSpeech

    init {
        textToSpeech = TextToSpeech(context, this)
        textToSpeech.setOnUtteranceProgressListener(this)
    }

    override fun onDone(p0: String?) {
        onDone(position, listParagraph)?.let {
            speakOut(it, textToSpeech)
        }
        position++

    }

    override fun onError(p0: String?) {
        println("onError")
    }

    override fun onStart(p0: String?) {
        println("onStart")
    }

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
        textToSpeech.isSpeaking
    }

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
        println(tts)
        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ttsGreaterAndroid21Version(text: String, tts: TextToSpeech?): Boolean {
        println(tts)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        return true
    }

    fun stop() {
        textToSpeech.stop()
    }

    fun destroy() {
        textToSpeech.shutdown()
    }

    fun isSpeaking(): Boolean {
        return textToSpeech.isSpeaking
    }
}

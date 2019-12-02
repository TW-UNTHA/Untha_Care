package com.untha.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import timber.log.Timber
import java.util.*

class UtilsTextToSpeech(
    val context: Context,
    private val listParagraph: MutableList<String>?,
    private val onDone: ((Int, MutableList<String>) -> String?)?
) :
    TextToSpeech.OnInitListener,
    UtteranceProgressListener() {
    private var position: Int = 0
    private var textToSpeech: TextToSpeech = TextToSpeech(context, this)

    init {
        textToSpeech.setOnUtteranceProgressListener(this)
    }

    override fun onDone(text: String?) {
        if (!listParagraph.isNullOrEmpty()) {
            val textToReproduce = onDone?.let { it(position, listParagraph) }
            if (textToReproduce != null) {
                speakOut(textToReproduce)
                position++
            }
        }
    }

    override fun onError(p0: String?) {
        println("On error audio")
    }

    override fun onStart(p0: String?) {
        println("on Start audio")
    }

    override fun onInit(status: Int) {
        val language = "spa"
        val country = "MEX"

        if (status == TextToSpeech.SUCCESS) {
            val locSpanish = Locale(language, country)
            val result = textToSpeech.setLanguage(locSpanish)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Timber.e("The Language specified is not supported!")
            }
        } else {
            Timber.e("Initialization Failed!")
        }
    }

    fun speakOut(title: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreaterAndroid21Version(title)
        } else {
            ttsUnderAndroid20Version(title)
        }
        return true
    }


    @Suppress("DEPRECATION")
    private fun ttsUnderAndroid20Version(text: String) {
        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ttsGreaterAndroid21Version(text: String): Boolean {
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

    fun restartPosition() {
        position = 0
    }
}

package com.untha.view.fragments

import android.graphics.Paint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.utils.ToSpeech
import com.untha.view.activities.MainActivity
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.allCaps
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageButton
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageView
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.themedButton
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class MainScreenLabourRouteFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity
        this.textToSpeech = TextToSpeech(context, this)
        return createMainLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view as _LinearLayout) {
            verticalLayout {
                verticalLayout {
                    loadImageRoute(view)
                }
                verticalLayout {
                    loadImageAudio()
                }

                verticalLayout {
                    showMessage("Hola!", R.font.proxima_nova_bold)
                }

                verticalLayout {
                    showMessage(
                        "Voy a hacerte unas preguntas para identificar si se estan cumpliendo tus derechos laborales",
                        R.font.proxima_nova_light
                    )
                }
                verticalLayout {
                    buttonNext()
                }
                verticalLayout {
                    linkUltimoResultado()
                }
            }.lparams(width = matchParent, height = matchParent) {
                margin = dip(Constants.TEMPORAL_MARGIN_SCREEN_LABOUR_ROUTE)
            }
        }
    }


    private fun createMainLayout(
    ): View {
        return UI {
            verticalLayout {
                backgroundColor =
                    ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadImageRoute(
        view: View
    ) {
        imageView {
            val imageUrl = resources.getIdentifier(
                "route_labour_pantalla_inicial",
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl)
                .into(this)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }.lparams(width = matchParent, height = wrapContent)
    }

    private fun _LinearLayout.loadImageAudio() {
        imageButton {
            val mainRouteMessage =
                "Hola! Voy a hacerte unas preguntas para identificar si se estan cumpliendo tus derechos laborales"

            gravity = Gravity.CENTER
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_XY
            imageResource = R.drawable.icon_question_audio

            val textQuestion = mainRouteMessage
            val contentQuestion = "${textQuestion}"
            background = null
            setOnLongClickListener {
                contentQuestion.let { ToSpeech.speakOut(it, textToSpeech) }
                true
            }
        }.lparams(
            width = wrapContent,
            height = wrapContent
        ) {
        }
    }

    private fun _LinearLayout.showMessage(message: String, idTypeFont: Int) {
        textView {
            gravity = Gravity.CENTER
            text = message
            textSizeDimen = R.dimen.text_size_question_route
            typeface = ResourcesCompat.getFont(
                context.applicationContext, idTypeFont
            )
        }.lparams(width = wrapContent, height = wrapContent) {
            gravity = Gravity.CENTER
            topMargin = dip(Constants.TEMPORAL_MARGIN_SCREEN_LABOUR_ROUTE)
            bottomMargin = dip(Constants.TEMPORAL_MARGIN_SCREEN_LABOUR_ROUTE)
        }
    }

    private fun _LinearLayout.buttonNext() {
        themedButton(theme = R.style.ButtonNext) {
            text = Constants.ROUTE_BUTTON_TEXT
            textSizeDimen = R.dimen.text_size_question_route
            textColor = ContextCompat.getColor(context, R.color.colorBackgroundCategoryRoute)
            allCaps = false
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_bold
            )
            onClick { /* Todo on click */ }
        }.lparams(
            width = matchParent,
            height = calculateHeightComponentsQuestion(Constants.SIZE_HEIGHT_PERCENTAGE_OPTION_BUTTON)
        )
    }

    private fun _LinearLayout.linkUltimoResultado() {
        textView {
            gravity = Gravity.CENTER
            text = getString(R.string.labour_route_result_link)
            textSizeDimen = R.dimen.text_size_question_route
            textColor = ContextCompat.getColor(context, R.color.colorGenericTitle)
            typeface = ResourcesCompat.getFont(
                context.applicationContext, R.font.proxima_nova_light
            )
            paintFlags = Paint.UNDERLINE_TEXT_FLAG
            isClickable = true
            onClick { view ->
                println("printing message")
            }
        }.lparams(width = wrapContent, height = wrapContent) {
            gravity = Gravity.CENTER
            topMargin = dip(Constants.TEMPORAL_MARGIN_SCREEN_LABOUR_ROUTE)
            bottomMargin = dip(Constants.TEMPORAL_MARGIN_SCREEN_LABOUR_ROUTE)
        }

    }

    private fun calculateHeightComponentsQuestion(heightComponent: Double): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR_ROUTE) * heightComponent
        return PixelConverter.toPixels(cardHeightInDps, context)
    }
}

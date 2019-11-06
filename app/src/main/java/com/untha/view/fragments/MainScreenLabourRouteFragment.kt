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
import androidx.core.text.parseAsHtml
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.ToSpeech
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.RoutesViewModel
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.allCaps
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageButton
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.themedButton
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import org.koin.android.viewmodel.ext.android.viewModel

class MainScreenLabourRouteFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private val routeViewModel : RoutesViewModel by viewModel()

    companion object {
        const val SPACE_BETWEEN_LINK_AND_BUTTON = 11
        const val HEIGHT_OF_BUTTON = 0.09375
        const val MARGINS = (((18 * 100) / 640) / 100F).toDouble()

        const val HEIGHT_BUTTON = ((((60) * 100) / 640) / 100F).toDouble()
        const val HEIGHT_DESCRIPTION = (((120 * 100) / 640) / 100F).toDouble()
        const val MAIN_ROUTE_MESSAGE =
            "¡Hola! Voy a hacerte unas preguntas para identificar si se están cumpliendo tus derechos laborales"
        const val ROUTE_BUTTON_TEXT = "Empezar"
        const val SPACE_AUDIO_WITH_IMAGE = 28
        val SPACE_TITLE_WITH_AUDIO = 14
        val SPACE_DESCRIPTION_WITH_TITLE = 18
    }

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
            val widthMainComponent =
                (PixelConverter.getScreenDpWidth(context) - Constants.SIZE_OF_ACTION_BAR) * MARGINS
            val marginMainComponent = PixelConverter.toPixels(widthMainComponent, context)
            verticalLayout {
                verticalLayout {

                    loadImageRoute(view)
                }.lparams(width = matchParent, height = wrapContent)


                verticalLayout {

                    loadImageAudio()
                }.lparams(width = matchParent, height = wrapContent) {
                    topMargin = dip(SPACE_AUDIO_WITH_IMAGE)
                }

                verticalLayout {
                    showMessage("¡Hola!", R.font.proxima_nova_bold)
                }

                val height =
                    (PixelConverter.getScreenDpHeight(context) - Constants.SIZE_OF_ACTION_BAR) * HEIGHT_DESCRIPTION

                verticalLayout {
                    showMessageDescription(
                        "Voy a hacerte unas preguntas para identificar si se están cumpliendo tus derechos laborales",
                        R.font.proxima_nova_light
                    )
                }.lparams(width = wrapContent, height = dip(height.toFloat())) {
                    gravity = Gravity.CENTER
                }
                relativeLayout {
                    gravity = Gravity.BOTTOM or Gravity.CENTER

                    verticalLayout {
                        buttonNext(view)
                        linkLastResult()
                    }.lparams(width = matchParent, height = wrapContent) {
                        alignParentBottom()
                    }
                }.lparams(width = matchParent, height = wrapContent)
            }.lparams(width = matchParent, height = matchParent) {
                topMargin = dip(marginMainComponent)
                leftMargin = dip(marginMainComponent)
                rightMargin = dip(marginMainComponent)
                bottomMargin = dip(marginMainComponent)
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
        val height = HEIGHT_BUTTON
        println("sizeNormal${calculateHeightButtonAudio(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_ROUTE)}")
        imageButton {
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_XY
            imageResource = R.drawable.icon_question_audio
            gravity = Gravity.CENTER
            background = null
            val contentQuestion = "${MAIN_ROUTE_MESSAGE}"
            onClick {
                logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
                contentQuestion.let { ToSpeech.speakOut(it, textToSpeech) }
            }
        }.lparams(
            width = dip(calculateHeightButtonAudio(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_ROUTE)),
            height = dip(calculateHeightButtonAudio(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_ROUTE))
        ) {
//            minimumWidth = dip(71)
//            minimumHeight = dip(71)

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
            topMargin = dip(SPACE_TITLE_WITH_AUDIO)
            bottomMargin = dip(SPACE_DESCRIPTION_WITH_TITLE)
        }
    }

    private fun _LinearLayout.showMessageDescription(message: String, idTypeFont: Int) {
        textView {
            gravity = Gravity.CENTER
            text = message.parseAsHtml()
            textSizeDimen = R.dimen.text_size_question_route
            typeface = ResourcesCompat.getFont(
                context.applicationContext, idTypeFont
            )
        }
    }


    private fun _LinearLayout.buttonNext(view: _LinearLayout) {
        val height =
            (PixelConverter.getScreenDpHeight(context) - Constants.SIZE_OF_ACTION_BAR) * HEIGHT_OF_BUTTON
        themedButton(theme = R.style.ButtonNext) {
            text = ROUTE_BUTTON_TEXT
            textSizeDimen = R.dimen.text_size_question_route
            textColor = ContextCompat.getColor(context, R.color.colorBackgroundCategoryRoute)
            allCaps = false
            backgroundDrawable =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.drawable_multiple_option_next_button
                )
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_bold
            )
            onClick {

                val goToBundle = Bundle().apply {
                    putInt("goTo",Constants.START_QUESTION_ROUTE_LABOUR)
                    putSerializable(
                        Constants.ROUTE_LABOUR,
                        routeViewModel.loadLabourRouteFromSharedPreferences())
                }
                view.findNavController()
                    .navigate(R.id.singleSelectQuestionFragment, goToBundle, navOptions, null)

            logAnalyticsCustomContentTypeWithId(ContentType.ROUTE, FirebaseEvent.ROUTE)
            }
        }.lparams(width = matchParent, height = dip(height.toFloat()))

    }

    private fun _LinearLayout.linkLastResult() {
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
            onClick {
                logAnalyticsCustomContentTypeWithId(ContentType.LINK, FirebaseEvent.LINK)
            }
        }.lparams(width = matchParent, height = wrapContent) {
            topMargin = dip(SPACE_BETWEEN_LINK_AND_BUTTON)
        }

    }
    private fun calculateHeightButtonAudio(percentageComponent: Double): Float {
        val cardHeightInDps =
            (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR_ROUTE) * percentageComponent
        return cardHeightInDps.toFloat()
    }
}

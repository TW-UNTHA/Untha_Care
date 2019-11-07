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
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.ToSpeech
import com.untha.view.activities.MainActivity
import com.untha.view.extension.getSelectableItemBackground
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.alignParentBottom
import org.jetbrains.anko.allCaps
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.dip
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
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class MainScreenLabourRouteFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity

    companion object {
        const val MAIN_ROUTE_MESSAGE =
            "¡Hola! Voy a hacerte unas preguntas para identificar si se están cumpliendo tus derechos laborales"
        const val ROUTE_BUTTON_TEXT = "Empezar"
        const val FIRST_MESSAGE = "¡Hola!"
        const val SECOND_MESSAGE =
            "Voy a hacerte unas preguntas para identificar si se están cumpliendo tus derechos laborales"
        const val IMAGE_NAME = "route_labour_pantalla_inicial"
        const val MARGINS = (((18 * 100) / 640) / 100F).toDouble()
        const val SPACE_TITLE_WITH_AUDIO = (((18 * 100) / 640) / 100F).toDouble()
        const val SPACE_LINK_BUTTON = (((20 * 100) / 640) / 100F).toDouble()
        const val SPACE_DESCRIPTION_WITH_TITLE = (((30 * 100) / 640) / 100F).toDouble()
        const val SIZE_SPACE_BUTTON_AND_IMAGE = 0.04375
        const val MARGIN_FOR_TOP_AND_BOTTOM = 0.5

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
        val sizeSpaceButtomAndImage =
            (PixelConverter.getScreenDpHeight(context) - Constants.SIZE_OF_ACTION_BAR) * SIZE_SPACE_BUTTON_AND_IMAGE
        val widthMainLayout =
            (PixelConverter.getScreenDpWidth(context) - Constants.SIZE_OF_ACTION_BAR) * MARGINS
        val marginMainLayout = PixelConverter.toPixels(widthMainLayout, context)
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            firebaseAnalytics.setCurrentScreen(
                it,
                Constants.LABOUR_ROUTE_PAGE,
                null
            )
        }

        with(view as _LinearLayout) {

            verticalLayout {
                verticalLayout {
                    loadImageRoute(view)
                }.lparams(width = matchParent, height = wrapContent)

                verticalLayout {
                    loadImageAudio()
                }.lparams(width = matchParent, height = wrapContent) {
                    topMargin = dip(sizeSpaceButtomAndImage.toFloat())
                }

                verticalLayout {
                    showMessage(FIRST_MESSAGE, R.font.proxima_nova_bold)
                }

                verticalLayout {
                    showMessageDescription(
                        SECOND_MESSAGE,
                        R.font.proxima_nova_light
                    )
                }.lparams(width = wrapContent, height = wrapContent) {
                    gravity = Gravity.CENTER
                }
                relativeLayout {
                    val paddingTopAndBottom =
                        (PixelConverter.getScreenDpHeight(context) - Constants.SIZE_OF_ACTION_BAR) * SPACE_LINK_BUTTON
                    gravity = Gravity.BOTTOM or Gravity.CENTER
                    verticalLayout {
                        buttonNext()
                        viewLastResult(
                            paddingTopAndBottom,
                            MARGIN_FOR_TOP_AND_BOTTOM
                        )
                    }.lparams(width = matchParent, height = wrapContent) {
                        alignParentBottom()
                    }
                }.lparams(width = matchParent, height = wrapContent)
            }.lparams(width = matchParent, height = matchParent) {
                topMargin = dip(marginMainLayout)
                leftMargin = dip(marginMainLayout)
                rightMargin = dip(marginMainLayout)
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.viewLastResult(
        paddingTopAndBottom: Double,
        marginTopAndBottom: Double
    ) {
        verticalLayout {
            linkLastResult()
            setOnClickListener {
                println("printing")
            }
            backgroundResource = getSelectableItemBackground().resourceId
            topPadding =
                dip((paddingTopAndBottom).toFloat())
            bottomPadding =
                dip((paddingTopAndBottom).toFloat())

        }.lparams(width = matchParent, height = wrapContent) {
            topMargin =
                dip((paddingTopAndBottom * marginTopAndBottom).toFloat())
            bottomMargin =
                dip((paddingTopAndBottom * marginTopAndBottom).toFloat())
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
                IMAGE_NAME,
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
        imageView {
            imageResource = R.drawable.icon_question_audio
            gravity = Gravity.CENTER
            backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
            val contentQuestion = "${MAIN_ROUTE_MESSAGE}"
            onClick {
                logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
                contentQuestion.let { ToSpeech.speakOut(it, textToSpeech) }
            }
        }.lparams(
            width = calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_ROUTE),
            height = calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_ROUTE)
        )
    }

    private fun _LinearLayout.showMessage(message: String, idTypeFont: Int) {
        val topSizeSpaceBetweenTitleAndAudio =
            (PixelConverter.getScreenDpHeight(context) - Constants.SIZE_OF_ACTION_BAR) * SPACE_TITLE_WITH_AUDIO
        val bottomSizeSpaceBetweenTitleAndDescription =
            (PixelConverter.getScreenDpHeight(context) - Constants.SIZE_OF_ACTION_BAR) * SPACE_DESCRIPTION_WITH_TITLE

        textView {
            gravity = Gravity.CENTER
            text = message
            textSizeDimen = R.dimen.text_size_question_route
            typeface = ResourcesCompat.getFont(
                context.applicationContext, idTypeFont
            )
        }.lparams(width = wrapContent, height = wrapContent) {
            gravity = Gravity.CENTER
            topMargin = dip(topSizeSpaceBetweenTitleAndAudio.toFloat())
            bottomMargin = dip(bottomSizeSpaceBetweenTitleAndDescription.toFloat())
        }
    }

    private fun _LinearLayout.showMessageDescription(message: String, idTypeFont: Int) {
        textView {
            gravity = Gravity.CENTER
            text = message
            textSizeDimen = R.dimen.text_size_question_route
            typeface = ResourcesCompat.getFont(
                context.applicationContext, idTypeFont
            )
        }
    }

    private fun _LinearLayout.buttonNext() {
        val height =
            (PixelConverter.getScreenDpHeight(context) - Constants.SIZE_OF_ACTION_BAR) * Constants.HEIGHT_OF_BUTTON
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
        }
    }

    private fun calculateHeightComponentsQuestion(percentageComponent: Double): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR_ROUTE) * percentageComponent
        return PixelConverter.toPixels(cardHeightInDps, context)
    }
}

package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.imageButton
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class SlideAboutUsFragment(private val myTextToSpeech: UtilsTextToSpeech) : BaseFragment() {

    companion object {
        private const val MARGIN_ABOUT_US = 0.038
        private const val MARGIN_ABOUT_US_LOGO = 0.078
        private const val SIZE_IMAGE_PERCENTAGE_ABOUT_US_AUDIO = 0.16
        private const val SIZE_IMAGE_PERCENTAGE_ABOUT_US_TOUCH = 0.22
        private const val MARGIN_TEXT_ABOUT_US = 0.166
        private const val HEIGHT_BOX_ABOUT_US = 0.28
        private const val HEIGHT_BOX_ABOUT_TOUCH = 0.36
        private const val HEIGHT_TAB_FOUR = 0.5
        private const val WIDTH_TAB_FOUR = 0.6
        private const val SCROLL_VIEW_PADDING = 0.05
        private const val INSTRUCTION_TEXT_BOTTOM_MARGIN = 0.02
        private const val SECOND_PAGE_LOGOS_BOTTOM_MARGIN = 0.010
        private const val LOGO_WIDTH_MARGIN_PERCENTAGE = 0.25
        private const val LOGO_HEIGHT_MARGIN_PERCENTAGE = 0.12
        private const val LOGO_RIGHT_MARGIN_PERCENTAGE = 0.056
        private const val MAIN_LOGO_WIDTH_MARGIN_PERCENTAGE = 0.356
        private const val MAIN_LOGO_HEIGHT_MARGIN_PERCENTAGE = 0.192
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createLayout(arguments?.getInt(Constants.POSITION_SLICE))
    }

    private fun createLayout(position: Int?): View {
        return UI {
            scrollView {
                lparams(width = matchParent, height = matchParent) {
                    padding = calculateWidthComponents(SCROLL_VIEW_PADDING)
                }
                backgroundColor = ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                verticalLayout {
                    when (position) {
                        Constants.POSITION_SLICE_PAGE_ONE -> {
                            myTextToSpeech.stop()
                            pageOne()
                        }
                        Constants.POSITION_SLICE_PAGE_TWO -> {
                            myTextToSpeech.stop()
                            pageTwo()
                        }
                        Constants.POSITION_SLICE_PAGE_THREE -> {
                            myTextToSpeech.stop()
                            pageThree()
                        }
                        Constants.POSITION_SLICE_PAGE_FOUR -> {
                            myTextToSpeech.stop()
                            pageFour()
                        }
                    }
                }.lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    private fun @AnkoViewDslMarker _LinearLayout.pageOne() {
        val msgWelcome = getString(R.string.about_us_msg_welcome)
        loadImageAudio(msgWelcome)
        textView {
            text = msgWelcome
            setTypeface(typeface, Typeface.BOLD)
            textSizeDimen = R.dimen.text_size_content_next_step
            textColor = ContextCompat.getColor(context, R.color.colorGenericTitle)
            gravity = Gravity.CENTER_HORIZONTAL
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_bold
            )
        }.lparams {
            margin = calculateWidthComponents(MARGIN_ABOUT_US)
            bottomMargin = calculateWidthComponents(MARGIN_ABOUT_US)
        }
        buildBoxWithClickInstructions(
            HEIGHT_BOX_ABOUT_US, getString(R.string.about_us_msg_touch_button_audio),
            context.getString(R.string.about_us_audio_name), SIZE_IMAGE_PERCENTAGE_ABOUT_US_AUDIO
        )
        buildBoxWithClickInstructions(
            HEIGHT_BOX_ABOUT_TOUCH, getString(R.string.about_us_msg_press_button_icon_audio),
            context.getString(R.string.about_us_touchs_name), SIZE_IMAGE_PERCENTAGE_ABOUT_US_TOUCH
        )
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildBoxWithClickInstructions(
        height: Double, textBox: String, image: String,
        heightImage: Double
    ) {
        verticalLayout {
            imageView {
                val imageUrl = resources.getIdentifier(
                    image,
                    "drawable",
                    context.applicationInfo.packageName
                )
                Glide.with(this)
                    .load(imageUrl).fitCenter()
                    .into(this)
                scaleType = ImageView.ScaleType.FIT_CENTER
            }.lparams(
                width = calculateHeightComponents(heightImage),
                height = calculateHeightComponents(heightImage)
            )
            textView {
                text = textBox
                textSizeDimen = R.dimen.text_size_question_route
                textColor = ContextCompat.getColor(context, R.color.colorGenericTitle)
                typeface = ResourcesCompat.getFont(
                    context.applicationContext,
                    R.font.proxima_nova_light
                )
                gravity = Gravity.CENTER_HORIZONTAL
            }.lparams {
                rightMargin = calculateWidthComponents(MARGIN_TEXT_ABOUT_US)
                leftMargin = calculateWidthComponents(MARGIN_TEXT_ABOUT_US)
                bottomMargin = calculateHeightComponents(INSTRUCTION_TEXT_BOTTOM_MARGIN)
            }
            backgroundDrawable = ContextCompat.getDrawable(
                context, R.drawable.drawable_about_us
            )
            gravity = Gravity.CENTER_HORIZONTAL
        }.lparams(
            width = matchParent, height = calculateHeightComponents(height)
        ) {
            topMargin = calculateWidthComponents(MARGIN_ABOUT_US)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.pageTwo() {

        val titleAboutUs = getString(R.string.about_us_title_app)
        val sectionOne = getString(R.string.about_us_title_app_section_one)
        val sectionTwo = getString(R.string.about_us_title_app_section_two)
        val sectionThree = getString(R.string.about_us_title_app_section_three)
        loadImageAudio("$sectionOne\n $sectionTwo\n $sectionThree")
        textView {
            text = titleAboutUs
            setTypeface(typeface, Typeface.BOLD)
            textSizeDimen = R.dimen.text_size_content_next_step
            textColor = ContextCompat.getColor(context, R.color.colorGenericTitle)
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_bold)
            gravity = Gravity.START
        }.lparams {
            margin = calculateWidthComponents(MARGIN_ABOUT_US)
            bottomMargin = calculateWidthComponents(MARGIN_ABOUT_US)
        }
        section(sectionOne)
        section(sectionTwo)
        section(sectionThree)
        verticalLayout {
            linearLayout {
                loadMainLogo(context.getString(R.string.home_sindicato_image_name))
            }.lparams(height = wrapContent, width = wrapContent) {
                gravity = Gravity.CENTER
            }
            linearLayout {
                loadImageLogo(context.getString(R.string.logo_care_image_name))
                loadImageLogo(context.getString(R.string.logo_mesa_trabajo_name))
                loadImageLogo(context.getString(R.string.logo_thoughtworks_name))
            }.lparams(width = wrapContent, height = wrapContent) {
                bottomMargin = calculateHeightComponents(SECOND_PAGE_LOGOS_BOTTOM_MARGIN)
                gravity = Gravity.CENTER
            }
        }

    }

    private fun @AnkoViewDslMarker _LinearLayout.loadImageLogo(logo: String) {
        imageView {
            val imageUrl = resources.getIdentifier(
                logo,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(this)
                .load(imageUrl).fitCenter()
                .into(this)
        }.lparams(
            width = calculateWidthComponents(LOGO_WIDTH_MARGIN_PERCENTAGE), height =
            calculateHeightComponents(LOGO_HEIGHT_MARGIN_PERCENTAGE)
        ) {
            rightMargin = calculateWidthComponents(LOGO_RIGHT_MARGIN_PERCENTAGE)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadMainLogo(logo: String) {
        imageView {
            val imageUrl = resources.getIdentifier(
                logo,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(this)
                .load(imageUrl).fitCenter()
                .into(this)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }.lparams(
            width = calculateWidthComponents(MAIN_LOGO_WIDTH_MARGIN_PERCENTAGE),
            height = calculateHeightComponents(MAIN_LOGO_HEIGHT_MARGIN_PERCENTAGE)
        )
    }

    private fun @AnkoViewDslMarker _LinearLayout.section(textSection: String) {
        textView {
            text = textSection
            textSizeDimen = R.dimen.text_size_question_route
            textColor = ContextCompat.getColor(context, R.color.colorGenericTitle)
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_light
            )
            gravity = Gravity.FILL
            lineSpacingExtra
        }.lparams(width = matchParent) {
            bottomMargin = calculateHeightComponents(Constants.MARGIN_SINGLE_SELECTION_QUESTION)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.footerText(textSection: String) {
        textView {
            text = textSection
            textSizeDimen = R.dimen.text_size_question_route
            textColor = ContextCompat.getColor(context, R.color.colorGenericTitle)
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_light
            )
            gravity = Gravity.CENTER
        }.lparams(width = matchParent) {
            bottomMargin = calculateWidthComponents(Constants.MARGIN_SINGLE_SELECTION_QUESTION)
            topMargin = calculateWidthComponents(Constants.MARGIN_SINGLE_SELECTION_QUESTION)
            leftMargin = calculateWidthComponents(Constants.MARGIN_SINGLE_SELECTION_QUESTION)
            rightMargin = calculateWidthComponents(Constants.MARGIN_SINGLE_SELECTION_QUESTION)
        }

    }

    private fun @AnkoViewDslMarker _LinearLayout.pageThree() {
        val textTab = getString(R.string.about_us_title_app_page_three)
        loadImageAudio(textTab)
        verticalLayout {
            imageView {
                val imageUrl = resources.getIdentifier(
                    context.getString(R.string.about_us_mesa_trabajo_name),
                    "drawable",
                    context.applicationInfo.packageName
                )
                Glide.with(this)
                    .load(imageUrl).fitCenter()
                    .into(this)
                scaleType = ImageView.ScaleType.FIT_CENTER
            }.lparams(
                width = calculateWidthComponents(WIDTH_TAB_FOUR),
                height = calculateHeightComponents(HEIGHT_TAB_FOUR)
            ) {
                topMargin = calculateWidthComponents(MARGIN_ABOUT_US)
                bottomMargin = calculateWidthComponents(MARGIN_ABOUT_US_LOGO)
                gravity = Gravity.CENTER
            }
        }
        footerText(textTab)

    }

    private fun @AnkoViewDslMarker _LinearLayout.pageFour() {
        val textTab = getString(R.string.about_us_title_app_page_fout)
        loadImageAudio(textTab)
        imageView {
            val imageUrl = resources.getIdentifier(
                context.getString(R.string.about_us_route_name),
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(this)
                .load(imageUrl).fitCenter()
                .into(this)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }.lparams(
            width = calculateWidthComponents(WIDTH_TAB_FOUR),
            height = calculateHeightComponents(HEIGHT_TAB_FOUR)
        ) {
            topMargin = calculateWidthComponents(MARGIN_ABOUT_US)
            bottomMargin = calculateWidthComponents(MARGIN_ABOUT_US_LOGO)
            gravity = Gravity.CENTER
        }
        footerText(textTab)

    }

    fun newInstance(position: Int): Fragment {
        val fragment = SlideAboutUsFragment(myTextToSpeech)
        val args = Bundle()
        args.putInt(Constants.POSITION_SLICE, position)
        fragment.arguments = args
        return fragment
    }

    private fun _LinearLayout.loadImageAudio(message: String) {
        imageButton {
            val imageUrl = resources.getIdentifier(
                "icon_question_audio",
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(this)
                .load(imageUrl).fitCenter()
                .into(this)
            gravity = Gravity.CENTER
            backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
            textToSpeech = UtilsTextToSpeech(context!!, null, null)
            onClick {
                myTextToSpeech.speakOut(message)
                logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
            }
        }.lparams(
            width = calculateHeightComponents(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION),
            height = calculateHeightComponents(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION)
        )
        {
            topMargin =
                calculateHeightComponents(Constants.MARGIN_HEIGHT_SELECTION_QUESTION) / 2
            bottomMargin =
                calculateHeightComponents(Constants.MARGIN_HEIGHT_SELECTION_QUESTION) / 2
        }
    }

    private fun calculateHeightComponents(percentageComponent: Double): Int {
        val cardHeightInDps =
            PixelConverter.getScreenDpHeight(context) * percentageComponent
        return PixelConverter.toPixels(cardHeightInDps, context)
    }

    private fun calculateWidthComponents(percentageComponent: Double): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpWidth(context)) * percentageComponent
        return PixelConverter.toPixels(cardHeightInDps, context)
    }

    override fun onDestroy() {
        super.onDestroy()
        myTextToSpeech.stop()
    }

    override fun onStop() {
        super.onStop()
        myTextToSpeech.stop()
    }
}

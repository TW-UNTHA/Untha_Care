package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.untha.R
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.utils.ToSpeech
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.imageButton
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class SlideAboutUsFragment(private val myTextToSpeech: TextToSpeech) : BaseFragment() {

    companion object {
        private const val MARGIN_ABOUT_US = 0.038
        private const val MARGIN_ABOUT_US_LOGO = 0.078
        private const val SIZE_IMAGE_PERCENTAGE_ABOUT_US_AUDIO = 0.16
        private const val SIZE_IMAGE_PERCENTAGE_ABOUT_US_TOUCH = 0.22
        private const val MARGIN_TEXT_ABOUT_US = 0.166
        private const val HEIGHT_BOX_ABOUT_US = 0.23
        private const val HEIGHT_BOX_ABOUT_TOUCH = 0.32
        private const val HEIGHT_TAB_FOUR = 0.5
        private const val WIDTH_TAB_FOUR = 0.6
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
            verticalLayout {
                backgroundColor = ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                verticalLayout {
                    when {
                        arguments?.getInt(Constants.POSITION_SLICE) ==
                                Constants.POSITION_SLICE_PAGE_ONE -> {
                            myTextToSpeech.stop()
                            pageOne()
                        }
                        arguments?.getInt(Constants.POSITION_SLICE) ==
                                Constants.POSITION_SLICE_PAGE_TWO -> {
                            myTextToSpeech.stop()
                            pageTwo()
                        }
                        arguments?.getInt(Constants.POSITION_SLICE) ==
                                Constants.POSITION_SLICE_PAGE_THREE -> {
                            myTextToSpeech.stop()
                            pageThree()
                        }
                        arguments?.getInt(Constants.POSITION_SLICE) ==
                                Constants.POSITION_SLICE_PAGE_FOUR -> {
                            myTextToSpeech.stop()
                            pageFour()
                        }
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    margin = calculateWidthComponents(MARGIN_ABOUT_US)
                }
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
        boxTrh(
            HEIGHT_BOX_ABOUT_US, getString(R.string.about_us_msg_touch_button_audio),
            R.drawable.about_us_audio, SIZE_IMAGE_PERCENTAGE_ABOUT_US_AUDIO
        )
        boxTrh(
            HEIGHT_BOX_ABOUT_TOUCH, getString(R.string.about_us_msg_press_button_icon_audio),
            R.drawable.about_us_touchs, SIZE_IMAGE_PERCENTAGE_ABOUT_US_TOUCH
        )
    }

    private fun @AnkoViewDslMarker _LinearLayout.boxTrh(
        height: Double, textBox: String, image: Int,
        heightImage: Double
    ) {
        verticalLayout {
            imageView {
                imageResource = image
            }.lparams(
                width = calculateHeightComponents(heightImage),
                height = calculateHeightComponents(
                    heightImage
                )
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
            typeface = ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_bold)
            gravity = Gravity.START
        }.lparams {
            margin = calculateWidthComponents(MARGIN_ABOUT_US)
            bottomMargin = calculateWidthComponents(MARGIN_ABOUT_US)
        }
        section(sectionOne)
        section(sectionTwo)
        section(sectionThree)
        linearLayout {
            loadImageLogo(R.drawable.home_sindicato, SIZE_IMAGE_PERCENTAGE_ABOUT_US_AUDIO)
            loadImageLogo(R.drawable.logo_care, SIZE_IMAGE_PERCENTAGE_ABOUT_US_AUDIO)
            loadImageLogo(R.drawable.logo_mesa_trabajo, SIZE_IMAGE_PERCENTAGE_ABOUT_US_AUDIO)
            loadImageLogo(R.drawable.logo_thoughtworks, SIZE_IMAGE_PERCENTAGE_ABOUT_US_AUDIO)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadImageLogo(logo: Int, widthImage: Double) {
        imageView {
            imageResource = logo
            scaleType = ImageView.ScaleType.FIT_XY
        }.lparams(
            height = calculateWidthComponents(widthImage),
            width = calculateWidthComponents(widthImage)
        ) {
            rightMargin = calculateWidthComponents(MARGIN_ABOUT_US_LOGO)
        }
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
            bottomMargin = calculateWidthComponents(Constants.MARGIN_SINGLE_SELECTION_QUESTION)
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
                imageResource = R.drawable.about_us_mesa_trabajo
                scaleType = ImageView.ScaleType.FIT_XY
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
            imageResource = R.drawable.about_us_route
            scaleType = ImageView.ScaleType.FIT_XY
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
        args.putInt("position", position)
        fragment.arguments = args
        return fragment

    }

    private fun _LinearLayout.loadImageAudio(message: String) {
        imageButton {
            scaleType = ImageView.ScaleType.FIT_CENTER
            imageResource = R.drawable.icon_question_audio
            backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
            gravity = Gravity.CENTER
            onClick {
                ToSpeech.speakOut(message, myTextToSpeech)

            }
        }.lparams(
            width = calculateHeightComponents(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION),
            height = calculateHeightComponents(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION)
        )
    }

    private fun calculateHeightComponents(percentageComponent: Double): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR_ROUTE) * percentageComponent
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

package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.parseAsHtml
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.Constants.CALCULATOR_FINIQUITO_RESULT_PAGE
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.CalculatorFiniquitoResultsViewModel
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class CalculatorFiniquitoResultFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private val calculatorFiniquitoResultsViewModel: CalculatorFiniquitoResultsViewModel by viewModel()
    private lateinit var categoriesCalculator: ArrayList<Category>
    private var contentAudio: StringBuffer = StringBuffer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculatorFiniquitoResultsViewModel.loadResultDynamicFromSharePreferences()
        calculatorFiniquitoResultsViewModel.loadResultStaticFromSharePreferences()
        calculatorFiniquitoResultsViewModel.answerSelectedCalculatorRoute()
        categoriesCalculator =
            arguments?.get(Constants.CATEGORIES_CALCULATORS) as ArrayList<Category>

    }

    override fun onResume() {
        super.onResume()
        titleActionBar()
        goBackMainScreenCategory(
            Constants.CATEGORIES_CALCULATORS,
            categoriesCalculator, R.id.calculatorsFragment, mainActivity
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity

        return createMainLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawLayoutResult(view)
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

    private fun drawLayoutResult(view: View) {
        with(view as _LinearLayout)
        {
            scrollView {
                verticalLayout {
                    loadHeaderResult(view)
                    buildRecommend()
                }
            }.lparams(width = matchParent, height = matchParent) {
                topMargin =
                    dip(calculateComponentsHeight(RouteResultsFragment.CONTAINER_MARGIN_TOP_BOTTOM))
                bottomMargin =
                    dip(calculateComponentsHeight(RouteResultsFragment.CONTAINER_MARGIN_TOP_BOTTOM))
                leftMargin =
                    dip(calculateComponentsHeight(RouteResultsFragment.CONTAINER_LATERAL_MARGIN))
                rightMargin =
                    dip(calculateComponentsHeight(RouteResultsFragment.CONTAINER_LATERAL_MARGIN))
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildRecommend() {
        calculatorFiniquitoResultsViewModel.resultCalculatorRecommend?.map { result ->
            buildTitleRecommend(result.title)
            buildContentRecommend(result.content)
            dividerLine()
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildTitleRecommend(
        title: String
    ) {
        if (title.isNotEmpty()) {
            title.let {
                contentAudio.append(title).append("\n")
                textView {
                    text = title
                    textColor =
                        ContextCompat.getColor(context, R.color.colorGenericTitle)
                    setTypeface(typeface, Typeface.BOLD)
                    textSizeDimen = R.dimen.text_size_content
                }.lparams(height = wrapContent, width = matchParent) {
                    bottomMargin =
                        dip(calculateComponentsHeight(RouteResultsFragment.SECTIONS_TOP_BOTTOM_MARGIN))
                    topMargin =
                        dip(calculateComponentsHeight(RouteResultsFragment.SECTIONS_TOP_BOTTOM_MARGIN))
                }
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildContentRecommend(
        content: String
    ): TextView {
        return textView {
            text = content.parseAsHtml()
            contentAudio.append(content.parseAsHtml()).append("\n")
            movementMethod = android.text.method.LinkMovementMethod.getInstance()
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_light)
            setLinkTextColor(ContextCompat.getColor(context, R.color.colorGenericTitle))
            textSizeDimen = R.dimen.text_size_content
        }.lparams(width = wrapContent, height = wrapContent)
    }


    private fun titleActionBar() {
        (activity as MainActivity).customActionBar(
            CALCULATOR_FINIQUITO_RESULT_PAGE,
            enableCustomBar = true,
            needsBackButton = false,
            backMethod = null,
            enableHelp = false
        )

    }

    private fun @AnkoViewDslMarker _LinearLayout.dividerLine() {
        linearLayout {
            backgroundColor =
                ContextCompat.getColor(context, R.color.colorHeaderBackground)
        }.lparams(width = matchParent, height = dip(RouteResultsFragment.DIVIDER_LINE_HEIGHT)) {
            topMargin = dip(calculateComponentsHeight(RouteResultsFragment.DIVIDER_TOP_MARGIN))
        }
    }

    private fun calculateComponentsHeight(heightComponent: Double): Float {
        return (PixelConverter.getScreenDpHeight(context) * heightComponent).toFloat()
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadHeaderResult(
        view: View
    ) {
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            drawHeaderAudioButton(view)
            drawHeaderDescription()
        }.lparams(width = wrapContent, height = wrapContent) {
            bottomMargin = dip(calculateComponentsHeight(RouteResultsFragment.HEADER_BOTTOM_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawHeaderAudioButton(
        view: View
    ) {
        imageView {
            val imageUrl = resources.getIdentifier(
                "ic_audio_without_shadow",
                "drawable",
                context.applicationInfo.packageName
            )
            textToSpeech = UtilsTextToSpeech(context!!, null, null)
            onClick {
                textToSpeech?.speakOut(contentAudio.toString())
                logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
            }
            Glide.with(view)
                .load(imageUrl)
                .into(this)
        }.lparams(
            width = dip(calculateComponentsWidth(RouteResultsFragment.AUDIO_IMAGE_VIEW_WIDTH)),
            height = dip(calculateComponentsHeight(RouteResultsFragment.AUDIO_IMAGE_VIEW_HEIGHT))
        ) {
            rightMargin =
                (dip(calculateComponentsWidth(RouteResultsFragment.AUDIO_IMAGE_VIEW_MARGIN))
                        / RouteResultsFragment.HALF_DIVIDER)
            leftMargin = dip(calculateComponentsWidth(RouteResultsFragment.AUDIO_IMAGE_VIEW_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawHeaderDescription() {
        val texto = context.getString(R.string.description_labour_result)
        textView {

            text = texto
            contentAudio.append(texto).append("\n")
            textSizeDimen = R.dimen.text_size_content
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_bold)
            textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
        }.lparams(
            width = wrapContent, height =
            dip(calculateComponentsHeight(RouteResultsFragment.AUDIO_IMAGE_VIEW_HEIGHT))
        )
    }

    private fun calculateComponentsWidth(widthComponent: Double): Float {
        return (PixelConverter.getScreenDpWidth(context) * widthComponent).toFloat()
    }
}

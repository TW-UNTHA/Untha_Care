package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.parseAsHtml
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.QuestionnaireRouteResult
import com.untha.model.transactionalmodels.RouteResult
import com.untha.model.transactionalmodels.Section
import com.untha.model.transactionalmodels.Step
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.viewmodels.RouteResultsViewModel
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import org.koin.android.viewmodel.ext.android.viewModel

class RouteResultsFragment : BaseFragment() {

    companion object {
        private const val HEADER_BOTTOM_MARGIN = 0.039
        private const val IMAGE_VIEW_RIGHT_MARGIN = 0.0056
        private const val IMAGE_VIEW_LEFT_MARGIN = 0.014
        private const val AUDIO_IMAGE_VIEW_WIDTH = 0.111
        private const val AUDIO_IMAGE_VIEW_HEIGHT = 0.0625
        private const val AUDIO_IMAGE_VIEW_MARGIN = 0.072
        private const val BUTTON_CONTAINER_HEIGHT = 0.09
        private const val CATEGORY_BUTTON_WIDTH = 0.417
        private const val RECOMMENDATION_DESCRIPTION_TOP_BOTTOM_MARGIN = 0.0125
        private const val RECOMMENDATION_DESCRIPTION_LEFT_RIGHT_MARGIN = 0.033
        private const val CONTAINER_MARGIN_TOP_BOTTOM = 0.022
        private const val CONTAINER_LATERAL_MARGIN = 0.026
        private const val FAULT_BOTTOM_MARGIN = 0.0109
        private const val DIVIDER_LINE_HEIGHT = 1
        private const val SECTIONS_TOP_BOTTOM_MARGIN = 0.022
        private const val DIVIDER_TOP_MARGIN = 0.036
        private const val CATEGORY_CARDS_RIGHT_MARGIN = 0.0167
        private const val FULL_WEIGHT_CATEGORY_BUTTON = 1.0F
        private const val IMAGE_WEIGHT_CATEGORY_BUTTON = 0.2F
        private const val TEXT_WEIGHT_CATEGORY_BUTTON = 0.8F
        private const val HALF_DIVIDER = 2
    }

    private val viewModel: RouteResultsViewModel by viewModel()

    private val isLabourRoute = true
    private val violenceLevel = "ALTO"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.retrieveRouteResults()
        viewModel.loadQuestionnaire()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.textToSpeech = TextToSpeech(context, this)
        return createMainLayout()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            firebaseAnalytics.setCurrentScreen(
                it,
                Constants.ROUTE_RESULT_PAGE,
                null
            )
        }
        viewModel.retrieveAllCategories().observe(this, Observer { queryingCategories ->
            viewModel.mapCategories(queryingCategories)
            with(view as _LinearLayout)
            {
                scrollView {
                    verticalLayout {
                        loadLabourRouteHeader(view)
                        loadRouteResultsByType(view, "fault")
                        //Recommendations
                        loadRouteResultsByType(view, "recommendation")
                        dividerLine()
                        val type = if (isLabourRoute) "LABOUR" else "VIOLENCE"
                        viewModel.getQuestionnairesByType(type).map { questionnaire ->
                            buildSections(questionnaire)
                        }
                    }
                }.lparams(width = matchParent, height = matchParent) {
                    topMargin = dip(calculateComponentsHeight(CONTAINER_MARGIN_TOP_BOTTOM))
                    bottomMargin = dip(calculateComponentsHeight(CONTAINER_MARGIN_TOP_BOTTOM))
                    leftMargin = dip(calculateComponentsHeight(CONTAINER_LATERAL_MARGIN))
                    rightMargin = dip(calculateComponentsHeight(CONTAINER_LATERAL_MARGIN))
                }
            }
        }
        )
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadRouteResultsByType(
        view: View,
        type: String
    ) {
        viewModel.getRouteResultsByType(type)?.map { result ->
            verticalLayout {
                backgroundDrawable = ContextCompat.getDrawable(
                    context, R.drawable.drawable_fault_container
                )
                drawRouteResultDescription(result)
                drawCategoryButtons(result, view)
            }.lparams(
                width = matchParent, height =
                wrapContent
            ) {
                bottomMargin = dip(calculateComponentsHeight(FAULT_BOTTOM_MARGIN))
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawCategoryButtons(
        result: RouteResult,
        view: View
    ) {
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            result.categories?.map { categoryId ->
                drawCategoryButton(categoryId, view)
            }
        }.lparams(height = wrapContent, width = wrapContent) {
            bottomMargin = dip(calculateComponentsHeight(FAULT_BOTTOM_MARGIN))
            gravity = Gravity.END
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawRouteResultDescription(
        result: RouteResult
    ) {
        textView {
            text = result.content
            textSizeDimen = R.dimen.text_size_content
            typeface =
                ResourcesCompat.getFont(
                    context.applicationContext,
                    R.font.proxima_nova_light
                )
        }.lparams(
            width = matchParent,
            height = wrapContent
        ) {
            bottomMargin =
                dip(
                    calculateComponentsHeight(
                        RECOMMENDATION_DESCRIPTION_TOP_BOTTOM_MARGIN
                    )
                )
            topMargin =
                dip(
                    calculateComponentsHeight(
                        RECOMMENDATION_DESCRIPTION_TOP_BOTTOM_MARGIN
                    )
                )
            leftMargin =
                dip(calculateComponentsWidth(RECOMMENDATION_DESCRIPTION_LEFT_RIGHT_MARGIN))
            rightMargin =
                dip(calculateComponentsWidth(RECOMMENDATION_DESCRIPTION_LEFT_RIGHT_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawCategoryButton(
        categoryId: Int,
        view: View
    ) {
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            backgroundDrawable = ContextCompat.getDrawable(
                context, R.drawable.drawable_main_route
            )
            weightSum = FULL_WEIGHT_CATEGORY_BUTTON
            drawCategoryButtonImage(view)
            drawCategoryButtonText(categoryId)
        }.lparams(
            width = dip(calculateComponentsWidth(CATEGORY_BUTTON_WIDTH)),
            height = dip(calculateComponentsHeight(BUTTON_CONTAINER_HEIGHT))
        ) {
            rightMargin = dip(calculateComponentsWidth(CATEGORY_CARDS_RIGHT_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawCategoryButtonText(
        categoryId: Int
    ) {
        textView {
            text = viewModel.getCategoryById(categoryId)?.title?.toLowerCase()?.capitalize()
            textSizeDimen = R.dimen.text_size
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_light)
            gravity = Gravity.CENTER
            textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
        }.lparams(width = dip(0), weight = TEXT_WEIGHT_CATEGORY_BUTTON, height = matchParent) {
            rightMargin = dip(calculateComponentsWidth(IMAGE_VIEW_RIGHT_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawCategoryButtonImage(
        view: View
    ) {
        imageView {
            val imageUrl = resources.getIdentifier(
                "result_category_icon",
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl).fitCenter()
                .into(this)
        }.lparams(
            width = dip(0),
            weight = IMAGE_WEIGHT_CATEGORY_BUTTON,
            height = matchParent
        ) {
            rightMargin = dip(calculateComponentsWidth(IMAGE_VIEW_RIGHT_MARGIN))
            leftMargin = dip(calculateComponentsWidth(IMAGE_VIEW_LEFT_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadLabourRouteHeader(
        view: View
    ) {
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            drawHeaderAudioButton(view)
            drawHeaderDescription()
        }.lparams(width = wrapContent, height = wrapContent) {
            bottomMargin = dip(calculateComponentsHeight(HEADER_BOTTOM_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawHeaderDescription() {
        textView {
            text =
                if (isLabourRoute) context.getString(R.string.description_labour_result)
                else context.getString(R.string.description_violence_result) + violenceLevel
            textSizeDimen = R.dimen.text_size_content
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_bold)
            textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
        }.lparams(
            width = wrapContent, height =
            dip(calculateComponentsHeight(AUDIO_IMAGE_VIEW_HEIGHT))
        )
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
            Glide.with(view)
                .load(imageUrl)
                .into(this)
        }.lparams(
            width = dip(calculateComponentsWidth(AUDIO_IMAGE_VIEW_WIDTH)),
            height = dip(calculateComponentsHeight(AUDIO_IMAGE_VIEW_HEIGHT))
        ) {
            rightMargin = dip(calculateComponentsWidth(AUDIO_IMAGE_VIEW_MARGIN)) / HALF_DIVIDER
            leftMargin = dip(calculateComponentsWidth(AUDIO_IMAGE_VIEW_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.dividerLine() {
        linearLayout {
            backgroundColor =
                ContextCompat.getColor(context, R.color.colorHeaderBackground)
        }.lparams(width = matchParent, height = dip(DIVIDER_LINE_HEIGHT)) {
            topMargin = dip(calculateComponentsHeight(DIVIDER_TOP_MARGIN))
        }
    }

    private fun calculateComponentsHeight(heightComponent: Double): Float {
        return (PixelConverter.getScreenDpHeight(context) * heightComponent).toFloat()
    }

    private fun calculateComponentsWidth(widthComponent: Double): Float {
        return (PixelConverter.getScreenDpWidth(context) * widthComponent).toFloat()
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildSections(questionnaireRouteResult: QuestionnaireRouteResult) {
        questionnaireRouteResult.sections.map { section ->
            val title = section.title
            if (title.isNotEmpty()) {
                title.let {
                    textView {
                        text = title
                        textColor =
                            ContextCompat.getColor(context, R.color.colorGenericTitle)
                        setTypeface(typeface, Typeface.BOLD)
                        textSizeDimen = R.dimen.text_size_content
                    }.lparams(height = wrapContent, width = matchParent) {
                        bottomMargin = dip(calculateComponentsHeight(SECTIONS_TOP_BOTTOM_MARGIN))
                        topMargin = dip(calculateComponentsHeight(SECTIONS_TOP_BOTTOM_MARGIN))
                    }
                }
            }
            buildSteps(section)
            dividerLine()
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildSteps(
        section: Section
    ) {
        section.steps?.let { steps ->
            steps.map { step ->
                buildRoundedCircleTextView(step)
                buildStepDescriptionTextView(step)
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildRoundedCircleTextView(
        step: Step
    ) {
        step.stepId?.let {
            textView {
                text = step.stepId.toString()
                gravity = Gravity.CENTER
                textColor =
                    ContextCompat.getColor(context, android.R.color.white)
                setTypeface(typeface, Typeface.BOLD)
                backgroundDrawable = ContextCompat.getDrawable(
                    context,
                    R.drawable.circular_background
                )
                textSizeDimen = R.dimen.text_size_content
            }.lparams(width = wrapContent, height = wrapContent) {
                bottomMargin = dip(calculateComponentsHeight(SECTIONS_TOP_BOTTOM_MARGIN))
                topMargin = dip(calculateComponentsHeight(SECTIONS_TOP_BOTTOM_MARGIN))
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildStepDescriptionTextView(
        step: Step
    ): TextView {
        return textView {
            text = step.description.parseAsHtml()
            movementMethod = android.text.method.LinkMovementMethod.getInstance()
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_light)
            setLinkTextColor(ContextCompat.getColor(context, R.color.colorGenericTitle))
            textSizeDimen = R.dimen.text_size_content
        }.lparams(width = wrapContent, height = wrapContent)
    }
}

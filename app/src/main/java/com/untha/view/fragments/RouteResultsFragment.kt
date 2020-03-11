package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.parseAsHtml
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.QuestionnaireRouteResult
import com.untha.model.transactionalmodels.RouteResult
import com.untha.model.transactionalmodels.Section
import com.untha.model.transactionalmodels.Step
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.CategoryViewModel
import com.untha.viewmodels.RouteResultsViewModel
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
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

class RouteResultsFragment : BaseFragment() {

    companion object {
        private const val HEADER_BOTTOM_MARGIN = 0.039
        private const val IMAGE_VIEW_RIGHT_MARGIN = 0.0056
        private const val IMAGE_VIEW_LEFT_MARGIN = 0.014
        private const val AUDIO_IMAGE_VIEW_WIDTH = 0.111
        private const val AUDIO_IMAGE_VIEW_HEIGHT = 0.0625
        private const val AUDIO_IMAGE_VIEW_MARGIN = 0.072
        private const val BUTTON_CONTAINER_HEIGHT = 0.11
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
        private const val NON_FAULT_IMAGE_HEIGHT = 0.342
        private const val VIOLENCE_TYPE = "VIOLENCE"
        private const val LABOUR_TYPE = "LABOUR"
        private const val RECOMMENDATION_TYPE = "recommendation"
        private const val FAULT_TYPE = "fault"
        private const val CALCULATOR_ID = 5
    }

    private val viewModel: RouteResultsViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()

    //    private var isLabourRoute: Boolean = false
    private var violenceLevel: String? = null
    private var contentAudio: StringBuffer = StringBuffer()
    private lateinit var typeRoute: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.retrieveRouteResults()
        viewModel.loadQuestionnaire()
        val bundle = arguments
        typeRoute = bundle?.get(Constants.IS_LABOUR_ROUTE) as String
        violenceLevel = viewModel.getHigherViolenceLevel()
    }

    override fun onResume() {
        super.onResume()
        loadTitleRoute()
        setCloseButtonAction()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                Constants.ROUTE_RESULT_PAGE + " " +
                        if (typeRoute.equals(Constants.ROUTE_LABOUR)) LABOUR_TYPE else VIOLENCE_TYPE,
                null
            )
        }

        if (areThereFaultsOrRecommendations()) {
            drawLayoutWithNoFaults(view)
        } else {
            drawLayoutWithFaults(view)
        }
    }

    private fun areThereFaultsOrRecommendations() =
        (typeRoute.equals(Constants.ROUTE_LABOUR) && viewModel.routeResults.isNullOrEmpty()) or
                (typeRoute.equals(Constants.ROUTE_VIOLENCE) && violenceLevel.isNullOrEmpty())

    private fun drawLayoutWithFaults(view: View) {
        viewModel.retrieveAllCategories().observe(this, Observer { queryingCategories ->
            viewModel.mapCategories(queryingCategories)
            with(view as _LinearLayout)
            {
                scrollView {
                    verticalLayout {
                        loadLabourRouteHeader(view)
                        if (typeRoute.equals(Constants.ROUTE_LABOUR)) {
                            loadRouteResultsByType(view, FAULT_TYPE)
                            //Recommendations
                            loadRouteResultsByType(view, RECOMMENDATION_TYPE)
                            dividerLine()
                        }
                        var questionnaires: List<QuestionnaireRouteResult>? = null
                        if (typeRoute.equals(Constants.ROUTE_LABOUR)) {
                            questionnaires = viewModel.getQuestionnairesByType(LABOUR_TYPE)
                        } else {
                            violenceLevel?.let {
                                questionnaires =
                                    viewModel.getQuestionnairesByTypeAndCode(VIOLENCE_TYPE, it)
                            }
                        }
                        questionnaires?.map { questionnaire ->
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

    private fun drawLayoutWithNoFaults(view: View) {
        with(view as _LinearLayout) {
            verticalLayout {
                val headerText =
                    if (typeRoute.equals(Constants.ROUTE_LABOUR))
                        R.string.not_labour_rights_violated
                    else R.string.not_violence_suffered
                loadLabourRouteHeader(view, headerText)
                imageView {
                    val imageUrl = resources.getIdentifier(
                        "route_positive_result",
                        "drawable",
                        context.applicationInfo.packageName
                    )
                    Glide.with(view)
                        .load(imageUrl).fitCenter()
                        .into(this)
                }.lparams(
                    width = wrapContent,
                    height = dip(calculateComponentsHeight(NON_FAULT_IMAGE_HEIGHT))
                ) {
                    rightMargin = dip(calculateComponentsWidth(IMAGE_VIEW_RIGHT_MARGIN))
                    leftMargin = dip(calculateComponentsWidth(IMAGE_VIEW_LEFT_MARGIN))
                    gravity = Gravity.CENTER
                }
            }.lparams(height = matchParent, width = matchParent) {
                topMargin = dip(calculateComponentsHeight(CONTAINER_MARGIN_TOP_BOTTOM))
                bottomMargin = dip(calculateComponentsHeight(CONTAINER_MARGIN_TOP_BOTTOM))
                leftMargin = dip(calculateComponentsHeight(CONTAINER_LATERAL_MARGIN))
                rightMargin = dip(calculateComponentsHeight(CONTAINER_LATERAL_MARGIN))
            }
        }
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
                contentAudio.append(result.content).append("\n")
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
            backgroundDrawable = ContextCompat.getDrawable(
                context, R.drawable.drawable_main_route
            )
            linearLayout {
                orientation = LinearLayout.HORIZONTAL
                backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
                weightSum = FULL_WEIGHT_CATEGORY_BUTTON
                isFocusable = true
                isClickable = true
                drawCategoryButtonImage(view)
                val category = viewModel.getCategoryById(categoryId)
                category?.let {
                    drawCategoryButtonText(it)
                    setOnClickListener {
                        logAnalyticsSelectContentWithId(category.title, ContentType.CATEGORY)
                        if (categoryId != CALCULATOR_ID) {
                            navigateToCategoriesInformation(category)
                        } else {
                            Toast.makeText(
                                context,
                                getString(R.string.coming_soon),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }.lparams(
                width = dip(calculateComponentsWidth(CATEGORY_BUTTON_WIDTH)),
                height = dip(calculateComponentsHeight(BUTTON_CONTAINER_HEIGHT))
            ) {
                rightMargin = dip(calculateComponentsWidth(CATEGORY_CARDS_RIGHT_MARGIN))
            }
        }
    }

    private fun navigateToCategoriesInformation(category: Category) {
        val categoryBundle = Bundle().apply {
            putSerializable(Constants.CATEGORIES, viewModel.categories as ArrayList)
            putSerializable(
                Constants.CATEGORY_PARAMETER,
                category
            )
        }
        NavHostFragment.findNavController(this@RouteResultsFragment)
            .navigate(
                R.id.genericInfoFragment,
                categoryBundle,
                navOptions,
                null
            )
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawCategoryButtonText(
        category: Category
    ) {
        textView {
            text = category.title.toLowerCase().capitalize()
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
        view: View,
        textId: Int? = null
    ) {
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            drawHeaderAudioButton(view)
            drawHeaderDescription(textId)
        }.lparams(width = wrapContent, height = wrapContent) {
            bottomMargin = dip(calculateComponentsHeight(HEADER_BOTTOM_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawHeaderDescription(textId: Int?) {
        val texto = if (textId == null) {
            if (typeRoute.equals(Constants.ROUTE_LABOUR)) context.getString(R.string.description_labour_result)
            else context.getString(R.string.description_violence_result) + " $violenceLevel"
        } else {
            context.getString(textId)
        }
        textView {

            text = texto
            contentAudio.append(texto).append("\n")
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
            textToSpeech = UtilsTextToSpeech(context!!, null, null)
            onClick {
                textToSpeech?.speakOut(contentAudio.toString())
                logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
            }
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
            contentAudio.append(title).append("\n")
            if (title.isNotEmpty()) {
                title.let {
                    textView {
                        text = title
                        textColor =
                            ContextCompat.getColor(context, R.color.colorGenericTitle)
                        setTypeface(typeface, Typeface.BOLD)
                        textSizeDimen = R.dimen.text_size_content
                    }.lparams(height = wrapContent, width = matchParent) {
                        bottomMargin =
                            dip(calculateComponentsHeight(SECTIONS_TOP_BOTTOM_MARGIN))
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
                contentAudio.append(step.stepId.toString()).append("\n")
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
            contentAudio.append(step.description.parseAsHtml()).append("\n")
            movementMethod = android.text.method.LinkMovementMethod.getInstance()
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_light)
            setLinkTextColor(ContextCompat.getColor(context, R.color.colorGenericTitle))
            textSizeDimen = R.dimen.text_size_content
        }.lparams(width = wrapContent, height = wrapContent)
    }

    private fun loadTitleRoute() {
        val nameRoute =
            if (typeRoute.equals(Constants.ROUTE_LABOUR)) Constants.NAME_SCREEN_LABOUR_ROUTE
            else Constants.NAME_SCREEN_VIOLENCE_ROUTE

        (activity as MainActivity).customActionBar(
            nameRoute,
            enableCustomBar = true,
            needsBackButton = false,
            backMethod = null,
            enableHelp = false
        )

    }

    override fun isRouteResultScreen(): Boolean {
        return true
    }

    private fun setCloseButtonAction() {
        val layoutActionBar = (activity as MainActivity).supportActionBar?.customView
        val categoriesRoutes = Bundle().apply {
            putSerializable(
                Constants.CATEGORIES_ROUTES,
                categoryViewModel.loadCategoriesRoutesFromSharedPreferences()
            )
        }

        val close = layoutActionBar?.findViewById(R.id.icon_go_back_route) as ImageView
        close.onClick {
            view?.findNavController()
                ?.navigate(
                    R.id.mainRouteFragment,
                    categoriesRoutes,
                    navOptionsToBackNavigation,
                    null
                )
            logAnalyticsCustomContentTypeWithId(ContentType.CLOSE, FirebaseEvent.CLOSE)
        }
    }
}

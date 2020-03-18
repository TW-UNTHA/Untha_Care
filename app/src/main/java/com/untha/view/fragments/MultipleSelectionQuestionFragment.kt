package com.untha.view.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.Route
import com.untha.model.transactionalmodels.RouteOption
import com.untha.model.transactionalmodels.RouteQuestion
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.MultipleSelectionOption
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.view.extension.loadHorizontalProgressBar
import com.untha.viewmodels.CategoryViewModel
import com.untha.viewmodels.MultipleSelectionQuestionViewModel
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.allCaps
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageButton
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.margin
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
import java.io.Serializable

class MultipleSelectionQuestionFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private val viewModel: MultipleSelectionQuestionViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()
    private lateinit var route: Route
    private var goTo: Int? = null
    private var isNoneOfTheAboveSelected = false
    private var noneOfTheAboveTextView: TextView? = null
    private var routeQuestion: RouteQuestion? = null
    private val options = mutableListOf<MultipleSelectionOption>()
    private var position: Int = 0
    private var questionAdvance: Int = 1
    private var remainingQuestion: Int = 0
    private var hint: String? = null
    private lateinit var typeRoute: String
    private var categoriesCalculator: ArrayList<Category>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        goTo = bundle?.get(Constants.ROUTE_QUESTION_GO_TO) as Int
        typeRoute = viewModel.getTypeRoute(bundle)

        route = viewModel.loadRoute(typeRoute, bundle)
        viewModel.loadQuestion(goTo, route)
        questionAdvance = bundle.getInt(Constants.QUESTION_ADVANCE).inc()
        routeQuestion = viewModel.question
        val optionWithMaxRemaining = routeQuestion?.options?.maxBy { it.remaining }
        remainingQuestion = optionWithMaxRemaining?.remaining ?: 0

        categoriesCalculator = if (bundle?.get(Constants.CATEGORIES_CALCULATORS) != null)
            bundle?.get(Constants.CATEGORIES_CALCULATORS) as ArrayList<Category> else null


    }

    private fun loadTitleRoute(typeRoute: String) {
        val nameScreen = when (typeRoute) {
            Constants.ROUTE_LABOUR -> Constants.NAME_SCREEN_LABOUR_ROUTE
            Constants.ROUTE_VIOLENCE -> Constants.NAME_SCREEN_VIOLENCE_ROUTE
            else -> Constants.NAME_SCREEN_CALCULATOR_ROUTE
        }

        (activity as MainActivity).customActionBar(
            nameScreen,
            enableCustomBar = true,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null
        )
    }

    private fun goBackScreenRoutes() {
        val categoriesRoutes = Bundle().apply {
            putSerializable(
                Constants.CATEGORIES_ROUTES,
                categoryViewModel.loadCategoriesRoutesFromSharedPreferences()
            )
        }
        val layoutActionBar = (activity as MainActivity).supportActionBar?.customView
        val close = layoutActionBar?.findViewById(R.id.icon_go_back_route) as ImageView
        close.onClick {
            view?.findNavController()
                ?.navigate(
                    R.id.mainRouteFragment,
                    categoriesRoutes,
                    navOptionsToBackNavigation,
                    null
                )
            logAnalyticsSelectContentWithId(hint, ContentType.CLOSE)

        }
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
        activity?.let {
            firebaseAnalytics.setCurrentScreen(
                it,
                Constants.MULTIPLE_QUESTION_PAGE + "_${routeQuestion?.id}",
                null
            )
        }
        with(view as _LinearLayout) {
            val percentageProgressBar = viewModel.calculatePercentQuestionsAnswered(
                questionAdvance, remainingQuestion, typeRoute
            )
            verticalLayout {
                loadHorizontalProgressBar(percentageProgressBar)
                verticalLayout {
                    loadImageAudio()
                }
                verticalLayout {
                    verticalLayout { question() }
                    verticalLayout { explanationQuestion() }
                    verticalLayout { buildAnswersLayout() }


                }.lparams(height = dip(0), weight = 0.9f, width = matchParent)
                if (typeRoute == Constants.ROUTE_CALCULATOR) {
                    verticalLayout { bottomHelpMessage() }
                }
                loadNextButton(view)
            }.lparams(width = matchParent, height = matchParent) {
                margin =
                    calculateWidthComponentsQuestion()
            }
        }
        loadTitleRoute(typeRoute)
        if (typeRoute == Constants.ROUTE_CALCULATOR) {
            goBackMainScreenCategory(
                Constants.CATEGORIES_CALCULATORS,
                categoriesCalculator!!, R.id.calculatorsFragment, mainActivity
            )
        } else {
            goBackScreenRoutes()

        }
    }

    private fun _LinearLayout.explanationQuestion() {
        routeQuestion?.explanation?.let {
            textView {
                text = routeQuestion?.explanation
                textSizeDimen = R.dimen.text_size_question_explanation
                textColor =
                    ContextCompat.getColor(context, R.color.colorGenericTitle)
                typeface = ResourcesCompat.getFont(
                    context.applicationContext,
                    R.font.proxima_nova_light
                )
                gravity = Gravity.CENTER_HORIZONTAL
            }.lparams(width = matchParent, height = matchParent)
        }
    }

    private fun calculateWidthComponentsQuestion(): Int {
        val cardHeightInDps =
            PixelConverter.getScreenDpWidth(context) * Constants.MARGIN_SINGLE_SELECTION_QUESTION
        return PixelConverter.toPixels(cardHeightInDps, context)
    }

    private fun createMainLayout(
    ): View {
        return UI {
            verticalLayout {
                backgroundColor =
                    ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                weightSum = Constants.FULL_SCREEN_WEIGHT
                lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildAnswersLayout() {
        val margin =
            if (typeRoute == Constants.ROUTE_CALCULATOR)
                0.toFloat()
            else calculateHeightComponentsQuestion(Constants.MARGIN_BOTTOM_PERCENTAGE_ANSWERS_LAYOUT)

        scrollView {
            verticalLayout {
                routeQuestion?.options?.let { questionOption ->
                    val maxIndex = questionOption.size - 1
                    for ((index, _) in questionOption.iterator().withIndex()) {
                        val firstElementIndex = index * 2
                        val secondElementIndex = (index * 2) + 1
                        if (firstElementIndex < maxIndex) {
                            buildLayoutWithTwoOptions(
                                questionOption[firstElementIndex],
                                questionOption[secondElementIndex],
                                secondElementIndex == maxIndex
                            )
                        } else if (firstElementIndex == maxIndex) {
                            buildLayoutWithOneOption(
                                questionOption[firstElementIndex]
                            )
                        }
                    }
                }
            }
        }.lparams(width = matchParent, height = wrapContent) {
            bottomMargin = dip(margin)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildLayoutWithOneOption(
        routeOption: RouteOption
    ) {
        linearLayout {
            weightSum = Constants.FULL_SCREEN_WEIGHT
            orientation = LinearLayout.HORIZONTAL
            var isNoneOfAbove = true
            if (typeRoute.equals(Constants.ROUTE_VIOLENCE)) {
                isNoneOfAbove = false
            }
            loadOption(
                routeOption,
                1,
                position,
                isNoneOfAbove
            )
            position += 1
        }.lparams(width = matchParent, height = wrapContent) {
            gravity = Gravity.CENTER
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildLayoutWithTwoOptions(
        firstRouteOption: RouteOption,
        secondRouteOption: RouteOption,
        isNoneOfAbove: Boolean
    ) {
        linearLayout {
            weightSum = Constants.FULL_SCREEN_WEIGHT
            orientation = LinearLayout.HORIZONTAL
            loadOption(
                firstRouteOption,
                2,
                position,
                false
            )
            position += 1
            loadOption(
                secondRouteOption,
                2,
                position,
                isNoneOfAbove
            )
            position += 1
        }.lparams(width = matchParent, height = wrapContent)
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadNextButton(view: View) {
        verticalLayout {
            textView {
                isClickable = true
                isFocusable = true

                onClick {
                    val isANormalOptionSelected = options.firstOrNull { option ->
                        option.isSelected
                    }

                    if (isANormalOptionSelected == null && !isNoneOfTheAboveSelected) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.choose_at_least_one_option),
                            Toast.LENGTH_LONG
                        ).show()
                        registerAnalyticsEvent(isNoneOfTheAboveSelected)

                    } else {
                        viewModel.getFaultForQuestion(false, typeRoute, options)
                        registerAnalyticsEvent(false)
                        viewModel.loadQuestion(routeQuestion?.goTo, route)
                        val isSingle = viewModel.isSingleQuestion(viewModel.question?.type)
                        val questionGoToInfo = mapParameters(isSingle)
                        manageGoToQuestion(questionGoToInfo, route, view, viewModel)
                    }
                }
                text = context.getString(R.string.next)
                textColor =
                    ContextCompat.getColor(context, R.color.colorWhiteText)
                textSizeDimen = R.dimen.text_size_content
                typeface = ResourcesCompat.getFont(
                    context.applicationContext,
                    R.font.proxima_nova_bold
                )
                backgroundDrawable =
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.drawable_multiple_option_next_button
                    )
                gravity = Gravity.CENTER
            }.lparams(
                width = matchParent,
                height = dip(calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION))
            )
        }.lparams(
            width = matchParent,
            weight = Constants.NEXT_BUTTON_WEIGHT
        ) {
            bottomMargin = dip(marginBottomNext())
            topMargin = dip(marginBottomNext())
            rightMargin = dip(calculateOptionContainerWidthMargin()) / 2
            leftMargin = dip(calculateOptionContainerWidthMargin())
        }
    }

    private fun mapParameters(isSingle: Boolean): Map<String, Serializable?> {
        return mapOf(
            "goTo" to routeQuestion?.goTo,
            "isSingle" to isSingle,
            "getTypeRoute" to typeRoute,
            "questionAdvance" to questionAdvance,
            "CATEGORIES_CALCULATORS" to categoriesCalculator
        )
    }

    private fun marginBottomNext(): Float {
        return calculateHeightComponentsQuestion(
            Constants.MARGIN_BOTTOM_PERCENTAGE_NEXT_BUTTON
        )
    }

    private fun registerAnalyticsEvent(isNoneOfAbove: Boolean) {
        val hint = viewModel.getHintForSelectedOption(isNoneOfAbove)
        this.hint = hint
        if (hint != null) {
            logAnalyticsCustomEvent(hint)
        }
    }

    private fun contentAudioOptions(): String {
        var contentOptions = ""
        routeQuestion?.options?.map { option ->
            contentOptions += "${option.value} \n"
        }
        return contentOptions
    }

    private fun _LinearLayout.question() {
        verticalLayout {
            textView {
                text = routeQuestion?.content
                textSizeDimen = R.dimen.text_size_content
                textColor =
                    ContextCompat.getColor(context, R.color.colorGenericTitle)
                typeface = ResourcesCompat.getFont(
                    context.applicationContext,
                    R.font.proxima_nova_bold
                )
                gravity = Gravity.CENTER
            }.lparams(width = wrapContent, height = wrapContent) {
                gravity = Gravity.CENTER
                bottomMargin =
                    calculateWidthComponentsQuestion()
                topMargin =
                    calculateWidthComponentsQuestion()
            }
        }
    }

    private fun _LinearLayout.loadOption(
        option: RouteOption,
        elementsInLayout: Int,
        position: Int,
        isNoneOfAbove: Boolean
    ) {
        linearLayout {
            this.gravity = Gravity.CENTER
            val actualTextView = textView {
                text = option.value
                textSizeDimen = R.dimen.text_size_content
                textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
                allCaps = false
                this.gravity = Gravity.CENTER
                typeface = ResourcesCompat.getFont(
                    context.applicationContext,
                    R.font.proxima_nova_light
                )
                isClickable = true
                isFocusable = true
                backgroundDrawable = ContextCompat.getDrawable(
                    context, R.drawable.drawable_main_route
                )
                optionClick(isNoneOfAbove, position)
                adjustTextSize()
            }.lparams(width = matchParent, height = matchParent) {
                leftMargin = dip(calculateWidthMarginQuestionText())
                rightMargin = dip(calculateWidthMarginQuestionText())
            }
            addDataToExternalCollections(
                isNoneOfAbove,
                position,
                actualTextView,
                option
            )
        }.lparams(
            weight = if (elementsInLayout == 2) Constants.HALF_SCREEN_WEIGHT else
                Constants.FULL_SCREEN_WEIGHT,
            width = dip(0),
            height = dip(
                calculateHeightComponentsQuestion
                    (Constants.HEIGHT_MULTIPLE_OPTION_PERCENTAGE)
            )
        )
    }

    private fun addDataToExternalCollections(
        isNoneOfAbove: Boolean,
        position: Int,
        tv: TextView,
        option: RouteOption
    ) {
        if (!isNoneOfAbove) {
            options.add(MultipleSelectionOption(position, false, tv, option.result, option.hint, option.remaining))
        } else {
            noneOfTheAboveTextView = tv
        }
    }

    private fun @AnkoViewDslMarker TextView.adjustTextSize() {
        viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    textSizeDimen = if (lineCount <= 2) {
                        R.dimen.text_size_content
                    } else {
                        R.dimen.text_size_content_for_many_characters
                    }
                    return true
                }
            }
        )
    }

    private fun @AnkoViewDslMarker TextView.optionClick(
        isNoneOfAbove: Boolean,
        position: Int
    ) {
        onClick {
            if (isNoneOfAbove) {
                isNoneOfAboveClick()
            } else {
                normalOptionClick(position)
            }
        }
    }

    private fun @AnkoViewDslMarker TextView.normalOptionClick(position: Int) {
        val clickedOption = options.firstOrNull { option ->
            option.position == position
        }
        clickedOption?.let { option ->
            if (option.isSelected) {
                setUnselectedColorSchema(this)
                option.isSelected = false
                option.textView = this
                option.remaining = option.remaining
            } else {
                setSelectedColorSchema(this)
                option.isSelected = true
                option.textView = this
                noneOfTheAboveTextView?.let {
                    setUnselectedColorSchema(it)
                    isNoneOfTheAboveSelected = false
                }
                option.remaining = option.remaining
            }
        }
    }

    private fun @AnkoViewDslMarker TextView.isNoneOfAboveClick() {
        if (isNoneOfTheAboveSelected) {
            setUnselectedColorSchema(this)
            isNoneOfTheAboveSelected = false
        } else {
            setSelectedColorSchema(this)
            isNoneOfTheAboveSelected = true
            options.map { option ->
                option.textView?.let { setUnselectedColorSchema(it) }
            }
            options.map { option ->
                option.isSelected = false
            }
        }

    }

    private fun
            calculateOptionContainerWidthMargin(): Float {
        val width = PixelConverter.getScreenDpWidth(context)
        return (width * Constants.MARGIN_LEFT_RIGHT_MULTIPLE_OPTION_SCREEN_PERCENTAGE).toFloat()
    }

    private fun _LinearLayout.loadImageAudio() {
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
            val textQuestion = routeQuestion?.content
            val contentQuestion = "$textQuestion ${contentAudioOptions()}"

            textToSpeech = UtilsTextToSpeech(context!!, null, null)
            onClick {
                textToSpeech?.speakOut(contentQuestion)
                logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
            }
        }.lparams(
            width = dip(calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION)),
            height = dip(calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION))
        )
        {
            topMargin =
                dip(calculateHeightComponentsQuestion(Constants.MARGIN_HEIGHT_SELECTION_QUESTION))
            bottomMargin =
                dip(calculateHeightComponentsQuestion(Constants.MARGIN_HEIGHT_SELECTION_QUESTION))
        }
    }

    private fun calculateHeightComponentsQuestion(heightComponent: Double): Float {
        return ((PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR_ROUTE) * heightComponent).toFloat()
    }

    private fun calculateWidthMarginQuestionText(): Float {
        return (PixelConverter.getScreenDpWidth(context) * Constants.MARGIN_QUESTION_TEXT).toFloat()
    }

    private fun setSelectedColorSchema(textView: TextView) {
        context?.let { context ->
            textView.backgroundDrawable =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.drawable_main_route_selected
                )
            textView.textColor =
                ContextCompat.getColor(context, R.color.colorWhiteText)
            isNoneOfTheAboveSelected = false
        }
    }

    private fun setUnselectedColorSchema(textView: TextView) {
        context?.let { ctx ->
            textView.backgroundDrawable =
                ContextCompat.getDrawable(
                    ctx,
                    R.drawable.drawable_main_route
                )
            textView.textColor =
                ContextCompat.getColor(ctx, R.color.colorHeaderBackground)
        }
    }

    private fun _LinearLayout.bottomHelpMessage() {
        textView {
            text = routeQuestion?.recommend
            textSizeDimen = R.dimen.text_size_question_explanation
            textColor =
                ContextCompat.getColor(context, R.color.colorGenericTitle)
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.helvetica_light_oblique
            )
            gravity = Gravity.LEFT
        }.lparams {
            rightMargin = dip(calculateOptionContainerWidthMargin()) / 2
            leftMargin = dip(calculateOptionContainerWidthMargin())
        }
    }
}

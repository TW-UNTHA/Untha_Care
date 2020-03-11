package com.untha.view.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Route
import com.untha.model.transactionalmodels.RouteQuestion
import com.untha.utils.Constants
import com.untha.utils.Constants.NAME_SCREEN_CALCULATOR_ROUTE
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.view.extension.loadHorizontalProgressBar
import com.untha.viewmodels.CategoryViewModel
import com.untha.viewmodels.SingleSelectionQuestionViewModel
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
import org.jetbrains.anko.themedButton
import org.jetbrains.anko.verticalLayout
import org.koin.android.viewmodel.ext.android.viewModel

class SingleSelectionQuestionFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var route: Route
    private var routeQuestion: RouteQuestion? = null
    private var goTo: Int? = null
    private val questionViewModel: SingleSelectionQuestionViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()
    private var remainingQuestion: Int = 0
    private var questionAdvance: Int = 1
    private var hint: String? = null
    private lateinit var typeRoute: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        goTo = bundle?.get("goTo") as Int
        typeRoute = questionViewModel.getTypeRoute(bundle)

        route = questionViewModel.loadRoute(typeRoute, bundle)
        questionAdvance += bundle.getInt(Constants.QUESTION_ADVANCE).inc()
        questionViewModel.loadQuestion(goTo, route)
        routeQuestion = questionViewModel.question
        val optionWithMaxRemaining = routeQuestion?.options?.maxBy { it.remaining }
        remainingQuestion = optionWithMaxRemaining?.remaining ?: 0
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
                Constants.SINGLE_QUESTION_PAGE + "_${routeQuestion?.id}",
                null
            )
        }
        with(view as _LinearLayout) {
            val percentageProgressBar = questionViewModel.calculatePercentQuestionsAnswered(
                questionAdvance, remainingQuestion, typeRoute
            )
            verticalLayout {
                loadHorizontalProgressBar(percentageProgressBar)
                verticalLayout {
                    loadImageAudio()
                }
                verticalLayout {
                    question()
                }
                verticalLayout {
                    explanationQuestion()
                }
                val margin =
                    calculateHeightComponentsQuestion(Constants.MARGIN_BOTTOM_PERCENTAGE_ANSWERS_LAYOUT)

                scrollView {
                    verticalLayout {
                        drawOptionsAnswer(view)
                    }

                }.lparams(
                    width = matchParent,
                    height = calculateHeightComponentsQuestion(Constants.SIZE_SCROLL_VIEW_SINGLE_OPTION)
                ) {
                    bottomMargin = dip(margin)
                }
                if (typeRoute == Constants.ROUTE_CALCULATOR) {
                    verticalLayout {

                        bottomHelpMessage()
                    }
                }

            }.lparams(width = matchParent, height = matchParent) {
                margin =
                    calculateWidthComponentsQuestion()
            }
        }
        val nameScreen = if (typeRoute == Constants.ROUTE_CALCULATOR) NAME_SCREEN_CALCULATOR_ROUTE
        else Constants.NAME_SCREEN_LABOUR_ROUTE

        mainActivity.customActionBar(
            nameScreen,
            enableCustomBar = true,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null
        )
        goBackScreenRoutes()
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawOptionsAnswer(
        view: View
    ) {
        var topMarginCalculated: Int = 0
        if (typeRoute != Constants.ROUTE_CALCULATOR) {
            topMarginCalculated =
                calculateHeightComponentsQuestion(Constants.MARGIN_HEIGHT_QUESTION)
        }
        val sizeOptions = routeQuestion?.options?.size ?: 0

        if (hasTwoOptions(sizeOptions)) {
            linearLayout {
                optionsAnswer(styleDisplayOptions(sizeOptions), view)
            }.lparams {
                topMargin = topMarginCalculated

            }
        } else {
            verticalLayout {
                optionsAnswer(styleDisplayOptions(sizeOptions), view)
            }.lparams {
                topMargin = topMarginCalculated
            }
        }

    }

    private fun styleDisplayOptions(numOptions: Int): Int {
        if (numOptions == Constants.STYLE_ANSWER_TWO_OPTION) {
            return calculateWidthOption() / Constants.STYLE_ANSWER_TWO_OPTION
        }
        return calculateWidthOption()
    }

    private fun hasTwoOptions(numOptions: Int): Boolean {
        if (numOptions == Constants.STYLE_ANSWER_TWO_OPTION) {
            return true
        }
        return false
    }

    private fun goBackScreenRoutes() {
        val categoriesRoutes = Bundle().apply {
            putSerializable(
                Constants.CATEGORIES_ROUTES,
                categoryViewModel.loadCategoriesRoutesFromSharedPreferences()
            )
        }
        val layoutActionBar = mainActivity.supportActionBar?.customView
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
            width = calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION),
            height = calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION)
        )
        {
            topMargin =
                calculateHeightComponentsQuestion(Constants.MARGIN_HEIGHT_SELECTION_QUESTION)
            bottomMargin =
                calculateHeightComponentsQuestion(Constants.MARGIN_HEIGHT_SELECTION_QUESTION)
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
        textView {
            text = routeQuestion?.content
            textSizeDimen = R.dimen.text_size_question_route
            textColor =
                ContextCompat.getColor(context, R.color.colorGenericTitle)
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_bold
            )
            gravity = Gravity.CENTER_HORIZONTAL
        }.lparams(width = matchParent, height = matchParent) {
            bottomMargin =
                calculateWidthComponentsQuestion()
            topMargin =
                calculateWidthComponentsQuestion()
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

    private fun
            calculateOptionContainerWidthMargin(): Float {
        val width = PixelConverter.getScreenDpWidth(context)
        return (width * Constants.MARGIN_LEFT_RIGHT_MULTIPLE_OPTION_SCREEN_PERCENTAGE).toFloat()
    }

    private fun _LinearLayout.optionsAnswer(width: Int, view: View) {
        routeQuestion?.options?.map { option ->
            verticalLayout {
                themedButton(theme = R.style.MyButtonStyle) {
                    text = option.value
                    textSizeDimen = R.dimen.text_size_question_route
                    textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
                    allCaps = false
                    backgroundDrawable = ContextCompat.getDrawable(
                        context, R.drawable.drawable_main_route
                    )
                    typeface = ResourcesCompat.getFont(
                        context.applicationContext,
                        R.font.proxima_nova_light
                    )
                    onClick {
                        option.hint?.let {
                            logAnalyticsCustomEvent(it)
                            hint = it
                        }
                        var faultAnswer: String = ""
                        when (typeRoute) {
                            Constants.ROUTE_LABOUR -> faultAnswer =
                                Constants.FAULT_ANSWER_ROUTE_LABOUR
                            Constants.ROUTE_VIOLENCE -> faultAnswer =
                                Constants.FAULT_ANSWER_ROUTE_VIOLENCE
                            Constants.ROUTE_CALCULATOR -> faultAnswer =
                                Constants.FAULT_ANSWER_ROUTE_CALCULATOR
                        }
                        option.result?.let {
                            questionViewModel.saveAnswerOption(
                                it,
                                faultAnswer
                            )
                        }
                        questionViewModel.loadQuestion(option.goTo, route)
                        val routeQuestionGoTo = questionViewModel.question
                        val isSingle = questionViewModel.isSingleQuestion(routeQuestionGoTo?.type)
                        val questionGoToInfo = mapOf(
                            "goTo" to option.goTo,
                            "isSingle" to isSingle,
                            "getTypeRoute" to typeRoute,
                            "questionAdvance" to questionAdvance

                        )
                        manageGoToQuestion(questionGoToInfo, route, view, questionViewModel)
                    }
                }.lparams(
                    width = width,
                    height = calculateHeightComponentsQuestion(Constants.SIZE_HEIGHT_PERCENTAGE_OPTION_BUTTON)
                )

            }
        }

    }

    private fun calculateHeightComponentsQuestion(percentageComponent: Double): Int {
        val cardHeightInDps =
            PixelConverter.getScreenDpHeight(context) * percentageComponent
        return PixelConverter.toPixels(cardHeightInDps, context)
    }

    private fun calculateWidthComponentsQuestion(): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpWidth(context)) * Constants.MARGIN_SINGLE_SELECTION_QUESTION
        return PixelConverter.toPixels(cardHeightInDps, context)
    }

    private fun calculateWidthOption(): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpWidth(context))
        val marginLateralSide =
            calculateWidthComponentsQuestion() *
                    Constants.DUPLICATE_MARGIN_LATERAL
        return PixelConverter.toPixels(cardHeightInDps.toDouble(), context) - marginLateralSide
    }
}

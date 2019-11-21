package com.untha.view.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import com.untha.R
import com.untha.model.transactionalmodels.Route
import com.untha.model.transactionalmodels.RouteQuestion
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.ToSpeech
import com.untha.view.activities.MainActivity
import com.untha.view.extension.loadHorizontalProgressBar
import com.untha.viewmodels.CategoryViewModel
import com.untha.viewmodels.SingleSelectionQuestionViewModel
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.allCaps
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.imageButton
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
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
    private var isLabourRoute: Boolean = false
    private var remainingQuestion: Int = 0
    private var questionAdvance: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        goTo = bundle?.get("goTo") as Int
        isLabourRoute = questionViewModel.isLabourRoute(bundle)
        route = questionViewModel.loadRoute(isLabourRoute, bundle)
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
        this.textToSpeech = TextToSpeech(context, this)
        return createMainLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryViewModel.getCategoryRoutes()
        activity?.let {
            firebaseAnalytics.setCurrentScreen(
                it,
                Constants.SINGLE_QUESTION_PAGE + "_${routeQuestion?.id}",
                null
            )
        }
        with(view as _LinearLayout) {
            val percentageProgressBar = questionViewModel.calculatePercentQuestionsAnswered(
                questionAdvance, remainingQuestion
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
                val sizeOptions = routeQuestion?.options?.size ?: 0
                if (hasTwoOptions(sizeOptions)) {
                    linearLayout {
                        options(styleDisplayOptions(sizeOptions), view)
                    }.lparams {
                        topMargin =
                            calculateHeightComponentsQuestion(Constants.MARGIN_HEIGHT_QUESTION)
                    }
                } else {
                    verticalLayout {
                        options(styleDisplayOptions(sizeOptions), view)
                    }.lparams {
                        topMargin =
                            calculateHeightComponentsQuestion(Constants.MARGIN_HEIGHT_QUESTION)
                    }
                }

            }.lparams(width = matchParent, height = matchParent) {
                margin =
                    calculateWidthComponentsQuestion()
            }
        }
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_LABOUR_ROUTE,
            enableCustomBar = true,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null
        )
        goBackScreenRoutes()
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
            gravity = Gravity.CENTER
            scaleType = ImageView.ScaleType.FIT_CENTER
            imageResource = R.drawable.icon_question_audio
            backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
            val textQuestion = routeQuestion?.content
            val contentQuestion = "$textQuestion ${contentAudioOptions()}"
            onClick {
                logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
                contentQuestion.let { ToSpeech.speakOut(it, textToSpeech) }
            }
        }.lparams(
            width = calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION),
            height = calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_QUESTION)
        )
        {
            topMargin =
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
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_light
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
                typeface = ResourcesCompat.getFont(
                    context.applicationContext,
                    R.font.proxima_nova_light
                )
                gravity = Gravity.CENTER_HORIZONTAL
            }.lparams(width = matchParent, height = matchParent)
        }
    }

    private fun _LinearLayout.options(width: Int, view: View) {
        routeQuestion?.options?.map { option ->
            verticalLayout {
                themedButton(theme = R.style.MyButtonStyle) {
                    text = option.value
                    textSizeDimen = R.dimen.text_size_question_route
                    textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
                    allCaps = false
                    typeface = ResourcesCompat.getFont(
                        context.applicationContext,
                        R.font.proxima_nova_bold
                    )
                    onClick {
                        option.hint?.let { logAnalyticsCustomEvent(it) }
                        when {
                            isLabourRoute -> option.result?.let {
                                questionViewModel.saveAnswerOption(
                                    it,
                                    Constants.FAULT_ANSWER_ROUTE_LABOUR
                                )
                            }
                            else -> option.result?.let {
                                questionViewModel.saveAnswerOption(
                                    it,
                                    Constants.FAULT_ANSWER_ROUTE_VIOLENCE
                                )
                            }
                        }
                        questionViewModel.loadQuestion(option.goTo, route)
                        val routeQuestionGoTo = questionViewModel.question
                        val isSingle = questionViewModel.isSingleQuestion(routeQuestionGoTo?.type)
                        val questionGoToInfo = mapOf(
                            "goTo" to option.goTo,
                            "isSingle" to isSingle,
                            "isLabourRoute" to isLabourRoute,
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
            (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR_ROUTE) * percentageComponent
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

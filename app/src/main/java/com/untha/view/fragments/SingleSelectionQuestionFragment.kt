package com.untha.view.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import com.untha.R
import com.untha.model.transactionalmodels.Route
import com.untha.model.transactionalmodels.RouteOption
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
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.themedButton
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import org.koin.android.viewmodel.ext.android.viewModel


class SingleSelectionQuestionFragment: BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var routeLabour: Route
    private var routeQuestion: RouteQuestion? = null
    private var goTo:Int = Constants.START_QUESTION_ROUTE_LABOUR
    private val questionViewModel:SingleSelectionQuestionViewModel? by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        routeLabour = bundle?.get(Constants.ROUTE_LABOUR) as Route
        routeQuestion = questionViewModel?.loadQuestionLabourRoute(goTo, routeLabour.questions)
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
        Constants.ROUTE_LABOUR
        with(view as _LinearLayout) {
            verticalLayout {
                loadHorizontalProgressBar(Constants.TEMPORAL_LOAD_PROGRESS_BAR)
                verticalLayout {
                    loadImageAudio()
                }
                verticalLayout {
                    question()
                }
                verticalLayout {
                    routeQuestion?.options?.map{option->
                        option(option)
                    }
                }
            }.lparams(width= matchParent, height = matchParent) {
                margin=calculateWidthComponentsQuestion(Constants.MARGIN_SINGLE_SELECTION_QUESTION)
            }
        }
        goBackRouteLabour(view)
    }

    private fun goBackRouteLabour(v:View) {
        val layout = ActionBar.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val view = layoutInflater.inflate(R.layout.action_bar, null)
        val close = view.findViewById(R.id.icon_go_back_route) as ImageView
        //val title = view.findViewById(R.id.title_actiov_bar) as TextView
        //title.text=Constants.NAME_SCREEN_LABOUR_ROUTE

        val categoriesRoutes = Bundle().apply {
            putSerializable(
                Constants.CATEGORIES_ROUTES,
                categoryViewModel.getCategoryRoutes()
            )
        }

        close.onClick {
                        v.findNavController()
                .navigate(R.id.mainRouteFragment, categoriesRoutes, navOptions, null)

        }

        mainActivity.supportActionBar?.setTitle(Constants.NAME_SCREEN_LABOUR_ROUTE)
        mainActivity.supportActionBar?.setCustomView(view, layout)
        mainActivity.supportActionBar?.setDisplayShowCustomEnabled(true)

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
            val contentQuestion ="${textQuestion} ${contentAudioOptions()}"
            onClick {
                logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
                contentQuestion.let {ToSpeech.speakOut(it, textToSpeech) }
            }
        }.lparams(width=calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_ROUTE),
            height=calculateHeightComponentsQuestion(Constants.SIZE_IMAGE_PERCENTAGE_AUDIO_ROUTE))
        {
            topMargin= calculateHeightComponentsQuestion(Constants.MARGIN_HEIGHT_SELECTION_QUESTION)
        }
    }

    private fun contentAudioOptions(): String {
        var contentOptions = ""
        routeQuestion?.options?.map {option->
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
        }.lparams(width = wrapContent, height = wrapContent) {
            gravity = Gravity.CENTER
            bottomMargin = calculateHeightComponentsQuestion(Constants.MARGIN_HEIGHT_SELECTION_QUESTION)
        }
    }

    private fun _LinearLayout.option(option: RouteOption) {
        themedButton(theme = R.style.MyButtonStyle){
            text= option.value
            textSizeDimen = R.dimen.text_size_question_route
            textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
            allCaps = false
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_bold
            )
            onClick {
                option.hint?.let { it -> logAnalyticsCustomEvent(it) }

                option.result?.let {
                        it -> questionViewModel?.saveAnswerOption(it)
                }
            }

        }.lparams(width = matchParent,
            height=calculateHeightComponentsQuestion(Constants.SIZE_HEIGHT_PERCENTAGE_OPTION_BUTTON))
    }

    private fun calculateHeightComponentsQuestion(percentageComponent: Double): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR_ROUTE) * percentageComponent
        return PixelConverter.toPixels(cardHeightInDps, context)
    }

    private fun calculateWidthComponentsQuestion(percentageComponent: Double): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpWidth(context) -
                    Constants.SIZE_OF_ACTION_BAR_ROUTE) * percentageComponent
        return PixelConverter.toPixels(cardHeightInDps, context)
    }


}

package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.parseAsHtml
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.News
import com.untha.model.transactionalmodels.NewsWrapper
import com.untha.model.transactionalmodels.Step
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.AboutUsViewModel
import com.untha.viewmodels.MainViewModel
import com.untha.viewmodels.NewsViewModel
import kotlinx.serialization.json.Json
import me.linshen.retrofit2.adapter.ApiSuccessResponse
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.allCaps
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageButton
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.rightPadding
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.themedButton
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class NewsFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private val newsViewModel: NewsViewModel by viewModel()
    lateinit var listParagraph: MutableList<String>
    private val viewModel: AboutUsViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity
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
            firebaseAnalytics.setCurrentScreen(it, Constants.NEWS_PAGE, null)
        }
        if (newsViewModel.isRetrievingDataFromInternet()) {
            newsViewModel.getNews().observe(this, Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        val sharedPreferencesResult =
                            newsViewModel.sharedPreferences.getString(Constants.NEWS, "")
                        if (!sharedPreferencesResult.isNullOrEmpty()) {
                            val resultWrapperSharedPreferences =
                                Json.parse(NewsWrapper.serializer(), sharedPreferencesResult)
                            if (response.body.version > resultWrapperSharedPreferences.version) {
                                newsViewModel.saveSharePreferences(response.body)
                            }
                            newsViewModel.loadResultDynamicFromSharePreferences()
                            drawNews(view)
                        } else {
                            newsViewModel.saveSharePreferences(response.body)
                            newsViewModel.loadResultDynamicFromSharePreferences()
                            drawNews(view)
                        }
                    }
                    else                  -> {
                        Timber.e("Error en la llamada de load news $response")
                    }
                }
            })
        } else {
            mainViewModel.loadDefaultBase(context!!, Constants.NEWS, R.raw.news)
            newsViewModel.loadResultDynamicFromSharePreferences()
            drawNews(view)
        }
    }


    private fun drawNews(view: View) {
        val informationToSpeech = contentAudioOptions().toString().toLowerCase()
        listParagraph = getListOfText(informationToSpeech)

        with(view as _LinearLayout) {
            verticalLayout {
                val marginTop = calculateTopMargin()
                val marginLeft = calculateMarginLeftAndRight()
                loadImageAudio()
                scrollView {
                    newsViewModel.news?.map {
                        verticalLayout {
                            drawTitle(it)
                            drawSubtitle(it)
                            buildSteps(it)
                            verticalLayout{
                            buttonNext(view)
                            }
                        }
                        backgroundColor =
                            ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                        lparams(width = matchParent, height = matchParent) {
                            leftPadding = marginLeft * Constants.GENERIC_STEP_MARGIN_MULTIPLIER
                            rightPadding = marginLeft * Constants.GENERIC_STEP_MARGIN_MULTIPLIER
                            bottomPadding = marginTop * Constants.GENERIC_STEP_MARGIN_MULTIPLIER
                        }
                    }
                }
            }.lparams(width = matchParent, height = matchParent)
        }
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_NEWS,
            enableCustomBar = false,
            needsBackButton = false,
            enableHelp = false,
            backMethod = null
        )
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
            val contentQuestion = "${contentAudioOptions()}"

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

    private fun @AnkoViewDslMarker _LinearLayout.drawSubtitle(it: News) {
        textView {
            text = it.subtitle
            textSizeDimen = R.dimen.text_size_content_for_many_characters
            textColor =
                ContextCompat.getColor(context, R.color.colorGenericTitle)
            gravity = Gravity.CENTER
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawTitle(it: News) {
        textView {
            text = it.title.parseAsHtml()
            textSizeDimen = R.dimen.text_size_main_component
            textColor =
                ContextCompat.getColor(context, R.color.colorGenericTitle)

            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
        }
    }

    private fun _LinearLayout.calculateMarginLeftAndRight(): Int {
        val widthFormula =
            (PixelConverter.getScreenDpWidth(context)) * Constants.MARGIN_WIDTH_PERCENTAGE
        return PixelConverter.toPixels(widthFormula, context)
    }

    private fun @AnkoViewDslMarker _LinearLayout.calculateTopMargin(): Int {
        val topMarginDps =
            PixelConverter.getScreenDpHeight(context) * Constants.MARGIN_TOP_PERCENTAGE
        return PixelConverter.toPixels(topMarginDps, context)
    }


    private fun @AnkoViewDslMarker _LinearLayout.buildSteps(
        news: News
    ) {
        news.steps?.let { steps ->
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
                bottomMargin = dip(calculateTopMargin())
                topMargin = dip(calculateTopMargin())
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

    private fun contentAudioOptions(): StringBuffer {
        val contentOptions = StringBuffer()
        getTextInformation().let {
            contentOptions.append(it)
            contentOptions.append("\n")
        }
        return contentOptions
    }

    private fun getTextInformation(): StringBuffer {
        val contentOptions = StringBuffer()
        newsViewModel.news?.forEach { information: News ->
            information.let {
                contentOptions.append(information.title.parseAsHtml())
                contentOptions.append("\n")
                contentOptions.append(information.subtitle.parseAsHtml())
                contentOptions.append("\n")
                information.steps?.forEach { step: Step ->
                    contentOptions.append(step.stepId)
                    contentOptions.append("\n")
                    contentOptions.append(step.description)
                    contentOptions.append("\n")
                }
            }
        }
        return contentOptions
    }

    private fun getListOfText(informationToSpeech: String): MutableList<String> {
        var textNews = informationToSpeech
        textNews = textNews.parseAsHtml().toString()
        val listParagraph: MutableList<String> = mutableListOf()
        val separated = textNews.split(".")
        separated.mapIndexed { index, item ->
            if (item != " ") {
                listParagraph.add(index, item)
            }
        }
        return listParagraph
    }

    private fun _LinearLayout.buttonNext(view: _LinearLayout) {
        val height = getHeightElementInDp(Constants.HEIGHT_OF_BUTTON)
        themedButton(theme = R.style.ButtonNext) {
            text = newsViewModel.buttonTitle
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
                nextButtonClick(view)
            }
        }.lparams(width = matchParent, height = dip(height.toFloat())){
            topPadding = calculateTopMargin() * Constants.GENERIC_STEP_MARGIN_MULTIPLIER
            topMargin = calculateTopMargin()

        }

    }

    private fun nextButtonClick(view: _LinearLayout) {
        val bundle = Bundle().apply {
            putBoolean("showScreen", false)
        }

        if (!viewModel.loadAboutUsFromSharedPreferences()) {
            view.findNavController()
                .navigate(
                    R.id.trhAboutInstructions,
                    null,
                    navOptionsToBackNavigation,
                    null
                )
        } else {
            view.findNavController()
                .navigate(
                    R.id.categoryFragment,
                    bundle,
                    navOptions,
                    null
                )
        }
          }
    private fun getHeightElementInDp(percentageComponent: Double) =
        (PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR_ROUTE) * percentageComponent
}

package com.untha.view.fragments

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.parseAsHtml
import androidx.navigation.findNavController
import com.untha.R
import com.untha.model.transactionalmodels.News
import com.untha.model.transactionalmodels.Step
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.view.extension.loadHorizontalProgressBarDinamic
import com.untha.view.extension.loadPlayAndPauseIcon
import com.untha.view.extension.putImageOnTheWidget
import com.untha.viewmodels.NewsViewModel
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.rightPadding
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class NewsFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private val newsViewModel: NewsViewModel by viewModel()
    lateinit var listParagraph: MutableList<String>
    lateinit var playAndPauseIcon: ImageView
    private var horizontalProgressBar: ProgressBar? = null
    private var isPaused = false
    var oldProgress = 0
    var indexCurrently = 0
    private lateinit var thread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsViewModel.loadResultDynamicFromSharePreferences()

        newsViewModel.news
    }

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
        val informationToSpeech = contentAudioOptions().toString().toLowerCase()
        listParagraph = getListOfText(informationToSpeech)
        textToSpeech = UtilsTextToSpeech(context!!, listParagraph, ::reproduceAudioCallBack)

        with(view as _LinearLayout) {
            verticalLayout {
                val imageSizeInDps = (PixelConverter.getScreenDpHeight(context) -
                        Constants.SIZE_OF_ACTION_BAR) * Constants.GENERIC_PERCENTAGE_PLAYER_HEADER
                val imageHeight = PixelConverter.toPixels(imageSizeInDps, context)
                val marginTop = calculateTopMargin()
                val marginLeft = calculateMarginLeftAndRight()
                relativeLayout {
                    imageView {
                        scaleType = ImageView.ScaleType.FIT_XY
                        val image = "home_rutas"
                        putImageOnTheWidget(image, view)
                        backgroundColor =
                            ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                    }.lparams(width = matchParent, height = matchParent)
                    playAndPauseIcon = loadPlayAndPauseIcon(
                        view
                    )
                    playAndPauseIcon.setOnClickListener {
                        playAndPauseAudio()
                    }
                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = imageHeight)
                horizontalProgressBar = loadHorizontalProgressBarDinamic(0)
                scrollView {
                    newsViewModel.news?.map {
                        verticalLayout {
                            drawTitle(it)
                            drawSubtitle(it)
                            buildSteps(it)
                            buildButtonNextStep(view, newsViewModel)
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

        super.onViewCreated(view, savedInstanceState)
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_NEWS,
            enableCustomBar = false,
            needsBackButton = false,
            enableHelp = false,
            backMethod = null
        )
        thread = incrementProgressBarThread(horizontalProgressBar)
        thread.start()
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
                contentOptions.append(information.title)
                contentOptions.append("\n")
                contentOptions.append(information.subtitle)
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

    private fun reproduceAudioCallBack(
        indexParameter: Int,
        listParagraph: MutableList<String>
    ): Pair<Boolean, String?> {
        if (!isPaused) {
            indexCurrently = indexParameter + 1
            setProgress(indexCurrently, listParagraph.size)
        }
        return if (indexCurrently < listParagraph.size) {
            isPaused to listParagraph[indexCurrently]
        } else {
            textToSpeech?.stop()
            (context as Activity).runOnUiThread {
                playAndPauseIcon.apply {
                    putImageOnTheWidget(Constants.PLAY_ICON, this)
                }
            }
            true to null
        }
    }

    private fun setProgress(progress: Int, total: Int) {
        oldProgress = progress * Constants.PROGRESS_TOTAL / total
    }

    private fun playAndPauseAudio(
    ) {
        val isSpeaking = textToSpeech?.isSpeaking() ?: false
        if (isSpeaking) {
            playAndPauseIcon.apply {
                putImageOnTheWidget(Constants.PLAY_ICON, this)
            }
            isPaused = true
            textToSpeech?.stop()
        } else {
            val reproduce = getStringToReproduce()
            if (reproduce == null) {
                playAndPauseIcon.apply {
                    putImageOnTheWidget(Constants.PLAY_ICON, this)
                }
                isPaused = true
                textToSpeech?.stop()

            } else {
                playAndPauseIcon.apply {
                    putImageOnTheWidget(Constants.STOP_ICON, this)
                }
                textToSpeech?.speakOut(reproduce)
                isPaused = false
            }
        }
    }

    private fun getStringToReproduce(): String? {
        return try {
            listParagraph[indexCurrently]
        } catch (e: IndexOutOfBoundsException) {
            restartAudioProgress()
        }
    }

    private fun restartAudioProgress(): String {
        textToSpeech?.restartPosition()
        indexCurrently = 0
        oldProgress = 0
        horizontalProgressBar?.progress = 0
        thread.interrupt()
        thread = incrementProgressBarThread(horizontalProgressBar)
        thread.start()
        return listParagraph[indexCurrently]
    }

    private fun incrementProgressBarThread(progressBar: ProgressBar?): Thread {
        return object : Thread() {
            override fun run() {
                progressBar?.let {
                    var value = 0
                    while (progressBar.progress < Constants.PROGRESS_TOTAL) {
                        context?.let {
                            (context as Activity).runOnUiThread {
                                if (progressBar.progress >= Constants.PROGRESS_TOTAL) {
                                    this.interrupt()
                                } else if (value != oldProgress) {
                                    value = oldProgress
                                    progressBar.progress = value
                                }
                            }
                        }
                        try {
                            sleep(Constants.TIME_SLEEP)
                        } catch (e: InterruptedException) {
                            Timber.e("Se detiene el hilo de la barra de progreso ${e.message}")
                        }
                    }
                }
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildButtonNextStep(
        view: View,
        newsViewModel: NewsViewModel
    ): LinearLayout {
        val heightFormula =
            (PixelConverter.getScreenDpHeight(context)) * Constants.SIZE_HEIGHT_NEWS

        return linearLayout {
            orientation = LinearLayout.HORIZONTAL
            isClickable = true
            backgroundDrawable = ContextCompat.getDrawable(
                context, R.drawable.drawable_style_corner_and_shadow_of_button
            )
            linearLayout {
                orientation = LinearLayout.HORIZONTAL

                buildBlockTextNextSteps(newsViewModel)

                setOnClickListener {
                    val bundle = Bundle().apply {
                        putBoolean("showScreen", false)
                    }
                    view.findNavController()
                        .navigate(
                            R.id.categoryFragment,
                            bundle,
                            navOptions,
                            null
                        )
                }
            }.lparams(
                width = matchParent, height = matchParent
            )
        }.lparams(
            width = matchParent, height = dip(heightFormula.toInt())
        ) {
            topMargin = dip(Constants.TOP_MARGIN_NEXT_STEP)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildBlockTextNextSteps(
        newsViewModel: NewsViewModel
    ) {
        val widthBlockDp =
            (PixelConverter.getScreenDpWidth(context)) * Constants.WIDTH_BLOCK_OF_TEXT
        val widthBlockPixel = PixelConverter.toPixels(widthBlockDp, context)
        verticalLayout {
            buildNextStepTitle(newsViewModel.buttonTitle!!)
            buildNextStepSubtitle(newsViewModel.buttonSubtitle!!)
        }.lparams(
            width = matchParent,
            height = dip(widthBlockPixel)
        ) {
            bottomMargin = dip(Constants.TOP_MARGIN_NEXT_STEP)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildNextStepTitle(buttonTitle: String) {
        textView {
            gravity = Gravity.CENTER
            text = buttonTitle
            textSizeDimen = R.dimen.text_size_question_route
            textColor =
                ContextCompat.getColor(
                    context,
                    R.color.colorGenericTitle
                )
            setTypeface(typeface, Typeface.BOLD)
        }.lparams(height = wrapContent, width = matchParent) {
            rightMargin = dip(calculateTopMargin())
            bottomMargin = dip(calculateTopMargin()) / Constants.MARGIN_BOTTOM_MIDDLE_NEXT_STEP
            topMargin = dip(calculateTopMargin())
        }
    }

    fun @AnkoViewDslMarker _LinearLayout.buildNextStepSubtitle(buttonSubtitle: String) {
        textView {
            textSizeDimen = R.dimen.text_size
            gravity = Gravity.CENTER
            text = buttonSubtitle
            textColor =
                ContextCompat.getColor(
                    context,
                    R.color.colorGenericTitle
                )
            setTypeface(typeface, Typeface.NORMAL)
        }
    }
}

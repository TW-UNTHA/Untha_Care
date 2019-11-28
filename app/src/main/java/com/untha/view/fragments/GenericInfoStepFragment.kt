package com.untha.view.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
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
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.CategoryInformation
import com.untha.model.transactionalmodels.Section
import com.untha.model.transactionalmodels.Step
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.PixelConverter
import com.untha.utils.PixelConverter.toPixels
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.view.extension.buildImageNextStep
import com.untha.view.extension.buildNextStepTitle
import com.untha.view.extension.getSelectableItemBackground
import com.untha.view.extension.loadHorizontalProgressBarDinamic
import com.untha.view.extension.loadImageBackground
import com.untha.view.extension.loadPlayAndPauseIcon
import com.untha.view.extension.putImageOnTheWidget
import com.untha.viewmodels.GenericInfoStepViewModel
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
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

class GenericInfoStepFragment : BaseFragment() {

    private lateinit var category: Category
    private var categories: List<Category>? = null
    private lateinit var mainActivity: MainActivity
    private val viewModel: GenericInfoStepViewModel by viewModel()

    var oldProgress = 0
    var indexCurrently = 0
    lateinit var listParagraph: MutableList<String>
    lateinit var playAndPauseIcon: ImageView
    private var horizontalProgressBar: ProgressBar? = null
    private lateinit var thread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        category = bundle?.get(Constants.CATEGORY_PARAMETER) as Category
        categories = bundle.get(Constants.CATEGORIES) as List<Category>?
        categories?.let {
            viewModel.getNextSteps(category, it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity
        return createMainLayout()
    }

    private fun getListOfText(informationToSpeech: String): MutableList<String> {
        var textCategory = informationToSpeech
        textCategory = textCategory.parseAsHtml().toString()
        val listParagraph: MutableList<String> = mutableListOf()
        val separated = textCategory.split(".")
        separated.mapIndexed { index, item ->
            if (item != " ") {
                listParagraph.add(index, item)
            }
        }
        return listParagraph
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            firebaseAnalytics.setCurrentScreen(it, "${category.title} Page", null)
        }
        val informationToSpeech = contentAudioOptions().toString()
        listParagraph = getListOfText(informationToSpeech)
        textToSpeech = UtilsTextToSpeech(context!!, listParagraph, ::reproduceAudioCallBack)

        with(view as _LinearLayout) {
            verticalLayout {
                val imageSizeInDps = (PixelConverter.getScreenDpHeight(context) -
                        Constants.SIZE_OF_ACTION_BAR) * Constants.GENERIC_PERCENTAGE_PLAYER_HEADER
                val imageHeight = toPixels(imageSizeInDps, context)
                val marginTop = calculateTopMargin()
                val marginLeft = calculateMarginLeftAndRight()
                relativeLayout {
                    loadImageBackground(view, category)
                    playAndPauseIcon = loadPlayAndPauseIcon(
                        view,
                        textToSpeech!!, ::getStringToReproduce
                    )
                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = imageHeight)
                horizontalProgressBar = loadHorizontalProgressBarDinamic(0)
                scrollView {
                    verticalLayout {
                        loadInformationDescription(view)
                    }
                    backgroundColor =
                        ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                    lparams(width = matchParent, height = matchParent) {
                        leftPadding = marginLeft * Constants.GENERIC_STEP_MARGIN_MULTIPLIER
                        rightPadding = marginLeft * Constants.GENERIC_STEP_MARGIN_MULTIPLIER
                        bottomPadding = marginTop * Constants.GENERIC_STEP_MARGIN_MULTIPLIER
                    }
                }
            }.lparams(width = matchParent, height = matchParent)
        }
        mainActivity.customActionBar(
            category.information?.get(0)?.screenTitle.toString(),
            enableCustomBar = false, needsBackButton = true, enableHelp = false, backMethod = null
        )
        thread = incrementProgressBarThread(horizontalProgressBar)
        thread.start()

    }

    override fun onStart() {
        super.onStart()
        (context as Activity).runOnUiThread {
            playAndPauseIcon.apply {
                //                this.performClick()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        thread.interrupt()
    }

    override fun onStop() {
        super.onStop()
        thread.interrupt()
    }

    private fun reproduceAudioCallBack(
        indexParameter: Int,
        listParagraph: MutableList<String>
    ): String? {
        indexCurrently = indexParameter + 1
        setProgress(indexCurrently, listParagraph.size)
        return if (indexCurrently < listParagraph.size) {
            listParagraph[indexCurrently]
        } else {
            textToSpeech?.stop()
            (context as Activity).runOnUiThread {
                playAndPauseIcon.apply {
                    putImageOnTheWidget(Constants.PLAY_ICON, this)
                }
            }
            null
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
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun setProgress(progress: Int, total: Int) {
        oldProgress = progress * Constants.PROGRESS_TOTAL / total
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildButtonNextStep(
        view: View,
        categoryNextStep: Category
    ): LinearLayout {
        val heightFormula =
            (PixelConverter.getScreenDpHeight(context)) * Constants.SIZE_HEIGHT_NEXT_STEP

        return linearLayout {
            orientation = LinearLayout.HORIZONTAL
            backgroundColor =
                ContextCompat.getColor(context, R.color.colorGenericTitle)
            isClickable = true
            backgroundDrawable = ContextCompat.getDrawable(
                context, R.drawable.drawable_style_corner_and_shadow_of_button
            )
            linearLayout {
                orientation = LinearLayout.HORIZONTAL

                buildImageNextStep(view, categoryNextStep)
                buildBlockTextNextSteps(categoryNextStep)

                setOnClickListener {
                    onItemClick(categoryNextStep, categories as ArrayList<Category>, view)
                }
                backgroundResource = getSelectableItemBackground().resourceId
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
        categoryNextStep: Category
    ) {
        val widthBlockDp =
            (PixelConverter.getScreenDpWidth(context)) * Constants.WIDTH_BLOCK_OF_TEXT
        val widthBlockPixel = toPixels(widthBlockDp, context)
        verticalLayout {
            buildNextStepSubtitle(categoryNextStep)
            buildNextStepTitle(categoryNextStep)
        }.lparams(
            width = matchParent,
            height = dip(widthBlockPixel)
        ) {
            bottomMargin = dip(Constants.TOP_MARGIN_NEXT_STEP)
        }
    }

    private fun _LinearLayout.calculateMarginLeftAndRight(): Int {
        val widthFormula =
            (PixelConverter.getScreenDpWidth(context)) * Constants.MARGIN_WIDTH_PERCENTAGE
        return toPixels(widthFormula, context)
    }

    private fun @AnkoViewDslMarker _LinearLayout.calculateTopMargin(): Int {
        val topMarginDps =
            PixelConverter.getScreenDpHeight(context) * Constants.MARGIN_TOP_PERCENTAGE
        return toPixels(topMarginDps, context)
    }

    private fun createMainLayout(): View {
        return UI {
            verticalLayout {
                backgroundColor =
                    ContextCompat.getColor(context, R.color.colorBackgroundGenericInfo)
                lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildNextStepSubtitle(categoryNextStep: Category) {
        textView {
            val title = categoryNextStep.subtitle ?: Constants.SUBTITLE_NEXT_STEP
            gravity = Gravity.START
            text = title
            textSizeDimen = R.dimen.text_size_content_next_step
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

    private fun @AnkoViewDslMarker _LinearLayout.buildSections(categoryInformation: CategoryInformation) {
        categoryInformation.sections?.map { section ->
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
                        bottomMargin = dip(calculateTopMargin())
                        topMargin = dip(calculateTopMargin())
                    }
                }
            }
            buildSteps(section)
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

    private fun @AnkoViewDslMarker _LinearLayout.loadInformationDescription(view: View) {
        category.information?.mapIndexed { index, categoryInformation ->
            val description = categoryInformation.description?.parseAsHtml()
            if (description?.isNotEmpty() == true) {
                description.let {
                    textView {
                        text = description
                        textSizeDimen = R.dimen.text_size_content
                        typeface = ResourcesCompat.getFont(
                            context.applicationContext,
                            R.font.proxima_nova_light
                        )
                    }.lparams(height = wrapContent, width = matchParent) {
                        topMargin = dip(calculateTopMargin())
                    }
                }
            }

            buildSections(categoryInformation)
            if (viewModel.categoriesNextStep.isNotEmpty()) {
                try {
                    buildButtonNextStep(
                        view,
                        viewModel.categoriesNextStep[index]
                    )
                } catch (ex: ArrayIndexOutOfBoundsException) {
                    Timber.e(ex)
                }
            }
            drawLineNextStep(index)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawLineNextStep(
        index: Int
    ) {
        if (category.information?.size!! > 1 && index < category.information?.size!! - 1)
            linearLayout {
                drawLine(R.color.colorGenericTitle, Constants.SIZE_LINE_HEIGHT_NEXT_STEP)
            }.lparams {
                topMargin = dip(calculateTopMargin())
            }
    }

    private fun _LinearLayout.drawLine(color: Int, height: Int) {
        imageView {
            val widthLine = toPixels(PixelConverter.getScreenDpWidth(context).toDouble(), context)
            val bitmap =
                Bitmap.createBitmap(widthLine, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(ContextCompat.getColor(context, color))
            val paint = Paint()
            paint.color = ContextCompat.getColor(context, color)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = widthLine.toFloat()
            paint.isAntiAlias = true
            val offset = Constants.COORDINATES_LINE_HEADER_GENERIC
            canvas.drawLine(
                offset.toFloat(), (canvas.height / 2).toFloat(),
                (canvas.width - offset).toFloat(),
                (canvas.height / 2).toFloat(),
                paint
            )
            setImageBitmap(bitmap)
        }.lparams(width = wrapContent, height = wrapContent)
    }

    private fun onItemClick(category: Category, categories: ArrayList<Category>?, itemView: View) {
        val categoryBundle = Bundle().apply {
            putSerializable(Constants.CATEGORIES, categories)
            putSerializable(Constants.CATEGORY_PARAMETER, category)
        }
        if (category.isRoute) {
            when (category.id) {
                Constants.ID_ROUTE_LABOUR -> {
                    onItemClickRouteLabour(itemView)
                }
                Constants.ID_ROUTE_VIOLENCE -> {
                    onItemClickRouteViolence(itemView)
                }
            }
            logAnalyticsSelectContentWithId(category.title, ContentType.ROUTE)
        } else {
            itemView.findNavController()
                .navigate(R.id.genericInfoFragment, categoryBundle, navOptions, null)
        }
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
        val contentOptions1 = StringBuffer()
        category.information?.forEach { information: CategoryInformation ->
            information.let {
                contentOptions1.append(information.description)
                contentOptions1.append("\n")
                contentOptions1.append(getTextSections(information))
                contentOptions1.append("\n")
            }
        }
        return contentOptions1
    }

    private fun getTextSections(
        option: CategoryInformation
    ): StringBuffer {
        val contentOptions1 = StringBuffer()
        option.sections?.forEach { section: Section ->
            section.let {
                contentOptions1.append(section.title)
                contentOptions1.append("\n")
                contentOptions1.append(getTextSteps(section))
                contentOptions1.append("\n")
            }
        }
        return contentOptions1
    }

    private fun getTextSteps(
        section: Section
    ): StringBuffer {
        val contentOptions1 = StringBuffer()
        section.steps?.forEach { step: Step ->
            step.let {
                contentOptions1.append(step.stepId).append("\n").append(step.description)
            }
        }
        return contentOptions1
    }


    private fun onItemClickRouteLabour(itemView: View) {
        val routeLabour = Bundle().apply {
            putString(Constants.TYPE_ROUTE, Constants.ROUTE_LABOUR)
        }
        itemView.findNavController()
            .navigate(R.id.mainScreenLabourRouteFragment, routeLabour, navOptions, null)
    }

    private fun onItemClickRouteViolence(itemView: View) {
        val violenceLabour = Bundle().apply {
            putString(Constants.TYPE_ROUTE, Constants.ROUTE_VIOLENCE)
        }
        itemView.findNavController()
            .navigate(R.id.mainScreenLabourRouteFragment, violenceLabour, navOptions, null)
    }
}

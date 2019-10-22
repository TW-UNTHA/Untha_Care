package com.untha.view.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import com.untha.utils.PixelConverter
import com.untha.utils.PixelConverter.toPixels
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.GenericInfoStepFragmentViewModel
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
import timber.log.Timber

class GenericInfoStepFragment : BaseFragment() {

    private lateinit var category: Category
    private var categories: List<Category>? = null
    private lateinit var mainActivity: MainActivity
    private val viewModel: GenericInfoStepFragmentViewModel by viewModel()


    fun onItemClick(category: Category, categories: ArrayList<Category>?, itemView: View) {
        val categoryBundle = Bundle().apply {
            putSerializable(Constants.CATEGORIES, categories)
            putSerializable(Constants.CATEGORY_PARAMETER, category)
        }
        itemView.findNavController()
            .navigate(R.id.genericInfoFragment, categoryBundle, navOptions, null)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics.setCurrentScreen(activity!!, Constants.STEP_PAGE_, null)
        with(view as _LinearLayout) {
            verticalLayout {
                val imageSizeInDps = (PixelConverter.getScreenDpHeight(context) -
                        Constants.SIZE_OF_ACTION_BAR) * Constants.GENERIC_PERCENTAGE_PLAYER_HEADER
                val imageHeight = toPixels(imageSizeInDps, context)
                val marginTop = calculateTopMargin()
                val marginLeft = calculateMarginLeftAndRight()

                verticalLayout {
                    loadImage(view, imageHeight,category)
                    drawLine(R.color.colorGenericLineHeader, Constants.HEIGHT_LINE_HEADER_GENERIC)
                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = wrapContent)

                scrollView {
                    verticalLayout {
                        loadInformationDescription(view)


                    }
                    backgroundColor =
                        ContextCompat.getColor(context, R.color.colorBackgroundGenericInfo)
                    lparams(width = matchParent, height = matchParent) {
                        leftMargin = marginLeft * Constants.GENERIC_STEP_MARGIN_MULTIPLIER
                        rightMargin = marginLeft
                        bottomMargin = marginTop * Constants.GENERIC_STEP_MARGIN_MULTIPLIER
                    }
                }
            }.lparams(width = matchParent, height = matchParent)
        }
        mainActivity.supportActionBar?.title = category.information?.get(0)?.screenTitle
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
                context, R.drawable.corners_round_next_item
            )
            buildImageNextStep(view, categoryNextStep)
            buildBlockTextNextSteps(categoryNextStep)

            setOnClickListener {
                onItemClick(categoryNextStep, categories as ArrayList<Category>, view)
            }

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
            buildNextStepTitle(categoryNextStep, dip(calculateTopMargin()))
        }.lparams(
            width = matchParent,
            height = dip(widthBlockPixel)
        )
    }

    private fun _LinearLayout.calculateMarginLeftAndRight(): Int {
        val widthFormula =
            (PixelConverter.getScreenDpWidth(context)) * Constants.MARGIN_WIDTH_PERCENTAGE
        return toPixels(widthFormula, context)
    }

    private fun @AnkoViewDslMarker _LinearLayout.calculateTopMargin(): Int {
        val topFormula = (PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR) * Constants.MARGIN_TOP_PERCENTAGE
        return toPixels(topFormula, context)
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
            gravity = Gravity.LEFT
            text = title
            textSizeDimen = R.dimen.text_size_content_next_step
            textColor =
                ContextCompat.getColor(
                    context,
                    R.color.colorGenericTitle
                )
            setTypeface(typeface, Typeface.BOLD)
        }.lparams(height = wrapContent, width = matchParent) {
            bottomMargin = dip(calculateTopMargin())
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


    private fun _LinearLayout.drawLine(color: Int, height:Int) {
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

}


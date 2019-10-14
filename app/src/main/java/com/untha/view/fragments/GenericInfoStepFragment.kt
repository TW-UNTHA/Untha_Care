package com.untha.view.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.CategoryInformation
import com.untha.model.transactionalmodels.Section
import com.untha.model.transactionalmodels.Step
import com.untha.utils.Constants
import com.untha.utils.Constants.COORDINATES_LINE_HEADER_GENERIC
import com.untha.utils.Constants.HEIGHT_LINE_HEADER_GENERIC
import com.untha.utils.PixelConverter
import com.untha.utils.PixelConverter.toPixels
import com.untha.view.activities.MainActivity
import com.untha.view.adapters.RightsAdapter
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class GenericInfoStepFragment : BaseFragment(),
    RightsAdapter.OnItemClickListener {

    private lateinit var category: Category
    private var categoryNextStep: Category? = null
    private lateinit var mainActivity: MainActivity


    override fun onItemClick(category: Category, categoryNextStep: Category?, itemView: View) {
        val categoryBundle = Bundle().apply {
            putSerializable(Constants.CATEGORY_PARAMETER, categoryNextStep)
            putSerializable(Constants.CATEGORY_PARAMETER_NEXT_STEP, category)
        }
        itemView.findNavController()
            .navigate(R.id.genericInfoFragment, categoryBundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        category = bundle?.get(Constants.CATEGORY_PARAMETER) as Category
        categoryNextStep = bundle.get(Constants.CATEGORY_PARAMETER_NEXT_STEP) as Category

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
        firebaseAnalytics.setCurrentScreen(activity!!, "Step Page", null)
        with(view as _LinearLayout) {
            verticalLayout {
                val imageSizeInDps = (PixelConverter.getScreenDpHeight(context) -
                        Constants.SIZE_OF_ACTION_BAR) * Constants.GENERIC_PERCENTAGE_PLAYER_HEADER
                val imageHeight = toPixels(imageSizeInDps, context)
                val marginTop = calculateTopMargin()
                val marginLeft = calculateMarginLeftAndRight()

                verticalLayout {
                    loadImage(view, imageHeight)
                    drawLine()
                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = wrapContent)

                scrollView {
                    verticalLayout {
                        loadInformationDescription()


                        categoryNextStep?.let {

                            categoryNextStep?.let {
                                val SIZE_HEIGHT_NEXT_STEP = 0.1981
                                val heightFormula =
                                    (PixelConverter.getScreenDpHeight(context)) * SIZE_HEIGHT_NEXT_STEP


                                val REST_IMAGE = 116
                                val REST_BORDER = 26
                                val PERCENTAGE_TEXT = (218 * 100) / 360

                                val PERCENTAGE_HEIGHT = (103 * 100) / 576

                                val heightScreenc =
                                    (PixelConverter.getScreenDpHeight(context) - Constants.SIZE_OF_ACTION_BAR) * PERCENTAGE_HEIGHT

                                val heightScreen =
                                    PixelConverter.toPixels(heightScreenc.toDouble(), context)

                                linearLayout {
                                    orientation = LinearLayout.HORIZONTAL
                                    backgroundColor =
                                        ContextCompat.getColor(context, R.color.colorGenericTitle)
                                    isClickable = true
                                    backgroundDrawable = ContextCompat.getDrawable(
                                        context, R.drawable.corners_round_next_item
                                    )

                                    val widthScreenImage =
                                        (PixelConverter.getScreenDpHeight(context)) * SIZE_HEIGHT_NEXT_STEP
                                    val widthScreenImagePixel =
                                        PixelConverter.toPixels(
                                            widthScreenImage.toDouble(),
                                            context
                                        )

                                    verticalLayout {
                                        loadImageNestStep(view)

                                    }.lparams(
                                        width = widthScreenImagePixel

                                    )

                                    val widthScreen =
                                        ((PixelConverter.getScreenDpWidth(context)) - (REST_BORDER + REST_IMAGE))
                                    val widthScreenPixel =
                                        PixelConverter.toPixels(widthScreen.toDouble(), context)
                                    println("widthScreenPixel" + widthScreenPixel)
                                    println(
                                        "getScreenDpWidth" + PixelConverter.getScreenDpWidth(
                                            context
                                        )
                                    )

                                    verticalLayout {
                                        buildNext()//Siguiente
                                        buildTitle()

                                    }.lparams(
                                        width = matchParent
                                    )
                                    setOnClickListener {
                                        println("setOnClickListenerGISF")
                                        onItemClick(category, categoryNextStep, view)
                                    }

                                }.lparams(
                                    width = matchParent, height = dip(heightFormula.toInt())

//                                    width = matchParent, height = (heightFormula *
//                                                Constants.GENERIC_PERCENTAGE_PLAYER_HEADER_NEXT_ITEM).toInt()
                                )

                            }


                        }
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

    private fun @AnkoViewDslMarker _LinearLayout.loadImage(
        view: View,
        imageHeight: Int
    ) {
        imageView {
            val imageUrl = resources.getIdentifier(
                category.information?.get(0)?.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl)
                .into(this)
            scaleType= ImageView.ScaleType.FIT_XY
        }.lparams(width = matchParent, height = imageHeight)
    }


    private fun @AnkoViewDslMarker _LinearLayout.calculateMarginLeftAndRight(): Int {
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

    private fun @AnkoViewDslMarker _LinearLayout.buildSections(categoryInformation: CategoryInformation) {
        categoryInformation.sections?.map { section ->
            val title = section.title
            if (title.isNotEmpty()) {
                title.let {
                    textView {
                        text = title
                        textColor =
                            ContextCompat.getColor(
                                context,
                                R.color.colorGenericTitle
                            )
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
                    ContextCompat.getColor(
                        context,
                        android.R.color.white
                    )
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

    private fun @AnkoViewDslMarker _LinearLayout.loadInformationDescription() {
        category.information?.map { categoryInformation ->
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
        }
    }

    //this is the old method
    private fun @AnkoViewDslMarker _LinearLayout.buildNextOld() {
        textView {
            val title = "SIGUIENTE:"
            gravity = Gravity.LEFT
            text = title
            textColor =
                ContextCompat.getColor(
                    context,
                    R.color.colorGenericTitle
                )
            setTypeface(typeface, Typeface.BOLD)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildNext() {
        textView {
            val title = "SIGUIENTE:"
            gravity = Gravity.LEFT
            text = title
            textColor =
                ContextCompat.getColor(
                    context,
                    R.color.colorGenericTitle
                )
            setTypeface(typeface, Typeface.BOLD)
            textSizeDimen = R.dimen.text_size_content
        }.lparams(height = wrapContent, width = matchParent) {
            bottomMargin = dip(calculateTopMargin())
            topMargin = dip(calculateTopMargin())
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildTitle() {
        textView {
            val title = categoryNextStep?.information?.get(0)?.screenTitle
            textSizeDimen = R.dimen.text_size_content
            gravity = Gravity.LEFT
            text = title
            textColor =
                ContextCompat.getColor(
                    context,
                    R.color.colorGenericTitle
                )

            setTypeface(typeface, Typeface.NORMAL)
//           textSizeDimen = R.dimen.text_size_content
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadImageNestStep(view: View) {
        val imageNextStepSizeInDps = (PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR) * Constants.GENERIC_PERCENTAGE_PLAYER_HEADER
//        val imageNextStepHeight = toPixels(103.00, context)
//        val imageNextStepWidth = toPixels(150.00, context)

        imageView {
            padding = 5
            Gravity.CENTER

            val imageUrl = resources.getIdentifier(
                categoryNextStep?.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl)
                .into(this)

        }.lparams(
        )
    }
    private fun _LinearLayout.drawLine() {
        imageView{
            val widthLine =  toPixels(PixelConverter.getScreenDpWidth(context).toDouble(), context)
            val bitmap = Bitmap.createBitmap(widthLine, HEIGHT_LINE_HEADER_GENERIC, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.LTGRAY)
            val paint = Paint()
            paint.color = Color.LTGRAY
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = widthLine.toFloat()
            paint.isAntiAlias = true
            val offset = COORDINATES_LINE_HEADER_GENERIC
            canvas.drawLine(offset.toFloat(), (canvas.height / 2).toFloat(),
                (canvas.width - offset).toFloat(),
                (canvas.height / 2).toFloat(),
                paint
            )
            setImageBitmap(bitmap)
        }.lparams(width = wrapContent, height = wrapContent)

    }

}

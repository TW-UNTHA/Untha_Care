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
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.CategoryInformation
import com.untha.model.transactionalmodels.Section
import com.untha.model.transactionalmodels.Step
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.utils.PixelConverter.toPixels
import com.untha.view.activities.MainActivity
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class GenericInfoStepFragment : BaseFragment() {

    private lateinit var mainActivity: MainActivity

    private lateinit var category: Category
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        category = bundle?.get(Constants.CATEGORY_PARAMETER) as Category
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
        with(view as _LinearLayout) {
            verticalLayout {
                val imageSizeInDps = (PixelConverter.getScreenDpHeight(context) -
                        Constants.SIZE_OF_ACTION_BAR) * Constants.GENERIC_PERCENTAGE_PLAYER_HEADER
                val imageHeight = toPixels(imageSizeInDps, context)
                val marginTop = calculateTopMargin()
                val marginLeft = calculateMarginLeftAndRight()

                verticalLayout {
                    loadImage(view)
                    backgroundDrawable = ContextCompat.getDrawable(
                        context, R.drawable.hearder_info_generic
                    )

                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = imageHeight) {
                    topMargin = Constants.MARGIN_HIDDEN_PLAYER
                    leftMargin = Constants.MARGIN_HIDDEN_PLAYER
                    rightMargin = Constants.MARGIN_HIDDEN_PLAYER
                }

                scrollView {
                    verticalLayout {
                        loadInformationDescription()
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

    private fun @AnkoViewDslMarker _LinearLayout.loadImage(view: View) {
        imageView {
            val imageUrl = resources.getIdentifier(
                category.information?.get(0)?.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl)
                .into(this)

        }.lparams(width = matchParent, height = matchParent)
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

}

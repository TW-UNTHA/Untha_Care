package com.untha.view.fragments

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.util.Linkify
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.Section
import com.untha.model.transactionalmodels.Step
import com.untha.utils.Constants
import com.untha.utils.Constants.GENERIC_MARGIN_MULTIPLER
import com.untha.utils.Constants.MARGIN_HIDDEN_PLAYER
import com.untha.utils.PixelConverter
import com.untha.utils.PixelConverter.toPixels
import com.untha.view.adapters.RightsAdapter
import kotlinx.serialization.json.Json
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent


class GenericInfoFragment : Fragment(),
    RightsAdapter.OnItemClickListener {
    private lateinit var category: Category
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
            val categoryModel  = bundle?.get("category")
            category = Json.parse(
                Category.serializer(),
                categoryModel.toString().replace("stepId", "step_id"))
            setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createMainLayout()
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        with(view as _LinearLayout) {
            verticalLayout {
                val heightFormula = (PixelConverter.getScreenDpHeight(context) -
                        Constants.SIZE_OF_ACTION_BAR) * Constants.PERCENTAGE_MAIN_LAYOUT

                val marginTop = calculateTopFormula()

                val widthFormula =
                    (PixelConverter.getScreenDpWidth(context)) * Constants.MARGIN_WIDTH_PERCENTAGE
                val marginLeft = PixelConverter.toPixels(widthFormula, context)

                verticalLayout {
                  loadImage(view)
                    lparams(width = matchParent, height = (heightFormula *
                            Constants.GENERIC_PERCENTAGE_PLAYER_HEADER).toInt()) {
                        topMargin = MARGIN_HIDDEN_PLAYER
                        leftMargin = MARGIN_HIDDEN_PLAYER
                        rightMargin = MARGIN_HIDDEN_PLAYER
                    }

                    backgroundDrawable =  ContextCompat.getDrawable(
                        context,R.drawable.hearder_info_generic
                    )
                }

                scrollView {
                    verticalLayout {
                        loadInformationDescription()
                        buildSections()
                    }
                    backgroundColor = ContextCompat.getColor(context, R.color.colorBackgroundGenericInfo)
                    lparams(width = matchParent, height = matchParent) {
                        topMargin =  marginTop * GENERIC_MARGIN_MULTIPLER
                        leftMargin = marginLeft * GENERIC_MARGIN_MULTIPLER
                        rightMargin = marginLeft
                        bottomMargin= marginTop * GENERIC_MARGIN_MULTIPLER

                    }
                }
                lparams(width = matchParent, height = matchParent)
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.calculateTopFormula(): Int {
        val topFormula = (PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR) * Constants.MARGIN_TOP_PERCENTAGE

        val marginTop = toPixels(topFormula, context)
        return marginTop
    }

    private fun createMainLayout(): View {
        return UI {
            verticalLayout {
                backgroundColor = ContextCompat.getColor(context, R.color.colorBackgroundGenericInfo)
                lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildSections() {
        category.information?.sections?.let { sections ->
            sections.map { section ->
                textView {
                    text = section.title
                    textColor =
                        ContextCompat.getColor(
                            context,
                            R.color.colorGenericTitle
                        )
                    setTypeface(typeface, Typeface.BOLD)
                }.lparams(height = wrapContent, width = matchParent) {
                    bottomMargin = dip(calculateTopFormula())
                }
                buildSteps(section)
            }
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
            text = step.description
            typeface = ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_light)
            linksClickable =  true
            Linkify.addLinks(this, Linkify.WEB_URLS)
            setLinkTextColor(resources.getColor(R.color.colorGenericTitle))
        }.lparams(width = wrapContent, height = wrapContent)
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildRoundedCircleTextView(
        step: Step) {
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

        }.lparams(width = wrapContent, height = wrapContent) {
            bottomMargin = dip(calculateTopFormula())
            topMargin = dip(calculateTopFormula())
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadInformationDescription() {
        category.information?.description?.let { description ->
            textView {
                text = description
                textSizeDimen = R.dimen.text_size_content
                typeface = ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_light)
            }.lparams(height = wrapContent, width = matchParent) {
                bottomMargin = dip( calculateTopFormula())

            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadImage(view: View) {
        imageView {
            val imageUrl = resources.getIdentifier(
                category.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl)
                .into(this)
        }
            .lparams(height = matchParent, width = matchParent){
                 bottomPadding = toPixels(Constants.PADDING_IMAGE_PLAYER_HEADER.toDouble(), context)
                 topPadding = toPixels(Constants.PADDING_IMAGE_PLAYER_HEADER.toDouble(), context)
            }
    }

    /**
     * Navigates to people details on item click
     */
    override fun onItemClick(category: Category, itemView: View) {
        println("toque el boton")
    }
}

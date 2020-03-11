package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.view.extension.loadImageNextStep
import com.untha.viewmodels.RoutesViewModel
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko._ScrollView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.dip
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.koin.android.viewmodel.ext.android.viewModel


class MainScreenCalculatorsFragment : BaseFragment() {
    private lateinit var categoriesCalculator: ArrayList<Category>
    private lateinit var categories: ArrayList<Category>
    private lateinit var mainActivity: MainActivity
    private val routeViewModel: RoutesViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        categoriesCalculator = bundle?.get(Constants.CATEGORIES_CALCULATORS) as ArrayList<Category>
        categories = bundle?.get(Constants.CATEGORIES) as ArrayList<Category>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        textToSpeech = UtilsTextToSpeech(context!!, null, null)

        mainActivity = this.activity as MainActivity
        return createMainLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view as _ScrollView) {
            verticalLayout {
                buildListCalculators(view)
            }.lparams(width = matchParent, height = matchParent)
        }
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_CALCULATOR_ROUTE,
            enableCustomBar = true,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null

        )

    }

    private fun createMainLayout(): View {
        return UI {
            scrollView {
                backgroundColor =
                    ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildListCalculators(view: View) {
        categoriesCalculator?.map { calculator ->
            logAnalyticsSelectContentWithId(
                "${Constants.CLICK_ROUTE_TITLE}${calculator.title}", ContentType.ROUTE
            )
            val heightFormula =
                (PixelConverter.getScreenDpHeight(context)) * Constants.SIZE_HEIGHT_NEXT_STEP
            linearLayout {
                isClickable = true
                orientation = LinearLayout.HORIZONTAL
                backgroundDrawable = ContextCompat.getDrawable(
                    context, R.drawable.drawable_main_route
                )
                linearLayout {
                    buildImageNextStep(view, calculator)
                    buildBlockTextNextSteps(calculator)
                }
                setOnClickListener { view ->
                    onItemClick(calculator, view)
                }
                setOnLongClickListener {
                    val textToSpeechCalculator =
                        calculator.title.plus("\n\n\n").plus(calculator.subtitle)
                    textToSpeech!!.speakOut(textToSpeechCalculator)

                }
            }.lparams(matchParent, height = dip(heightFormula.toInt())) {
                topMargin = calculateTopMargin()
                rightMargin = calculateLateralMargin() - dip(Constants.SHADOW_PADDING_SIZE)
                leftMargin = calculateLateralMargin()
            }
        }
    }

    private fun onItemClick(category: Category, itemView: View) {
        val categoriesBundle = Bundle().apply {
            putSerializable(
                Constants.CATEGORIES_CALCULATORS,
                categoriesCalculator
            )
            putSerializable(Constants.CATEGORIES, categories)
        }
        if (category.type == "calculator") {
            when (category.id) {
                Constants.ID_CALCULATOR_BENEFIT -> {
                    itemView.findNavController()
                        .navigate(
                            R.id.calculatorBenefitFragment,
                            categoriesBundle,
                            navOptions,
                            null
                        )
                }
                Constants.ID_CALCULATOR_FINIQUIO -> {
                    val goToBundle = Bundle().apply {
                        putInt(Constants.REMAINING_QUESTION, Constants.TEMPORAL_LOAD_PROGRESS_BAR)
                        putInt(Constants.QUESTION_ADVANCE, Constants.COUNT_QUESTION_ADVANCE)
                        putInt(
                            Constants.ROUTE_QUESTION_GO_TO,
                            Constants.START_QUESTION_ROUTE_LABOUR
                        )
                        putSerializable(
                            Constants.ROUTE_CALCULATOR,
                            routeViewModel.loadRouteFromSharedPreferences(Constants.CALCULATOR_ROUTE)
                        )
                        putSerializable(
                            Constants.CATEGORIES_CALCULATORS,
                            categoriesCalculator
                        )
                        putSerializable(Constants.CATEGORIES, categories)
                    }
                    itemView.findNavController()
                        .navigate(
                            R.id.multipleSelectionQuestionFragment,
                            goToBundle,
                            navOptions,
                            null
                        )
                }
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildBlockTextNextSteps(
        category: Category
    ) {
        val widthBlockDp =
            (PixelConverter.getScreenDpWidth(context)) * Constants.WIDTH_BLOCK_OF_TEXT
        val widthBlockPixel = PixelConverter.toPixels(widthBlockDp, context)
        verticalLayout {
            loadTitleCalculator(category)
            loadSubtitleCalculator(category)
        }.lparams(
            width = matchParent,
            height = dip(widthBlockPixel)
        ) {
            bottomMargin = dip(Constants.TOP_MARGIN_NEXT_STEP)
        }
    }

    fun @AnkoViewDslMarker _LinearLayout.buildImageNextStep(
        view: View,
        category: Category
    ) {
        val widthImageDp =
            (PixelConverter.getScreenDpWidth(context)) * Constants.WIDTH_IMAGE
        val widthImagePixel =
            PixelConverter.toPixels(
                widthImageDp,
                context
            )
        linearLayout {
            padding = dip(Constants.PERCENTAGE_PADDING_ELEMENT_NEXT_STEP_IMAGE)
            loadImageNextStep(view, category)
        }.lparams(
            width = widthImagePixel
        )
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadTitleCalculator(
        calculator: Category
    ) {
        textView {
            text = calculator.title
            textSizeDimen = R.dimen.text_size_calculators_home
            textColor =
                ContextCompat.getColor(context, R.color.colorGenericTitle)
            setTypeface(typeface, Typeface.BOLD)
        }.lparams {
            topMargin = calculateTopMargin()
            rightMargin = calculateLateralMargin()
            leftMargin = calculateLateralMargin()
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadSubtitleCalculator(
        calculator: Category
    ) {
        textView {
            text = calculator.subtitle
            textSizeDimen = R.dimen.text_size_calculators_home
            textColor =
                ContextCompat.getColor(context, R.color.colorGenericTitle)
            typeface = ResourcesCompat.getFont(
                context.applicationContext,
                R.font.proxima_nova_light
            )
        }.lparams {
            topMargin = calculateTopMargin()
            rightMargin = calculateLateralMargin()
            leftMargin = calculateLateralMargin()
        }
    }

    private fun calculateTopMargin(): Int {
        val topMarginDps = (PixelConverter.getScreenDpHeight(context) -
                Constants.SIZE_OF_ACTION_BAR) * Constants.MARGIN_TOP_PERCENTAGE_MAIN_ROUTE

        return PixelConverter.toPixels(topMarginDps, context)

    }

    private fun calculateLateralMargin(): Int {
        val cardWidthInDps =
            PixelConverter.getScreenDpWidth(context) * Constants.MARGIN_LATERAL_PERCENTAGE_MAIN_ROUTE
        return PixelConverter.toPixels(cardWidthInDps, context)
    }
}

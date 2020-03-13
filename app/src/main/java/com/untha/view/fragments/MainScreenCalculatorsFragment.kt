package com.untha.view.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.RoutesViewModel
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko._ScrollView
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
import org.koin.android.viewmodel.ext.android.viewModel


class MainScreenCalculatorsFragment : BaseFragment() {
    private lateinit var categoriesCalculator: ArrayList<Category>
    private lateinit var mainActivity: MainActivity
    private val routeViewModel: RoutesViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        categoriesCalculator = bundle?.get(Constants.CATEGORIES_CALCULATORS) as ArrayList<Category>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        textToSpeech = UtilsTextToSpeech(context!!, null, null)

        mainActivity = this.activity as MainActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return createMainLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            firebaseAnalytics.setCurrentScreen(
                it,
                Constants.CALCULATORS_PAGE,
                null
            )
        }

        with(view as _ScrollView) {
            verticalLayout {
                buildListCalculators(view)
                buildMessageAlert()
            }.lparams(width = matchParent, height = matchParent)

        }

        (activity as MainActivity).customActionBar(
            Constants.NAME_SCREEN_CALCULATORS,
            enableCustomBar = false,
            needsBackButton = true,
            enableHelp = false,
            backMethod = ::navigateToCategory

        )

    }

    private fun @AnkoViewDslMarker _LinearLayout.buildMessageAlert() {
        textView {
            this.gravity = Gravity.RIGHT
            text = context.getString(R.string.description_calculators)
            textSizeDimen = R.dimen.text_size_content_next_step
            textColor =
                ContextCompat.getColor(context, R.color.colorCalculatorText)
            setTypeface(typeface, Typeface.ITALIC)
        }.lparams {
            topMargin = calculateTopMargin()
            rightMargin = calculateLateralMargin()
            leftMargin = calculateLateralMargin()
            bottomMargin = calculateTopMargin()
        }
    }

    private fun navigateToCategory() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.categoryFragment, null, navOptionsToBackNavigation, null)
        logAnalyticsCustomContentTypeWithId(ContentType.CLOSE, FirebaseEvent.CLOSE)
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
            verticalLayout {
                isClickable = true
                loadTitleCalculator(calculator)
                loadSubtitleCalculator(calculator)
                loadImageRoute(view, calculator)
                backgroundDrawable = ContextCompat.getDrawable(
                    context, R.drawable.drawable_main_route
                )
                setOnClickListener { view ->
                    onItemClick(calculator, view)
                }
                setOnLongClickListener {
                    val textToSpeechCalculator =
                        calculator.title.plus("\n\n\n").plus(calculator.subtitle)
                    textToSpeech!!.speakOut(textToSpeechCalculator)

                }

            }
                .lparams(matchParent, calculateHeightRoute()) {
                    topMargin = calculateTopMargin()
                    rightMargin = calculateLateralMargin() - dip(Constants.SHADOW_PADDING_SIZE)
                    leftMargin = calculateLateralMargin()
                }
        }

    }

    private fun calculateHeightRoute(): Int {
        val cardHeightInDps =
            (PixelConverter.getScreenDpHeight(context) -
                    Constants.SIZE_OF_ACTION_BAR_ROUTE) * Constants.SIZE_ROUTE_CATEGORY

        return PixelConverter.toPixels(cardHeightInDps, context)
    }

    private fun onItemClick(category: Category, itemView: View) {
        val categoriesBundle = Bundle().apply {
            putSerializable(
                Constants.CATEGORIES_CALCULATORS,
                categoriesCalculator
            )
        }
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

    private fun @AnkoViewDslMarker _LinearLayout.loadImageRoute(
        view: View,
        category: Category
    ) {
        imageView {
            val imageUrl = resources.getIdentifier(
                category.image,
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl)
                .into(this)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }.lparams(width = matchParent, height = wrapContent) {
            topMargin = calculateTopMargin()
            bottomMargin = calculateTopMargin()
        }
    }


    private fun @AnkoViewDslMarker _LinearLayout.loadTitleCalculator(
        calculator: Category
    ) {
        gravity = Gravity.CENTER
        textView {
            text = calculator.title
            textSizeDimen = R.dimen.text_size_main_component
            textColor =
                ContextCompat.getColor(context, R.color.colorCalculatorText)
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
            this.gravity = Gravity.CENTER
            textSizeDimen = R.dimen.text_size_content_for_many_characters
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

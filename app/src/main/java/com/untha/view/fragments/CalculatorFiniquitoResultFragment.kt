package com.untha.view.fragments

import android.content.Context
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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.untha.R
import com.untha.R.layout
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.RouteResult
import com.untha.utils.Constants
import com.untha.utils.Constants.CALCULATOR_FINIQUITO_RESULT_PAGE
import com.untha.utils.ConstantsCalculators.SBU
import com.untha.utils.ConstantsCalculators.SEVENTEEN_AGE
import com.untha.utils.ConstantsCalculators.SIXTEEN_AGE
import com.untha.utils.ConstantsCalculators.TRIAL_PERIOD
import com.untha.utils.ConstantsCalculators.WEEKLY_HOURS_COMPLETE
import com.untha.utils.ConstantsCalculators.WEEKLY_HOURS_MAXIMUM_LEGAL_MINOR
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.utils.getAge
import com.untha.utils.numberDaysWorked
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.CalculatorFiniquitoResultsViewModel
import kotlinx.android.synthetic.main.fragment_item_result_monthly.view.*
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.attr
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textSizeDimen
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class CalculatorFiniquitoResultFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private val calculatorFiniquitoResultsViewModel: CalculatorFiniquitoResultsViewModel by viewModel()
    private lateinit var categoriesCalculator: ArrayList<Category>
    private var contentAudio: StringBuffer = StringBuffer()
    private lateinit var bornDate: String
    private lateinit var startDate: String
    private lateinit var endDate: String
    private lateinit var salary: String
    private var hours: Int = 0
    private var idArea: Int = 0
    private var decimoTercero: Int = 0
    private var decimoCuarto: Int = 0
    private var fondosReserva: Int = 0
    private var vacationsDaysTaken: Int = 0
    private lateinit var discounts: String


    companion object {
        const val PAY_NOT_COMPLETE = "F1"
        const val LEGAL_MINOR_AND_OVER_HOURS_WORKED = "F2"
        const val PREGNANT_AND_TRIAL_PERIOD = "F3"
        const val DESPIDO_INTEMPESTIVO = "F4"
        const val DISABILITY = "F5"
        const val OVER_HOURS_WORKED = "F6"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculatorFiniquitoResultsViewModel.loadResultDynamicFromSharePreferences()
        calculatorFiniquitoResultsViewModel.loadResultStaticFromSharePreferences()
        calculatorFiniquitoResultsViewModel.answerSelectedCalculatorRoute()
        calculatorFiniquitoResultsViewModel.answerHintSelectedCalculatorRoute()

        loadDataFromBundle()
    }

    private fun loadDataFromBundle() {
        categoriesCalculator =
            arguments?.get(Constants.CATEGORIES_CALCULATORS) as ArrayList<Category>
        bornDate = arguments?.get("bornDate") as String
        startDate = arguments?.get("startDate") as String
        endDate = arguments?.get("endDate") as String
        salary = arguments?.get("salary") as String
        hours = arguments?.get("hours") as Int
        idArea = arguments?.get("idArea") as Int
        decimoTercero = arguments?.get("decimoTercero") as Int
        decimoCuarto = arguments?.get("decimoCuarto") as Int
        fondosReserva = arguments?.get("fondosReserva") as Int
        vacationsDaysTaken = arguments?.get("vacationsDaysTaken") as Int
        discounts = arguments?.get("discounts") as String
    }

    override fun onResume() {
        super.onResume()
        titleActionBar()
        goBackMainScreenCategory(
            Constants.CATEGORIES_CALCULATORS,
            categoriesCalculator, R.id.calculatorsFragment, mainActivity
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity

        return createMainLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawLayoutResult(view)
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

    private fun drawLayoutResult(view: View) {
        val layoutInflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutMonthly = layoutInflater.inflate(layout.fragment_item_result_monthly, null)
        calculatorFiniquitoResultsViewModel.retrieveAllCategories()
            .observe(this, Observer { queryingCategories ->
                calculatorFiniquitoResultsViewModel.mapCategories(queryingCategories)

                with(view as _LinearLayout)
                {
                    scrollView {
                        layoutMonthly.tv_decimo_tercero_monthly.text = "555"
                        addView(layoutMonthly)
                        verticalLayout {
                            loadHeaderResult(view)
                            loadCalculatorFaults(view)
                            buildRecommend()
                        }
                    }.lparams(width = matchParent, height = matchParent) {
                        topMargin =
                            dip(calculateComponentsHeight(RouteResultsFragment.CONTAINER_MARGIN_TOP_BOTTOM))
                        bottomMargin =
                            dip(calculateComponentsHeight(RouteResultsFragment.CONTAINER_MARGIN_TOP_BOTTOM))
                        leftMargin =
                            dip(calculateComponentsHeight(RouteResultsFragment.CONTAINER_LATERAL_MARGIN))
                        rightMargin =
                            dip(calculateComponentsHeight(RouteResultsFragment.CONTAINER_LATERAL_MARGIN))
                    }
                }
            })
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadCalculatorFaults(
        view: View
    ) {
        val answersApplicable = mutableListOf<String>()
        getFaultsApplicable(answersApplicable)

        calculatorFiniquitoResultsViewModel.resultCalculatorFaults?.map { result ->
            val contains =
                answersApplicable.let { it!!.contains(result.id) }
            if (contains) {
                verticalLayout {
                    backgroundDrawable = ContextCompat.getDrawable(
                        context, R.drawable.drawable_fault_container
                    )
                    contentAudio.append(result.content).append("\n")
                    drawRouteResultDescription(result)
                    drawCategoryButtons(result, view)
                }.lparams(
                    width = matchParent, height =
                    wrapContent
                ) {
                    bottomMargin =
                        dip(calculateComponentsHeight(RouteResultsFragment.FAULT_BOTTOM_MARGIN))
                }
            }
        }
    }

    private fun getFaultsApplicable(answersApplicable: MutableList<String>) {


        if (salary.toBigDecimal() < SBU.toBigDecimal()
            && hours >= WEEKLY_HOURS_COMPLETE
        ) {
            answersApplicable.add(PAY_NOT_COMPLETE)
        }
        if (hours > WEEKLY_HOURS_MAXIMUM_LEGAL_MINOR && (getAge(
                bornDate,
                startDate
            ) in SIXTEEN_AGE..SEVENTEEN_AGE)
        ) {
            answersApplicable.add(LEGAL_MINOR_AND_OVER_HOURS_WORKED)

        }

        if (calculatorFiniquitoResultsViewModel.resultsSelected!!.contains(PREGNANT_AND_TRIAL_PERIOD)
            && (numberDaysWorked(endDate, startDate) > TRIAL_PERIOD)
        ) {
            answersApplicable.add(PREGNANT_AND_TRIAL_PERIOD)

        }

        if (calculatorFiniquitoResultsViewModel.resultsSelected!!.contains(DESPIDO_INTEMPESTIVO)) {
            answersApplicable.add(DESPIDO_INTEMPESTIVO)
        }

        if (calculatorFiniquitoResultsViewModel.resultsSelected!!.contains(DISABILITY)) {
            answersApplicable.add(DISABILITY)
        }
        if (hours > WEEKLY_HOURS_COMPLETE) {
            answersApplicable.add(OVER_HOURS_WORKED)
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildRecommend() {
        calculatorFiniquitoResultsViewModel.resultCalculatorRecommend?.map { result ->
            buildTitleRecommend(result.title)
            buildContentRecommend(result.content)
            dividerLine()
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildTitleRecommend(
        title: String
    ) {
        if (title.isNotEmpty()) {
            title.let {
                contentAudio.append(title).append("\n")
                textView {
                    text = title
                    textColor =
                        ContextCompat.getColor(context, R.color.colorGenericTitle)
                    setTypeface(typeface, Typeface.BOLD)
                    textSizeDimen = R.dimen.text_size_content
                }.lparams(height = wrapContent, width = matchParent) {
                    bottomMargin =
                        dip(calculateComponentsHeight(RouteResultsFragment.SECTIONS_TOP_BOTTOM_MARGIN))
                    topMargin =
                        dip(calculateComponentsHeight(RouteResultsFragment.SECTIONS_TOP_BOTTOM_MARGIN))
                }
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.buildContentRecommend(
        content: String
    ): TextView {
        return textView {
            text = content.parseAsHtml()
            contentAudio.append(content.parseAsHtml()).append("\n")
            movementMethod = android.text.method.LinkMovementMethod.getInstance()
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_light)
            setLinkTextColor(ContextCompat.getColor(context, R.color.colorGenericTitle))
            textSizeDimen = R.dimen.text_size_content
        }.lparams(width = wrapContent, height = wrapContent)
    }


    private fun titleActionBar() {
        (activity as MainActivity).customActionBar(
            CALCULATOR_FINIQUITO_RESULT_PAGE,
            enableCustomBar = true,
            needsBackButton = false,
            backMethod = null,
            enableHelp = false
        )

    }

    private fun @AnkoViewDslMarker _LinearLayout.dividerLine() {
        linearLayout {
            backgroundColor =
                ContextCompat.getColor(context, R.color.colorHeaderBackground)
        }.lparams(width = matchParent, height = dip(RouteResultsFragment.DIVIDER_LINE_HEIGHT)) {
            topMargin = dip(calculateComponentsHeight(RouteResultsFragment.DIVIDER_TOP_MARGIN))
        }
    }

    private fun calculateComponentsHeight(heightComponent: Double): Float {
        return (PixelConverter.getScreenDpHeight(context) * heightComponent).toFloat()
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadHeaderResult(
        view: View
    ) {
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            drawHeaderAudioButton(view)
            drawHeaderDescription()
        }.lparams(width = wrapContent, height = wrapContent) {
            bottomMargin = dip(calculateComponentsHeight(RouteResultsFragment.HEADER_BOTTOM_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawHeaderAudioButton(
        view: View
    ) {
        imageView {
            val imageUrl = resources.getIdentifier(
                "ic_audio_without_shadow",
                "drawable",
                context.applicationInfo.packageName
            )
            textToSpeech = UtilsTextToSpeech(context!!, null, null)
            onClick {
                textToSpeech?.speakOut(contentAudio.toString())
                logAnalyticsCustomContentTypeWithId(ContentType.AUDIO, FirebaseEvent.AUDIO)
            }
            Glide.with(view)
                .load(imageUrl)
                .into(this)
        }.lparams(
            width = dip(calculateComponentsWidth(RouteResultsFragment.AUDIO_IMAGE_VIEW_WIDTH)),
            height = dip(calculateComponentsHeight(RouteResultsFragment.AUDIO_IMAGE_VIEW_HEIGHT))
        ) {
            rightMargin =
                (dip(calculateComponentsWidth(RouteResultsFragment.AUDIO_IMAGE_VIEW_MARGIN))
                        / RouteResultsFragment.HALF_DIVIDER)
            leftMargin = dip(calculateComponentsWidth(RouteResultsFragment.AUDIO_IMAGE_VIEW_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawHeaderDescription() {
        val texto = context.getString(R.string.description_labour_result)
        textView {

            text = texto
            contentAudio.append(texto).append("\n")
            textSizeDimen = R.dimen.text_size_content
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_bold)
            textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
        }.lparams(
            width = wrapContent, height =
            dip(calculateComponentsHeight(RouteResultsFragment.AUDIO_IMAGE_VIEW_HEIGHT))
        )
    }

    private fun calculateComponentsWidth(widthComponent: Double): Float {
        return (PixelConverter.getScreenDpWidth(context) * widthComponent).toFloat()
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawRouteResultDescription(
        result: RouteResult
    ) {
        textView {
            text = result.content
            textSizeDimen = R.dimen.text_size_content
            typeface =
                ResourcesCompat.getFont(
                    context.applicationContext,
                    R.font.proxima_nova_light
                )
        }.lparams(
            width = matchParent,
            height = wrapContent
        ) {
            bottomMargin =
                dip(
                    calculateComponentsHeight(
                        RouteResultsFragment.RECOMMENDATION_DESCRIPTION_TOP_BOTTOM_MARGIN
                    )
                )
            topMargin =
                dip(
                    calculateComponentsHeight(
                        RouteResultsFragment.RECOMMENDATION_DESCRIPTION_TOP_BOTTOM_MARGIN
                    )
                )
            leftMargin =
                dip(calculateComponentsWidth(RouteResultsFragment.RECOMMENDATION_DESCRIPTION_LEFT_RIGHT_MARGIN))
            rightMargin =
                dip(calculateComponentsWidth(RouteResultsFragment.RECOMMENDATION_DESCRIPTION_LEFT_RIGHT_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawCategoryButtons(
        result: RouteResult,
        view: View
    ) {
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            result.categories?.map { categoryId ->
                drawCategoryButton(categoryId, view)
            }
        }.lparams(height = wrapContent, width = wrapContent) {
            bottomMargin = dip(calculateComponentsHeight(RouteResultsFragment.FAULT_BOTTOM_MARGIN))
            gravity = Gravity.END
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawCategoryButton(
        categoryId: Int,
        view: View
    ) {
        linearLayout {
            backgroundDrawable = ContextCompat.getDrawable(
                context, R.drawable.drawable_main_route
            )
            linearLayout {
                orientation = LinearLayout.HORIZONTAL
                backgroundResource = attr(R.attr.selectableItemBackgroundBorderless).resourceId
                weightSum = RouteResultsFragment.FULL_WEIGHT_CATEGORY_BUTTON
                isFocusable = true
                isClickable = true
                drawCategoryButtonImage(view)
                val category = calculatorFiniquitoResultsViewModel.getCategoryById(categoryId)
                category?.let {
                    drawCategoryButtonText(it)
                    setOnClickListener {
                        logAnalyticsSelectContentWithId(category.title, ContentType.CATEGORY)
                        if (categoryId == RouteResultsFragment.ROUTE_LABOUR) {
                            navigateToLabourRoute(category)
                        } else
                            navigateToCategoriesInformation(category)
                    }
                }
            }.lparams(
                width = dip(calculateComponentsWidth(RouteResultsFragment.CATEGORY_BUTTON_WIDTH)),
                height = dip(calculateComponentsHeight(RouteResultsFragment.BUTTON_CONTAINER_HEIGHT))
            ) {
                rightMargin =
                    dip(calculateComponentsWidth(RouteResultsFragment.CATEGORY_CARDS_RIGHT_MARGIN))
            }
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawCategoryButtonImage(
        view: View
    ) {
        imageView {
            val imageUrl = resources.getIdentifier(
                "result_category_icon",
                "drawable",
                context.applicationInfo.packageName
            )
            Glide.with(view)
                .load(imageUrl).fitCenter()
                .into(this)
        }.lparams(
            width = dip(0),
            weight = RouteResultsFragment.IMAGE_WEIGHT_CATEGORY_BUTTON,
            height = matchParent
        ) {
            rightMargin =
                dip(calculateComponentsWidth(RouteResultsFragment.IMAGE_VIEW_RIGHT_MARGIN))
            leftMargin = dip(calculateComponentsWidth(RouteResultsFragment.IMAGE_VIEW_LEFT_MARGIN))
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawCategoryButtonText(
        category: Category
    ) {
        textView {
            text = category.title.toLowerCase().capitalize()
            textSizeDimen = R.dimen.text_size
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_light)
            gravity = Gravity.CENTER
            textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
        }.lparams(
            width = dip(0),
            weight = RouteResultsFragment.TEXT_WEIGHT_CATEGORY_BUTTON,
            height = matchParent
        ) {
            rightMargin =
                dip(calculateComponentsWidth(RouteResultsFragment.IMAGE_VIEW_RIGHT_MARGIN))
        }
    }

    private fun navigateToCategoriesInformation(category: Category) {
        val categoryBundle = Bundle().apply {
            putSerializable(
                Constants.CATEGORIES,
                calculatorFiniquitoResultsViewModel.categories as ArrayList
            )
            putSerializable(
                Constants.CATEGORY_PARAMETER,
                category
            )
        }
        NavHostFragment.findNavController(this@CalculatorFiniquitoResultFragment)
            .navigate(
                R.id.genericInfoFragment,
                categoryBundle,
                navOptions,
                null
            )
    }

    private fun navigateToLabourRoute(category: Category) {
        val categoryBundle = Bundle().apply {
            putSerializable(
                Constants.CATEGORIES,
                calculatorFiniquitoResultsViewModel.categories as ArrayList
            )
            putSerializable(
                Constants.CATEGORY_PARAMETER,
                category
            )
            putString(Constants.TYPE_ROUTE, Constants.ROUTE_LABOUR)
        }
        NavHostFragment.findNavController(this@CalculatorFiniquitoResultFragment)
            .navigate(
                R.id.mainScreenLabourRouteFragment,
                categoryBundle,
                navOptions,
                null
            )
    }

}

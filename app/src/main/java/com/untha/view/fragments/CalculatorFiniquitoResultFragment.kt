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
import com.untha.utils.ContentType
import com.untha.utils.FirebaseEvent
import com.untha.utils.PixelConverter
import com.untha.utils.UtilsTextToSpeech
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.CalculatorFiniquitoResultsViewModel
import kotlinx.android.synthetic.main.fragment_calculator_finiquito_result.view.*
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
import java.math.BigDecimal
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
    private var salaryFiniquito: BigDecimal = 0.toBigDecimal()
    private var decimoTerceroResult: BigDecimal = 0.toBigDecimal()
    private var decimoCuartoResult: BigDecimal = 0.toBigDecimal()
    private var fondosReservaResult: BigDecimal = 0.toBigDecimal()
    private var vacationsResult: BigDecimal = 0.toBigDecimal()
    private var listHintIndemnizaciones: List<String> = mutableListOf()
    private lateinit var causalHint: String
    private var desahucioResult: BigDecimal = 0.toBigDecimal()
    private var indemnizaciones: BigDecimal = 0.toBigDecimal()
    private var subtotalResult: BigDecimal = 0.toBigDecimal()
    private var total: BigDecimal = 0.toBigDecimal()


    companion object {
        const val TOP_MARGIN_RESULTS_DINAMIC = 40
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculatorFiniquitoResultsViewModel.loadResultDynamicFromSharePreferences()
        calculatorFiniquitoResultsViewModel.loadResultStaticFromSharePreferences()

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
        val layoutInflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutResultsFiniquito =
            layoutInflater.inflate(layout.fragment_calculator_finiquito_result, null)
        val listResult = calculatorFiniquitoResultsViewModel.hintsSelected
        calculateTotalFiniquito(listResult)

        drawLayoutResult(view, layoutResultsFiniquito)
    }

    private fun calculateTotalFiniquito(listResult: List<String>?) {
        salaryFiniquito = calculatorFiniquitoResultsViewModel.getSalaryLastMonth(
            salary.toBigDecimal(), endDate, startDate
        )
        decimoTerceroResult = calculatorFiniquitoResultsViewModel.getDecimoTercero(
            decimoTercero, startDate, endDate, salary.toBigDecimal()
        )
        decimoCuartoResult = calculatorFiniquitoResultsViewModel.getDecimoCuarto(
            decimoCuarto, startDate, endDate, idArea, hours
        )
        fondosReservaResult = calculatorFiniquitoResultsViewModel.getFondosReserva(
            fondosReserva, startDate, endDate, salary.toBigDecimal()
        )
        vacationsResult = calculatorFiniquitoResultsViewModel.getVacationsNotTaken(
            vacationsDaysTaken, startDate, endDate, bornDate, salary.toDouble()
        )
        listHintIndemnizaciones = getIndemnizacionHint(listResult!!)
        causalHint = getCausalHint(listResult)

        desahucioResult = calculatorFiniquitoResultsViewModel.getDesahucio(
            causalHint, salary.toBigDecimal(), startDate, endDate
        )
        for (hint in listHintIndemnizaciones) {
            indemnizaciones += calculatorFiniquitoResultsViewModel.getIndemnizacion(
                hint,
                salary.toBigDecimal()
            )
        }
        subtotalResult =
            salaryFiniquito + decimoTerceroResult + decimoCuartoResult + fondosReservaResult +
                    (vacationsResult + desahucioResult + indemnizaciones)
        total = subtotalResult - discounts.toBigDecimal()
    }


    private fun drawLayoutResult(view: View, layoutResultsFiniquito: View) {
        calculatorFiniquitoResultsViewModel.retrieveAllCategories()
            .observe(this, Observer { queryingCategories ->
                calculatorFiniquitoResultsViewModel.mapCategories(queryingCategories)

                with(view as _LinearLayout)
                {
                    scrollView {
                        verticalLayout {
                            loadCalculations(layoutResultsFiniquito)
                            addView(layoutResultsFiniquito)
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

    private fun loadCalculations(layoutResultsFiniquito: View) {
        layoutResultsFiniquito.tv_salary_finiquito.text = "$ ".plus(salaryFiniquito)
        layoutResultsFiniquito.tv_decimo_tercero_finiquito.text =
            "$ ".plus(decimoTerceroResult)
        layoutResultsFiniquito.tv_decimo_cuarto_finiquito.text =
            "$".plus(decimoCuartoResult)
        layoutResultsFiniquito.tv_fondos_reserva_finiquito.text =
            "$".plus(fondosReservaResult)
        layoutResultsFiniquito.tv_vacations_finiquito.text =
            "$ ".plus(vacationsResult)
        layoutResultsFiniquito.tv_desahucio_finiquito.text =
            "$".plus(desahucioResult)
        layoutResultsFiniquito.tv_indemnizacion_finiquito.text =
            "$".plus(indemnizaciones)
        layoutResultsFiniquito.tv_subtotal_finiquito.text =
            "$ ".plus(subtotalResult)
        layoutResultsFiniquito.tv_discounts_finiquito.text =
            "-($ ".plus(discounts).plus(")")
        layoutResultsFiniquito.tv_total_finiquito.text = "$ ".plus(total)
    }

    private fun @AnkoViewDslMarker _LinearLayout.loadCalculatorFaults(
        view: View
    ) {
        val answersApplicable = calculatorFiniquitoResultsViewModel.getFaultsApplicable(
            salary,
            hours,
            bornDate,
            startDate,
            endDate
        )
        drawMesssage(R.string.notification)
        if (answersApplicable.isNullOrEmpty()) {
            loadHeaderResult(view, R.string.description_labour_result_not_found)
        } else {
            loadHeaderResult(view, R.string.description_labour_result)
        }
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


    private fun @AnkoViewDslMarker _LinearLayout.buildRecommend() {
        drawMesssage(R.string.calculator_information_finiquito_recommend)
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
        view: View, idText: Int
    ) {
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            drawHeaderAudioButton(view)
            drawHeaderDescription(idText)
        }.lparams(width = wrapContent, height = wrapContent) {
            bottomMargin = dip(calculateComponentsHeight(RouteResultsFragment.HEADER_BOTTOM_MARGIN))
            gravity = Gravity.LEFT
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
        }
    }

    private fun @AnkoViewDslMarker _LinearLayout.drawHeaderDescription(idText: Int) {
        val texto = context.getString(idText)
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

    private fun @AnkoViewDslMarker _LinearLayout.drawMesssage(idText: Int) {
        val texto = context.getString(idText)
        textView {

            text = texto
            contentAudio.append(texto).append("\n")
            textSizeDimen = R.dimen.text_size_content
            typeface =
                ResourcesCompat.getFont(context.applicationContext, R.font.proxima_nova_bold)
            textColor = ContextCompat.getColor(context, R.color.colorHeaderBackground)
        }.lparams(
            width = wrapContent, height = wrapContent
        ) {
            topMargin = dip(TOP_MARGIN_RESULTS_DINAMIC)
        }
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

    private fun getIndemnizacionHint(listHints: List<String>): List<String> {
        val listImdemnizaciones: MutableList<String> = mutableListOf()
        for (hint in listHints) {
            if (hint == "R3P1R1" || hint == "R3P1R2") {
                listImdemnizaciones.add(hint)
            }
            if (hint == "R3P1R3" || hint == "R3P1R4") {
                listImdemnizaciones.add(hint)
            }
        }
        return listImdemnizaciones
    }

    private fun getCausalHint(listHints: List<String>): String {
        var causal = ""
        for (hint in listHints) {
            if (hint == "R3P2R1" || hint == "R3P2R2" || hint == "R3P2R3") {
                causal = hint
            }
            if (hint == "R3P2R4" || hint == "R3P2R5") {
                causal = hint
            }
        }
        return causal
    }

}

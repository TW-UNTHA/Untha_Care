package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ConstantsSpinnerCalculators.CURRENT_YEAR
import com.untha.utils.ConstantsSpinnerCalculators.LAST_FIVE_YEARS
import com.untha.utils.ConstantsSpinnerCalculators.MAX_AGE_WORKING
import com.untha.utils.ConstantsSpinnerCalculators.MAX_YEARS_OF_WORK
import com.untha.utils.ConstantsSpinnerCalculators.MIN_AGE_WORKING
import com.untha.utils.buildDate
import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.endDateToString
import com.untha.utils.getArea
import com.untha.utils.isDaySelectedMajorOfLastDayMonth
import com.untha.utils.loadDaysSpinner
import com.untha.utils.loadSpinnerData
import com.untha.utils.loadYearsAdapter
import com.untha.utils.showToast
import com.untha.utils.transformToCalendarDate
import com.untha.utils.validationDates
import com.untha.utils.validationHours
import com.untha.utils.validationSalaryInput
import com.untha.utils.validationStartDate
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.CalculatorFiniquitoInputThreeViewModel
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerArea
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerEndDateDay
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerEndDateMonth
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerEndDateYear
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerStartDateDay
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerStartDateMonth
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerStartDateYear
import kotlinx.android.synthetic.main.fragment_calculator_finiquito_input_three.*
import kotlinx.android.synthetic.main.fragment_calculator_finiquito_input_three.inputHours
import kotlinx.android.synthetic.main.fragment_calculator_finiquito_input_three.inputSalary
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class CalculatorFiniquitoInputThreeFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var categoriesCalculator: ArrayList<Category>
    private val finiquitoViewModel : CalculatorFiniquitoInputThreeViewModel by viewModel()
    companion object {
        const val TRIAL_PERIOD =14
    }

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
        mainActivity = this.activity as MainActivity
        val calculatorBenefit =
            inflater.inflate(R.layout.fragment_calculator_finiquito_input_three, container, false)
        return calculatorBenefit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_CALCULATOR_ROUTE,
            enableCustomBar = true,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null
        )
        loadAllSpinners()
        goBackMainScreenCategory(Constants.CATEGORIES_CALCULATORS,
            categoriesCalculator,R.id.calculatorsFragment, mainActivity)

        btnSiguiente.setOnClickListener {
            val listResult =finiquitoViewModel.answerSelectedCalculatorRoute()
            val resultCausal = listResult.contains("R3P2R2")

            if (!validationStartDate( spinnerStartDateYear,spinnerStartDateMonth, spinnerStartDateDay,
                    context!!)) {
                return@setOnClickListener
            }
            val startDate = transformToCalendarDate(spinnerStartDateYear, spinnerStartDateMonth,
                spinnerStartDateDay)
            val endDate: Calendar
            if (isDaySelectedMajorOfLastDayMonth(spinnerEndDateYear, spinnerEndDateMonth, spinnerEndDateDay)
            ) {
                showToast(R.string.wrong_date_end, context!!)
                return@setOnClickListener
            } else {
                endDate = transformToCalendarDate( spinnerEndDateYear, spinnerEndDateMonth, spinnerEndDateDay)
            }
            var bornDate: Calendar
            var isValidBornDate: Boolean
            if (isDaySelectedMajorOfLastDayMonth(spinnerBornDateYear,  spinnerBornDateMonth, spinnerBornDateDay
                )) {
                showToast(R.string.wrong_date_born, context!!)
                return@setOnClickListener
            } else {
                bornDate = transformToCalendarDate(spinnerBornDateYear, spinnerBornDateMonth, spinnerBornDateDay)
                isValidBornDate = true
            }

            if (validationDates(startDate, endDate, context!!) && validationSalaryInput(inputSalary, context!!)
                && validationHours(inputHours, context!!)
            ) {

                if(resultCausal && calculateNumberOfDayBetween(startDate, endDate)> TRIAL_PERIOD){
                    showToast(R.string.wrong_date_trial_period, context!!)
                    return@setOnClickListener
                }

                if(isValidBornDate){
                    val bundle = loadBundle(endDate, bornDate)
                    view?.findNavController()?.navigate(
                        R.id.calculatorFiniquitoInputFourFragment, bundle,
                        navOptions, null
                    )
                }
            }

        }

    }

    private fun loadAllSpinners() {
        loadDaysSpinner(spinnerStartDateDay, context!!)
        loadDaysSpinner(spinnerEndDateDay, context!!)
        loadDaysSpinner(spinnerBornDateDay, context!!)

        loadSpinnerData(spinnerStartDateMonth, R.array.months_array, context!!)
        loadSpinnerData(spinnerEndDateMonth, R.array.months_array, context!!)
        loadSpinnerData(spinnerBornDateMonth, R.array.months_array, context!!)

        loadYearsAdapter(spinnerStartDateYear, CURRENT_YEAR, MAX_YEARS_OF_WORK, context!!)
        loadYearsAdapter(spinnerEndDateYear, CURRENT_YEAR, LAST_FIVE_YEARS, context!!)
        loadYearsAdapter(spinnerBornDateYear, MIN_AGE_WORKING, MAX_AGE_WORKING, context!!)

        loadSpinnerData(spinnerArea, R.array.areas_array, context!!)}




    private fun loadBundle(endDate: Calendar, bornDate: Calendar): Bundle {
        val verifiedBornDate = endDateToString(bornDate)
        val verifiedStartDate =
            buildDate(spinnerStartDateYear, spinnerStartDateMonth, spinnerStartDateDay)
        val verifiedEndDate = endDateToString(endDate)
        val area = getArea(spinnerArea)
        val hours = inputHours.text.toString().toInt()
        val salary = inputSalary.text.toString().toBigDecimal()

        return Bundle().apply {
            putString("bornDate", verifiedBornDate)
            putString("startDate", verifiedStartDate)
            putString("endDate", verifiedEndDate)
            putString("salary", salary.toString())
            putInt("hours", hours)
            putInt("idArea", area)
            putSerializable(
                Constants.CATEGORIES_CALCULATORS,
                categoriesCalculator
            )
        }
    }
}

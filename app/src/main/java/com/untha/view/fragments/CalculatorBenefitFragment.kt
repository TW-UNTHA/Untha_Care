package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.navigation.findNavController
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ConstantsSpinnerCalculators.CURRENT_YEAR
import com.untha.utils.ConstantsSpinnerCalculators.LAST_FIVE_YEARS
import com.untha.utils.buildDate
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
import kotlinx.android.synthetic.main.fragment_calculator_benefit.*
import java.util.*

class CalculatorBenefitFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var categoriesCalculator: ArrayList<Category>

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
            inflater.inflate(R.layout.fragment_calculator_benefit, container, false)
        return calculatorBenefit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.customActionBar(
            Constants.NAME_BENEFITS_CALCULATOR,
            enableCustomBar = false,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null
        )
        loadAllSpinners()
        changeStateSpinnersEndDate()

        btnCalcular.setOnClickListener {

            if (!validationStartDate(
                    spinnerStartDateYear,
                    spinnerStartDateMonth,
                    spinnerStartDateDay,
                    context!!
                )
            ) {
                return@setOnClickListener
            }
            val startDate = transformToCalendarDate(
                spinnerStartDateYear,
                spinnerStartDateMonth,
                spinnerStartDateDay
            )


            var endDate: Calendar
            if (checkBoxEndDate.isChecked) {
                endDate = Calendar.getInstance()
            } else {
                if (isDaySelectedMajorOfLastDayMonth(
                        spinnerEndDateYear,
                        spinnerEndDateMonth,
                        spinnerEndDateDay
                    )
                ) {
                    showToast(R.string.wrong_date_end, context!!)
                    return@setOnClickListener
                } else {
                    endDate = transformToCalendarDate(
                        spinnerEndDateYear,
                        spinnerEndDateMonth,
                        spinnerEndDateDay
                    )
                }
            }
            val validDates = validationDates(startDate, endDate, context!!)
            val validSalary = validationSalaryInput(inputSalary, context!!)
            val validWorkday = validationHours(inputHours, context!!)
            if (validDates && validSalary && validWorkday) {
                val bundle = loadBundle(endDate)
                view.findNavController().navigate(
                    R.id.calculatorBenefitResultFragment, bundle,
                    navOptions, null
                )
            }

        }
    }

    private fun loadBundle(endDate: Calendar): Bundle {
        val verifiedStartDate =
            buildDate(spinnerStartDateYear, spinnerStartDateMonth, spinnerStartDateDay)
        val verifiedEndDate = endDateToString(endDate)
        val area = getArea(spinnerArea)
        val hours = inputHours.text.toString().toInt()
        val salary = inputSalary.text.toString().toBigDecimal()

        return Bundle().apply {
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


    private fun changeStateSpinnersEndDate() {
        checkBoxEndDate.setOnClickListener {
            if (checkBoxEndDate.isChecked) {
                changeStateSpinners(spinnerEndDateDay, false)
                changeStateSpinners(spinnerEndDateMonth, false)
                changeStateSpinners(spinnerEndDateYear, false)

                val calendar = Calendar.getInstance()
                spinnerEndDateDay.setSelection(calendar.get(Calendar.DAY_OF_MONTH) - 1)
                spinnerEndDateMonth.setSelection(calendar.get(Calendar.MONTH))
                spinnerEndDateYear.setSelection(0)

            } else {
                changeStateSpinners(spinnerEndDateDay, true)
                changeStateSpinners(spinnerEndDateMonth, true)
                changeStateSpinners(spinnerEndDateYear, true)
            }
        }
    }


    private fun loadAllSpinners() {
        loadDaysSpinner(spinnerStartDateDay, context!!)
        loadDaysSpinner(spinnerEndDateDay, context!!)

        loadSpinnerData(spinnerStartDateMonth, R.array.months_array, context!!)
        loadSpinnerData(spinnerEndDateMonth, R.array.months_array, context!!)

        loadYearsAdapter(spinnerStartDateYear, CURRENT_YEAR, LAST_FIVE_YEARS, context!!)
        loadYearsAdapter(spinnerEndDateYear, CURRENT_YEAR, LAST_FIVE_YEARS, context!!)

        loadSpinnerData(spinnerArea, R.array.areas_array, context!!)
    }

    private fun changeStateSpinners(spinner: Spinner, state: Boolean) {
        spinner.isEnabled = state
        spinner.isClickable = state
        spinner.isActivated = state

    }


}

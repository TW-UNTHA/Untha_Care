package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.findNavController
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ConstantsSpinnerCalculators.CURRENT_YEAR
import com.untha.utils.ConstantsSpinnerCalculators.LAST_FIVE_YEARS
import com.untha.utils.loadDaysSpinner
import com.untha.utils.loadSpinnerData
import com.untha.utils.loadYearsAdapter
import com.untha.utils.stringToCalendar
import com.untha.view.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_calculator_benefit.*
import java.math.RoundingMode
import java.util.*

class CalculatorBenefitFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var categoriesCalculator: ArrayList<Category>


    companion object {
        const val MONTH_WITH_TWO_DIGITS = 10
        const val PARTIAL_TIME = 2
        const val COMPLETE_TIME = 1
        const val COMPLETE_HOURS = 40
        const val MAX_HOURS_WEEK = 60
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
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

            if (!validationStartDate()) {
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
                    showToast(R.string.wrong_date_end)
                    return@setOnClickListener
                } else {
                    endDate = transformToCalendarDate(
                        spinnerEndDateYear,
                        spinnerEndDateMonth,
                        spinnerEndDateDay
                    )
                }
            }
            val validDates = validationDates(startDate, endDate)
            val validSalary = validationSalaryInput()
            val validWorkday = validationHours()
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
        val area = getArea()
        val hours = inputHours.text.toString().toInt()
        val workday = getTypeWorkday()
        val salary = inputSalary.text.toString().toBigDecimal()

        return Bundle().apply {
            putString("startDate", verifiedStartDate)
            putString("endDate", verifiedEndDate)
            putString("salary", salary.toString())
            putInt("hours", hours)
            putInt("idWorkday", workday)
            putInt("idArea", area)
            putSerializable(
                Constants.CATEGORIES_CALCULATORS,
                categoriesCalculator
            )
        }
    }

    private fun getTypeWorkday() = if (inputHours.text.toString().toInt() < COMPLETE_HOURS)
        PARTIAL_TIME else COMPLETE_TIME


    private fun getArea() = if (spinnerArea.selectedItemPosition == 0)
        SIERRA_ORIENTE else COSTA_GALAPAGOS

    private fun endDateToString(endDate: Calendar): String {
        return endDate.get(Calendar.YEAR).toString().plus("-")
            .plus(transformationMonth(endDate.get(Calendar.MONTH))).plus("-")
            .plus(addZero(endDate.get(Calendar.DAY_OF_MONTH)))
    }

    private fun validationDates(startDate: Calendar, endDate: Calendar): Boolean {
        if (startDate.after(endDate)) {
            showToast(R.string.wrong_date_compare_dates)
            return false
        }
        if (startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR) && startDate.get(Calendar.MONTH) == endDate.get(
                Calendar.MONTH
            ) && startDate.get(Calendar.DAY_OF_MONTH) == endDate.get(Calendar.DAY_OF_MONTH)
        ) {
            showToast(R.string.wrong_date_equal_dates)
            return false
        }
        return true
    }

    private fun validationStartDate(): Boolean {
        val isInvalidDate =
            isDaySelectedMajorOfLastDayMonth(
                spinnerStartDateYear,
                spinnerStartDateMonth,
                spinnerStartDateDay
            )
        if (isInvalidDate) {
            showToast(R.string.wrong_date_start)
            return false
        }
        return true
    }

    private fun validationSalaryInput(): Boolean {
        if (inputSalary.text.toString().isEmpty()) {
            showToast(R.string.wrong_salary_empty)
            return false
        }
        val salary = inputSalary.text.toString().toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        if (salary == 0.toBigDecimal().setScale(2, RoundingMode.HALF_UP)) {
            showToast(R.string.wrong_salary_zero)
            return false
        }
        return true
    }

    private fun validationHours(): Boolean {
        if (inputHours.text.toString().isEmpty()) {
            showToast(R.string.wrong_number_of_hours_minimun)
            return false
        }
        if (inputHours.text.toString().toInt() == 0) {
            showToast(R.string.wrong_number_of_hours_zero)
            return false
        }

        if (inputHours.text.toString().toInt() > MAX_HOURS_WEEK) {
            showToast(R.string.wrong_number_of_hours_maximum)
            return false
        }

        return true
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

        loadYearsAdapter(spinnerStartDateYear, CURRENT_YEAR, LAST_FIVE_YEARS,context!!)
        loadYearsAdapter(spinnerEndDateYear, CURRENT_YEAR, LAST_FIVE_YEARS,context!!)

        loadSpinnerData(spinnerArea, R.array.areas_array, context!!)
    }

    private fun transformToCalendarDate(
        spinnerYear: Spinner,
        spinnerMonth: Spinner,
        spinnerDay: Spinner
    ): Calendar {
        val buildDate = buildDate(spinnerYear, spinnerMonth, spinnerDay)
        return stringToCalendar(buildDate)
    }

    private fun buildDate(
        spinnerYear: Spinner,
        spinnerMonth: Spinner,
        spinnerDay: Spinner
    ): String {
        val positionIncremented = spinnerMonth.selectedItemPosition

        val transformMonth = transformationMonth(positionIncremented)
        val buildDate = spinnerYear.selectedItem.toString().plus("-")
            .plus(transformMonth).plus("-")
            .plus(addZero(spinnerDay.selectedItem.toString().toInt()))
        return buildDate
    }

    private fun showToast(idString: Int) {
        Toast.makeText(
            context,
            getString(idString),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun changeStateSpinners(spinner: Spinner, state: Boolean) {
        spinner.isEnabled = state
        spinner.isClickable = state
        spinner.isActivated = state

    }

    private fun transformationMonth(month: Int): String {
        val positionIncremented = month + 1
        val addZeroToMonth = addZero(positionIncremented)

        return addZeroToMonth
    }

    private fun addZero(number: Int): String {
        return if (number < MONTH_WITH_TWO_DIGITS) ("0").plus(number)
        else number.toString()
    }

    private fun isDaySelectedMajorOfLastDayMonth(
        spinnerYear: Spinner,
        spinnerMonth: Spinner,
        spinnerDay: Spinner
    ): Boolean {
        val dateSelected = Calendar.getInstance()
        dateSelected[Calendar.MONTH] = spinnerMonth.selectedItemPosition
        dateSelected[Calendar.YEAR] = spinnerYear.selectedItem.toString().toInt()

        val isMoreThanDayLimit =
            spinnerDay.selectedItem.toString().toInt() > getLastDayOfMonth(dateSelected)
        return isMoreThanDayLimit
    }


    private fun getLastDayOfMonth(date: Calendar) = date.getActualMaximum((Calendar.DAY_OF_MONTH))


}

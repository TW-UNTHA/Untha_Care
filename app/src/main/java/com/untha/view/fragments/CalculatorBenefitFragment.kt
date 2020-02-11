package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.findNavController
import com.untha.R
import com.untha.applicationservices.CalculatorsService
import com.untha.utils.Constants
import com.untha.view.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_calculator_benefit.*
import java.util.*

class CalculatorBenefitFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private var calculatorService: CalculatorsService = CalculatorsService()

    companion object {
        const val RANGE_LAST_DAY_OF_MONTH = 32
        const val LAST_FIVE_YEARS = 5
        const val MONTH_WITH_TWO_DIGITS = 10
        const val PARTIAL_TIME = 1
        const val COMPLETE_TIME = 2
        const val MAX_PARTIAL_HOURS = 36
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        changeAppearancePartialHoursThroughtWorkday()

        changeStateSpinnersEndDate()

        btnCalcular.setOnClickListener {
            val startDate = getStartDateTransformed()
            val endDate = if (checkBoxEndDate.isChecked) Calendar.getInstance() else getEndDate()
            val validDates = validationDates(startDate, endDate)
            val validSalary = validationSalaryInput()
            val validWorkday = validationSpinnerWorkday()
            if (validDates && validSalary && validWorkday) {
                val verifiedStartDate =
                    buildDate(spinnerStartDateYear, spinnerStartDateMonth, spinnerStartDateDay)
                val verifiedEndDate = endDateToString(endDate)
                val area = getArea()
                val hours = getHours()
                val workday = getWorkday()
                val salary = inputSalary.text.toString()

                val bundle = Bundle().apply {
                    putString("startDate", verifiedStartDate)
                    putString("endDate", verifiedEndDate)
                    putString("salary", salary)
                    putInt("hours", hours)
                    putInt("idWorkday", workday)
                    putInt("idArea", area)
                }
                view.findNavController().navigate(
                    R.id.calculatorBenefitResultFragment, bundle,
                    navOptions, null
                )
            }

        }
    }

    private fun getWorkday() = if (spinnerWorkday.selectedItemPosition == PARTIAL_TIME)
        PARTIAL_TIME else COMPLETE_TIME

    private fun getHours() = if (spinnerWorkday.selectedItemPosition == PARTIAL_TIME)
        inputPartialHours.text.toString().toInt() else 0

    private fun getArea() = if (spinnerArea.selectedItemPosition == 0)
        SIERRA_ORIENTE else COSTA_GALAPAGOS

    private fun endDateToString(endDate: Calendar): String {
        return endDate.get(Calendar.YEAR).toString().plus("-")
            .plus(transformationMonth(endDate.get(Calendar.MONTH))).plus("-")
            .plus(addZero(endDate.get(Calendar.DAY_OF_MONTH)))
    }

    private fun getStartDateTransformed(): Calendar {
        validationStartDate()
        val startDate = transformToCalendarDate(
            spinnerStartDateYear,
            spinnerStartDateMonth,
            spinnerStartDateDay
        )
        return startDate
    }

    private fun validationDates(startDate: Calendar, endDate: Calendar): Boolean {
        if (startDate.after(endDate)) {
            showToast(R.string.wrong_date_compare_dates)
            return false
        }
        return true
    }

    private fun validationStartDate(): Boolean {
        val isValidDate =
            isValidDate(spinnerStartDateYear, spinnerStartDateMonth, spinnerStartDateDay)
        if (isValidDate) {
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
        val salary = inputSalary.text.toString().toInt()
        if (salary == 0) {
            showToast(R.string.wrong_salary_zero)
            return false
        }
        return true
    }

    private fun validationSpinnerWorkday(): Boolean {
        if (spinnerWorkday.selectedItemPosition == PARTIAL_TIME) {
            if (inputPartialHours.text.toString().isEmpty()) {
                showToast(R.string.wrong_number_of_hours_minimun)
                return false
            }
            if (inputPartialHours.text.toString().toInt() == 0) {
                showToast(R.string.wrong_number_of_hours_zero)
                return false
            }
            val hours = inputPartialHours.text.toString().toInt()
            if (hours > MAX_PARTIAL_HOURS) {
                showToast(R.string.wrong_number_of_hours_max)
            }
        }
        return true
    }

    private fun changeAppearancePartialHoursThroughtWorkday() {
        spinnerWorkday.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("nothing selected")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                inputPartialHours.visibility =
                    if (position == PARTIAL_TIME) View.VISIBLE else View.GONE
            }
        }
    }

    private fun changeStateSpinnersEndDate() {
        checkBoxEndDate.setOnClickListener {
            if (checkBoxEndDate.isChecked) {
                changeStateSpinners(spinnerEndDateDay, false)
                changeStateSpinners(spinnerEndDateMonth, false)
                changeStateSpinners(spinnerEndDateYear, false)
            } else {
                changeStateSpinners(spinnerEndDateDay, true)
                changeStateSpinners(spinnerEndDateMonth, true)
                changeStateSpinners(spinnerEndDateYear, true)
            }
        }
    }

    private fun getEndDate(): Calendar {
        var date: Calendar = Calendar.getInstance()
        if (isValidDate(spinnerEndDateYear, spinnerEndDateMonth, spinnerEndDateDay)
        ) {
            showToast(R.string.wrong_date_end)
        } else {
            date = transformToCalendarDate(
                spinnerEndDateYear,
                spinnerEndDateMonth,
                spinnerEndDateDay
            )
        }
        return date
    }

    private fun loadAllSpinners() {
        loadDaysSpinner(spinnerStartDateDay)
        loadDaysSpinner(spinnerEndDateDay)

        loadSpinnerData(spinnerStartDateMonth, R.array.days_array)
        loadSpinnerData(spinnerEndDateMonth, R.array.days_array)

        loadYearsAdapter(spinnerStartDateYear)
        loadYearsAdapter(spinnerEndDateYear)

        loadSpinnerData(spinnerWorkday, R.array.workday_array)
        loadSpinnerData(spinnerArea, R.array.areas_array)
    }

    private fun transformToCalendarDate(
        spinnerYear: Spinner,
        spinnerMonth: Spinner,
        spinnerDay: Spinner
    ): Calendar {
        val buildDate = buildDate(spinnerYear, spinnerMonth, spinnerDay)
        return calculatorService.stringToCalendar(buildDate)
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

    private fun isValidDate(
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


    private fun loadYearsAdapter(spinner: Spinner) {
        spinner.adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            loadSpinnerYear()
        )
    }


    private fun loadDaysSpinner(spinner: Spinner) {
        spinner.adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            loadSpinnerDays()
        )
    }

    private fun loadSpinnerDays(): List<Int> {
        val days = mutableListOf<Int>()
        for (day in 1 until RANGE_LAST_DAY_OF_MONTH) {
            days.add(day)
        }
        return days
    }

    private fun loadSpinnerYear(): List<Int> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = mutableListOf<Int>()
        for (year in currentYear downTo (currentYear - LAST_FIVE_YEARS)) {
            years.add(year)
        }
        return years
    }

    private fun loadSpinnerData(spinner: Spinner, idArray: Int) {
        ArrayAdapter.createFromResource(
            context!!,
            idArray,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter

        }
    }
}

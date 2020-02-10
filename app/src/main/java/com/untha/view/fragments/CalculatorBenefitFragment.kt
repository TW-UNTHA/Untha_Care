package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
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
                if (position != 0) {
                    inputPartialHours.visibility = View.VISIBLE
                } else {
                    inputPartialHours.visibility = View.GONE
                }
            }
        }

        checkBoxEndDate.setOnClickListener {
            if (checkBoxEndDate.isChecked) {
                changeStateSpinnersEndDate(spinnerEndDateDay, false)
                changeStateSpinnersEndDate(spinnerEndDateMonth, false)
                changeStateSpinnersEndDate(spinnerEndDateYear, false)
            } else {
                changeStateSpinnersEndDate(spinnerEndDateDay, true)
                changeStateSpinnersEndDate(spinnerEndDateMonth, true)
                changeStateSpinnersEndDate(spinnerEndDateYear, true)
            }
        }

        btnCalcular.setOnClickListener {
            if (isValidDate(spinnerStartDateYear, spinnerStartDateMonth, spinnerStartDateDay)
            ) {
                showToast(R.string.wrong_date_start)
            }
            lateinit var endDate: Calendar
            if (checkBoxEndDate.isChecked) {
                endDate = Calendar.getInstance()
            } else {
                endDate = getEndDate()
            }
            val startDate = transformToCalendarDate(
                spinnerStartDateYear,
                spinnerStartDateMonth,
                spinnerStartDateDay
            )
            if (startDate.compareTo(endDate) > 0) {
                showToast(R.string.wrong_date_compare_dates)
            }

        }
    }

    private fun getEndDate(): Calendar {
        var endDate1: Calendar = Calendar.getInstance()
        if (isValidDate(spinnerEndDateYear, spinnerEndDateMonth, spinnerEndDateDay)
        ) {
            showToast(R.string.wrong_date_end)
        } else {
            endDate1 = transformToCalendarDate(
                spinnerEndDateYear,
                spinnerEndDateMonth,
                spinnerEndDateDay
            )
        }
        return endDate1
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
        val transformMonth = transformationMonth(spinnerMonth)
        val stringDate = spinnerYear.selectedItem.toString().plus("-")
            .plus(transformMonth).plus("-")
            .plus(spinnerDay.selectedItem.toString())
        val date = calculatorService.stringToCalendar(stringDate)
        return date
    }

    private fun showToast(idString: Int) {
        Toast.makeText(
            context,
            getString(idString),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun changeStateSpinnersEndDate(spinner: Spinner, state: Boolean) {
        spinner.isEnabled = state
        spinner.isClickable = state
    }

    private fun transformationMonth(spinnerMonth: Spinner): String {
        val month = spinnerMonth.selectedItemPosition + 1
        val addZeroToMonth = if (month < MONTH_WITH_TWO_DIGITS) {
            ("0").plus(month)
        } else {
            month
        }
        return addZeroToMonth.toString()
    }

    private fun isValidDate(
        spinnerYear: Spinner,
        spinnerMonth: Spinner,
        spinnerDay: Spinner
    ): Boolean {
        val yearMonth = Calendar.getInstance()
        yearMonth[Calendar.MONTH] = spinnerMonth.selectedItemPosition
        yearMonth[Calendar.YEAR] =
            spinnerYear.selectedItem.toString().toInt()

        val isMoreThanDayLimit =
            spinnerDay.selectedItem.toString().toInt() > getLastDayOfMonth(
                yearMonth
            )
        return isMoreThanDayLimit
    }


    private fun getLastDayOfMonth(date: Calendar): Int {
        return date.getActualMaximum((Calendar.DAY_OF_MONTH))
    }


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
        for (i in 1 until RANGE_LAST_DAY_OF_MONTH) {
            days.add(i)
        }
        return days
    }

    private fun loadSpinnerYear(): List<Int> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = mutableListOf<Int>()
        for (i in currentYear downTo (currentYear - LAST_FIVE_YEARS)) {
            years.add(i)
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

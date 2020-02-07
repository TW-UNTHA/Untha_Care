package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.untha.R
import com.untha.utils.Constants
import com.untha.view.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_calculator_benefit.*
import java.util.*

class CalculatorBenefitFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity

    companion object {
        const val RANGE_LAST_DAY_OF_MONTH = 32
        const val LAST_FIVE_YEARS = 5
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
        loadDaysSpinner(spinnerStartDateDay)
        loadDaysSpinner(spinnerEndDateDay)

        loadSpinnerData(spinnerStartDateMonth, R.array.days_array)
        loadSpinnerData(spinnerEndDateMonth, R.array.days_array)

        loadYearsAdapter(spinnerStartDateYear)
        loadYearsAdapter(spinnerEndDateYear)

        loadSpinnerData(spinnerWorkday, R.array.workday_array)
        loadSpinnerData(spinnerArea, R.array.areas_array)

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

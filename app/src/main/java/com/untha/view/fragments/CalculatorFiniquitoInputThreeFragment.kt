package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ConstantsSpinnerCalculators.CURRENT_YEAR
import com.untha.utils.ConstantsSpinnerCalculators.LAST_FIVE_YEARS
import com.untha.utils.ConstantsSpinnerCalculators.MAX_AGE_WORKING
import com.untha.utils.ConstantsSpinnerCalculators.MAX_YEARS_OF_WORK
import com.untha.utils.ConstantsSpinnerCalculators.MIN_AGE_WORKING
import com.untha.utils.loadDaysSpinner
import com.untha.utils.loadSpinnerData
import com.untha.utils.loadYearsAdapter
import com.untha.view.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerArea
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerEndDateDay
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerEndDateMonth
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerEndDateYear
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerStartDateDay
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerStartDateMonth
import kotlinx.android.synthetic.main.fragment_calculator_benefit.spinnerStartDateYear
import kotlinx.android.synthetic.main.fragment_calculator_finiquito_input_three.*
import java.util.*

class CalculatorFiniquitoInputThreeFragment : BaseFragment() {
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
            inflater.inflate(R.layout.fragment_calculator_finiquito_input_three, container, false)
        return calculatorBenefit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.customActionBar(
            Constants.NAME_FINIQUITO_CALCULATOR,
            enableCustomBar = true,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null
        )
        loadAllSpinners()
        goBackMainScreenCategory(
            Constants.CATEGORIES_CALCULATORS,
            categoriesCalculator, R.id.calculatorsFragment, mainActivity
        )

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

        loadSpinnerData(spinnerArea, R.array.areas_array, context!!)
    }


}

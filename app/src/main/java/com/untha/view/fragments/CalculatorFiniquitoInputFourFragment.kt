package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.ConstantsCalculators.DAYS_OF_YEAR
import com.untha.utils.calculateNumberOfDayBetween
import com.untha.utils.stringToCalendar
import com.untha.view.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_calculator_finiquito_input_four.*
import java.util.*

class CalculatorFiniquitoInputFourFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var categoriesCalculator: ArrayList<Category>
    private lateinit var bornDate: String
    private lateinit var startDate: String
    private lateinit var endDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        categoriesCalculator = bundle?.get(Constants.CATEGORIES_CALCULATORS) as ArrayList<Category>
        bornDate = bundle.get("bornDate") as String
        startDate = bundle.get("startDate") as String
        endDate = bundle.get("endDate") as String
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity
        val calculatorFiniquito =
            inflater.inflate(R.layout.fragment_calculator_finiquito_input_four, container, false)
        return calculatorFiniquito
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
        getStartPeriodVacations()
        goBackMainScreenCategory(
            Constants.CATEGORIES_CALCULATORS,
            categoriesCalculator, R.id.calculatorsFragment, mainActivity
        )
        val bundle = Bundle().apply {
            putSerializable(
                Constants.CATEGORIES_CALCULATORS,
                categoriesCalculator
            )
        }
        btnCalcular.setOnClickListener {
            view.findNavController()
                .navigate(
                    R.id.calculatorFiniquitoResultFragment,
                    bundle,
                    navOptions,
                    null
                )
        }
    }

    private fun getStartPeriodVacations() {
        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)
        val numberOfDays = calculateNumberOfDayBetween(calendarStartDate, calendarEndDate)
        val messageDescriptionVacations = "Días de vacaciones tomados desde "
        val spanish = Locale("es", "ES")

        if (numberOfDays >= DAYS_OF_YEAR) {
            val dateOnePeriod = (calendarEndDate.get(Calendar.YEAR) - 1).toString().plus("/")
                .plus(calendarEndDate.getDisplayName(Calendar.MONTH, Calendar.LONG, spanish))
                .plus("/").plus(calendarEndDate.get(Calendar.DAY_OF_MONTH))
            tv_description_vacation.text = messageDescriptionVacations.plus(dateOnePeriod)

        } else {
            tv_description_vacation.text =
                messageDescriptionVacations.plus(calendarStartDate.get(Calendar.YEAR)).plus("/")
                    .plus(calendarStartDate.getDisplayName(Calendar.MONTH, Calendar.LONG, spanish))
                    .plus("/").plus(calendarStartDate.get(Calendar.DAY_OF_MONTH))

        }
    }

}

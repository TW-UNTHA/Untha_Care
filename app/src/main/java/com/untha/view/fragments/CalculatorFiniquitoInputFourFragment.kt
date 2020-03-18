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
import com.untha.utils.showToast
import com.untha.utils.stringToCalendar
import com.untha.view.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_calculator_finiquito_input_four.*
import java.util.*

class CalculatorFiniquitoInputFourFragment : BaseFragment() {
    companion object {
        const val MENSUAL = 1
        const val ACUMULADO = 2
        const val MAX_DAYS_OF_VACATION = 20
    }

    private lateinit var mainActivity: MainActivity
    private lateinit var categoriesCalculator: ArrayList<Category>
    private lateinit var bornDate: String
    private lateinit var startDate: String
    private lateinit var endDate: String
    private lateinit var salary: String
    private var hours: Int =0
    private var idWorkday: Int =0
    private var idArea: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        categoriesCalculator = bundle?.get(Constants.CATEGORIES_CALCULATORS) as ArrayList<Category>
        bornDate = bundle.get("bornDate") as String
        startDate = bundle.get("startDate") as String
        endDate = bundle.get("endDate") as String
        salary = bundle.get("salary") as String
        hours = bundle.get("hours") as Int
        idArea = bundle.get("idArea") as Int


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
            Constants.NAME_SCREEN_CALCULATOR_ROUTE,
            enableCustomBar = true,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null
        )

        val calendarStartDate = stringToCalendar(startDate)
        val calendarEndDate = stringToCalendar(endDate)

        getStartPeriodVacations(calendarStartDate, calendarEndDate)
        goBackMainScreenCategory(
            Constants.CATEGORIES_CALCULATORS,
            categoriesCalculator, R.id.calculatorsFragment, mainActivity
        )
        btnCalcular.setOnClickListener {
            val vacationsDaysTaken = if(input_vacations.text.isEmpty()){0}else{input_vacations.text.toString().toInt()}

            if(vacationsDaysTaken> MAX_DAYS_OF_VACATION) {
                showToast(R.string.wrong_number_of_days_vacations, context!!)
                return@setOnClickListener
            }
            val bundle = loadBundle(vacationsDaysTaken)
            view?.findNavController()?.navigate(
                R.id.calculatorFiniquitoResultFragment, bundle,
                navOptions, null
            )

        }

    }

    private fun loadBundle(vacationsDaysTaken: Int): Bundle {
        val decimoTerceroSelected = rg_decimo_tercero.checkedRadioButtonId
        val decimoCuartoSelected = rg_decimo_cuarto.checkedRadioButtonId
        val fondosReservaReservaSelected = rg_fondos_reserva.checkedRadioButtonId
        val discounts = if (input_discounts.text.isEmpty()) {
            0.toString()
        } else {
            input_discounts.text.toString()
        }


        val decimoTerceroOption =
            getIdFromSelectedOption(decimoTerceroSelected, rb_mensual_decimo_tercero.id)
        val decimoCuartoOption =
            getIdFromSelectedOption(decimoCuartoSelected, rb_mensual_decimo_cuarto.id)
        val fondosReservaOption =
            getIdFromSelectedOption(fondosReservaReservaSelected, rb_mensual_fondos_reserva.id)

        val bundle = Bundle().apply {
            putString("bornDate", bornDate)
            putString("startDate", startDate)
            putString("endDate", endDate)
            putString("salary", salary)
            putInt("hours", hours)
            putInt("idWorkday", idWorkday)
            putInt("idArea", idArea)
            putInt("decimoTercero", decimoTerceroOption)
            putInt("decimoCuarto", decimoCuartoOption)
            putInt("fondosReserva", fondosReservaOption)
            putInt("vacationsDaysTaken", vacationsDaysTaken)
            putString("discounts", discounts)
            putSerializable(
                Constants.CATEGORIES_CALCULATORS,
                categoriesCalculator
            )

        }
        return bundle
    }

    private fun getIdFromSelectedOption(
        selectedItemId: Int,
        rbMensualId: Int
    ): Int {
        var result: Int

        if (selectedItemId == rbMensualId) {
            result = MENSUAL
        } else {
            result = ACUMULADO
        }
        return result
    }

    private fun getStartPeriodVacations(calendarStartDate: Calendar, calendarEndDate : Calendar) {

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

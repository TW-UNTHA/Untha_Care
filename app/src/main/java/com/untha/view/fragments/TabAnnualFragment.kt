package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.untha.viewmodels.CalculatorViewModel
import kotlinx.android.synthetic.main.fragment_item_result_annual.*
import org.koin.android.viewmodel.ext.android.viewModel

class TabAnnualFragment(
    private val salary: String,
    private val startDate: String,
    private val endDate: String,
    private val idArea: Int,
    private val hours: Int

) : BaseFragment() {
    private val calculatorViewModel: CalculatorViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            com.untha.R.layout.fragment_item_result_annual,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_decimo_tercero_annual.text =
            "$".plus(calculatorViewModel.getDecimoTercerSueldoAcumulado(startDate, endDate, salary)
                .toString())
        tv_decimo_cuarto_annual.text =
            "$".plus(calculatorViewModel.getDecimoCuartoAcumulado(startDate, endDate,idArea,hours)
                .toString())

    }
}

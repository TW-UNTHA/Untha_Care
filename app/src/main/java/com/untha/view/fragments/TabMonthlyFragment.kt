package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.untha.viewmodels.CalculatorViewModel
import kotlinx.android.synthetic.main.fragment_item_result_annual.*
import kotlinx.android.synthetic.main.fragment_item_result_monthly.*
import org.koin.android.viewmodel.ext.android.viewModel

class TabMonthlyFragment(
    private val salary: String,
    private val idWorkday: Int,
    private val hours: Int
) : BaseFragment() {
    private val calculatorViewModel: CalculatorViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            com.untha.R.layout.fragment_item_result_monthly,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_decimo_tercero_monthly.text =
            "$".plus(calculatorViewModel.getDecimoTerceroMensualizado(salary.toBigDecimal())
                .toString())
        tv_decimo_cuarto_monthly.text =
            "$".plus(calculatorViewModel.getDecimoCuartoMensualizado(idWorkday, hours).toString())

    }
}

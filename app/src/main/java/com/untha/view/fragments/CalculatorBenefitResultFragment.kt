package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.untha.utils.Constants
import com.untha.view.activities.MainActivity
import com.untha.view.adapters.TabAdapter
import com.untha.viewmodels.CalculatorViewModel
import kotlinx.android.synthetic.main.fragment_calculator_benefit_result.*
import org.koin.android.viewmodel.ext.android.viewModel


class CalculatorBenefitResultFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private val calculatorViewModel: CalculatorViewModel by viewModel()
    private lateinit var salary: String
    private lateinit var startDate: String
    private lateinit var endDate: String
    private var hours: Int = 0
    private var idArea: Int = 0
    private var idWorkday: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        salary = bundle?.get("salary") as String
        startDate = bundle?.get("startDate") as String
        endDate = bundle?.get("endDate") as String
        idWorkday = bundle?.get("idWorkday") as Int
        idArea = bundle?.get("idArea") as Int
        hours = bundle?.get("hours") as Int
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity
        val calculatorBenefitResult =
            inflater.inflate(
                com.untha.R.layout.fragment_calculator_benefit_result,
                container,
                false
            )
        return calculatorBenefitResult
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpViewPager()
        tl_tabs.setupWithViewPager(vp_tabs)
        tl_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                print("")
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                print("")
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                print("")
            }
        })
    }

    private fun setUpViewPager() {
        val adapter = TabAdapter(childFragmentManager)
        adapter.addFragment(
            TabAnnualFragment(salary, startDate, endDate, idWorkday, idArea, hours),
            "Anual"
        )
        adapter.addFragment(TabMonthlyFragment(salary, idWorkday, hours), "Mensual")
        vp_tabs.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val percentageIESS = calculatorViewModel.getAportacionMensualIESS(salary)
        tv_IESS.text = percentageIESS

        tv_salary.text = (salary.toBigDecimal() - percentageIESS.toBigDecimal()).toString()
        tv_fondos_reserva.text =
            calculatorViewModel.getFondoReservaMensualizado(startDate, endDate, salary).toString()


        mainActivity.customActionBar(
            Constants.NAME_BENEFITS_CALCULATOR,
            enableCustomBar = false,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null
        )
    }
}



package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.untha.utils.Constants
import com.untha.view.activities.MainActivity


class CalculatorBenefitResultFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.customActionBar(
            Constants.NAME_BENEFITS_CALCULATOR,
            enableCustomBar = false,
            needsBackButton = true,
            enableHelp = false,
            backMethod = null
        )
    }
}



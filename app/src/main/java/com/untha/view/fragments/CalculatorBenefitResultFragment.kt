package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            inflater.inflate(0, container, false)
        return calculatorBenefitResult
    }
}

package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.untha.R
import com.untha.utils.Constants
import com.untha.utils.PixelConverter
import com.untha.view.activities.MainActivity
import com.untha.view.adapters.CalculatorAdapter
import kotlinx.android.synthetic.main.fragment_calculator.*


class CalculatorFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var calculatorAdapter: CalculatorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity
        val calculatorView = inflater.inflate(R.layout.fragment_calculator, container, false)
        return calculatorView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.customActionBar(
            Constants.NAME_SCREEN_CALCULATOR,
            enableCustomBar = true,
            needsBackButton = true,
            enableHelp = true,
            backMethod = null
        )
        setMarginsToRecyclerView()
        val calculators: ArrayList<String> = arrayListOf("beneficios", "liquidacion")
        populateCategoryList(calculators)

    }

    private fun populateCategoryList(calculators: ArrayList<String>) {
        val layoutManager = GridLayoutManager(context, Constants.SPAN_TWO_COLUMNS)
        calculatorRecyclerView.layoutManager = layoutManager
        calculatorAdapter = CalculatorAdapter(calculators)
        calculatorRecyclerView.adapter = calculatorAdapter
    }

    private fun setMarginsToRecyclerView() {
        val marginInDps =
            PixelConverter.getScreenDpHeight(context) * Constants.MARGIN_TOP_PERCENTAGE
        context?.let { context ->
            val pixelBottomMargin = PixelConverter.toPixels(marginInDps, context)

            val param = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            param.setMargins(0, pixelBottomMargin, pixelBottomMargin, pixelBottomMargin)
            calculatorRecyclerView.layoutParams = param
        }
    }

}

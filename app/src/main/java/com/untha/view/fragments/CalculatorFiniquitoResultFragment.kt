package com.untha.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.untha.R
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.untha.utils.Constants.CALCULATOR_FINIQUITO_RESULT_PAGE
import com.untha.view.activities.MainActivity
import com.untha.viewmodels.CalculatorFiniquitoResultsViewModel
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.verticalLayout
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.ArrayList

class CalculatorFiniquitoResultFragment : BaseFragment() {
    private lateinit var mainActivity: MainActivity
    private val calculatorFiniquitoResultsViewModel: CalculatorFiniquitoResultsViewModel by viewModel()
    private lateinit var categoriesCalculator: ArrayList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculatorFiniquitoResultsViewModel.loadResultDynamicFromSharePreferences()
        calculatorFiniquitoResultsViewModel.loadResultStaticFromSharePreferences()
        categoriesCalculator =
            arguments?.get(Constants.CATEGORIES_CALCULATORS) as ArrayList<Category>

    }

    override fun onResume() {
        super.onResume()
        titleActionBar()
        goBackMainScreenCategory(
            Constants.CATEGORIES_CALCULATORS,
            categoriesCalculator, R.id.calculatorsFragment, mainActivity
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = this.activity as MainActivity

        return createMainLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun createMainLayout(
    ): View {
        return UI {
            verticalLayout {
                backgroundColor =
                    ContextCompat.getColor(context, R.color.colorBackgroundMainRoute)
                lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    private fun titleActionBar() {
        (activity as MainActivity).customActionBar(
            CALCULATOR_FINIQUITO_RESULT_PAGE,
            enableCustomBar = true,
            needsBackButton = false,
            backMethod = null,
            enableHelp = false
        )

    }

}

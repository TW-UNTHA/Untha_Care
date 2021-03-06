package com.untha.view.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.untha.R
import com.untha.utils.Constants
import com.untha.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.textSizeDimen
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModel()

    private lateinit var navigationController: NavController

    private var backMethod: (() -> Unit)? = null
    var isLastScreen: (() -> Boolean)? = null
    var isRouteResultsScreen: (() -> Boolean)? = null
    private var isInternetAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isInternetAvailable = intent.getBooleanExtra(UpdateActivity.INTERNET_AVAILABLE, false)
        loadData()
        navigationController = findNavController(navigationHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navigationController)
    }

    private fun loadData() {
        if (isInternetAvailable) {

            viewModel.retrieveUpdatedCategories(this)
            viewModel.loadLabourRoute(this)
            viewModel.loadViolenceRoute(this)
            viewModel.loadRouteResults(this)
            viewModel.loadQuestionnaireRouteResult(this)
            viewModel.loadCalculatorRoute(this)
            viewModel.getResultCalculator(this)
            viewModel.getRecommendCalculator(this)
            viewModel.getConstants(this)
            viewModel.getNews(this)


        } else {
            viewModel.loadDefaultCategories(this)
            viewModel.loadDefaultBase(this, Constants.LABOUR_ROUTE, R.raw.labour_route)
            viewModel.loadDefaultBase(this, Constants.VIOLENCE_ROUTE, R.raw.violence_route)
            viewModel.loadDefaultBase(this, Constants.ROUTE_RESULT, R.raw.result)
            viewModel.loadDefaultBase(
                this,
                Constants.QUESTIONNAIRE_ROUTE,
                R.raw.questionnaire_route_result
            )
            viewModel.loadDefaultBase(this, Constants.CALCULATOR_ROUTE, R.raw.calculator_route)
            viewModel.loadDefaultBase(
                this,
                Constants.CALCULATOR_ROUTE_RESULT,
                R.raw.result_calculator
            )
            viewModel.loadDefaultBase(
                this,
                Constants.CALCULATOR_RECOMMEND,
                R.raw.recommend_calculator
            )
            viewModel.loadDefaultBase(this, Constants.CONSTANTS, R.raw.constants)
            viewModel.loadDefaultBase(this, Constants.NEWS, R.raw.news)

        }
    }

    fun customActionBar(
        title: String, enableCustomBar: Boolean, needsBackButton: Boolean,
        enableHelp: Boolean,
        backMethod: (() -> Unit)?
    ) {
        val layoutActionBar = layoutInflater.inflate(R.layout.action_bar, null)
        val titleView = layoutActionBar.findViewById(R.id.title_action_bar) as TextView
        val close = layoutActionBar?.findViewById(R.id.icon_go_back_route) as ImageView
        val iconHelp = layoutActionBar.findViewById(R.id.icon_help) as ImageView

        titleView.text = title
        titleView.textSizeDimen = R.dimen.text_size_question_route
        val layout = ActionBar.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        supportActionBar?.setCustomView(layoutActionBar, layout)
        this.backMethod = backMethod
        supportActionBar?.title = title
        supportActionBar?.setDisplayShowCustomEnabled(enableCustomBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(needsBackButton)

        if (enableHelp) {
            iconHelp.isVisible = enableHelp
            close.isVisible = false
        } else {
            iconHelp.isVisible = false
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            backMethod?.let {
                it()
            } ?: super.onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        isLastScreen?.let {
            if (it()) {
                finish()
            }
        }

        isRouteResultsScreen?.let {
            if (!it()) {
                super.onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp() = navigationController.navigateUp()
}



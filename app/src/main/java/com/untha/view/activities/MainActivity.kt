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
import com.untha.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.navigationHostFragment
import org.jetbrains.anko.textSizeDimen
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModel()

    private lateinit var navigationController: NavController

    private var backMethod: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadData()
        navigationController = findNavController(navigationHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navigationController)
    }

    private fun isConnected(): Boolean {
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }

    private fun loadData() {
        if (isConnected()) {
            viewModel.retrieveUpdatedCategories(this)
            viewModel.loadLabourRoute(this)
            viewModel.loadViolenceRoute(this)
            viewModel.loadRouteResults(this)
            viewModel.loadQuestionnaireRouteResult(this)
        } else {
            viewModel.loadDefaultCategories(this)
            viewModel.loadDefaultLabourRoute(this)
            viewModel.loadDefaultViolenceRoute(this)
            viewModel.loadDefaultResult(this)
            viewModel.loadDefaultQuestionnaireRouteResult(this)
        }
    }

    fun customActionBar(
        title: String, enableCustomBar: Boolean, needsBackButton: Boolean,
        backMethod: (() -> Unit)?
    ) {
        val layoutActionBar = layoutInflater.inflate(R.layout.action_bar, null)
        val titleView = layoutActionBar.findViewById(R.id.title_actiov_bar) as TextView
        val close = layoutActionBar?.findViewById(R.id.icon_go_back_route) as ImageView
        titleView.text = title
        titleView.textSizeDimen = R.dimen.text_size_question_route
        val layout = ActionBar.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        supportActionBar?.setCustomView(layoutActionBar, layout)
        this.backMethod = backMethod
        supportActionBar?.setDisplayShowCustomEnabled(needsBackButton)
        supportActionBar?.setDisplayHomeAsUpEnabled(needsBackButton)
        close.isVisible = enableCustomBar
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            backMethod?.let {
                it()
            } ?: super.onBackPressed()
        }
        return true
    }

    override fun onSupportNavigateUp() = navigationController.navigateUp()
}

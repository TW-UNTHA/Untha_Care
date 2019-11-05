package com.untha.view.activities

import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    private lateinit var navigationController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.untha.R.layout.activity_main)
        loadData()
        navigationController = findNavController(navigationHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navigationController)
    }

    fun isConnected(): Boolean {
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }

    private fun loadData() {
        if (isConnected()) {
            viewModel.retrieveUpdatedCategories(this)
            viewModel.loadLabourRoute(this)
            viewModel.loadViolenceRoute(this)
            viewModel.loadResult(this)
        } else {
            viewModel.loadDefaultLabourRoute(this)
            viewModel.loadDefaultViolenceRoute(this)
            viewModel.loadDefaultResult(this)
        }
    }

    fun customActionBar(title:String, enableCustomBar:Boolean){
        val layoutActionBar = layoutInflater.inflate(R.layout.action_bar, null)
        val titleView = layoutActionBar.findViewById(R.id.title_actiov_bar) as TextView
        val close = layoutActionBar?.findViewById(R.id.icon_go_back_route) as ImageView
        titleView.text=title
        val layout = ActionBar.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        supportActionBar?.setCustomView(layoutActionBar, layout)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        close.isVisible = enableCustomBar
    }

    override fun onSupportNavigateUp() = navigationController.navigateUp()

}


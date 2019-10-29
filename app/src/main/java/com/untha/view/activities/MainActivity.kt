package com.untha.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI
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

    override fun onSupportNavigateUp() = navigationController.navigateUp()
}

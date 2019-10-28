package com.untha.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        setContentView(R.layout.activity_main)
        loadApplicationData()
        navigationController = findNavController(navigationHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navigationController)
    }

    private fun loadApplicationData() {
        viewModel.retrieveUpdatedCategories(this)
        viewModel.loadDefaultLabourRoute(this)
        viewModel.loadDefaultViolenceRoute(this)
        viewModel.loadLabourRoute(this)
        viewModel.loadViolenceRoute(this)
    }

    override fun onSupportNavigateUp() = navigationController.navigateUp()
}

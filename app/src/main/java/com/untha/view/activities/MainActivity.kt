package com.untha.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.untha.R
import com.untha.viewmodels.MainViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.retrieveUpdatedCategories(this)
    }
}

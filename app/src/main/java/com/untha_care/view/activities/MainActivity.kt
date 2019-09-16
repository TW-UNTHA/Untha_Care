package com.untha_care.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.untha_care.R
import com.untha_care.viewmodels.ExampleViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: ExampleViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

package com.untha.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.untha.R
import com.untha.viewmodels.ExampleViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: ExampleViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

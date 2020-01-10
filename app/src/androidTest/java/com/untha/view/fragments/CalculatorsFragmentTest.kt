package com.untha.view.fragments

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import com.untha.model.transactionalmodels.Category
import com.untha.utils.Constants
import com.utils.MockObjects
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import android.support.test.espresso.Espresso

@RunWith(JUnit4::class)
class CalculatorsFragmentTest {

    val mockCategoriesCalculators = arrayListOf(MockObjects.mockQueryingCategory("calculator"))

    @Before
    fun setUp() {
    }
    @Test
    public fun `is fragment launched`() {
        val fragmentArgs = Bundle().apply {
            putSerializable(Constants.CATEGORIES_CALCULATORS, mockCategoriesCalculators)
        }

        val scenario = launchFragmentInContainer<CalculatorFragment>(
            fragmentArgs)

        Espress

    }
}
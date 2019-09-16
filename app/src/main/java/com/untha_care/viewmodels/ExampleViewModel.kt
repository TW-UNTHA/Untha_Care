package com.untha_care.viewmodels

import androidx.lifecycle.ViewModel
import com.untha_care.database.ApplicationDatabase
import com.untha_care.model.services.LawsAndRightsService

class ExampleViewModel(
    val database: ApplicationDatabase,
    val lawsAndRightsService: LawsAndRightsService
) : ViewModel()
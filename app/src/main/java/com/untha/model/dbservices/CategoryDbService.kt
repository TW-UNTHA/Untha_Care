package com.untha.model.dbservices

import com.untha.model.models.CategoryInformationModel
import com.untha.model.models.CategoryModel
import com.untha.model.models.SectionModel
import com.untha.model.models.SectionStepModel
import com.untha.model.repositories.CategoryRepository
import com.untha.model.repositories.InformationCategoryRepository
import com.untha.model.repositories.SectionRepository
import com.untha.model.repositories.SectionStepRepository
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread

class CategoryDbService(
    private val categoryRepository: CategoryRepository,
    private val informationCategoryRepository: InformationCategoryRepository,
    private val sectionRepository: SectionRepository,
    private val sectionStepRepository: SectionStepRepository
) {

    fun saveCategory(categories: List<CategoryModel>, callback: (result: String) -> Unit) {

        doAsync {
            categoryRepository.insert(categories)
            uiThread {
                callback("Categories Successfully Saved")
            }
        }
    }

    fun saveCategoryInformation(
        categoryInformationModels: List<CategoryInformationModel>,
        callback: (result: String) -> Unit
    ) {
        doAsync {
            informationCategoryRepository.insert(categoryInformationModels)
            uiThread {
                callback("Information Category Successfully Saved")
            }
        }
    }

    fun saveSections(sections: List<SectionModel>, callback: (result: String) -> Unit) {

        doAsync {
            sectionRepository.insert(sections)
            uiThread {
                callback("Sections succesfully saved")
            }
        }
    }

    fun saveSectionSteps(steps: List<SectionStepModel>, callback: (result: String) -> Unit) {

        doAsyncResult {
            sectionStepRepository.insert(steps)
            uiThread {
                callback("Section Steps successfully saved")
            }
        }
    }
}

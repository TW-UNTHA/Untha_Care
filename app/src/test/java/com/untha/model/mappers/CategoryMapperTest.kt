package com.untha.model.mappers

import com.untha.model.models.CategoryInformationModel
import com.untha.model.models.CategoryModel
import com.untha.model.models.QueryingCategory
import com.untha.model.models.QueryingCategoryInformation
import com.untha.model.models.QueryingSection
import com.untha.model.models.SectionModel
import com.untha.model.models.SectionStepModel
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.CategoryInformation
import com.untha.model.transactionalmodels.Section
import com.untha.model.transactionalmodels.Step
import com.utils.RandomGenerator.generateIntBetween0AndTwenty
import com.utils.RandomGenerator.generateRandomString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CategoryMapperTest {

    @Test
    fun `should convert Category to CategoryModel`() {
        val step = Step(1, generateRandomString(5))
        val step2 = Step(2, generateRandomString(5))
        val section = Section(
            1,
            title = generateRandomString(5),
            steps = listOf(step)
        )
        val section2 = Section(
            2,
            title = generateRandomString(5),
            steps = listOf(step2)
        )
        val categoryInformation =
            CategoryInformation(
                1,
                description = generateRandomString(5),
                image = generateRandomString(size = 5),
                screenTitle = generateRandomString(size = 5),
                sections = listOf(section, section2)
            )
        val category = Category(
            1,
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            false,
            generateIntBetween0AndTwenty(),
            "route",
            listOf(categoryInformation)
        )

        val stepModel = SectionStepModel(0, step.description, step.stepId, section.id)
        val stepModel2 = SectionStepModel(0, step2.description, step2.stepId, section2.id)
        val sectionModel =
            SectionModel(section.id, section.title, category.id, listOf(stepModel))
        val sectionModel2 =
            SectionModel(section2.id, section2.title, category.id, listOf(stepModel2))
        val informationModel = CategoryInformationModel(
            categoryInformation.id,
            categoryInformation.description!!,
            categoryInformation.image!!,
            categoryInformation.screenTitle!!,
            categoryInformation.nextStep,
            category.id,
            listOf(sectionModel, sectionModel2)
        )

        val categoryModel = CategoryModel(
            category.id,
            category.title,
            category.subtitle,
            category.titleNextStep,
            category.image,
            category.isRoute,
            category.parentId,
            category.type,
            listOf(informationModel)
        )

        val categoryMapper = CategoryMapper()
        val result = categoryMapper.mapToModel(category)
        assertThat(result.categoryInformationModel, `is`(categoryModel.categoryInformationModel))
        assertThat(result.id, `is`(categoryModel.id))
        assertThat(result.title, `is`(categoryModel.title))
        assertThat(result.subtitle, `is`(categoryModel.subtitle))
        assertThat(result.parentId, `is`(categoryModel.parentId))
    }

    @Test
    fun `should convert CategoryModel to Category`() {

        val stepModel1 = SectionStepModel(1, generateRandomString(5), 1, 1)
        val stepModel2 = SectionStepModel(2, generateRandomString(5), 1, 1)
        val stepModel3 = SectionStepModel(3, generateRandomString(5), 2, 2)
        val sectionModel1 = SectionModel(1, generateRandomString(5), 1)
        val sectionModel2 = SectionModel(2, generateRandomString(5), 2)
        val categoryInformationModel1 = CategoryInformationModel(
            1,
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            1
        )
        val categoryInformationModel2 = CategoryInformationModel(
            2,
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            2
        )
        val categoryModel1 = CategoryModel(
            1,
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            false,
            0
        )
        val categoryModel2 = CategoryModel(
            2,
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            false,
            1
        )

        val queryingSection = QueryingSection().apply {
            steps = listOf(stepModel1, stepModel2)
            section = sectionModel1
        }

        val queryingCategoryInformation =
            QueryingCategoryInformation().apply {
                categoryInformation = categoryInformationModel1
                this.queryingSection = listOf(queryingSection)
            }

        val queryingCategory = QueryingCategory().apply {
            categoryModel = categoryModel1
            this.queryingCategoryInformation = listOf(queryingCategoryInformation)
        }


        val queryingSection2 = QueryingSection().apply {
            steps = listOf(stepModel3)
            section = sectionModel2
        }

        val queryingCategoryInformation2 =
            QueryingCategoryInformation().apply {
                categoryInformation = categoryInformationModel2
                this.queryingSection = listOf(queryingSection2)
            }

        val queryingCategory2 = QueryingCategory().apply {
            categoryModel = categoryModel2
            this.queryingCategoryInformation = listOf(queryingCategoryInformation2)
        }

        val expectedStep = Step(stepModel1.stepId!!, stepModel1.description)
        val expectedStep2 = Step(stepModel2.stepId!!, stepModel2.description)
        val expectedStep3 = Step(stepModel3.stepId!!, stepModel3.description)
        val expectedSection = Section(
            sectionModel1.id,
            title = sectionModel1.title,
            steps = listOf(expectedStep, expectedStep2)
        )
        val expectedSection2 =
            Section(
                sectionModel2.id,
                title = sectionModel2.title,
                steps = listOf(expectedStep3)
            )
        val expectedCategoryInformation =
            CategoryInformation(
                id = categoryInformationModel1.id,
                description = categoryInformationModel1.description,
                image = categoryInformationModel1.image,
                screenTitle = categoryInformationModel1.screenTitle,
                nextStep = categoryInformationModel1.nextStep,
                sections = listOf(expectedSection)
            )

        val expectedCategoryInformation2 =
            CategoryInformation(
                id = categoryInformationModel2.id,
                description = categoryInformationModel2.description,
                image = categoryInformationModel2.image,
                screenTitle = categoryInformationModel2.screenTitle,
                nextStep = categoryInformationModel2.nextStep,
                sections = listOf(expectedSection2)
            )
        val expectedCategory = Category(
            categoryModel1.id,
            categoryModel1.title,
            categoryModel1.subtitle,
            categoryModel1.titleNextStep,
            categoryModel1.image,
            categoryModel1.isRoute,
            categoryModel1.parentId,
            categoryModel1.type,
            listOf(expectedCategoryInformation)
        )

        val expectedCategory2 = Category(
            categoryModel2.id,
            categoryModel2.title,
            categoryModel2.subtitle,
            categoryModel2.titleNextStep,
            categoryModel2.image,
            categoryModel2.isRoute,
            categoryModel2.parentId,
            categoryModel1.type,
            listOf(expectedCategoryInformation2)
        )

        val categoryMapper = CategoryMapper()
        val result = listOf(
            queryingCategory,
            queryingCategory2
        ).map { categoryMapper.mapFromModel(it) }


        assertThat(result[0], `is`(expectedCategory))
        assertThat(result[1], `is`(expectedCategory2))
    }
}
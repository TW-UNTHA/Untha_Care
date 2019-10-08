package com.untha.model.mappers

import com.untha.model.models.CategoryInformationModel
import com.untha.model.models.CategoryModel
import com.untha.model.models.QueryingCategory
import com.untha.model.models.QueryingCategoryInformation
import com.untha.model.models.SectionModel
import com.untha.model.models.SectionStepModel
import com.untha.model.models.QueryingSection
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
                description = generateRandomString(5),
                image = generateRandomString(size = 5),
                sections = listOf(section, section2)
            )
        val category = Category(
            1,
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            generateIntBetween0AndTwenty(),
            generateIntBetween0AndTwenty(),
            categoryInformation
        )

        val stepModel = SectionStepModel(0, step.description, step.stepId, section.id)
        val stepModel2 = SectionStepModel(0, step2.description, step2.stepId, section2.id)
        val sectionModel =
            SectionModel(section.id, section.title, category.id, listOf(stepModel))
        val sectionModel2 =
            SectionModel(section2.id, section2.title, category.id, listOf(stepModel2))
        val informationModel = CategoryInformationModel(
            category.id,
            categoryInformation.description!!,
            categoryInformation.image!!,
            category.id,
            listOf(sectionModel, sectionModel2)
        )

        val categoryModel = CategoryModel(
            category.id,
            category.title,
            category.subtitle,
            category.image,
            category.parentId,
            category.nextStep,
            informationModel
        )

        val categoryMapper = CategoryMapper()
        val result = categoryMapper.mapToModel(category)

        assertThat(result.categoryInformationModel, `is`(informationModel))
        assertThat(result.categoryInformationModel?.sections!![0], `is`(sectionModel))
        assertThat(result.categoryInformationModel?.sections!![0].steps!![0], `is`(stepModel))
        assertThat(result.categoryInformationModel?.sections!![1], `is`(sectionModel2))
        assertThat(result.categoryInformationModel?.sections!![1].steps!![0], `is`(stepModel2))
        assertThat(result.id, `is`(categoryModel.id))
        assertThat(result.title, `is`(categoryModel.title))
        assertThat(result.subtitle, `is`(categoryModel.subtitle))
        assertThat(result.parentId, `is`(categoryModel.parentId))
        assertThat(result.nextStep, `is`(categoryModel.nextStep))
    }

    @Test
    fun `should convert CategoryModel to Category`() {

        val stepModel1 = SectionStepModel(1, generateRandomString(5), 1, 1)
        val stepModel2 = SectionStepModel(2, generateRandomString(5), 1, 1)
        val stepModel3 = SectionStepModel(3, generateRandomString(5), 2, 2)
        val sectionModel1 = SectionModel(1, generateRandomString(5), 1)
        val sectionModel2 = SectionModel(2, generateRandomString(5), 2)
        val categoryInformationModel1 = CategoryInformationModel(1, generateRandomString(5), generateRandomString(5),1)
        val categoryInformationModel2 = CategoryInformationModel(2, generateRandomString(5), generateRandomString(5),2)
        val categoryModel1 = CategoryModel(
            1,
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            0,
            1
        )
        val categoryModel2 = CategoryModel(
            2,
            generateRandomString(5),
            generateRandomString(5),
            generateRandomString(5),
            1,
            2
        )

        val queryingSection = QueryingSection().apply {
            steps = listOf(stepModel1, stepModel2)
            section = sectionModel1
        }

        val queryingCategoryInformation =
            QueryingCategoryInformation().apply {
                categoryInformationModel = categoryInformationModel1
                this.queryingSection = listOf(queryingSection)
            }

        val queryingCategory = QueryingCategory().apply {
            categoryModel = categoryModel1
            this.queryingCategoryInformation = queryingCategoryInformation
        }


        val queryingSection2 = QueryingSection().apply {
            steps = listOf(stepModel3)
            section = sectionModel2
        }

        val queryingCategoryInformation2 =
            QueryingCategoryInformation().apply {
                categoryInformationModel = categoryInformationModel2
                this.queryingSection = listOf(queryingSection2)
            }

        val queryingCategory2 = QueryingCategory().apply {
            categoryModel = categoryModel2
            this.queryingCategoryInformation = queryingCategoryInformation2
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
                image =  categoryInformationModel1.image,
                sections = listOf(expectedSection)
            )

        val expectedCategoryInformation2 =
            CategoryInformation(
                id = categoryInformationModel2.id,
                description = categoryInformationModel2.description,
                image = categoryInformationModel2.image,
                sections = listOf(expectedSection2)
            )
        val expectedCategory = Category(
            categoryModel1.id,
            categoryModel1.title,
            categoryModel1.subtitle,
            categoryModel1.image,
            categoryModel1.parentId,
            categoryModel1.nextStep,
            expectedCategoryInformation
        )

        val expectedCategory2 = Category(
            categoryModel2.id,
            categoryModel2.title,
            categoryModel2.subtitle,
            categoryModel2.image,
            categoryModel2.parentId,
            categoryModel2.nextStep,
            expectedCategoryInformation2
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
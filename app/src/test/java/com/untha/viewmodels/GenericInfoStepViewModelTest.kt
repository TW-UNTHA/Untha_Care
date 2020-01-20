package com.untha.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.untha.model.transactionalmodels.Category
import com.untha.model.transactionalmodels.CategoryInformation
import com.untha.model.transactionalmodels.Section
import com.untha.model.transactionalmodels.Step
import com.utils.RandomGenerator
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.test.KoinTest

@RunWith(JUnit4::class)
class GenericInfoStepViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun shouldGiveMeZeroWhenNextStepsAreNull() {
        val genericInfoStepViewModelMock: GenericInfoStepViewModel =
            GenericInfoStepViewModel()
        val categories: ArrayList<Category> = arrayListOf()
        val category: Category = getCategoryMock(1, "DERECHOS", null)

        genericInfoStepViewModelMock.getNextSteps(category, categories)

        Assert.assertEquals(0, genericInfoStepViewModelMock.categoriesNextStep.size)
    }

    @Test
    fun shouldGiveMeOneElementWhenNextStepsIsDiferentOfNull() {
        val genericInfoStepViewModelMock: GenericInfoStepViewModel =
            GenericInfoStepViewModel()
        val categories: ArrayList<Category> = arrayListOf()
        var categoryOne: Category = getCategoryMock(1, "DERECHOS", 2)
        var categoryTwo: Category = getCategoryMock(2, "MATERNIDAD", 3)
        categories.add(categoryOne)
        categories.add(categoryTwo)
        val category: Category = getCategoryMock(1, "DERECHOS", 2)

        genericInfoStepViewModelMock.getNextSteps(category, categories)

        Assert.assertEquals(1, genericInfoStepViewModelMock.categoriesNextStep.size)

        Assert.assertEquals("MATERNIDAD", genericInfoStepViewModelMock.categoriesNextStep[0].title)
    }

    @Test
    fun shouldReturnTheNextStepCategoryObject() {
        val genericInfoStepViewModelMock: GenericInfoStepViewModel =
            GenericInfoStepViewModel()
        val categories: ArrayList<Category> = arrayListOf()
        var categoryOne: Category = getCategoryMock(1, "DERECHOS", 2)
        var categoryTwo: Category = getCategoryMock(2, "MATERNIDAD", 3)
        categories.add(categoryOne)
        categories.add(categoryTwo)
        val category: Category = getCategoryMock(1, "DERECHOS", 2)

        genericInfoStepViewModelMock.getNextSteps(category, categories)

        Assert.assertEquals("MATERNIDAD", genericInfoStepViewModelMock.categoriesNextStep[0].title)
    }

    private fun getCategoryMock(id: Int, title: String, nextStep: Int?): Category {
        var step = Step(1, RandomGenerator.generateRandomString(5))
        var step2 = Step(2, RandomGenerator.generateRandomString(5))
        var section = Section(
            1,
            title = RandomGenerator.generateRandomString(5),
            steps = listOf(step)
        )
        var section2 = Section(
            2,
            title = RandomGenerator.generateRandomString(5),
            steps = listOf(step2)
        )
        var categoryInformation =
            CategoryInformation(
                1,
                description = RandomGenerator.generateRandomString(5),
                image = RandomGenerator.generateRandomString(size = 5),
                screenTitle = RandomGenerator.generateRandomString(size = 5),
                nextStep = nextStep,
                sections = listOf(section, section2)
            )

        var category = Category(
            id,
            title,
            RandomGenerator.generateRandomString(5),
            RandomGenerator.generateRandomString(5),
            RandomGenerator.generateRandomString(5),
            false,
            RandomGenerator.generateIntBetween0AndTwenty(),
            "route",
            listOf(categoryInformation)
        )
        return category
    }
}

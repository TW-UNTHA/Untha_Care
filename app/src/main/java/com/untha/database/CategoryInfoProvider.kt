package com.untha.database

import com.untha.model.models.Category

class CategoryInfoProvider {

    companion object {
        var categoryList = initCategoryList()

        /**
         * Initialises peopleList with dummy data
         */
        private fun initCategoryList(): MutableList<Category> {
            var categories = mutableListOf<Category>()
            categories.add(Category(
                "TitleButton1",
                "DescButton1",
                "subtitleButton1"
                ).apply { id = 1 })
            categories.add(Category(
                "TitleButton2",
                "DescButton2",
                "subTitleButton2"
            ).apply { id = 2 })

            return categories
        }
    }
}

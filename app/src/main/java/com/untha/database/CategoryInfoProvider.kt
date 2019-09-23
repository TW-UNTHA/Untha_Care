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
                "SALARIO Y BENEFICIOS",
                "DescButton1",
                "subtitleButton1",
                "salarios",
                "derechos"
                ).apply { id = 1 })
            categories.add(Category(
                "JORNADA DE TRABAJO Y VACACIONES",
                "DescButton2",
                "subTitleButton2",
                "salarios",
                "derechos"
            ).apply { id = 2 })
            categories.add(Category(
                "EMBARAZO",
                "DescButton2",
                "subTitleButton2",
                "salarios",
                "derechos"
            ).apply { id = 3 })
            categories.add(Category(
                "NIÑAS Y ADOLESCENTES",
                "DescButton2",
                "subTitleButton2",
                "salarios",
                "derechos"
            ).apply { id = 4 })
            categories.add(Category(
                "DESPIDOS Y TÉRMINO DE CONTRATOS",
                "DescButton2",
                "subTitleButton2",
                "salarios",
                "derechos"
            ).apply { id = 5 })
            categories.add(Category(
                "LEYES",
                "DescButton2",
                "subTitleButton2",
                "salarios",
                "derechos"
            ).apply { id = 6 })

            return categories
        }
    }
}

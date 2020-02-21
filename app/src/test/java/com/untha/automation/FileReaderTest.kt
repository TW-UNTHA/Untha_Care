package com.untha.automation

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class FileReaderTest(
    private val excelModel: ExcelModel
) {
    companion object {
        var manageFile = FileReader()
        val excelData = manageFile.excelModels
        @JvmStatic
        @Parameterized.Parameters
        fun data() = excelData
    }

    @Test
    fun `should return true if is the same`() {
        println(excelModel.weekHours)
        assertThat(excelModel.weekHours, equalTo(excelModel.weekHours))
    }
}




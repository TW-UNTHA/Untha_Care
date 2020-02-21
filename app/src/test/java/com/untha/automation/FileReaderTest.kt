package com.untha.automation

import org.junit.Assert.assertEquals
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
    fun `should return true if is equals`() {
        println(excelModel.weekHours)
        assertEquals(excelModel.weekHours, equals(25.0))
    }
}




package com.untha.automation

import jxl.Sheet
import jxl.Workbook
import jxl.read.biff.BiffException
import kotlinx.io.IOException
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

class FileReader {

    var excelModels: ArrayList<ExcelModel> = ArrayList()

    companion object {
        const val AREA_COSTA_GALAPAGOS = "Costa"
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
        const val ITEM = 0
        const val AREA = 4
        const val WEEK_HOURS = 1
        const val SALARY_FINAL = 9
        const val START_DATE = 5
        const val PERCENTAGE_IESS = 12
        const val FONDOS_RESERVA = 13
        const val DECIMO_TERCERO = 8
        const val DECIMO_TERCERO_MONTLY = 11
        const val DECIMO_CUARTO = 7
        const val DECIMO_CUARTO_MONTLY = 10
        const val END_DATE = 6
        const val PARCIAL = 2
        const val COMPLETA = 1
        const val HOURS_COMPLETA = 40
        const val MAX_ROWS = 999
    }

    init {
        populationWithExcelData()
    }

    fun populationWithExcelData(): Boolean {
        var workbook: Workbook? = null
        try {
            workbook =
                Workbook.getWorkbook(File("QA_formulas_decimos.xls"))
            val sheet = workbook.getSheet("CasosDePruebaCsv")
            for (counterRows in 1..MAX_ROWS) {
                if (isNullOrEmpty(sheet, counterRows)
                ) {
                    return true
                }
                val excelModel = ExcelModel(
                    sheet.getCell(ITEM, counterRows).contents.toDouble(),
                    sheet.getCell(WEEK_HOURS, counterRows).contents.toDouble(),
                    getValueFormatBigDecimal(sheet, counterRows, SALARY_FINAL),
                    getIdARea(sheet.getCell(AREA, counterRows).contents),
                    getIdWorkday(sheet.getCell(WEEK_HOURS, counterRows).contents.toInt()),
                    sheet.getCell(START_DATE, counterRows).contents,
                    sheet.getCell(END_DATE, counterRows).contents,
                    getValueFormatBigDecimal(sheet, counterRows, PERCENTAGE_IESS),
                    getValueFormatBigDecimal(sheet, counterRows, FONDOS_RESERVA),
                    getValueFormatBigDecimal(sheet, counterRows, DECIMO_TERCERO),
                    getValueFormatBigDecimal(sheet, counterRows, DECIMO_TERCERO_MONTLY),
                    getValueFormatBigDecimal(sheet, counterRows, DECIMO_CUARTO),
                    getValueFormatBigDecimal(sheet, counterRows, DECIMO_CUARTO_MONTLY)
                )
                excelModels.add(excelModel)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: BiffException) {
            e.printStackTrace()
        } finally {
            workbook?.close()
        }
        return true
    }

    private fun getValueFormatBigDecimal(
        sheet: Sheet,
        counterRows: Int,
        column: Int
    ): BigDecimal {
        return sheet.getCell(column, counterRows).contents.toBigDecimal().setScale(
            2, RoundingMode.HALF_UP
        )
    }

    private fun isNullOrEmpty(sheet: Sheet, counterRows: Int): Boolean {
        return sheet.getCell(ITEM, counterRows).contents == null || sheet.getCell(
            ITEM,
            counterRows
        ).contents.contentEquals("")
    }


    private fun getIdARea(area: String) =
        if (area == AREA_COSTA_GALAPAGOS)
            COSTA_GALAPAGOS else SIERRA_ORIENTE

    private fun getIdWorkday(numberOfHours: Int) =
        if (numberOfHours < HOURS_COMPLETA)
            PARCIAL else COMPLETA
}

package com.untha.automation

import jxl.Sheet
import jxl.Workbook
import jxl.read.biff.BiffException
import kotlinx.io.IOException
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

class FileReaderFiniquito {

    var excelModels: ArrayList<ExcelModelFiniquito> = ArrayList()

    companion object {
        const val AREA_COSTA_GALAPAGOS = "Costa"
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
        const val ITEM = 0
        const val TYPE_INDEMNIZACION = 1
        const val TYPE_CAUSAL = 2
        const val HORAS_SEMANA = 3
        const val SALARIO = 4
        const val REGIMEN = 5
        const val DECIMO_CUARTO_OPTION = 6
        const val DECIMO_TERCERO_OPTION = 7
        const val FONDOS_RESERVA_OPTION = 8
        const val START_DATE = 9
        const val END_DATE = 10
        const val BIRTH_DATE = 11
        const val DISCOUNTS = 12
        const val DAYS_TAKEN = 13
        const val VACATION_PAY = 14
        const val FINAL_SALARY = 15
        const val DECIMO_CUARTO = 16
        const val DECIMO_TERCERO = 17
        const val FONDOS_RESERVA = 18
        const val COMPENSATION = 19
        const val CAUSAL = 20
        const val SUBTOTAL = 21
        const val TOTAL = 22
        const val MAX_ROWS = 45


    }

    init {
        populationWithExcelData()
    }

    fun populationWithExcelData(): Boolean {
        var workbook: Workbook? = null
        try {
            workbook =
                Workbook.getWorkbook(File("QA_formulas_decimos.xls"))
            val sheet = workbook.getSheet("QAFiniquito")
            for (counterRows in 1..MAX_ROWS) {
                if (isNullOrEmpty(sheet, counterRows)
                ) {
                    return true
                }
                val excelModelFiniquito = ExcelModelFiniquito(
                    sheet.getCell(ITEM, counterRows).contents.toInt(),
                    sheet.getCell(TYPE_INDEMNIZACION, counterRows).contents,
                    sheet.getCell(TYPE_CAUSAL, counterRows).contents,
                    sheet.getCell(HORAS_SEMANA, counterRows).contents.toInt(),
                    getValueFormatBigDecimal(sheet, counterRows, SALARIO),
                    getIdARea(sheet.getCell(REGIMEN, counterRows).contents),
                    sheet.getCell(DECIMO_TERCERO_OPTION, counterRows).contents.toInt(),
                    sheet.getCell(DECIMO_CUARTO_OPTION, counterRows).contents.toInt(),
                    sheet.getCell(FONDOS_RESERVA_OPTION, counterRows).contents.toInt(),
                    sheet.getCell(START_DATE, counterRows).contents,
                    sheet.getCell(END_DATE, counterRows).contents,
                    sheet.getCell(BIRTH_DATE, counterRows).contents,
                    getValueFormatBigDecimal(sheet, counterRows, DISCOUNTS),
                    sheet.getCell(DAYS_TAKEN, counterRows).contents.toInt(),
                    getValueFormatBigDecimal(sheet, counterRows, VACATION_PAY),
                    getValueFormatBigDecimal(sheet, counterRows, FINAL_SALARY),
                    getValueFormatBigDecimal(sheet, counterRows, DECIMO_CUARTO),
                    getValueFormatBigDecimal(sheet, counterRows, DECIMO_TERCERO),
                    getValueFormatBigDecimal(sheet, counterRows, FONDOS_RESERVA),
                    getValueFormatBigDecimal(sheet, counterRows, COMPENSATION),
                    getValueFormatBigDecimal(sheet, counterRows, CAUSAL),
                    getValueFormatBigDecimal(sheet, counterRows, SUBTOTAL),
                    getValueFormatBigDecimal(sheet, counterRows, TOTAL)

                )
                excelModels.add(excelModelFiniquito)
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


}

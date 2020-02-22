package com.untha.automation

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
        const val TWO_DIGITS_MONTH = 10
    }

    init {
        populationWithExcelData()
    }

    fun populationWithExcelData(): Boolean {
        val excelFile =
            FileInputStream(File("QA_formulas_decimos.xlsx"))
        val workbook = XSSFWorkbook(excelFile)

        val sheet = workbook.getSheet("CasosDePruebaCsv")
        val rows = sheet.iterator()

        while (rows.hasNext()) {
            val currentRow = rows.next()
            if (currentRow.getCell(ITEM) == null) {
                return true
            }

            if (!(currentRow.getCell(ITEM).toString() == "#")) {
                val idArea =
                    getIdARea(currentRow.getCell(AREA).stringCellValue)

                val idWorkday =
                    getIdWorkday(currentRow.getCell(WEEK_HOURS).numericCellValue.toInt())

                val excelModel = ExcelModel(
                    currentRow.getCell(ITEM).numericCellValue,
                    currentRow.getCell(WEEK_HOURS).numericCellValue,
                    currentRow.getCell(SALARY_FINAL).numericCellValue.toBigDecimal().setScale(
                        2,
                        RoundingMode.HALF_UP
                    ),
                    idArea,
                    idWorkday,
                    changeFormatDate(currentRow.getCell(START_DATE).toString()),
                    changeFormatDate(currentRow.getCell(END_DATE).toString()),
                    currentRow.getCell(PERCENTAGE_IESS).numericCellValue.toBigDecimal().setScale(
                        2,
                        RoundingMode.HALF_UP
                    ),
                    currentRow.getCell(FONDOS_RESERVA).numericCellValue.toBigDecimal().setScale(
                        2,
                        RoundingMode.HALF_UP
                    ),
                    currentRow.getCell(DECIMO_TERCERO).numericCellValue.toBigDecimal().setScale(
                        2,
                        RoundingMode.HALF_UP
                    ),
                    currentRow.getCell(DECIMO_TERCERO_MONTLY).numericCellValue.toBigDecimal().setScale(
                        2,
                        RoundingMode.HALF_UP
                    ),
                    currentRow.getCell(DECIMO_CUARTO).numericCellValue.toBigDecimal().setScale(
                        2,
                        RoundingMode.HALF_UP
                    ),
                    currentRow.getCell(DECIMO_CUARTO_MONTLY).numericCellValue.toBigDecimal().setScale(
                        2,
                        RoundingMode.HALF_UP
                    )
                )
                excelModels.add(excelModel)
            }
        }
        excelFile.close()
        return true
    }

    private fun getIdARea(area: String) =
        if (area == AREA_COSTA_GALAPAGOS)
            COSTA_GALAPAGOS else SIERRA_ORIENTE

    private fun getIdWorkday(numberOfHours: Int) =
        if (numberOfHours < HOURS_COMPLETA)
            PARCIAL else COMPLETA

    fun changeFormatDate(date: String): String {
        val date = SimpleDateFormat("dd-MMMM-yyy").parse(date)
        val cal = Calendar.getInstance()
        cal.setTime(date)
        val calendarMonth = cal.get(Calendar.MONTH) + 1
        var month = ""
        if (cal.get(Calendar.MONTH) < TWO_DIGITS_MONTH) {
            month = "0".plus(calendarMonth)
        } else {
            month = calendarMonth.toString()
        }
        var dayOfMonth = ""
        if (cal.get(Calendar.DAY_OF_MONTH) < TWO_DIGITS_MONTH) {
            dayOfMonth = "0".plus(cal.get(Calendar.DAY_OF_MONTH))
        } else {
            dayOfMonth = cal.get(Calendar.DAY_OF_MONTH).toString()
        }
        return (cal.get(Calendar.YEAR).toString().plus("-").plus(month).plus("-").plus(dayOfMonth))


    }
}

package com.untha.automation

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

class FileReader {

    var excelModels: ArrayList<ExcelModel> = ArrayList()

    companion object {
        const val AREA_COSTA_GALAPAGOS = "Costa"
        const val SIERRA_ORIENTE = 2
        const val COSTA_GALAPAGOS = 1
        const val ITEM = 0
        const val AREA = 4
        const val WEEK_HOURS = 1
        const val SALARY = 9
        const val START_DATE = 5
        const val PERCENTAGE_IESS = 12
        const val FONDOS_RESERVA = 13
        const val DECIMO_TERCERO = 8
        const val DECIMO_TERCERO_MONTLY = 11
        const val DECIMO_CUARTO = 7
        const val DECIMO_CUARTO_MONTLY = 10
        const val END_DATE = 6
    }

    init {
        populationWithExcelData()
    }

    fun populationWithExcelData(): Boolean {
        val excelFile =
            FileInputStream(File("/Users/lapcajilema/Documents/UNTHA/QA_formulasDecimos.xlsx"))
        val workbook = XSSFWorkbook(excelFile)

        val sheet = workbook.getSheet("CasosDePruebaCsv")
        val rows = sheet.iterator()

        while (rows.hasNext()) {
            val currentRow = rows.next()
            if (currentRow.getCell(ITEM) == null) {
                return true
            }
            val isRowTitle = currentRow.getCell(ITEM).toString() == "#"
            if (!isRowTitle) {
                val idArea =
                    if (currentRow.getCell(AREA).stringCellValue == AREA_COSTA_GALAPAGOS)
                        COSTA_GALAPAGOS else SIERRA_ORIENTE
                val excelModel = ExcelModel(
                    currentRow.getCell(ITEM).numericCellValue,
                    currentRow.getCell(WEEK_HOURS).numericCellValue,
                    currentRow.getCell(SALARY).numericCellValue.toBigDecimal(),
                    idArea,
                    currentRow.getCell(START_DATE).stringCellValue,
                    currentRow.getCell(END_DATE).stringCellValue,
                    currentRow.getCell(PERCENTAGE_IESS).numericCellValue.toBigDecimal(),
                    currentRow.getCell(FONDOS_RESERVA).numericCellValue.toBigDecimal(),
                    currentRow.getCell(DECIMO_TERCERO).numericCellValue.toBigDecimal(),
                    currentRow.getCell(DECIMO_TERCERO_MONTLY).numericCellValue.toBigDecimal(),
                    currentRow.getCell(DECIMO_CUARTO).numericCellValue.toBigDecimal(),
                    currentRow.getCell(DECIMO_CUARTO_MONTLY).numericCellValue.toBigDecimal()
                )
                excelModels.add(excelModel)
            }
        }
        excelFile.close()
        return true
    }
}

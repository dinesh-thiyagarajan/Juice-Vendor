package file

import java.io.File
import java.io.IOException

class FilesRepository(private val filesDirectory: File) {

    fun createCSVFile(fileContent: String, startDate: String, endDate: String): File? {
        val csvFileName =
            if (startDate != endDate) "juice orders $startDate - $endDate.csv" else "juice orders $startDate"
        val csvFile = File(filesDirectory, csvFileName)
        try {
            csvFile.writeText(fileContent)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return csvFile
    }

}
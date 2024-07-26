package file

import java.io.File
import java.io.IOException

class FilesRepository(private val filesDirectory: File) {

    fun createCSVFile(fileContent: String): File? {
        val csvFileName = "data.csv"
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
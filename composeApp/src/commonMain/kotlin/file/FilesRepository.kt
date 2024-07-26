package file

import android.net.Uri
import java.io.File
import java.io.IOException

class FilesRepository(private val filesDirectory: File) {

    fun createCSVFile(fileContent: String): Pair<String, Uri> {
        val csvFileName = "data.csv"
        val csvFile = File(filesDirectory, csvFileName)

        try {
            csvFile.writeText(fileContent)
        } catch (e: IOException) {
            e.printStackTrace()
            return Pair("", Uri.parse(""))
        }
        val uri = Uri.parse(csvFile.absolutePath.toString())
        return Pair(csvFile.absolutePath, uri)
    }

}
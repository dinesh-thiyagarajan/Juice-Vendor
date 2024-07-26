package file

import android.net.Uri
import java.io.File

interface FileSharingService {
    fun shareFile(file: File)
}

object Platform {
    lateinit var fileSharingService: FileSharingService

    fun init(fileSharingService: FileSharingService) {
        Platform.fileSharingService = fileSharingService
    }
}
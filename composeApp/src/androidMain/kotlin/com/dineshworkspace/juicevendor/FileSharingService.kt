package com.dineshworkspace.juicevendor

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import file.FileSharingService
import java.io.File

class FileSharingService(private val context: Context) : FileSharingService {
    override fun shareFile(file: File) {
        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/csv"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, fileUri)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share CSV"))
    }
}
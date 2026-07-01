package com.test.design.core.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object DesignExportHelper {

    fun shareJson(
        context: Context,
        fileName: String,
        json: String,
        chooserTitle: String,
    ) {
        val exportDir = File(context.cacheDir, "design_exports").apply { mkdirs() }
        val file = File(exportDir, fileName)
        file.writeText(json)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, chooserTitle)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, chooserTitle))
    }

    fun buildDeepLink(demoId: String): String = "oemdesign://demo/$demoId"
}

package com.test.design.core.export

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.IOException

sealed interface ExportResult {
    data object Success : ExportResult
    data class Failure(val message: String) : ExportResult
}

object DesignExportHelper {

    fun shareJson(
        context: Context,
        fileName: String,
        json: String,
        chooserTitle: String,
    ): ExportResult {
        return runCatching {
            val exportDir = context.cacheDir.resolve("design_exports").apply { mkdirs() }
            val file = exportDir.resolve(fileName)
            try {
                file.writeText(json)
            } catch (error: IOException) {
                return ExportResult.Failure("Could not write export file.")
            }

            val uri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file,
                )
            } catch (error: IllegalArgumentException) {
                return ExportResult.Failure("Export provider is not configured.")
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, chooserTitle)
                clipData = ClipData.newRawUri(chooserTitle, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, chooserTitle)
            if (shareIntent.resolveActivity(context.packageManager) == null) {
                return ExportResult.Failure("No app available to receive the export.")
            }
            context.startActivity(chooser)
            ExportResult.Success
        }.getOrElse { error ->
            ExportResult.Failure(error.message ?: "Export failed.")
        }
    }

    fun buildDeepLink(demoId: String): String = "oemdesign://demo/$demoId"
}

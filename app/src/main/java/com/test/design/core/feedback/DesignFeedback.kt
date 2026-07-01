package com.test.design.core.feedback

import android.content.Context
import android.widget.Toast
import com.test.design.core.export.ExportResult

object DesignFeedback {

    fun showExportResult(context: Context, result: ExportResult) {
        when (result) {
            ExportResult.Success -> Unit
            is ExportResult.Failure -> Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
        }
    }
}

package com.test.design.core.navigation

import android.content.Intent
import android.net.Uri

object DeepLinkParser {

    private const val SCHEME = "oemdesign"
    private const val HOST = "demo"

    fun parseDemoId(intent: Intent?): String? = parseDemoId(intent?.data)

    fun parseDemoId(uri: Uri?): String? {
        if (uri == null) return null
        return parseDemoPath(uri.scheme, uri.host, uri.pathSegments)
    }

    internal fun parseDemoPath(
        scheme: String?,
        host: String?,
        pathSegments: List<String>?,
    ): String? {
        if (scheme != SCHEME || host != HOST) return null
        return pathSegments?.firstOrNull()?.takeIf { it.isNotBlank() }
    }

    fun buildDemoUri(demoId: String): Uri = Uri.parse("oemdesign://demo/$demoId")
}

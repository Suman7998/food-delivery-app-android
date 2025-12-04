package com.munchmatch.app.net

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object DataExchange {
    data class Result(val success: Boolean, val code: Int, val bodySnippet: String)

    // Simple GET to a fast, reliable endpoint. 204 means success without body.
    private const val PROBE_URL = "https://www.google.com/generate_204"

    fun perform(): Result {
        return try {
            val url = URL(PROBE_URL)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                instanceFollowRedirects = false
            }
            conn.connect()
            val code = conn.responseCode
            val stream = try {
                if (code in 200..299) conn.inputStream else conn.errorStream
            } catch (e: Exception) { null }
            val body = stream?.use { s ->
                BufferedReader(InputStreamReader(s)).readLine() ?: ""
            } ?: ""
            conn.disconnect()
            Result(success = code in 200..399, code = code, bodySnippet = body.take(120))
        } catch (e: Exception) {
            Result(success = false, code = -1, bodySnippet = e.message ?: "")
        }
    }
}

package com.rk.librunner

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Patterns
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.io.File

object Runner {

    fun isRunnable(file:File) : Boolean{
        return when(file.name.substringAfterLast('.', "")){
            "bsh" -> false
            "js" -> false
            "html" -> true
            "sh" -> false
            "bash" -> false
            else -> false
        }
    }

    fun run(file: File?, context: Context, webView: WebView) {
        if (file == null || !isRunnable(file)) {
            Toast.makeText(context, context.getString(R.string.not_an_html_file), Toast.LENGTH_SHORT).show()
            return
        }

        runHtml(file, context,webView)
    }
    private fun runHtml(file: File, context: Context, webView: WebView) {

        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("file://${file.absolutePath}")

        Toast.makeText(context,
            context.getString(R.string.previewing_html_files), Toast.LENGTH_SHORT).show()
    }

}
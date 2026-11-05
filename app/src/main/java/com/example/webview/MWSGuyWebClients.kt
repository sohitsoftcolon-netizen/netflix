package com.example.webview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.adblock.AdBlocker

class MWSGuyWebViewClient(
    private val context: Context,
    private val isAdBlockEnabled: () -> Boolean,
    private val onAdBlocked: () -> Unit,
    private val onLoadingChanged: (isLoading: Boolean, progress: Int) -> Unit,
    private val onNavStateChanged: (canGoBack: Boolean, canGoForward: Boolean, url: String?) -> Unit,
    private val onError: (message: String?) -> Unit
) : WebViewClient() {

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val url = request?.url?.toString() ?: return super.shouldInterceptRequest(view, request)
        if (isAdBlockEnabled() && AdBlocker.isAd(url)) {
            onAdBlocked()
            return AdBlocker.createBlockedResponse()
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val uri = request?.url ?: return false
        val url = uri.toString()
        val scheme = uri.scheme?.lowercase() ?: ""

        // If ad network redirect, prevent navigation
        if (isAdBlockEnabled() && AdBlocker.isAd(url)) {
            onAdBlocked()
            return true
        }

        // Standard web navigation
        if (scheme == "http" || scheme == "https") {
            return false // Let WebView load it
        }

        // Handle external protocols (tel, mailto, etc.) safely
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            // No app can handle scheme
        }
        return true
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onLoadingChanged(true, 10)
        view?.let {
            onNavStateChanged(it.canGoBack(), it.canGoForward(), url)
        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onLoadingChanged(false, 100)
        view?.let {
            onNavStateChanged(it.canGoBack(), it.canGoForward(), url)
            if (isAdBlockEnabled()) {
                it.evaluateJavascript(AdBlocker.getAdBlockingScript(), null)
            }
            // Ensure responsive viewport for all screen sizes (phone, tablet, TV)
            val viewportScript = """
                if (!document.querySelector('meta[name=viewport]')) {
                    var meta = document.createElement('meta');
                    meta.name = 'viewport';
                    meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=5.0';
                    document.head.appendChild(meta);
                }
            """.trimIndent()
            it.evaluateJavascript(viewportScript, null)
        }
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        // Only trigger error screen if the main web page itself failed to load
        if (request?.isForMainFrame == true) {
            val description = error?.description?.toString()
            onError(description)
        }
    }
}

class MWSGuyWebChromeClient(
    private val onProgressUpdate: (progress: Int) -> Unit,
    private val onTitleReceived: (title: String?) -> Unit,
    private val onShowFullscreenVideo: (view: View, callback: CustomViewCallback) -> Unit,
    private val onHideFullscreenVideo: () -> Unit
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        onProgressUpdate(newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        onTitleReceived(title)
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (view != null && callback != null) {
            onShowFullscreenVideo(view, callback)
        }
    }

    override fun onHideCustomView() {
        onHideFullscreenVideo()
    }
}

package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.webview.MWSGuyWebChromeClient
import com.example.webview.MWSGuyWebViewClient

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    targetUrl: String,
    isDesktopMode: Boolean,
    isAdBlockEnabled: Boolean,
    onWebViewCreated: (WebView) -> Unit,
    onAdBlocked: () -> Unit,
    onLoadingChanged: (isLoading: Boolean, progress: Int) -> Unit,
    onNavStateChanged: (canGoBack: Boolean, canGoForward: Boolean, url: String?) -> Unit,
    onError: (errorMessage: String?) -> Unit,
    onTitleReceived: (title: String?) -> Unit,
    onShowFullscreenVideo: (view: View, callback: WebChromeClient.CustomViewCallback) -> Unit,
    onHideFullscreenVideo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Hold reference to WebView
    val webView = remember {
        createConfiguredWebView(
            context = context,
            isAdBlockEnabled = { isAdBlockEnabled },
            onAdBlocked = onAdBlocked,
            onLoadingChanged = onLoadingChanged,
            onNavStateChanged = onNavStateChanged,
            onError = onError,
            onTitleReceived = onTitleReceived,
            onShowFullscreenVideo = onShowFullscreenVideo,
            onHideFullscreenVideo = onHideFullscreenVideo
        ).also {
            onWebViewCreated(it)
        }
    }

    // React to Desktop Mode toggle
    LaunchedEffect(isDesktopMode) {
        val settings = webView.settings
        if (isDesktopMode) {
            settings.userAgentString = DESKTOP_USER_AGENT
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
        } else {
            settings.userAgentString = null // Reset to default mobile user-agent
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
        }
        // Reload current page if already loaded
        if (webView.url != null) {
            webView.reload()
        }
    }

    // Clean up on dispose
    DisposableEffect(Unit) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    )
}

@SuppressLint("SetJavaScriptEnabled")
private fun createConfiguredWebView(
    context: Context,
    isAdBlockEnabled: () -> Boolean,
    onAdBlocked: () -> Unit,
    onLoadingChanged: (isLoading: Boolean, progress: Int) -> Unit,
    onNavStateChanged: (canGoBack: Boolean, canGoForward: Boolean, url: String?) -> Unit,
    onError: (errorMessage: String?) -> Unit,
    onTitleReceived: (title: String?) -> Unit,
    onShowFullscreenVideo: (view: View, callback: WebChromeClient.CustomViewCallback) -> Unit,
    onHideFullscreenVideo: () -> Unit
): WebView {
    return WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // TV / Remote and Keyboard support
        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
        requestFocus()

        // Enable cookies
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

            // Zoom & Responsive options
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false

            // Disallow popup windows to protect user from ad tabs
            setSupportMultipleWindows(false)
            javaScriptCanOpenWindowsAutomatically = false

            // Security best practices
            allowFileAccess = false
            allowContentAccess = false

            // Performance & Caching
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        webViewClient = MWSGuyWebViewClient(
            context = context,
            isAdBlockEnabled = isAdBlockEnabled,
            onAdBlocked = onAdBlocked,
            onLoadingChanged = onLoadingChanged,
            onNavStateChanged = onNavStateChanged,
            onError = onError
        )

        webChromeClient = MWSGuyWebChromeClient(
            onProgressUpdate = { progress ->
                onLoadingChanged(progress < 100, progress)
            },
            onTitleReceived = onTitleReceived,
            onShowFullscreenVideo = onShowFullscreenVideo,
            onHideFullscreenVideo = onHideFullscreenVideo
        )

        // Initial load
        loadUrl("https://www.mwsguy.sbs/p/home.html")
    }
}

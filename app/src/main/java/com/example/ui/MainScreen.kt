package com.example.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BottomNavBar
import com.example.ui.components.ErrorView
import com.example.ui.components.TopControlsBar
import com.example.ui.components.TvOverlayControls
import com.example.ui.components.WebViewContainer
import com.example.util.DeviceFormFactor
import com.example.util.DeviceUtils

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formFactor = remember(context) { DeviceUtils.getDeviceFormFactor(context) }

    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var fullscreenCustomView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }

    // System Back Handler
    BackHandler {
        when {
            // If fullscreen video is showing, dismiss video first
            fullscreenCustomView != null -> {
                customViewCallback?.onCustomViewHidden()
                fullscreenCustomView = null
                customViewCallback = null
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
            // If webview can navigate back, go back in web history
            webViewRef?.canGoBack() == true -> {
                webViewRef?.goBack()
            }
            // If in fullscreen mode, exit fullscreen mode
            uiState.isFullscreen -> {
                viewModel.setFullscreen(false)
            }
            // If TV menu is showing, close menu
            uiState.isTvControlsVisible -> {
                viewModel.setTvControlsVisible(false)
            }
            // Otherwise finish activity
            else -> {
                activity?.finish()
            }
        }
    }

    // Fullscreen Video View (HTML5 video player)
    if (fullscreenCustomView != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = {
                    fullscreenCustomView?.apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    } ?: View(it)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        topBar = {
            // Show top bar for phones and tablets unless user entered Fullscreen mode
            // On TV, hide top bar by default for a 100% immersive 10-foot experience
            if (!uiState.isFullscreen && formFactor != DeviceFormFactor.TV) {
                TopControlsBar(
                    uiState = uiState,
                    onRefresh = { webViewRef?.reload() },
                    onToggleAdBlock = {
                        viewModel.toggleAdBlock()
                        webViewRef?.reload()
                    },
                    onToggleDesktopMode = { viewModel.toggleDesktopMode() },
                    onToggleFullscreen = { viewModel.toggleFullscreen() }
                )
            }
        },
        bottomBar = {
            // Show bottom navigation bar on phones (or small tablets) when not in fullscreen
            if (!uiState.isFullscreen && formFactor == DeviceFormFactor.PHONE) {
                BottomNavBar(
                    uiState = uiState,
                    onBack = { webViewRef?.goBack() },
                    onForward = { webViewRef?.goForward() },
                    onHome = { webViewRef?.loadUrl(uiState.homeUrl) },
                    onRefresh = {
                        if (uiState.isLoading) {
                            webViewRef?.stopLoading()
                        } else {
                            webViewRef?.reload()
                        }
                    },
                    onToggleDesktopMode = { viewModel.toggleDesktopMode() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Web View
            WebViewContainer(
                targetUrl = uiState.homeUrl,
                isDesktopMode = uiState.isDesktopMode,
                isAdBlockEnabled = uiState.isAdBlockEnabled,
                onWebViewCreated = { webView -> webViewRef = webView },
                onAdBlocked = { viewModel.incrementBlockedAds() },
                onLoadingChanged = { isLoading, progress ->
                    viewModel.updateLoading(isLoading, progress)
                },
                onNavStateChanged = { canBack, canForward, url ->
                    viewModel.updateNavigationState(canBack, canForward, url)
                },
                onError = { err -> viewModel.setConnectionError(err) },
                onTitleReceived = { title -> viewModel.updatePageTitle(title) },
                onShowFullscreenVideo = { view, callback ->
                    fullscreenCustomView = view
                    customViewCallback = callback
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                },
                onHideFullscreenVideo = {
                    fullscreenCustomView = null
                    customViewCallback = null
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("webview_container")
            )

            // Connection Error Overlay
            AnimatedVisibility(
                visible = uiState.hasError,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ErrorView(
                    errorMessage = uiState.errorMessage,
                    onRetry = {
                        viewModel.clearError()
                        webViewRef?.reload()
                    },
                    onGoHome = {
                        viewModel.clearError()
                        webViewRef?.loadUrl(uiState.homeUrl)
                    }
                )
            }

            // TV Overlay Controls (D-pad focusable navigation)
            if (formFactor == DeviceFormFactor.TV) {
                TvOverlayControls(
                    uiState = uiState,
                    isVisible = uiState.isTvControlsVisible,
                    onToggleVisibility = { viewModel.toggleTvControls() },
                    onBack = { webViewRef?.goBack() },
                    onForward = { webViewRef?.goForward() },
                    onHome = { webViewRef?.loadUrl(uiState.homeUrl) },
                    onRefresh = { webViewRef?.reload() },
                    onToggleDesktopMode = { viewModel.toggleDesktopMode() },
                    onToggleAdBlock = {
                        viewModel.toggleAdBlock()
                        webViewRef?.reload()
                    },
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }

            // Floating exit-fullscreen button for Phone / Tablet when Fullscreen mode is engaged
            if (uiState.isFullscreen && formFactor != DeviceFormFactor.TV) {
                FloatingActionButton(
                    onClick = { viewModel.setFullscreen(false) },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(48.dp)
                        .testTag("btn_exit_fullscreen_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.FullscreenExit,
                        contentDescription = "Exit Fullscreen",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

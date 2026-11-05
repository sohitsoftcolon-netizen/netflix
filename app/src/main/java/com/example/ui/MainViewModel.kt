package com.example.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WebUiState(
    val homeUrl: String = "https://www.mwsguy.sbs/p/home.html",
    val currentUrl: String = "https://www.mwsguy.sbs/p/home.html",
    val pageTitle: String = "MWSGuy",
    val isLoading: Boolean = false,
    val loadingProgress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isAdBlockEnabled: Boolean = true,
    val blockedAdsCount: Int = 0,
    val isDesktopMode: Boolean = false,
    val isFullscreen: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val isTvControlsVisible: Boolean = false
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WebUiState())
    val uiState: StateFlow<WebUiState> = _uiState.asStateFlow()

    fun updateLoading(isLoading: Boolean, progress: Int = 0) {
        _uiState.update {
            it.copy(
                isLoading = isLoading,
                loadingProgress = progress,
                // Clear error on fresh start of loading
                hasError = if (isLoading && progress == 0) false else it.hasError,
                errorMessage = if (isLoading && progress == 0) null else it.errorMessage
            )
        }
    }

    fun updateNavigationState(canGoBack: Boolean, canGoForward: Boolean, currentUrl: String?) {
        _uiState.update {
            it.copy(
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                currentUrl = currentUrl ?: it.currentUrl
            )
        }
    }

    fun updatePageTitle(title: String?) {
        if (!title.isNullOrBlank()) {
            _uiState.update { it.copy(pageTitle = title) }
        }
    }

    fun incrementBlockedAds() {
        _uiState.update { it.copy(blockedAdsCount = it.blockedAdsCount + 1) }
    }

    fun toggleAdBlock() {
        _uiState.update { it.copy(isAdBlockEnabled = !it.isAdBlockEnabled) }
    }

    fun toggleDesktopMode() {
        _uiState.update { it.copy(isDesktopMode = !it.isDesktopMode) }
    }

    fun toggleFullscreen() {
        _uiState.update { it.copy(isFullscreen = !it.isFullscreen) }
    }

    fun setFullscreen(enabled: Boolean) {
        _uiState.update { it.copy(isFullscreen = enabled) }
    }

    fun toggleTvControls() {
        _uiState.update { it.copy(isTvControlsVisible = !it.isTvControlsVisible) }
    }

    fun setTvControlsVisible(visible: Boolean) {
        _uiState.update { it.copy(isTvControlsVisible = visible) }
    }

    fun setConnectionError(message: String?) {
        _uiState.update {
            it.copy(
                hasError = true,
                errorMessage = message ?: "Unable to load page. Check your internet connection.",
                isLoading = false
            )
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(hasError = false, errorMessage = null)
        }
    }
}

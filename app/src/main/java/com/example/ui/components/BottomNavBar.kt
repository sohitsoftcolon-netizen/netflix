package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.WebUiState

@Composable
fun BottomNavBar(
    uiState: WebUiState,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onRefresh: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBack,
                enabled = uiState.canGoBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_nav_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.nav_back),
                    tint = if (uiState.canGoBack) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )
            }

            // Forward Button
            IconButton(
                onClick = onForward,
                enabled = uiState.canGoForward,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_nav_forward")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.nav_forward),
                    tint = if (uiState.canGoForward) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )
            }

            // Home Button
            IconButton(
                onClick = onHome,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_nav_home")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(R.string.nav_home),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Refresh / Stop Button
            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_nav_refresh")
            ) {
                Icon(
                    imageVector = if (uiState.isLoading) Icons.Default.Close else Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.nav_refresh),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Desktop Toggle Button
            IconButton(
                onClick = onToggleDesktopMode,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_nav_desktop")
            ) {
                Icon(
                    imageVector = if (uiState.isDesktopMode) Icons.Default.Smartphone else Icons.Default.DesktopWindows,
                    contentDescription = if (uiState.isDesktopMode) {
                        stringResource(R.string.nav_mobile_mode)
                    } else {
                        stringResource(R.string.nav_desktop_mode)
                    },
                    tint = if (uiState.isDesktopMode) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

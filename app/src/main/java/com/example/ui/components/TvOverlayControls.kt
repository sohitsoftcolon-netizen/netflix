package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.WebUiState
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldShield

@Composable
fun TvOverlayControls(
    uiState: WebUiState,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onRefresh: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onToggleAdBlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Expanded TV Menu Card
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 12.dp,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CyanAccent.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .testTag("tv_controls_menu")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TV Navigation Menu",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TvMenuButton(
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                label = stringResource(R.string.nav_back),
                                isEnabled = uiState.canGoBack,
                                onClick = onBack,
                                testTag = "tv_btn_back"
                            )

                            TvMenuButton(
                                icon = Icons.AutoMirrored.Filled.ArrowForward,
                                label = stringResource(R.string.nav_forward),
                                isEnabled = uiState.canGoForward,
                                onClick = onForward,
                                testTag = "tv_btn_forward"
                            )

                            TvMenuButton(
                                icon = Icons.Default.Home,
                                label = stringResource(R.string.nav_home),
                                isEnabled = true,
                                onClick = onHome,
                                testTag = "tv_btn_home"
                            )

                            TvMenuButton(
                                icon = Icons.Default.Refresh,
                                label = stringResource(R.string.nav_refresh),
                                isEnabled = true,
                                onClick = onRefresh,
                                testTag = "tv_btn_refresh"
                            )

                            TvMenuButton(
                                icon = if (uiState.isDesktopMode) Icons.Default.Smartphone else Icons.Default.DesktopWindows,
                                label = if (uiState.isDesktopMode) "Mobile" else "Desktop",
                                isEnabled = true,
                                onClick = onToggleDesktopMode,
                                testTag = "tv_btn_desktop"
                            )

                            TvMenuButton(
                                icon = Icons.Default.Shield,
                                label = if (uiState.isAdBlockEnabled) "AdBlock ON" else "AdBlock OFF",
                                isEnabled = true,
                                accentColor = if (uiState.isAdBlockEnabled) EmeraldShield else Color.Gray,
                                onClick = onToggleAdBlock,
                                testTag = "tv_btn_adblock"
                            )
                        }
                    }
                }
            }

            // Floating Toggle Button (Always visible on TV corner, D-pad focusable)
            var isToggleFocused by remember { mutableStateOf(false) }
            Button(
                onClick = onToggleVisibility,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isToggleFocused) CyanAccent else DarkSurfaceElevated.copy(alpha = 0.85f),
                    contentColor = if (isToggleFocused) Color.Black else Color.White
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                modifier = Modifier
                    .size(52.dp)
                    .onFocusChanged { isToggleFocused = it.isFocused }
                    .focusable()
                    .border(
                        width = if (isToggleFocused) 2.5.dp else 1.dp,
                        color = if (isToggleFocused) Color.White else CyanAccent.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .testTag("btn_tv_controls_toggle")
            ) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = stringResource(R.string.tv_controls_label),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun TvMenuButton(
    icon: ImageVector,
    label: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    testTag: String,
    accentColor: Color? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val baseColor = accentColor ?: if (isFocused) CyanAccent else MaterialTheme.colorScheme.onSurface

    Surface(
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(12.dp),
        color = if (isFocused) CyanAccent.copy(alpha = 0.25f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isFocused) 2.dp else 1.dp,
            color = if (isFocused) CyanAccent else Color.White.copy(alpha = 0.15f)
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(enabled = isEnabled)
            .testTag(testTag)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isEnabled) baseColor else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isEnabled) baseColor else Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

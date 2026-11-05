package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.WebUiState
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.EmeraldShieldBg

@Composable
fun TopControlsBar(
    uiState: WebUiState,
    onRefresh: () -> Unit,
    onToggleAdBlock: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onToggleFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title and SSL pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure HTTPS",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = uiState.pageTitle.ifBlank { "MWSGuy" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "mwsguy.sbs",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Action controls
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ad Shield Pill
                    AdShieldChip(
                        isAdBlockEnabled = uiState.isAdBlockEnabled,
                        blockedCount = uiState.blockedAdsCount,
                        onClick = onToggleAdBlock
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Desktop / Mobile View toggle
                    IconButton(
                        onClick = onToggleDesktopMode,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_toggle_desktop_mode")
                    ) {
                        Icon(
                            imageVector = if (uiState.isDesktopMode) {
                                Icons.Default.Smartphone
                            } else {
                                Icons.Default.DesktopWindows
                            },
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

                    // Fullscreen toggle
                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_toggle_fullscreen")
                    ) {
                        Icon(
                            imageVector = if (uiState.isFullscreen) {
                                Icons.Default.FullscreenExit
                            } else {
                                Icons.Default.Fullscreen
                            },
                            contentDescription = if (uiState.isFullscreen) {
                                stringResource(R.string.nav_exit_fullscreen)
                            } else {
                                stringResource(R.string.nav_fullscreen)
                            },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Refresh
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_top_refresh")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.nav_refresh),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Progress bar
            AnimatedVisibility(
                visible = uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LinearProgressIndicator(
                    progress = { (uiState.loadingProgress / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
fun AdShieldChip(
    isAdBlockEnabled: Boolean,
    blockedCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isAdBlockEnabled) EmeraldShieldBg else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isAdBlockEnabled) EmeraldShield else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("chip_ad_shield")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgedBox(
                badge = {
                    if (isAdBlockEnabled && blockedCount > 0) {
                        Badge(
                            containerColor = EmeraldShield,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = if (blockedCount > 99) "99+" else blockedCount.toString(),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = stringResource(R.string.ad_shield_active),
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = if (isAdBlockEnabled) "No Ads" else "Ads Allowed",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

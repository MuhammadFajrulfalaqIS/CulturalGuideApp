package com.example.cultural_navigation_papb.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cultural_navigation_papb.data.audio.AudioPlayerState

/**
 * Floating Audio Player Controls dengan Material Design 3
 */
@Composable
fun AudioPlayerControls(
    state: AudioPlayerState,
    progress: Float,
    currentSpeed: Float,
    placeName: String,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onReplay: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onLanguageChange: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf("id") }

    // Animasi untuk expand/collapse
    val heightAnimation by animateDpAsState(
        targetValue = if (isExpanded) 160.dp else 80.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(heightAnimation),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4A3428) // Dark brown
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header dengan title dan control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Audio Guide",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = placeName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                }

                // Expand/Collapse button
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color.White
                    )
                }

                // Close button
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress bar
                    Column {
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFFFF6F00), // Orange accent
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Progress text
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Control buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Speed control
                        Box {
                            OutlinedButton(
                                onClick = { showSpeedMenu = true },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.size(56.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "${currentSpeed}x",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            DropdownMenu(
                                expanded = showSpeedMenu,
                                onDismissRequest = { showSpeedMenu = false }
                            ) {
                                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                    DropdownMenuItem(
                                        text = { Text("${speed}x") },
                                        onClick = {
                                            onSpeedChange(speed)
                                            showSpeedMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Replay button
                        IconButton(
                            onClick = onReplay,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Replay",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Play/Pause button (primary action)
                        FloatingActionButton(
                            onClick = {
                                when (state) {
                                    is AudioPlayerState.Playing -> onPause()
                                    is AudioPlayerState.Paused -> onPlay()
                                    else -> onPlay()
                                }
                            },
                            containerColor = Color(0xFFFF6F00), // Orange accent
                            contentColor = Color.White,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = when (state) {
                                    is AudioPlayerState.Playing -> Icons.Default.Pause
                                    is AudioPlayerState.Loading -> Icons.Default.HourglassEmpty
                                    else -> Icons.Default.PlayArrow
                                },
                                contentDescription = when (state) {
                                    is AudioPlayerState.Playing -> "Pause"
                                    else -> "Play"
                                },
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Stop button
                        IconButton(
                            onClick = onStop,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Language toggle
                        Box {
                            IconButton(
                                onClick = { showLanguageMenu = true },
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Change Language",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showLanguageMenu,
                                onDismissRequest = { showLanguageMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("🇮🇩 Indonesia") },
                                    onClick = {
                                        currentLanguage = "id"
                                        onLanguageChange("id")
                                        showLanguageMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🇺🇸 English") },
                                    onClick = {
                                        currentLanguage = "en"
                                        onLanguageChange("en")
                                        showLanguageMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Status indicator
    if (state is AudioPlayerState.Error) {
        Text(
            text = state.message,
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

/**
 * Floating Audio Guide Button untuk trigger audio guide
 */
@Composable
fun AudioGuideButton(
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFFFF6F00), // Orange accent
        contentColor = Color.White,
        modifier = modifier.size(64.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Audio Guide",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Status message untuk audio guide
 */
@Composable
fun AudioGuideStatus(
    message: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFD32F2F) else Color(0xFF4A3428)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isError) Icons.Default.Error else Icons.Default.Info,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}


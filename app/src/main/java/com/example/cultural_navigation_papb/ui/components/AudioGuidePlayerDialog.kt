package com.example.cultural_navigation_papb.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.cultural_navigation_papb.data.audio.AudioPlayerState
import com.example.cultural_navigation_papb.data.models.Narration

/**
 * Dialog untuk Audio Guide Player dengan full controls
 */
@Composable
fun AudioGuidePlayerDialog(
    onDismiss: () -> Unit,
    audioPlayerState: AudioPlayerState,
    audioProgress: Float,
    audioSpeed: Float,
    isGeneratingNarration: Boolean,
    currentNarration: Narration?,
    audioError: String?,
    distanceToPlace: Float?,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onRequestNarration: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Audio Guide",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A3428)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF4A3428)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Section
                when {
                    isGeneratingNarration -> {
                        LoadingNarrationSection()
                    }
                    audioError != null -> {
                        ErrorSection(message = audioError)
                    }
                    currentNarration == null -> {
                        NoNarrationSection(onRequestNarration = onRequestNarration)
                    }
                    else -> {
                        NarrationPlayerSection(
                            narration = currentNarration,
                            playerState = audioPlayerState,
                            progress = audioProgress,
                            speed = audioSpeed,
                            distanceToPlace = distanceToPlace,
                            onPlayPause = onPlayPause,
                            onStop = onStop,
                            onSpeedChange = onSpeedChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingNarrationSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = Color(0xFF4A3428),
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Generating narration...",
            fontSize = 16.sp,
            color = Color(0xFF4A3428)
        )
        Text(
            text = "Powered by AI",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun ErrorSection(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = Color(0xFFD32F2F),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun NoNarrationSection(onRequestNarration: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF4A3428)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No narration available",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4A3428)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Generate AI-powered audio guide for this place",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestNarration,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6F00)
            )
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate Narration")
        }
    }
}

@Composable
private fun NarrationPlayerSection(
    narration: Narration,
    playerState: AudioPlayerState,
    progress: Float,
    speed: Float,
    distanceToPlace: Float?,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Place Info
        Text(
            text = narration.placeName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A3428)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Distance info (if available)
        if (distanceToPlace != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatDistance(distanceToPlace),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Progress Bar
        Column {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFFFF6F00),
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime((progress * narration.duration).toInt()),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatTime(narration.duration),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed Control
            var showSpeedMenu by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(
                    onClick = { showSpeedMenu = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF4A3428)
                    )
                ) {
                    Text("${speed}x")
                }

                DropdownMenu(
                    expanded = showSpeedMenu,
                    onDismissRequest = { showSpeedMenu = false }
                ) {
                    listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { s ->
                        DropdownMenuItem(
                            text = { Text("${s}x") },
                            onClick = {
                                onSpeedChange(s)
                                showSpeedMenu = false
                            }
                        )
                    }
                }
            }

            // Stop Button
            IconButton(
                onClick = onStop,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = Color(0xFF4A3428),
                    modifier = Modifier.size(32.dp)
                )
            }

            // Play/Pause Button (Primary)
            FloatingActionButton(
                onClick = onPlayPause,
                containerColor = Color(0xFFFF6F00),
                contentColor = Color.White,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = when (playerState) {
                        is AudioPlayerState.Playing -> Icons.Default.Pause
                        else -> Icons.Default.PlayArrow
                    },
                    contentDescription = when (playerState) {
                        is AudioPlayerState.Playing -> "Pause"
                        else -> "Play"
                    },
                    modifier = Modifier.size(36.dp)
                )
            }

            // Replay Button
            IconButton(
                onClick = {
                    onStop()
                    onPlayPause()
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Replay",
                    tint = Color(0xFF4A3428),
                    modifier = Modifier.size(32.dp)
                )
            }

            // Language (placeholder for future)
            IconButton(
                onClick = { /* TODO: Language switch */ },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Language",
                    tint = Color(0xFF4A3428),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Text
        Text(
            text = when (playerState) {
                is AudioPlayerState.Playing -> "Playing..."
                is AudioPlayerState.Paused -> "Paused"
                is AudioPlayerState.Loading -> "Loading..."
                is AudioPlayerState.Stopped -> "Stopped"
                is AudioPlayerState.Error -> "Error"
                else -> "Ready"
            },
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Narration Text Preview (scrollable)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 150.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Text(
                text = narration.narrationText,
                fontSize = 13.sp,
                color = Color(0xFF4A3428),
                lineHeight = 20.sp,
                modifier = Modifier
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}

private fun formatDistance(meters: Float): String {
    return when {
        meters < 1000 -> "${meters.toInt()} m away"
        else -> "${"%.1f".format(meters / 1000)} km away"
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(minutes, secs)
}

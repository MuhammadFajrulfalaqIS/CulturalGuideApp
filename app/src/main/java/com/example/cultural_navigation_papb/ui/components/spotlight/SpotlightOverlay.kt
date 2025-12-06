package com.example.cultural_navigation_papb.ui.components.spotlight

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

data class SpotlightTarget(
    val bounds: Rect,
    val title: String,
    val description: String,
    val shape: SpotlightShape = SpotlightShape.Circle
)

enum class SpotlightShape {
    Circle,
    RoundedRect
}

enum class TooltipPosition {
    TOP, BOTTOM, LEFT, RIGHT
}

@Composable
fun SpotlightOverlay(
    target: SpotlightTarget?,
    currentStep: Int,
    totalSteps: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (target == null) return

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Pulse animation untuk spotlight - more subtle
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Determine tooltip position based on target location
    val tooltipPosition = remember(target.bounds, screenHeight.value) {
        when {
            target.bounds.bottom < screenHeight.value * 0.5f -> TooltipPosition.BOTTOM
            target.bounds.top > screenHeight.value * 0.5f -> TooltipPosition.TOP
            else -> TooltipPosition.BOTTOM
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Semi-transparent overlay with clear spotlight area
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Create spotlight hole path
            val spotlightPath = Path().apply {
                when (target.shape) {
                    SpotlightShape.Circle -> {
                        val centerX = target.bounds.center.x
                        val centerY = target.bounds.center.y
                        val radius = maxOf(target.bounds.width, target.bounds.height) / 2f + 20f
                        addOval(
                            Rect(
                                centerX - radius,
                                centerY - radius,
                                centerX + radius,
                                centerY + radius
                            )
                        )
                    }
                    SpotlightShape.RoundedRect -> {
                        val padding = 20f
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                left = target.bounds.left - padding,
                                top = target.bounds.top - padding,
                                right = target.bounds.right + padding,
                                bottom = target.bounds.bottom + padding,
                                radiusX = 20f,
                                radiusY = 20f
                            )
                        )
                    }
                }
            }

            // Create full screen path
            val fullPath = Path().apply {
                addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
            }

            // Subtract spotlight from full screen to create hole
            val overlayPath = Path().apply {
                op(fullPath, spotlightPath, androidx.compose.ui.graphics.PathOperation.Difference)
            }

            // Draw only the overlay area (not the spotlight)
            drawPath(overlayPath, Color.Black.copy(alpha = 0.6f))
        }

        // Close button at top right
        IconButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.95f), CircleShape)
                .size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }

        // Tooltip with arrow near the target element
        TooltipCallout(
            target = target,
            currentStep = currentStep,
            totalSteps = totalSteps,
            onNext = onNext,
            onSkip = onSkip,
            position = tooltipPosition,
            modifier = Modifier
        )
    }
}

@Composable
private fun TooltipCallout(
    target: SpotlightTarget,
    currentStep: Int,
    totalSteps: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    position: TooltipPosition,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // Card dimensions - smaller
    val cardHeightPx = with(density) { 140.dp.toPx() }
    val arrowHeightPx = with(density) { 12.dp.toPx() }
    val cardWidthPx = with(density) { 250.dp.toPx() }
    val cardMarginPx = with(density) { 16.dp.toPx() }
    val bottomMarginPx = with(density) { 80.dp.toPx() }
    val topMarginPx = with(density) { 16.dp.toPx() }

    // Calculate tooltip Y position DIRECTLY based on target element bounds
    val tooltipY = remember(target.bounds, position, screenHeightPx, cardHeightPx, arrowHeightPx) {
        when (position) {
            TooltipPosition.BOTTOM -> {
                // Place tooltip RIGHT BELOW the target element
                val spacingFromTarget = 10f
                val idealY = target.bounds.bottom + spacingFromTarget
                // Adjust Y position to move tooltip up by 1/4 of card height
                val adjustedY = idealY - (cardHeightPx * 0.25f)
                // Make sure it doesn't go off screen
                val maxY = screenHeightPx - cardHeightPx - arrowHeightPx - bottomMarginPx
                adjustedY.coerceAtMost(maxY)
            }
            TooltipPosition.TOP -> {
                // Place tooltip RIGHT ABOVE the target element
                val spacingFromTarget = 10f
                val idealY = target.bounds.top - cardHeightPx - arrowHeightPx - spacingFromTarget
                // Adjust Y position to move tooltip up by 1/4 of card height
                val adjustedY = idealY - (cardHeightPx * 0.25f)
                adjustedY.coerceAtLeast(topMarginPx)
            }
            TooltipPosition.LEFT -> {
                target.bounds.center.y - cardHeightPx / 2f
            }
            TooltipPosition.RIGHT -> {
                target.bounds.center.y - cardHeightPx / 2f
            }
        }
    }

    // Calculate card X position (centered on target element exactly)
    val cardXPos = with(density) {
        (target.bounds.center.x - cardWidthPx / 2f).coerceIn(
            cardMarginPx,
            screenWidthPx - cardWidthPx - cardMarginPx
        ).toDp()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.offset(
                x = cardXPos,
                y = with(density) { tooltipY.toDp() }
            )
        ) {
            // TOP arrow (when tooltip is below target)
            if (position == TooltipPosition.BOTTOM) {
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .height(12.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cardLeft = with(density) { cardXPos.toPx() }
                        val targetCenterX = target.bounds.center.x
                        // Arrow points to EXACT center of target element
                        val arrowXInCard = (targetCenterX - cardLeft).coerceIn(20.dp.toPx(), 230.dp.toPx())

                        val arrowPath = Path().apply {
                            moveTo(arrowXInCard, 12.dp.toPx())
                            lineTo(arrowXInCard - 8.dp.toPx(), 0f)
                            lineTo(arrowXInCard + 8.dp.toPx(), 0f)
                            close()
                        }
                        drawPath(arrowPath, Color.White)
                    }
                }
            }

            // Tooltip Card - more compact
            Card(
                modifier = Modifier.width(250.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(11.dp)
                ) {
                    // Step indicator
                    Text(
                        text = "Step ${currentStep + 1} of $totalSteps",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        letterSpacing = 0.3.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Title
                    Text(
                        text = target.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Description
                    Text(
                        text = target.description,
                        fontSize = 10.sp,
                        color = Color.DarkGray,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(9.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onSkip,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Lewati",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }

                        Button(
                            onClick = onNext,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00C896)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (currentStep == totalSteps - 1) "Selesai" else "Lanjut",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // BOTTOM arrow (when tooltip is above target)
            if (position == TooltipPosition.TOP) {
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .height(12.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cardLeft = with(density) { cardXPos.toPx() }
                        val targetCenterX = target.bounds.center.x
                        val arrowXInCard = (targetCenterX - cardLeft).coerceIn(20.dp.toPx(), 230.dp.toPx())

                        val arrowPath = Path().apply {
                            moveTo(arrowXInCard, 0f)
                            lineTo(arrowXInCard - 8.dp.toPx(), 12.dp.toPx())
                            lineTo(arrowXInCard + 8.dp.toPx(), 12.dp.toPx())
                            close()
                        }
                        drawPath(arrowPath, Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SkipConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Lewati Panduan?",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = "Anda bisa mengakses panduan ini lagi dari menu Pengaturan.",
                color = Color.DarkGray,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00C896)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "Ya, Lewati",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    "Batal",
                    color = Color.Gray
                )
            }
        }
    )
}

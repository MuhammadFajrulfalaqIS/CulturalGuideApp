package com.example.cultural_navigation_papb.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.viewmodels.AuthViewModel
import com.example.cultural_navigation_papb.data.viewmodels.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleReviewDialog(
    place: Place,
    onDismiss: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    // State untuk dialog
    var showReviewDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(0f) }
    var reviewText by remember { mutableStateOf("") }

    // Get current user
    val currentUser by authViewModel.currentUser.collectAsState()

    // Button untuk membuka dialog
    Button(
        onClick = { showReviewDialog = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4A3428),
            contentColor = Color.White
        )
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = "Write Review",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Write a Review",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }

    // Simple review dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = {
                Text(
                    text = "Review for ${place.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A3428)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // User info
                    if (currentUser == null) {
                        Text(
                            text = "Please log in to write a review",
                            color = Color(0xFFFF6F00),
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Rating:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4A3428)
                        )

                        // Star rating
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..5).forEach { star ->
                                IconButton(
                                    onClick = { rating = star.toFloat() },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                        contentDescription = "Star $star",
                                        tint = if (star <= rating) Color(0xFFFF6F00) else Color(0xFF9E9E9E),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Text(
                                text = "${rating.toInt()} / 5",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A3428)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Review text
                        Text(
                            text = "Your Review:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4A3428)
                        )

                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            label = { Text("Share your experience...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentUser != null && rating > 0 && reviewText.isNotBlank()) {
                            // Submit review logic
                            showReviewDialog = false
                            // Reset form
                            rating = 0f
                            reviewText = ""
                        }
                    },
                    enabled = currentUser != null && rating > 0 && reviewText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A3428),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Submit Review",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
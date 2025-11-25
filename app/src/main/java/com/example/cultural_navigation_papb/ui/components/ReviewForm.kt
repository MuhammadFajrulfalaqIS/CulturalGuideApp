package com.example.cultural_navigation_papb.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cultural_navigation_papb.data.viewmodels.ReviewViewModel

/**
 * Composable untuk form review tempat wisata
 */
@Composable
fun ReviewForm(
    userId: String,
    userName: String,
    onReviewSubmitted: () -> Unit,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val rating by viewModel.rating.collectAsState()
    val comment by viewModel.comment.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val submitError by viewModel.submitError.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title
            Text(
                text = "Tulis Review",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Rating Stars
            Text(
                text = "Rating",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                repeat(5) { index ->
                    IconButton(
                        onClick = { viewModel.updateRating(index + 1f) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = "Rating ${index + 1}",
                            tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = if (rating > 0) "$rating.0" else "0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // Comment Field
            OutlinedTextField(
                value = comment,
                onValueChange = { viewModel.updateComment(it) },
                label = { Text("Komentar (opsional)") },
                placeholder = { Text("Bagikan pengalaman Anda di tempat ini...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Error Message
            submitError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Submit Button
            Button(
                onClick = {
                    viewModel.submitReview(userId, userName)
                    onReviewSubmitted()
                },
                enabled = !isSubmitting && rating > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Mengirim...")
                } else {
                    Text("Kirim Review")
                }
            }
        }
    }
}

/**
 * Composable untuk menampilkan list review
 */
@Composable
fun ReviewList(
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val reviews by viewModel.reviews.collectAsState()
    val averageRating = viewModel.getAverageRating()

    Column {
        // Rating Summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Rating & Review",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Average Rating
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("%.1f", averageRating),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = "${reviews.size} review",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Rating Distribution (simplified)
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        repeat(5) { index ->
                            val starCount = 5 - index
                            val count = reviews.count { it.rating >= starCount && it.rating < starCount + 1 }
                            val percentage = if (reviews.isNotEmpty()) count.toFloat() / reviews.size * 100 else 0f

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 1.dp)
                            ) {
                                Text(
                                    text = "$starCount",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.width(16.dp)
                                )
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                LinearProgressIndicator(
                                    progress = percentage / 100,
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(4.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // Individual Reviews
        if (reviews.isNotEmpty()) {
            reviews.forEach { review ->
                ReviewItem(review = review)
                Spacer(Modifier.height(12.dp))
            }
        } else {
            Text(
                text = "Belum ada review",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * Composable untuk menampilkan satu review
 */
@Composable
fun ReviewItem(
    review: com.example.cultural_navigation_papb.data.models.Review,
    onMarkHelpful: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )

                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = null,
                                tint = if (index < review.rating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = String.format("%.1f", review.rating),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Date
                Text(
                    text = java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(Date(review.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Comment
            if (review.comment.isNotEmpty()) {
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Helpful button
            Row(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                TextButton(
                    onClick = { onMarkHelpful(review.id) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "👍 Helpful (${review.helpfulCount})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
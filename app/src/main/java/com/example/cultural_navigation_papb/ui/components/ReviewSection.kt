package com.example.cultural_navigation_papb.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cultural_navigation_papb.data.models.Review
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReviewSection(
    reviews: List<Review>,
    averageRating: Float,
    ratingDistribution: Map<Int, Int>,
    onAddReview: () -> Unit,
    onHelpfulClick: (String) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ulasan Pengunjung",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = onAddReview) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Review",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah")
            }
        }

        // Rating Overview
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Average Rating
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (averageRating > 0) String.format("%.1f", averageRating) else "0.0",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    RatingStarsDisplay(
                        rating = averageRating,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "${reviews.size} ulasan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Rating Distribution
                if (reviews.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 5 downTo 1) {
                            val count = ratingDistribution[i] ?: 0
                            val percentage = if (reviews.isNotEmpty()) {
                                count.toFloat() / reviews.size
                            } else 0f

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "$i",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.width(12.dp)
                                )

                                LinearProgressIndicator(
                                    progress = animateFloatAsState(targetValue = percentage).value,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.width(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Reviews List
        if (reviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.StarRate,
                        contentDescription = "No Reviews",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Belum ada ulasan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Jadi yang pertama memberikan ulasan!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(reviews) { review ->
                    ReviewItem(
                        review = review,
                        dateFormatter = dateFormatter,
                        onHelpfulClick = onHelpfulClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewItem(
    review: com.example.cultural_navigation_papb.data.models.Review,
    dateFormatter: SimpleDateFormat,
    onHelpfulClick: (String) -> Unit
) {
    var isHelpful by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = review.userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    RatingStarsDisplay(
                        rating = review.rating,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Text(
                        text = dateFormatter.format(Date(review.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Comment
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Photo feature not available in current Review model

            // Helpful Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            if (!isHelpful) {
                                onHelpfulClick(review.id)
                                isHelpful = true
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isHelpful) Icons.Filled.ThumbUp else Icons.Filled.ThumbUpOffAlt,
                        contentDescription = "Helpful",
                        modifier = Modifier.size(16.dp),
                        tint = if (isHelpful) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${review.helpfulCount + if (isHelpful) 1 else 0}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isHelpful) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isHelpful) "Membantu" else "Bermanfaat?",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isHelpful) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingStarsDisplay(
    rating: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = when {
                    i <= rating -> Icons.Filled.Star
                    i - 0.5f <= rating -> Icons.Filled.Star // Half star approximation
                    else -> Icons.Outlined.StarBorder
                },
                contentDescription = "Star $i",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
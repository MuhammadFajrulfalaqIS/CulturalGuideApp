package com.example.cultural_navigation_papb.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.cultural_navigation_papb.data.models.Review
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
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
    @Suppress("DEPRECATION")
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }

    // Sort reviews by rating (highest first)
    val sortedReviews = remember(reviews) {
        reviews.sortedByDescending { it.rating }
    }

    // State for showing more reviews
    var showAllReviews by remember { mutableStateOf(false) }

    // Determine how many reviews to show
    val reviewsToShow = remember(sortedReviews, showAllReviews) {
        if (sortedReviews.isEmpty()) {
            emptyList()
        } else if (showAllReviews) {
            sortedReviews
        } else {
            // Show only first review (highest rating)
            sortedReviews.take(1)
        }
    }

    val hasMoreReviews = sortedReviews.size > 1

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ===== COMPACT HEADER =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Ulasan & Rating",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                if (reviews.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.1f", averageRating),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFF6F00)
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFF6F00),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "• ${reviews.size} ulasan",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = onAddReview,
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Add Review",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tulis", fontSize = 13.sp)
            }
        }

        // ===== COMPACT RATING OVERVIEW =====
        if (reviews.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF8E1).copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Average Rating (Compact)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.1f", averageRating),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6F00)
                        )
                        Column {
                            RatingStarsDisplay(
                                rating = averageRating,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                            Text(
                                text = "${reviews.size} ulasan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Compact Rating Distribution
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        for (i in 5 downTo 1) {
                            val count = ratingDistribution[i] ?: 0
                            val percentage = if (reviews.isNotEmpty()) {
                                count.toFloat() / reviews.size
                            } else 0f

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "$i",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.width(10.dp),
                                    fontSize = 11.sp
                                )
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFF6F00),
                                    modifier = Modifier.size(10.dp)
                                )

                                LinearProgressIndicator(
                                    progress = animateFloatAsState(targetValue = percentage, label = "progress").value,
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = Color(0xFFFF6F00),
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.width(16.dp),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // ===== REVIEWS LIST (COMPACT) =====
        if (reviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.RateReview,
                        contentDescription = "No Reviews",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Belum ada ulasan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Jadi yang pertama!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Show reviews (1 by default, all when expanded)
                reviewsToShow.forEach { review ->
                    CompactReviewItem(
                        review = review,
                        dateFormatter = dateFormatter,
                        onHelpfulClick = onHelpfulClick
                    )
                }

                // Show More / Show Less Button
                if (hasMoreReviews) {
                    TextButton(
                        onClick = { showAllReviews = !showAllReviews },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (showAllReviews)
                                "Tampilkan Lebih Sedikit"
                            else
                                "Tampilkan ${sortedReviews.size - 1} Ulasan Lainnya",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = if (showAllReviews)
                                Icons.Filled.ExpandLess
                            else
                                Icons.Filled.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactReviewItem(
    review: Review,
    dateFormatter: SimpleDateFormat,
    onHelpfulClick: (String) -> Unit
) {
    var isHelpful by remember { mutableStateOf(false) }
    var showImagePreview by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header (Compact)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User Avatar
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6D4C41)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.userName.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = review.userName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = dateFormatter.format(Date(review.timestamp)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // Rating Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFF6F00).copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.1f", review.rating),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6F00)
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFF6F00),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Comment (Compact, max 3 lines)
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            // Photos (if any) - NOW CLICKABLE
            if (review.photos.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(review.photos.take(3)) { photoUrl ->
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Review photo",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedImageIndex = review.photos.indexOf(photoUrl)
                                    showImagePreview = true
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (review.photos.size > 3) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .clickable {
                                        selectedImageIndex = 3
                                        showImagePreview = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${review.photos.size - 3}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Helpful Button (Compact) - NOW TOGGLEABLE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isHelpful)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else
                        Color.Transparent,
                    modifier = Modifier.clickable {
                        // Toggle helpful status
                        isHelpful = !isHelpful
                        onHelpfulClick(review.id)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isHelpful) Icons.Filled.ThumbUp else Icons.Filled.ThumbUpOffAlt,
                            contentDescription = "Helpful",
                            modifier = Modifier.size(14.dp),
                            tint = if (isHelpful)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${review.helpfulCount + if (isHelpful) 1 else 0}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = if (isHelpful)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Image Preview Dialog
    if (showImagePreview && review.photos.isNotEmpty()) {
        ImagePreviewDialog(
            images = review.photos,
            initialPage = selectedImageIndex,
            onDismiss = { showImagePreview = false }
        )
    }
}

/**
 * Fullscreen Image Preview Dialog with Pager
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ImagePreviewDialog(
    images: List<String>,
    initialPage: Int = 0,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialPage.coerceIn(0, images.size - 1))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Image Pager
            HorizontalPager(
                count = images.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = images[page],
                        contentDescription = "Preview image ${page + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Top bar with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image counter
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${images.size}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Close button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Navigation indicators (dots)
            if (images.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    images.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                                .background(
                                    color = if (pagerState.currentPage == index)
                                        Color.White
                                    else
                                        Color.White.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                        )
                    }
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
                    i - 0.5f <= rating -> Icons.Filled.Star
                    else -> Icons.Outlined.StarBorder
                },
                contentDescription = "Star $i",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
package com.example.cultural_navigation_papb.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Star
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
import coil.compose.AsyncImage
import com.example.cultural_navigation_papb.data.models.Place
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailPopup(
    place: Place,
    onDismiss: () -> Unit,
    onViewDetail: (String) -> Unit,
    onGetDirections: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 450.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Destinasi",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color(0xFF8D6E63)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Multiple images for carousel
                val carouselImages = remember {
                    listOf(
                        place.imageUrl,
                        com.example.cultural_navigation_papb.R.drawable.explore_pic,
                        com.example.cultural_navigation_papb.R.drawable.photos_pic,
                        com.example.cultural_navigation_papb.R.drawable.prambanan_shadow
                    )
                }

                val pagerState = rememberPagerState(pageCount = { carouselImages.size })
                val scope = rememberCoroutineScope()

                // Auto-scroll every 2 seconds
                LaunchedEffect(pagerState.currentPage) {
                    while (true) {
                        delay(2000)
                        scope.launch {
                            val nextPage = (pagerState.currentPage + 1) % carouselImages.size
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = carouselImages[page],
                            contentDescription = "${place.name} Image ${page + 1}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Carousel indicators
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(carouselImages.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (index == pagerState.currentPage)
                                            Color(0xFF8D6E63)
                                        else
                                            Color.Gray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Place name and rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = place.category.replace("_", " ").replaceFirstChar {
                                if (it.isLowerCase()) it.uppercase() else it.toString()
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8D6E63)
                        )
                    }

                    // Rating only (no reviews section)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", place.rating),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                        Text(
                            text = " (${place.reviewCount})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = place.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.8f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Lihat Detail button
                    OutlinedButton(
                        onClick = { onViewDetail(place.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF8D6E63)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF8D6E63)
                        )
                    ) {
                        Text(
                            text = "Lihat Detail",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Route button
                    Button(
                        onClick = { onGetDirections(place) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8D6E63),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = "Route",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Route",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
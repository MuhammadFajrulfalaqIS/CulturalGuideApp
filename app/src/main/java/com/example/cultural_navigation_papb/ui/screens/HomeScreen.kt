// File: ui/screens/HomeScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.cultural_navigation_papb.R
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.models.prambananHighlights
import com.example.cultural_navigation_papb.data.models.prambananSummaries
import kotlinx.coroutines.delay
// --- Impor untuk Preview ---
import androidx.compose.ui.tooling.preview.Preview
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme

// Tambahkan dependensi: implementation("io.coil-kt:coil-compose:2.6.0")
// Tambahkan dependensi: implementation("androidx.compose.material:material-icons-extended")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Define brown color scheme matching the image
    val darkBrown = Color(0xFF3E2723)
    val lightBrown = Color(0xFF5D4037)

    Scaffold(
        bottomBar = {
            HomeBottomNavBar(
                onHomeClick = { /* Do nothing, already on Home */ },
                onProfileClick = onNavigateToProfile,
                backgroundColor = darkBrown
            )
        },
        containerColor = Color(0xFFF5F5F5),
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background shadow image
            Image(
                painter = rememberAsyncImagePainter(model = R.drawable.prambanan_shadow),
                contentDescription = "Prambanan Shadow",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.75f), // Subtle shadow effect
                alignment = Alignment.Center
            )

            // Main content over the background
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
            // Header with dark brown background and temple icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(darkBrown)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Temple icon placeholder - you can replace with actual icon/image
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Temple Icon",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Selamat Datang, User!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Carousel Section with title overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                PrambananCarousel(highlights = prambananHighlights)
            }

            // Action Cards Row (Explore & List)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Explore Card
                // IMPORTANT: First move images from drawable/homescreen/ to drawable/
                // Then use: R.drawable.pramb1 (no quotes, no extension)
                ImageActionCard(
                    text = "Explore",
                    onClick = onNavigateToMap,
                    imageUrl = R.drawable.explore_pic, // After moving file from homescreen folder
                    modifier = Modifier.weight(1f)
                )

                // List Card with badge
                ImageActionCard(
                    text = "List",
                    onClick = onNavigateToList,
                    imageUrl = R.drawable.photos_pic, // After moving file from homescreen folder
                    badgeText = "2 photos",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prambanan Info Card
            PrambananInfoCard(
                backgroundColor = lightBrown,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            }
        }
    }
}


// --- Komponen Pembantu (Composable Reusable) ---

@Composable
fun PrambananCarousel(highlights: List<Place>) {
    val listState = rememberLazyListState()

    // Get current visible item index
    val currentIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }

    // Auto-scroll effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Hold for 3 seconds
            val nextIndex = (listState.firstVisibleItemIndex + 1) % highlights.size
            listState.animateScrollToItem(nextIndex, 0)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Carousel images with pager-like behavior
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
            ) {
                items(highlights) { place ->
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .fillMaxHeight()
                    ) {
                        // Background image
                        val painter = rememberAsyncImagePainter(model = place.imageUrl)
                        Image(
                            painter = painter,
                            contentDescription = place.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient overlay at bottom
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.7f)
                                        ),
                                        startY = 200f
                                    )
                                )
                        )

                        // Temple name at bottom
                        Text(
                            text = place.name,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        )
                    }
                }
            }

            // "Slides Carousel" label at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Slides Carousel",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Dot indicators
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                highlights.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentIndex.value) 8.dp else 6.dp)
                            .background(
                                color = if (index == currentIndex.value)
                                    Color.White
                                else
                                    Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun ImageActionCard(
    text: String,
    onClick: () -> Unit,
    imageUrl: Any, // Changed from String to Any to support both URLs and resource IDs
    badgeText: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image - Coil can handle both URLs and resource IDs
            val painter = rememberAsyncImagePainter(model = imageUrl)
            Image(
                painter = painter,
                contentDescription = text,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            // Text centered
            Text(
                text = text,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )

            // Badge if provided
            badgeText?.let { badge ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun PrambananInfoCard(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val summaries = prambananSummaries
    val listState = rememberLazyListState()

    // Get current visible item index
    val currentIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Slideable content
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = true,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
            ) {
                items(summaries) { summary ->
                    Box(
                        modifier = Modifier.fillParentMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = summary.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // Highlight info badge
                                if (summary.highlightInfo.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color.White.copy(alpha = 0.2f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = summary.highlightInfo,
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = summary.summary,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    lineHeight = 16.sp
                                )
                            }

                            // Temple illustration placeholder
                            // TODO: Replace with actual temple illustration image
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Temple Icon",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            // Dot indicators at the bottom
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                summaries.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentIndex.value) 8.dp else 6.dp)
                            .background(
                                color = if (index == currentIndex.value)
                                    Color.White
                                else
                                    Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavBar(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    backgroundColor: Color
) {
    NavigationBar(
        containerColor = backgroundColor,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            selected = true, // Home always selected on this screen
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.White.copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun HomeScreenPreview() {
    CulturalnavigationpapbTheme {
        HomeScreen(
            onNavigateToMap = {},
            onNavigateToList = {},
            onNavigateToProfile = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImageActionCardPreview() {
    CulturalnavigationpapbTheme {
        ImageActionCard(
            text = "Explore",
            onClick = {},
            imageUrl = "https://picsum.photos/400/300"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBottomNavBarPreview() {
    CulturalnavigationpapbTheme {
        HomeBottomNavBar(
            onHomeClick = {},
            onProfileClick = {},
            backgroundColor = Color(0xFF3E2723)
        )
    }
}
//
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cultural_navigation_papb.R
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.PrambananData
import com.example.cultural_navigation_papb.data.prambananSummaries
import com.example.cultural_navigation_papb.data.viewmodels.AuthViewModel
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    // Inject AuthViewModel untuk mengambil data user
    authViewModel: AuthViewModel = viewModel()
) {
    // Ambil data user saat ini (nama, email, foto) dari ViewModel
    val user by authViewModel.currentUser.collectAsState()
    
    // Format username untuk ditampilkan dengan lebih baik
    val displayName = user?.name?.let { name ->
        // Capitalize first letter of each word
        name.split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
                }
            }
    } ?: "Pengunjung"
    
    // Pilih 3 destinasi acak dari semua destinasi yang tersedia
    val randomHighlights = remember {
        PrambananData.allTemples.shuffled().take(3)
    }

    // Warna tema coklat
    val darkBrown = Color(0xFF3E2723)
    val lightBrown = Color(0xFF5D4037)

    Scaffold(
        bottomBar = {
            HomeBottomNavBar(
                onHomeClick = { /* Sudah di Home */ },
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
                painter = painterResource(id = R.drawable.prambanan_shadow),
                contentDescription = "Prambanan Shadow",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.75f),
                alignment = Alignment.Center
            )

            // Main content
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // --- HEADER: Menampilkan Nama User ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(darkBrown)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Temple Icon",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        // Menampilkan Nama User Dinamis
                        Text(
                            text = "Selamat Datang, $displayName!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Carousel Section - Shows 3 random destinations each time the app opens
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    PrambananCarousel(highlights = randomHighlights)
                }

                // Action Cards Row (Explore & List)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImageActionCard(
                        text = "Map Interaktif",
                        onClick = onNavigateToMap,
                        imageUrl = R.drawable.explore_pic,
                        modifier = Modifier.weight(1f)
                    )

                    ImageActionCard(
                        text = "Semua Destinasi",
                        onClick = onNavigateToList,
                        imageUrl = R.drawable.photos_pic,

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

// --- Komponen Pembantu (Sama seperti sebelumnya) ---

@Composable
fun PrambananCarousel(highlights: List<Place>) {
    val listState = rememberLazyListState()
    val currentIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }

    // Auto-scroll effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
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
                        Image(
                            painter = painterResource(id = place.imageUrl),
                            contentDescription = place.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                        startY = 200f
                                    )
                                )
                        )
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
                                color = if (index == currentIndex.value) Color.White else Color.White.copy(alpha = 0.5f),
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
    imageUrl: Int,
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
            Image(
                painter = painterResource(id = imageUrl),
                contentDescription = text,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient overlay for better text contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
            Text(
                text = text,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
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
    val currentIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = true,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
            ) {
                items(summaries) { summary ->
                    Box(modifier = Modifier.fillParentMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = summary.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
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
                                color = if (index == currentIndex.value) Color.White else Color.White.copy(alpha = 0.5f),
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
            selected = true,
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
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cultural_navigation_papb.R
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.PrambananData
import com.example.cultural_navigation_papb.data.prambananSummaries
import com.example.cultural_navigation_papb.data.viewmodels.AuthViewModel
import com.example.cultural_navigation_papb.data.viewmodels.FeatureGuideViewModel
import com.example.cultural_navigation_papb.ui.components.spotlight.*
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ========== DEBUG FLAG ==========
// Set ke true untuk testing onboarding (muncul terus)
// Set ke false untuk production (muncul sekali saja)
private const val DEBUG_FORCE_SHOW_ONBOARDING = true
// ================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    featureGuideViewModel: FeatureGuideViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState()
    val isGuideCompleted by featureGuideViewModel.isFeatureGuideCompleted.collectAsState()

    val darkBrown = Color(0xFF3E2723)
    val lightBrown = Color(0xFF5D4037)

    // Spotlight states
    var carouselBounds by remember { mutableStateOf<Rect?>(null) }
    var exploreCardBounds by remember { mutableStateOf<Rect?>(null) }
    var listCardBounds by remember { mutableStateOf<Rect?>(null) }
    var bottomNavBounds by remember { mutableStateOf<Rect?>(null) }

    var currentSpotlightStep by remember { mutableStateOf(0) }
    var showSpotlight by remember { mutableStateOf(false) }
    var showSkipDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Define spotlight targets
    val spotlightTargets = remember(carouselBounds, exploreCardBounds, listCardBounds, bottomNavBounds) {
        listOfNotNull(
            carouselBounds?.let {
                SpotlightTarget(
                    bounds = it,
                    title = "📸 Galeri Candi",
                    description = "Geser carousel untuk melihat berbagai candi bersejarah yang ada di Indonesia",
                    shape = SpotlightShape.RoundedRect
                )
            },
            exploreCardBounds?.let {
                SpotlightTarget(
                    bounds = it,
                    title = "🗺️ Explore Map",
                    description = "Tap untuk membuka peta interaktif dan menjelajahi lokasi candi di sekitar Anda",
                    shape = SpotlightShape.RoundedRect
                )
            },
            listCardBounds?.let {
                SpotlightTarget(
                    bounds = it,
                    title = "📋 Daftar Candi",
                    description = "Lihat daftar lengkap semua candi dengan informasi detail dan foto-foto",
                    shape = SpotlightShape.RoundedRect
                )
            },
            bottomNavBounds?.let {
                SpotlightTarget(
                    bounds = it,
                    title = "🧭 Navigasi",
                    description = "Gunakan menu ini untuk berpindah ke halaman Profile dan fitur lainnya",
                    shape = SpotlightShape.RoundedRect
                )
            }
        )
    }

    // Auto-start showcase on first launch
    LaunchedEffect(isGuideCompleted, spotlightTargets) {
        // DEBUG MODE: Force show onboarding untuk testing
        val shouldShowGuide = if (DEBUG_FORCE_SHOW_ONBOARDING) {
            true // Selalu tampilkan untuk testing
        } else {
            !isGuideCompleted // Normal: tampilkan hanya jika belum selesai
        }

        if (shouldShowGuide && spotlightTargets.size == 4) {
            delay(500) // Delay untuk memastikan layout sudah render
            showSpotlight = true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = darkBrown,
                    contentColor = Color.White,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        val position = coordinates.positionInWindow()
                        bottomNavBounds = Rect(
                            left = position.x,
                            top = position.y,
                            right = position.x + coordinates.size.width,
                            bottom = position.y + coordinates.size.height
                        )
                    }
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { /* Sudah di Home */ },
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
                        onClick = onNavigateToProfile,
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
            },
            containerColor = Color(0xFFF5F5F5)
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
                                text = "Selamat Datang, ${user?.name ?: "Pengunjung"}!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Carousel with bounds tracking
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .onGloballyPositioned { coordinates ->
                                val position = coordinates.positionInWindow()
                                carouselBounds = Rect(
                                    left = position.x,
                                    top = position.y,
                                    right = position.x + coordinates.size.width,
                                    bottom = position.y + coordinates.size.height
                                )
                            }
                    ) {
                        PrambananCarousel(highlights = PrambananData.allTemples.take(3))
                    }

                    // Action Cards with bounds tracking
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .onGloballyPositioned { coordinates ->
                                    val position = coordinates.positionInWindow()
                                    exploreCardBounds = Rect(
                                        left = position.x,
                                        top = position.y,
                                        right = position.x + coordinates.size.width,
                                        bottom = position.y + coordinates.size.height
                                    )
                                }
                        ) {
                            ImageActionCard(
                                text = "Explore",
                                onClick = onNavigateToMap,
                                imageUrl = R.drawable.explore_pic,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .onGloballyPositioned { coordinates ->
                                    val position = coordinates.positionInWindow()
                                    listCardBounds = Rect(
                                        left = position.x,
                                        top = position.y,
                                        right = position.x + coordinates.size.width,
                                        bottom = position.y + coordinates.size.height
                                    )
                                }
                        ) {
                            ImageActionCard(
                                text = "List",
                                onClick = onNavigateToList,
                                imageUrl = R.drawable.photos_pic,
                                badgeText = "2 photos",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
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

        // Spotlight overlay
        if (showSpotlight && spotlightTargets.isNotEmpty()) {
            SpotlightOverlay(
                target = spotlightTargets.getOrNull(currentSpotlightStep),
                currentStep = currentSpotlightStep,
                totalSteps = spotlightTargets.size,
                onNext = {
                    if (currentSpotlightStep < spotlightTargets.size - 1) {
                        currentSpotlightStep++
                    } else {
                        showSpotlight = false
                        scope.launch {
                            featureGuideViewModel.setFeatureGuideCompleted()
                        }
                    }
                },
                onSkip = {
                    showSkipDialog = true
                }
            )
        }

        // Skip confirmation dialog
        if (showSkipDialog) {
            SkipConfirmationDialog(
                onConfirm = {
                    showSpotlight = false
                    showSkipDialog = false
                    scope.launch {
                        featureGuideViewModel.setFeatureGuideCompleted()
                    }
                },
                onDismiss = {
                    showSkipDialog = false
                }
            )
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
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
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

// Preview untuk Onboarding Spotlight
@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Onboarding Step 1 - Carousel")
@Composable
fun OnboardingCarouselPreview() {
    CulturalnavigationpapbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background content (simplified HomeScreen)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                // Header - height sekitar 56dp + padding 24dp = 80dp total
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3E2723))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Selamat Datang, User!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Carousel - posisi Y sekitar 80 + 16 = 96dp, tinggi 180dp
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Explore", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("List", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Spotlight overlay - bounds realistis untuk carousel
            SpotlightOverlay(
                target = SpotlightTarget(
                    bounds = Rect(
                        left = 16f, // padding horizontal
                        top = 96f, // header 80dp + spacer 16dp
                        right = 360f - 16f, // lebar screen - padding
                        bottom = 96f + 180f // top + tinggi carousel
                    ),
                    title = "📸 Galeri Candi",
                    description = "Geser carousel untuk melihat berbagai candi bersejarah yang ada di Indonesia",
                    shape = SpotlightShape.RoundedRect
                ),
                currentStep = 0,
                totalSteps = 4,
                onNext = {},
                onSkip = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Onboarding Step 2 - Explore")
@Composable
fun OnboardingExplorePreview() {
    CulturalnavigationpapbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                // Header - 80dp total height
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3E2723))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Selamat Datang, User!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Carousel - 180dp
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action cards - posisi Y: 80 + 16 + 180 + 16 = 292dp
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Explore", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("List", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Spotlight overlay - bounds realistis untuk explore card
            SpotlightOverlay(
                target = SpotlightTarget(
                    bounds = Rect(
                        left = 16f, // padding kiri
                        top = 292f, // posisi Y realistis
                        right = 166f, // setengah dari lebar (360/2) - spacing 12dp/2
                        bottom = 292f + 140f // top + tinggi card
                    ),
                    title = "🗺️ Explore Map",
                    description = "Tap untuk membuka peta interaktif dan menjelajahi lokasi candi di sekitar Anda",
                    shape = SpotlightShape.RoundedRect
                ),
                currentStep = 1,
                totalSteps = 4,
                onNext = {},
                onSkip = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Onboarding Step 3 - List")
@Composable
fun OnboardingListPreview() {
    CulturalnavigationpapbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                // Header - 80dp total height
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3E2723))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Selamat Datang, User!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Carousel - 180dp
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action cards - posisi Y: 80 + 16 + 180 + 16 = 292dp
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Explore", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF2196F3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("List", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Spotlight overlay - bounds realistis untuk list card
            SpotlightOverlay(
                target = SpotlightTarget(
                    bounds = Rect(
                        left = 194f, // posisi X untuk card kedua (setengah + spacing)
                        top = 292f, // posisi Y yang sama seperti explore
                        right = 344f, // kanan screen - padding
                        bottom = 292f + 140f // top + tinggi card
                    ),
                    title = "📋 Daftar Candi",
                    description = "Lihat daftar lengkap semua candi dengan informasi detail dan foto-foto",
                    shape = SpotlightShape.RoundedRect
                ),
                currentStep = 2,
                totalSteps = 4,
                onNext = {},
                onSkip = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Onboarding Step 4 - Navigation")
@Composable
fun OnboardingNavigationPreview() {
    CulturalnavigationpapbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background content
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF5F5F5))
                ) {
                    // Header - 80dp total height
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF3E2723))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Selamat Datang, User!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Carousel - 180dp
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action cards
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(140.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Explore", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(140.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("List", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Bottom Navigation - height 80dp (typical navigation bar height)
                NavigationBar(
                    containerColor = Color(0xFF3E2723),
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
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
                        onClick = {},
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

            // Spotlight overlay - bounds realistis untuk bottom navigation
            SpotlightOverlay(
                target = SpotlightTarget(
                    bounds = Rect(
                        left = 0f, // mulai dari kiri screen
                        top = 640f - 80f, // screen height - navigation bar height
                        right = 360f, // sampai kanan screen
                        bottom = 640f // sampai bawah screen
                    ),
                    title = "🧭 Navigasi",
                    description = "Gunakan menu ini untuk berpindah ke halaman Profile dan fitur lainnya",
                    shape = SpotlightShape.RoundedRect
                ),
                currentStep = 3,
                totalSteps = 4,
                onNext = {},
                onSkip = {}
            )
        }
    }
}

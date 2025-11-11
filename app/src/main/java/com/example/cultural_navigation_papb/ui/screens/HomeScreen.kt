// File: ui/screens/HomeScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.models.prambananHighlights
import com.example.cultural_navigation_papb.navigation.Destinations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    // Scaffold memberikan struktur dasar untuk UI: TopBar, Content, BottomBar
    Scaffold(
        bottomBar = {
            HomeBottomNavBar(
                onHomeClick = { /* Do nothing, already on Home */ },
                onProfileClick = onNavigateToProfile
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header: Selamat datang, User
            Text(
                text = "Selamat datang, User!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Highlight Lokasi: Carousel Foto Prambanan
            Text(
                text = "Highlight Lokasi Hari Ini",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PrambananCarousel(highlights = prambananHighlights)

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Navigasi Utama (Explore & Photos)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Tombol Explore (ke Slide 2: Map)
                ActionButton(
                    text = "Explore",
                    icon = Icons.Default.Place,
                    onClick = onNavigateToMap,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Tombol Photos (ke Slide 3: List/Katalog)
                ActionButton(
                    text = "Photos",
                    icon = Icons.Default.Photo,
                    onClick = onNavigateToList,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


// --- Komponen Pembantu (Composable Reusable) ---

@Composable
fun PrambananCarousel(highlights: List<Place>) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val itemWidth = 300.dp // Lebar Carousel Item

    // Efek Sliding Otomatis
    LaunchedEffect(Unit) {
        //
        while (true) {
            delay(3000) // Tahan selama 3 detik
            val nextIndex = (listState.firstVisibleItemIndex + 1) % highlights.size
            coroutineScope.launch {
                listState.animateScrollToItem(nextIndex, 0)
            }
        }
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(highlights) { place ->
            Card(
                modifier = Modifier
                    .width(itemWidth)
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Gunakan Coil untuk memuat gambar dari URL
                    val painter = rememberAsyncImagePainter(model = place.imageUrl)
                    Image(
                        painter = painter,
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay Deskripsi
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = place.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = place.description,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text)
        }
    }
}

@Composable
fun HomeBottomNavBar(onHomeClick: () -> Unit, onProfileClick: () -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        NavigationBarItem(
            selected = true, // Home selalu dipilih di layar ini
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text(Destinations.HOME) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text(Destinations.PROFILE) }
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
fun ActionButtonPreview() {
    CulturalnavigationpapbTheme {
        ActionButton(
            text = "Explore",
            icon = Icons.Default.Place,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBottomNavBarPreview() {
    CulturalnavigationpapbTheme {
        HomeBottomNavBar(
            onHomeClick = {},
            onProfileClick = {}
        )
    }
}
//
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.TempleHindu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// --- Definisi Warna Khusus untuk Onboarding ---
// Anda bisa menyesuaikan kode warna ini agar lebih pas dengan tema beranda Anda
private val BrownPrimary = Color(0xFF5D4037) // Coklat Tua
private val BrownLight = Color(0xFFA1887F)   // Coklat Muda
private val BackgroundCream = Color(0xFFFFF8E1) // Krem Terang (Opsional untuk latar belakang)

// Data class sederhana untuk konten halaman
data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    // Daftar halaman onboarding dengan Ikon yang Lebih Unik (Outlined)
    val pages = listOf(
        OnboardingPage(
            title = "Jelajahi Candi Prambanan",
            description = "Temukan keindahan warisan budaya dunia Candi Prambanan langsung dari genggaman Anda.",
            // Menggunakan ikon TempleHindu (Candi) yang lebih spesifik
            icon = Icons.Outlined.TempleHindu
        ),
        OnboardingPage(
            title = "Navigasi Interaktif",
            description = "Gunakan peta digital untuk menemukan lokasi candi, arca, dan fasilitas dengan mudah.",
            // Menggunakan ikon Map (Peta) bergaya outlined
            icon = Icons.Outlined.Map
        ),
        OnboardingPage(
            title = "Pelajari Sejarah",
            description = "Dapatkan informasi mendalam mengenai sejarah dan cerita di balik setiap relief.",
            // Menggunakan ikon AutoStories (Buku Terbuka dengan Cerita) yang lebih unik
            icon = Icons.Outlined.AutoStories
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        // Mengatur warna latar belakang Scaffold agar sesuai tema
        containerColor = BackgroundCream,
        topBar = {
            // Tombol Skip di pojok kanan atas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onFinishOnboarding) {
                    Text("Lewati", color = BrownPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bagian Slide (Pager)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                OnboardingPageContent(page = pages[pageIndex])
            }

            // Indikator Titik-titik (Dots Indicator) dengan Warna Coklat
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration)
                        BrownPrimary // Coklat Tua untuk yang aktif
                    else
                        BrownLight   // Coklat Muda untuk yang tidak aktif

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(if (pagerState.currentPage == iteration) 12.dp else 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Next / Mulai dengan Warna Coklat
            Button(
                onClick = {
                    if (pagerState.currentPage == pages.size - 1) {
                        onFinishOnboarding()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp), // Membuat tombol lebih membulat
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrownPrimary // Warna latar tombol coklat tua
                )
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.size - 1) "Mulai Sekarang" else "Lanjut",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White // Warna teks putih agar kontras
                )
                if (pagerState.currentPage != pages.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Outlined.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon / Ilustrasi dengan Warna Coklat
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            modifier = Modifier
                .size(220.dp)
                .padding(bottom = 40.dp),
            tint = BrownPrimary // Menggunakan warna coklat tua untuk ikon
        )

        // Judul dengan Warna Coklat
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = BrownPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Deskripsi dengan Warna Coklat yang Lebih Terang
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = BrownPrimary.copy(alpha = 0.8f), // Sedikit transparan agar tidak terlalu gelap
            lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.5
        )
    }
}
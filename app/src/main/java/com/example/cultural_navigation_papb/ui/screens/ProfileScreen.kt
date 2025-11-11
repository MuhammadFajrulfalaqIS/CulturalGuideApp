// File: ui/screens/ProfileScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    // Nanti kita akan tambahkan parameter untuk ViewModel dan Navigasi
    // onSignOut: () -> Unit,
    // onChangePassword: () -> Unit,
    // userEmail: String?
) {
    // --- Data Placeholder untuk Desain ---
    // Nanti ini akan diganti dengan data dari Firebase ViewModel
    val userEmail = "muhammad.fajrulfalaq@example.com"
    // ------------------------------------

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profil Saya") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Avatar Pengguna
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar Profil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Info Card Email (Sesuai permintaan Anda)
            InfoCard(
                icon = Icons.Default.Email,
                label = "Email Pengguna",
                value = userEmail // Ini akan diambil dari Firebase Auth
            )

            // 3. Tombol Aksi
            Spacer(modifier = Modifier.weight(1f)) // Mendorong tombol ke bawah

            // Tombol Ubah Password (Sesuai permintaan Anda)
            OutlinedButton(
                onClick = { /* TODO: Navigasi ke layar ubah password */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Ubah Password")
            }

            // Tombol Log Out
            Button(
                onClick = { /* TODO: Panggil fungsi signOut dari ViewModel */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Log Out")
            }
        }
    }
}

/**
 * Composable kustom untuk menampilkan sebaris info
 */
@Composable
private fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


// ------------------------------------
// --- PREVIEW UNTUK DESAIN ---
// ------------------------------------

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ProfileScreenPreview() {
    CulturalnavigationpapbTheme {
        ProfileScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun InfoCardPreview() {
    CulturalnavigationpapbTheme {
        InfoCard(
            icon = Icons.Default.Email,
            label = "Email",
            value = "email.anda@example.com",
            modifier = Modifier.padding(16.dp)
        )
    }
}
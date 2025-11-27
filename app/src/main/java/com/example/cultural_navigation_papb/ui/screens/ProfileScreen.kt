package com.example.cultural_navigation_papb.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.cultural_navigation_papb.data.viewmodels.AuthViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignOutSuccess: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    // Image Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                // Saat foto dipilih, langsung simpan ke database via ViewModel
                // Nama user dikirim ulang agar tidak hilang
                user?.name?.let { viewModel.updateProfile(it, uri) }
            }
        }
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profil Saya") }) }
    ) { paddingValues ->
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Loading state
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Avatar Pengguna dengan Tombol Edit
                Box(contentAlignment = Alignment.BottomEnd) {
                    val imagePainter = if (user?.profileImagePath != null) {
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(File(user!!.profileImagePath!!))
                                .build()
                        )
                    } else {
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data("https://ui-avatars.com/api/?name=${user?.name}&background=random")
                                .build()
                        )
                    }

                    Image(
                        painter = imagePainter,
                        contentDescription = "Avatar Profil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable {
                                // Buka galeri saat foto diklik
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    )

                    // Icon Edit Kecil di pojok foto
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Ubah Foto",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(6.dp)
                            .size(16.dp)
                    )
                }

                Text(
                    text = user?.name ?: "Nama Pengguna",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Info Cards
                InfoCard(Icons.Default.Email, "Email", user?.email ?: "")
                // Tampilkan path/lokasi foto tersimpan (opsional, untuk debug)
                // InfoCard(Icons.Default.Image, "Lokasi Foto", user?.profileImagePath ?: "Belum ada foto")

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Tombol Aksi
                OutlinedButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.padding(end = 8.dp))
                    Text("Edit Profil (Nama)")
                }

                OutlinedButton(
                    onClick = { showPasswordDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, null, modifier = Modifier.padding(end = 8.dp))
                    Text("Ubah Password")
                }

                Button(
                    onClick = { viewModel.signOut { onSignOutSuccess() } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.ExitToApp, null, modifier = Modifier.padding(end = 8.dp))
                    Text("Log Out")
                }
            }
        }
    }

    // --- Dialog Edit Nama ---
    if (showEditDialog) {
        var newName by remember { mutableStateOf(user?.name ?: "") }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profil") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nama Lengkap") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateProfile(newName, null) // Update nama saja, foto null
                    showEditDialog = false
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Batal") }
            }
        )
    }

    // --- Dialog Ubah Password ---
    if (showPasswordDialog) {
        var newPass by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Ubah Password") },
            text = {
                Column {
                    Text("Masukkan password baru Anda.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("Password Baru") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newPass.isNotEmpty()) {
                        viewModel.updatePassword(newPass) { showPasswordDialog = false }
                    }
                }) { Text("Ubah") }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun InfoCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = label, style = MaterialTheme.typography.bodySmall)
                Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
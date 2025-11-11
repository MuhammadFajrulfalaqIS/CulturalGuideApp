// File: ui/screens/SignUpScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme

@Composable
fun SignUpScreen(
    // Aksi ini akan dipicu saat tombol "Sign Up" diklik
    onSignUp: (String, String) -> Unit,
    // Aksi untuk navigasi kembali ke layar login
    onNavigateToSignIn: () -> Unit
) {
    // State untuk menyimpan input pengguna
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Validasi sederhana
    val isPasswordMatch = password == confirmPassword

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Buat Akun Baru",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Silakan daftar untuk memulai",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Input Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = "Toggle password visibility")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Konfirmasi Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Konfirmasi Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(image, contentDescription = "Toggle password visibility")
                }
            },
            // Menampilkan error jika password tidak cocok
            isError = !isPasswordMatch,
            modifier = Modifier.fillMaxWidth()
        )
        if (!isPasswordMatch) {
            Text(
                text = "Password tidak cocok",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Sign Up
        Button(
            onClick = { onSignUp(email, password) },
            // Tombol dinonaktifkan jika password tidak cocok atau kolom kosong
            enabled = isPasswordMatch && email.isNotEmpty() && password.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol untuk ke Halaman Sign In
        TextButton(onClick = onNavigateToSignIn) {
            Text("Sudah punya akun? Sign In")
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SignUpScreenPreview() {
    CulturalnavigationpapbTheme {
        SignUpScreen(
            onSignUp = { _, _ -> }, // Lambda kosong untuk preview
            onNavigateToSignIn = {} // Lambda kosong untuk preview
        )
    }
}
package com.example.cultural_navigation_papb.data.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cultural_navigation_papb.data.database.AppDatabase
import com.example.cultural_navigation_papb.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDao = AppDatabase.getDatabase(application).userDao()

    // State user saat ini
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg = _errorMsg.asStateFlow()

    init {
        // Cek sesi login saat aplikasi dibuka pertama kali
        auth.currentUser?.uid?.let { uid ->
            startObservingUser(uid)
        }
    }

    // Fungsi pembantu untuk memantau data user secara real-time
    private fun startObservingUser(uid: String) {
        viewModelScope.launch {
            userDao.getUser(uid).collect { user ->
                _currentUser.value = user
            }
        }
    }

    fun signUp(email: String, pass: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Buat akun di Firebase
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val uid = result.user?.uid ?: throw Exception("Gagal mendapatkan UID")

                // 2. Simpan data profil ke Database Lokal (Room)
                val newUser = User(userId = uid, name = name, email = email)
                userDao.insertUser(newUser)

                // 3. Mulai pantau data
                startObservingUser(uid)

                // 4. Sukses
                onSuccess()
            } catch (e: Exception) {
                _errorMsg.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null // Reset error
            try {
                // 1. Login ke Firebase Authentication
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user ?: throw Exception("Login gagal")
                val uid = firebaseUser.uid

                // 2. Cek sinkronisasi data (PENTING: Jika login di HP baru)
                // Kita pakai .first() untuk cek sekali saja tanpa blocking selamanya
                val localUser = userDao.getUser(uid).first()

                if (localUser == null) {
                    // Jika login sukses tapi data tidak ada di HP ini (misal install ulang)
                    // Maka buat data lokal baru berdasarkan info Firebase
                    val syncedUser = User(
                        userId = uid,
                        name = firebaseUser.displayName ?: "User", // Pakai nama default jika kosong
                        email = email
                    )
                    userDao.insertUser(syncedUser)
                }

                // 3. Mulai pantau data user di background
                startObservingUser(uid)

                // 4. PINDAH LAYAR (Panggil onSuccess sekarang, jangan tunggu collect selesai)
                onSuccess()

            } catch (e: Exception) {
                _errorMsg.value = "Login Gagal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        _currentUser.value = null
        onComplete()
    }

    fun updateProfile(name: String, imageUri: Uri?) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            try {
                var imagePath = user.profileImagePath

                if (imageUri != null) {
                    val context = getApplication<Application>()
                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    val file = File(context.filesDir, "profile_${user.userId}.jpg")
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    imagePath = file.absolutePath
                    inputStream?.close()
                    outputStream.close()
                }

                val updatedUser = user.copy(name = name, profileImagePath = imagePath)
                userDao.updateUser(updatedUser)

                // Update juga Display Name di Firebase (Opsional tapi bagus)
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                auth.currentUser?.updateProfile(profileUpdates)?.await()

            } catch (e: Exception) {
                _errorMsg.value = "Gagal update profil: ${e.message}"
            }
        }
    }

    fun updatePassword(newPass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.currentUser?.updatePassword(newPass)?.await()
                onSuccess()
            } catch (e: Exception) {
                _errorMsg.value = "Gagal ganti password. Coba Login ulang."
            }
        }
    }
}
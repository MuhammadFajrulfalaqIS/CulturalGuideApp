package com.example.cultural_navigation_papb.data.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cultural_navigation_papb.data.database.AppDatabase
import com.example.cultural_navigation_papb.data.models.User
import com.example.cultural_navigation_papb.fcm.FCMNotificationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val fcmNotificationManager: FCMNotificationManager
) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDao = AppDatabase.getDatabase(application).userDao()

    // State user saat ini
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg = _errorMsg.asStateFlow()

    // PENTING: State untuk menandai login berhasil (untuk trigger navigasi dari UI)
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

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
            _errorMsg.value = null
            try {
                Log.d("AuthViewModel", "Starting signup...")

                // 1. Buat akun di Firebase
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val uid = result.user?.uid ?: throw Exception("Gagal mendapatkan UID")

                Log.d("AuthViewModel", "Firebase signup successful, uid: $uid")

                // 2. Simpan data profil ke Database Lokal (Room)
                val newUser = User(id = uid, name = name, email = email)
                userDao.insertUser(newUser)

                Log.d("AuthViewModel", "User saved to local database")

                // 3. Mulai pantau data
                startObservingUser(uid)

                Log.d("AuthViewModel", "Observer started")

                // 4. Set loading false SEBELUM callback
                _isLoading.value = false

                // 5. Callback untuk navigasi
                Log.d("AuthViewModel", "Calling onSuccess callback...")
                onSuccess()

                Log.d("AuthViewModel", "Signup complete!")

            } catch (e: Exception) {
                _errorMsg.value = e.message
                _isLoading.value = false
                Log.e("AuthViewModel", "Signup failed", e)
            }
        }
    }

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            _loginSuccess.value = false

            try {
                Log.d("AuthViewModel", "Starting login process...")

                // 1. Login ke Firebase Authentication
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user ?: throw Exception("Login gagal")
                val uid = firebaseUser.uid

                Log.d("AuthViewModel", "Firebase login successful, uid: $uid")

                // 2. Cek sinkronisasi data (PENTING: Jika login di HP baru)
                val localUser = userDao.getUser(uid).first()

                if (localUser == null) {
                    Log.d("AuthViewModel", "Creating local user data...")
                    // Jika login sukses tapi data tidak ada di HP ini
                    val syncedUser = User(
                        id = uid,
                        name = firebaseUser.displayName ?: "User",
                        email = email
                    )
                    userDao.insertUser(syncedUser)
                    Log.d("AuthViewModel", "Local user created")
                } else {
                    Log.d("AuthViewModel", "Local user exists: ${localUser.name}")
                }

                // 3. Mulai pantau data user di background
                startObservingUser(uid)

                Log.d("AuthViewModel", "Observer started")

                // 4. PENTING: Set loading false SEBELUM mengubah success state
                _isLoading.value = false

                // 5. Set success flag untuk trigger navigasi di UI
                _loginSuccess.value = true

                Log.d("AuthViewModel", "Login complete, loginSuccess set to true")

            } catch (e: Exception) {
                _errorMsg.value = "Login Gagal: ${e.message}"
                _isLoading.value = false
                _loginSuccess.value = false
                Log.e("AuthViewModel", "Login failed: ${e.message}", e)
            }
        }
    }

    // Reset login success state
    fun resetLoginSuccess() {
        _loginSuccess.value = false
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
                    val file = File(context.filesDir, "profile_${user.id}.jpg")
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
package com.example.cultural_navigation_papb.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.example.cultural_navigation_papb.data.converters.Converters

// File: data/models/Place.kt

@Entity(tableName = "places")
@TypeConverters(Converters::class)
data class Place(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val category: String, // "candi_utama", "candi_perwara", "others"
    val isAvailable: Boolean = true,
    val openTime: String = "06:00",
    val closeTime: String = "17:00",
    val ticketPrice: Int = 0,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0
) {
    // Helper untuk mendapatkan LatLng
    fun getLatLng(): LatLng = LatLng(latitude, longitude)

    // Helper untuk status buka/tutup
    fun isOpen(): Boolean {
        return isAvailable // Untuk sederhana, bisa diperluas dengan cek waktu
    }
}

// Data Placeholder untuk Carousel
val prambananHighlights = listOf(
    Place(
        id = "1",
        name = "Candi Siwa",
        description = "Candi utama dan tertinggi yang didedikasikan untuk Dewa Siwa.",
        imageUrl = "https://example.com/siwa.jpg", // Ganti dengan URL riil nanti
        latitude = -7.7520,
        longitude = 110.4891,
        category = "candi_utama"
    ),
    Place(
        id = "2",
        name = "Candi Wisnu",
        description = "Berada di utara, didedikasikan untuk Dewa Wisnu, sang pemelihara.",
        imageUrl = "https://example.com/wisnu.jpg",
        latitude = -7.7515,
        longitude = 110.4896,
        category = "candi_utama"
    ),
    Place(
        id = "3",
        name = "Candi Brahma",
        description = "Berada di selatan, didedikasikan untuk Dewa Brahma, sang pencipta.",
        imageUrl = "https://example.com/brahma.jpg",
        latitude = -7.7525,
        longitude = 110.4886,
        category = "candi_utama"
    )
)

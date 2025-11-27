package com.example.cultural_navigation_papb.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_places")
data class SavedPlace(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val imageResId: Int, // Kita simpan ID gambar (Drawable)
    val downloadedAt: Long = System.currentTimeMillis()
)
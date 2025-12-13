package com.example.cultural_navigation_papb.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk menyimpan narasi audio guide yang sudah di-generate
 * Cache narration untuk offline mode dan mengurangi API calls
 */
@Entity(tableName = "narrations")
data class Narration(
    @PrimaryKey
    val placeId: String,
    val placeName: String,
    val narrationText: String,
    val language: String = "id", // "id" untuk Indonesian, "en" untuk English
    val generatedAt: Long = System.currentTimeMillis(),
    val duration: Int = 0, // Estimasi durasi dalam detik
    val lastPlayedPosition: Int = 0, // Untuk continue from last position
    val isDownloaded: Boolean = false // Untuk offline mode
)


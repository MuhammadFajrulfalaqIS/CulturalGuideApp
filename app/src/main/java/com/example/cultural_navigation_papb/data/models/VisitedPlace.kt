package com.example.cultural_navigation_papb.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for tracking downloaded/offline places
 * Simplified version for inbox functionality
 */
@Entity(tableName = "visited_places")
data class VisitedPlace(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val visitDate: Long = System.currentTimeMillis()
)
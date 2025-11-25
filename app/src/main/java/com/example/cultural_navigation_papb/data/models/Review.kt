package com.example.cultural_navigation_papb.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

/**
 * Entity untuk review/tempat wisata
 * Foreign Key ke Place entity
 */
@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = Place::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Review(
    @PrimaryKey
    val id: String,
    val placeId: String,
    val userId: String,
    val userName: String,
    val rating: Float, // 1.0 - 5.0
    val comment: String,
    val timestamp: Long = System.currentTimeMillis(),
    val helpfulCount: Int = 0
)
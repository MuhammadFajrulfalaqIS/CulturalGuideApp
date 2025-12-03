package com.example.cultural_navigation_papb.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverters
import com.example.cultural_navigation_papb.data.converters.Converters

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
    ],
    indices = [
        Index(value = ["placeId", "userId"])
    ]
)
@TypeConverters(Converters::class)
data class Review(
    @PrimaryKey
    val id: String,
    val placeId: String,
    val userId: String,
    val userName: String,
    val userPhoto: String = "", // User profile photo URL
    val rating: Float, // 1.0 - 5.0
    val comment: String,
    val photos: List<String> = emptyList(), // Firebase Storage URLs
    val timestamp: Long = System.currentTimeMillis(),
    val helpfulCount: Int = 0
)
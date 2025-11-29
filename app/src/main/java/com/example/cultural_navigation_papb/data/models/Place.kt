package com.example.cultural_navigation_papb.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.cultural_navigation_papb.data.converters.Converters
import com.google.android.gms.maps.model.LatLng

// File: data/models/Place.kt

@Entity(tableName = "places")
@TypeConverters(Converters::class)
data class Place(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: Int,
    val detailedDescription: String = description, // Full detailed description for detail screen
    val historicalInfo: String = "", // Historical background
    val architectureInfo: String = "", // Architectural details
    val visitingInfo: String = "", // Visiting hours, tips, etc.
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: String = "",
    val isAvailable: Boolean = true,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0
) {
    // Helper function to get LatLng for maps
    fun getLatLng(): LatLng = LatLng(latitude, longitude)

    // Helper function to check if place is open
    fun isOpen(): Boolean = isAvailable
}

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
    val reviewCount: Int = 0,
    // Geofencing fields
    val isVisited: Boolean = false,
    val visitCount: Int = 0,
    val lastVisitedAt: Long? = null,
    val geofenceRadius: Float = 75f // Default 75 meters radius
) {
    // Helper function to get LatLng for maps
    fun getLatLng(): LatLng = LatLng(latitude, longitude)

    // Helper function to check if place is open
    fun isOpen(): Boolean = isAvailable

    // Helper function to mark place as visited
    fun markAsVisited(): Place {
        return this.copy(
            isVisited = true,
            visitCount = visitCount + 1,
            lastVisitedAt = System.currentTimeMillis()
        )
    }

    // Helper function to get geofence info
    fun getGeofenceInfo(): GeofenceInfo {
        return GeofenceInfo(
            id = id,
            latitude = latitude,
            longitude = longitude,
            radius = geofenceRadius
        )
    }
}

// Geofence data class for geofence management
data class GeofenceInfo(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float = 30f,
    val transitionTypes: Int = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER,
    val expirationDuration: Long = com.google.android.gms.location.Geofence.NEVER_EXPIRE
)

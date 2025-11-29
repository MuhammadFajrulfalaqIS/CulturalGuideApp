package com.example.cultural_navigation_papb.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

/**
 * Utility class for geofencing and location calculations
 * Provides methods to detect when user is within range of a location
 */
object GeofencingUtils {

    // Constants for geofencing
    const val DEFAULT_VISIT_RADIUS = 50.0 // 50 meters default
    const val MIN_VISIT_DURATION = 30000L // 30 seconds in milliseconds
    const val LOCATION_UPDATE_INTERVAL = 5000L // 5 seconds in milliseconds

    /**
     * Calculate distance between two LatLng points using Haversine formula
     * @param latLng1 First point
     * @param latLng2 Second point
     * @return Distance in meters
     */
    fun calculateDistance(latLng1: LatLng, latLng2: LatLng): Float {
        val lat1 = Math.toRadians(latLng1.latitude)
        val lon1 = Math.toRadians(latLng1.longitude)
        val lat2 = Math.toRadians(latLng2.latitude)
        val lon2 = Math.toRadians(latLng2.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) +
                cos(lat1) * cos(lat2) *
                sin(dLon / 2).pow(2)

        val c = 2 * asin(sqrt(a))

        return (6371000.0 * c).toFloat() // Earth's radius in meters, convert to Float
    }

    /**
     * Check if user is within specified radius of a location
     * @param userLocation Current user location
     * @param targetLocation Target location to check
     * @param radiusInMeters Radius to check (default 50m)
     * @return true if user is within radius
     */
    fun isUserWithinRadius(
        userLocation: LatLng,
        targetLocation: LatLng,
        radiusInMeters: Double = DEFAULT_VISIT_RADIUS
    ): Boolean {
        return calculateDistance(userLocation, targetLocation) <= radiusInMeters
    }

    /**
     * Get all places within radius of user location
     * @param userLocation Current user location
     * @param places List of places to check
     * @param radiusInMeters Radius to check (default 50m)
     * @return List of places within radius
     */
    fun getPlacesWithinRadius(
        userLocation: LatLng,
        places: List<com.example.cultural_navigation_papb.data.models.Place>,
        radiusInMeters: Double = DEFAULT_VISIT_RADIUS
    ): List<com.example.cultural_navigation_papb.data.models.Place> {
        return places.filter { place ->
            isUserWithinRadius(userLocation, place.getLatLng(), radiusInMeters)
        }
    }

    /**
     * Check if user has been at location for minimum duration
     * @param entryTime Time when user entered the location
     * @param currentTime Current time
     * @param minDuration Minimum duration required (default 30 seconds)
     * @return true if user has been there for minimum duration
     */
    fun hasMinimumStayDuration(
        entryTime: Long,
        currentTime: Long = System.currentTimeMillis(),
        minDuration: Long = MIN_VISIT_DURATION
    ): Boolean {
        return (currentTime - entryTime) >= minDuration
    }

    /**
     * Calculate visit duration in a readable format
     * @param startTimeMs Visit start time in milliseconds
     * @param endTimeMs Visit end time in milliseconds (default: current time)
     * @return Formatted duration string (e.g., "2 menit 30 detik")
     */
    fun formatVisitDuration(
        startTimeMs: Long,
        endTimeMs: Long = System.currentTimeMillis()
    ): String {
        val durationMs = endTimeMs - startTimeMs
        val durationSeconds = durationMs / 1000

        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60

        return when {
            hours > 0 -> "${hours} jam ${minutes} menit"
            minutes > 0 -> "${minutes} menit ${seconds} detik"
            else -> "${seconds} detik"
        }
    }

    /**
     * Get battery-friendly location update interval based on accuracy
     * @param accuracy Desired accuracy in meters
     * @return Update interval in milliseconds
     */
    fun getBatteryFriendlyUpdateInterval(accuracy: Int): Long {
        return when {
            accuracy < 10 -> 2000L // High accuracy: 2 seconds
            accuracy < 50 -> 5000L // Medium accuracy: 5 seconds
            else -> 10000L // Low accuracy: 10 seconds
        }
    }

    /**
     * Calculate the midpoint between two LatLng points
     * @param latLng1 First point
     * @param latLng2 Second point
     * @return Midpoint
     */
    fun calculateMidpoint(latLng1: LatLng, latLng2: LatLng): LatLng {
        val lat = (latLng1.latitude + latLng2.latitude) / 2
        val lng = (latLng1.longitude + latLng2.longitude) / 2
        return LatLng(lat, lng)
    }

    /**
     * Check if a location is valid (not 0,0 and within reasonable bounds)
     * @param location Location to validate
     * @return true if location is valid
     */
    fun isValidLocation(location: LatLng): Boolean {
        return location.latitude != 0.0 &&
                location.longitude != 0.0 &&
                location.latitude in -90.0..90.0 &&
                location.longitude in -180.0..180.0
    }

    /**
     * Get optimal zoom level for displaying route
     * @param routePoints List of points in the route
     * @return Zoom level (2.0 - 21.0)
     */
    fun getOptimalZoomForRoute(routePoints: List<LatLng>): Float {
        if (routePoints.isEmpty()) return 15.0f

        val lats = routePoints.map { it.latitude }
        val lngs = routePoints.map { it.longitude }

        val minLat = lats.minOrNull() ?: -7.75
        val maxLat = lats.maxOrNull() ?: -7.75
        val minLng = lngs.minOrNull() ?: 110.49
        val maxLng = lngs.maxOrNull() ?: 110.49

        val latDiff = maxLat - minLat
        val lngDiff = maxLng - minLng

        // Approximate zoom calculation
        val maxDiff = maxOf(latDiff, lngDiff)
        return when {
            maxDiff < 0.001 -> 18.0f
            maxDiff < 0.005 -> 16.0f
            maxDiff < 0.01 -> 15.0f
            maxDiff < 0.05 -> 14.0f
            else -> 13.0f
        }
    }
}
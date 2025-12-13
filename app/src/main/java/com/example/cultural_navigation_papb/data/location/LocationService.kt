package com.example.cultural_navigation_papb.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

/**
 * Service untuk tracking lokasi user dan menghitung jarak ke tempat wisata
 */
@Singleton
class LocationService @Inject constructor(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Mendapatkan lokasi user saat ini
     */
    suspend fun getCurrentLocation(): Location? {
        return try {
            if (!hasLocationPermission()) {
                return null
            }
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            android.util.Log.e("LocationService", "Error getting location: ${e.message}")
            null
        }
    }

    /**
     * Stream lokasi user secara real-time
     */
    fun getLocationUpdates(intervalMillis: Long = 10000): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close()
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMillis
        ).apply {
            setMinUpdateIntervalMillis(5000)
            setWaitForAccurateLocation(false)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    trySend(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    /**
     * Menghitung jarak antara dua koordinat menggunakan Haversine Formula
     * @return jarak dalam meter
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val earthRadius = 6371000.0 // meter

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return (earthRadius * c).toFloat()
    }

    /**
     * Check apakah user berada dalam radius tertentu dari koordinat
     */
    suspend fun isWithinRadius(
        targetLat: Double,
        targetLon: Double,
        radiusMeters: Float = 100f
    ): Boolean {
        val currentLocation = getCurrentLocation() ?: return false
        val distance = calculateDistance(
            currentLocation.latitude,
            currentLocation.longitude,
            targetLat,
            targetLon
        )
        return distance <= radiusMeters
    }

    /**
     * Format jarak untuk ditampilkan ke user
     */
    fun formatDistance(distanceMeters: Float): String {
        return when {
            distanceMeters < 1000 -> "${distanceMeters.toInt()} m"
            else -> "%.1f km".format(distanceMeters / 1000)
        }
    }

    /**
     * Check permission
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val DEFAULT_GEOFENCE_RADIUS = 100f // meter
        const val NOTIFICATION_RADIUS = 100f // meter untuk trigger notifikasi
    }
}


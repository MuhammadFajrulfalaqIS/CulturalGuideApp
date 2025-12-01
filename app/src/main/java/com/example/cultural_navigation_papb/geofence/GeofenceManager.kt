package com.example.cultural_navigation_papb.geofence

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.cultural_navigation_papb.data.models.Place
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    companion object {
        private const val TAG = "GeofenceManager"
        private const val GEOFENCE_PENDING_INTENT_REQUEST_CODE = 1001
        private const val DEFAULT_RADIUS = 30f // 50 meters radius
        private const val GEOFENCE_EXPIRATION = Geofence.NEVER_EXPIRE
    }

    @SuppressLint("MissingPermission")
    fun registerGeofences(
        places: List<Place>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "=== Starting Geofence Registration ===")
        Log.d(TAG, "Total places received: ${places.size}")

        // Filter out visited places
        val unvisitedPlaces = places.filter { !it.isVisited }
        Log.d(TAG, "Unvisited places: ${unvisitedPlaces.size}")

        if (unvisitedPlaces.isEmpty()) {
            Log.d(TAG, "No unvisited places to register geofences for")
            onSuccess()
            return
        }

        // Check permissions
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Location permission not granted")
            onError("Location permission not granted")
            return
        }

        val geofences = createGeofences(unvisitedPlaces)
        Log.d(TAG, "Created ${geofences.size} geofence objects")

        val geofencingRequest = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofences)
        }.build()

        val geofencePendingIntent = getGeofencePendingIntent()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "✅ Successfully registered ${geofences.size} geofences")
                unvisitedPlaces.forEach { place ->
                    Log.d(TAG, "  - ${place.name} at (${place.latitude}, ${place.longitude}) radius: ${place.geofenceRadius}m")
                }
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to register geofences: ${e.message}", e)
                onError("Failed to register geofences: ${e.message}")
            }
    }

    fun removeGeofences(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "Removing all geofences")
        val geofencePendingIntent = getGeofencePendingIntent()

        geofencingClient.removeGeofences(geofencePendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "✅ Successfully removed all geofences")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to remove geofences: ${e.message}", e)
                onError("Failed to remove geofences: ${e.message}")
            }
    }

    private fun createGeofences(places: List<Place>): List<Geofence> {
        return places.map { place ->
            val radius = if (place.geofenceRadius > 0) place.geofenceRadius else DEFAULT_RADIUS

            Log.d(TAG, "Creating geofence for: ${place.name}")
            Log.d(TAG, "  ID: ${place.id}")
            Log.d(TAG, "  Location: (${place.latitude}, ${place.longitude})")
            Log.d(TAG, "  Radius: ${radius}m")

            Geofence.Builder()
                .setRequestId(place.id)
                .setCircularRegion(
                    place.latitude,
                    place.longitude,
                    radius
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(0) // Trigger immediately on enter
                .build()
        }
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            GEOFENCE_PENDING_INTENT_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}
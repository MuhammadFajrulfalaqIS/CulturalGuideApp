package com.example.cultural_navigation_papb.geofence

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.cultural_navigation_papb.R
import com.example.cultural_navigation_papb.data.database.AppDatabase
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceReceiver"
        private const val CHANNEL_ID = "geofence_notifications"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive called with action: ${intent.action}")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null) {
            Log.e(TAG, "GeofencingEvent is null")
            return
        }

        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Geofencing error: ${geofencingEvent.errorCode}")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        Log.d(TAG, "Geofence transition type: $geofenceTransition")

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                val triggeredGeofences = geofencingEvent.triggeringGeofences
                Log.d(TAG, "ENTER transition detected: ${triggeredGeofences?.size} geofences triggered")

                triggeredGeofences?.forEach { geofence ->
                    Log.d(TAG, "Processing geofence: ${geofence.requestId}")
                    handleGeofenceEnter(context, geofence.requestId)
                }
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.d(TAG, "EXIT transition detected")
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.d(TAG, "DWELL transition detected")
            }
            else -> {
                Log.w(TAG, "Unknown transition type: $geofenceTransition")
            }
        }
    }

    private fun handleGeofenceEnter(context: Context, placeId: String) {
        Log.d(TAG, ">>> Handling geofence ENTER for place: $placeId")

        // Create notification channel first
        createNotificationChannel(context)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val placeDao = database.placeDao()

                Log.d(TAG, "Fetching place from database: $placeId")
                val place = placeDao.getPlaceById(placeId)

                if (place == null) {
                    Log.e(TAG, "Place not found in database: $placeId")
                    return@launch
                }

                Log.d(TAG, "Place found: ${place.name}, isVisited: ${place.isVisited}")

                if (!place.isVisited) {
                    // Mark place as visited
                    val updatedPlace = place.copy(
                        isVisited = true,
                        visitCount = place.visitCount + 1
                    )
                    placeDao.updatePlace(updatedPlace)

                    Log.d(TAG, "✅ Successfully marked place as visited: ${updatedPlace.name}")

                    // ✅ FIX: Pass placeId instead of place.name
                    showNotification(context, placeId, place.name, "Selamat datang di ${place.name}! Jelajahi tempat bersejarah ini.")
                } else {
                    Log.d(TAG, "Place already visited: ${place.name}")
                    showNotification(context, placeId, place.name, "Selamat datang kembali di ${place.name}!")
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error handling geofence transition", e)
                e.printStackTrace()
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Geofence Notifications"
            val descriptionText = "Notifications when you enter a cultural site"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d(TAG, "Notification channel created")
        }
    }

    private fun showNotification(context: Context, placeId: String, placeName: String, message: String) {
        try {
            // ✅ CREATE DEEP LINK INTENT TO OPEN DETAIL SCREEN WITH REVIEW DIALOG
            val intent = Intent(context, com.example.cultural_navigation_papb.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                action = "OPEN_REVIEW_DIALOG"
                putExtra("placeId", placeId) // ✅ Pass correct placeId like "candi_siwa"
                putExtra("openReviewDialog", true)
            }

            val pendingIntent = android.app.PendingIntent.getActivity(
                context,
                placeId.hashCode(),
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_temple)
                .setContentTitle("📍 $placeName")
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(
                    "$message\n\n✍️ Bagikan pengalaman Anda dan bantu pengunjung lain!"
                ))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 250, 500))
                .setContentIntent(pendingIntent)
                .addAction(
                    R.drawable.ic_review,
                    "Tulis Review",
                    pendingIntent
                )

            val notificationManager = NotificationManagerCompat.from(context)

            // Check permission for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(placeId.hashCode(), builder.build())
                    Log.d(TAG, "✅ Notification shown with review action: $placeName (ID: $placeId)")
                } else {
                    Log.w(TAG, "⚠️ Notification permission not granted")
                }
            } else {
                notificationManager.notify(placeId.hashCode(), builder.build())
                Log.d(TAG, "✅ Notification shown: $placeName")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error showing notification", e)
        }
    }
}

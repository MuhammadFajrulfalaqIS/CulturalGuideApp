package com.example.cultural_navigation_papb.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.cultural_navigation_papb.MainActivity
import com.example.cultural_navigation_papb.R
import com.example.cultural_navigation_papb.data.database.AppDatabase
import com.example.cultural_navigation_papb.data.models.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CulturalGuideFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val GEOFENCE_CHANNEL_ID = "geofence_notifications"
        private const val REVIEW_CHANNEL_ID = "review_notifications"
        private const val GEOFENCE_CHANNEL_NAME = "Destination Arrivals"
        private const val REVIEW_CHANNEL_NAME = "Review Reminders"
    }

    @Inject
    lateinit var database: AppDatabase

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "FCM message received: ${remoteMessage.data}")

        // Handle notification with data payload
        remoteMessage.data?.let { data ->
            val placeId = data["placeId"] ?: data["destinationId"]
            val type = data["type"]

            when (type) {
                "geofence_enter" -> {
                    placeId?.let {
                        sendGeofenceNotification(it)
                    }
                }
                "review_reminder" -> {
                    placeId?.let {
                        sendReviewReminderNotification(it)
                    }
                }
                else -> {
                    Log.d(TAG, "Unknown notification type: $type")
                }
            }
        }

        // Also handle notification payload if present
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            // You can handle this as well if needed
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token refreshed: $token")

        // Send token to server or store in Firestore
        sendTokenToServer(token)
    }

    private fun sendGeofenceNotification(placeId: String) {
        Log.d(TAG, "Sending geofence notification for place: $placeId")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val place = database.placeDao().getPlaceById(placeId)

                place?.let {
                    // Create deep link intent
                    val intent = Intent(this@CulturalGuideFirebaseMessagingService, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        action = "REVIEW_DESTINATION"
                        putExtra("placeId", placeId)
                        data = android.net.Uri.parse("culturalguide://review/$placeId")
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        this@CulturalGuideFirebaseMessagingService,
                        placeId.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val notification = NotificationCompat.Builder(
                        this@CulturalGuideFirebaseMessagingService,
                        GEOFENCE_CHANNEL_ID
                    )
                        .setSmallIcon(R.drawable.ic_temple)
                        .setContentTitle("You've arrived at ${place.name}!")
                        .setContentText("Share your experience and help others discover this place.")
                        .setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText(
                                    "${place.name} has amazing historical significance. " +
                                    "Tap to write a review and share your thoughts!"
                                )
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .addAction(
                            R.drawable.ic_review,
                            "Write Review",
                            pendingIntent
                        )
                        .build()

                    // Show notification
                    if (ContextCompat.checkSelfPermission(
                            this@CulturalGuideFirebaseMessagingService,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val notificationManager = NotificationManagerCompat.from(
                            this@CulturalGuideFirebaseMessagingService
                        )

                        notificationManager.notify(placeId.hashCode(), notification)
                        Log.d(TAG, "Geofence notification shown for ${place.name}")
                    } else {
                        Log.w(TAG, "POST_NOTIFICATIONS permission not granted")
                    }
                } ?: run {
                    Log.w(TAG, "Place not found for ID: $placeId")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error showing geofence notification", e)
            }
        }
    }

    private fun sendReviewReminderNotification(placeId: String) {
        Log.d(TAG, "Sending review reminder notification for place: $placeId")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val place = database.placeDao().getPlaceById(placeId)

                place?.let {
                    val intent = Intent(this@CulturalGuideFirebaseMessagingService, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        action = "REVIEW_DESTINATION"
                        putExtra("placeId", placeId)
                        data = android.net.Uri.parse("culturalguide://review/$placeId")
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        this@CulturalGuideFirebaseMessagingService,
                        "review_$placeId".hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val notification = NotificationCompat.Builder(
                        this@CulturalGuideFirebaseMessagingService,
                        REVIEW_CHANNEL_ID
                    )
                        .setSmallIcon(R.drawable.ic_review)
                        .setContentTitle("Review Your Visit")
                        .setContentText("How was your experience at ${place.name}?")
                        .setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText("Share your thoughts about ${place.name} to help other visitors plan their trip.")
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .addAction(
                            R.drawable.ic_review,
                            "Write Review",
                            pendingIntent
                        )
                        .build()

                    // Show notification
                    if (ContextCompat.checkSelfPermission(
                            this@CulturalGuideFirebaseMessagingService,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val notificationManager = NotificationManagerCompat.from(
                            this@CulturalGuideFirebaseMessagingService
                        )

                        notificationManager.notify("review_$placeId".hashCode(), notification)
                        Log.d(TAG, "Review reminder notification shown for ${place.name}")
                    }
                } ?: run {
                    Log.w(TAG, "Place not found for review reminder: $placeId")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error showing review reminder notification", e)
            }
        }
    }

    private fun sendTokenToServer(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Store token in Firestore for the current user
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .update("fcmToken", token)
                        .addOnSuccessListener {
                            Log.d(TAG, "FCM token updated in Firestore")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Failed to update FCM token in Firestore", e)
                        }

                    Log.d(TAG, "FCM token sent to server")
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending FCM token to server", e)
                }
            }
        } else {
            Log.w(TAG, "User not authenticated, not storing FCM token")
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val geofenceChannel = NotificationChannel(
                GEOFENCE_CHANNEL_ID,
                GEOFENCE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when arriving at cultural destinations"
                enableVibration(true)
                enableLights(true)
            }

            val reviewChannel = NotificationChannel(
                REVIEW_CHANNEL_ID,
                REVIEW_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to write reviews after visiting places"
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(geofenceChannel)
            notificationManager.createNotificationChannel(reviewChannel)

            Log.d(TAG, "Notification channels created")
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        Log.d(TAG, "FCM Service created")
    }
}
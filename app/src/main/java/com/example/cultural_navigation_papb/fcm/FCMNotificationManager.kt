package com.example.cultural_navigation_papb.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.cultural_navigation_papb.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "FCMNotificationManager"
        private const val WELCOME_CHANNEL_ID = "welcome_notifications"
        private const val GEOFENCE_CHANNEL_ID = "geofence_notifications"
        private const val REVIEW_CHANNEL_ID = "review_notifications"
        private const val GENERAL_CHANNEL_ID = "general_notifications"

        private const val PREFS_NAME = "fcm_prefs"
        private const val KEY_WELCOME_SHOWN = "welcome_notification_shown"
        private const val KEY_FCM_TOKEN = "fcm_token"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        createNotificationChannels()
    }

    /**
     * Setup FCM for a newly logged-in user
     * This should be called after successful login/registration
     */
    suspend fun setupFCMForNewUser() {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.w(TAG, "No user logged in, skipping FCM setup")
                return
            }

            // Get FCM token
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "FCM Token obtained: $token")

            // Save token to Firestore for server-side notifications
            saveTokenToFirestore(currentUser.uid, token)

            // Save token locally
            prefs.edit().putString(KEY_FCM_TOKEN, token).apply()

            // Show welcome notification if not shown before
            if (!hasShownWelcomeNotification()) {
                showWelcomeNotification(currentUser.displayName ?: "Traveler")
                markWelcomeNotificationShown()
            }

            // Subscribe to general topics
            subscribeToTopics()

            Log.d(TAG, "FCM setup completed for user: ${currentUser.uid}")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up FCM", e)
        }
    }

    /**
     * Save FCM token to Firestore for backend notifications
     */
    private suspend fun saveTokenToFirestore(userId: String, token: String) {
        try {
            val userDocRef = firestore.collection("users").document(userId)

            val tokenData = hashMapOf(
                "fcmToken" to token,
                "platform" to "android",
                "lastUpdated" to com.google.firebase.Timestamp.now()
            )

            userDocRef.update(tokenData as Map<String, Any>).await()
            Log.d(TAG, "FCM token saved to Firestore")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving token to Firestore", e)
            // Try to set if update fails (document doesn't exist)
            try {
                val userDocRef = firestore.collection("users").document(userId)
                userDocRef.set(
                    hashMapOf(
                        "fcmToken" to token,
                        "platform" to "android",
                        "lastUpdated" to com.google.firebase.Timestamp.now()
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()
            } catch (e2: Exception) {
                Log.e(TAG, "Error setting token to Firestore", e2)
            }
        }
    }

    /**
     * Subscribe to FCM topics for general notifications
     */
    private suspend fun subscribeToTopics() {
        try {
            // Subscribe to general app updates
            FirebaseMessaging.getInstance().subscribeToTopic("all_users").await()
            Log.d(TAG, "Subscribed to topic: all_users")

            // Subscribe to cultural updates
            FirebaseMessaging.getInstance().subscribeToTopic("cultural_updates").await()
            Log.d(TAG, "Subscribed to topic: cultural_updates")

            // Subscribe to new destinations
            FirebaseMessaging.getInstance().subscribeToTopic("new_destinations").await()
            Log.d(TAG, "Subscribed to topic: new_destinations")
        } catch (e: Exception) {
            Log.e(TAG, "Error subscribing to topics", e)
        }
    }

    /**
     * Show a personalized welcome notification
     */
    private fun showWelcomeNotification(userName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        try {
            val notification = NotificationCompat.Builder(context, WELCOME_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_temple)
                .setContentTitle("Welcome to Cultural Guide, $userName! 🎉")
                .setContentText("Discover amazing cultural destinations nearby")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "Hi $userName! 👋\n\n" +
                            "Start exploring cultural destinations around you. We'll notify you when you're near interesting places!\n\n" +
                            "• Get arrival notifications\n" +
                            "• Share your experiences\n" +
                            "• Discover hidden gems"
                        )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(WELCOME_CHANNEL_ID.hashCode(), notification)

            Log.d(TAG, "Welcome notification shown")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing welcome notification", e)
        }
    }

    /**
     * Create all notification channels
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Welcome Channel
            val welcomeChannel = NotificationChannel(
                WELCOME_CHANNEL_ID,
                "Welcome Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Welcome notifications for new users"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(welcomeChannel)

            // Geofence Channel
            val geofenceChannel = NotificationChannel(
                GEOFENCE_CHANNEL_ID,
                "Destination Arrivals",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when you arrive at cultural destinations"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(geofenceChannel)

            // Review Channel
            val reviewChannel = NotificationChannel(
                REVIEW_CHANNEL_ID,
                "Review Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to review places you've visited"
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(reviewChannel)

            // General Channel
            val generalChannel = NotificationChannel(
                GENERAL_CHANNEL_ID,
                "General Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app updates and announcements"
            }
            notificationManager.createNotificationChannel(generalChannel)

            Log.d(TAG, "Notification channels created")
        }
    }

    /**
     * Check if welcome notification has been shown
     */
    private fun hasShownWelcomeNotification(): Boolean {
        return prefs.getBoolean(KEY_WELCOME_SHOWN, false)
    }

    /**
     * Mark welcome notification as shown
     */
    private fun markWelcomeNotificationShown() {
        prefs.edit().putBoolean(KEY_WELCOME_SHOWN, true).apply()
    }

    /**
     * Reset welcome notification flag (for testing)
     */
    fun resetWelcomeNotification() {
        prefs.edit().putBoolean(KEY_WELCOME_SHOWN, false).apply()
    }

    /**
     * Get current FCM token
     */
    suspend fun getCurrentToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting FCM token", e)
            null
        }
    }

    /**
     * Update FCM token when it changes
     */
    suspend fun updateToken(newToken: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            saveTokenToFirestore(currentUser.uid, newToken)
            prefs.edit().putString(KEY_FCM_TOKEN, newToken).apply()
        }
    }
}

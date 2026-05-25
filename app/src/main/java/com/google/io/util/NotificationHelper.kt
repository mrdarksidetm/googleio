package com.google.io.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * TASK 3: Notification System
 * This helper class makes it easy to show alerts to the user.
 */
object NotificationHelper {
    private const val CHANNEL_ID = "session_alerts"
    private const val CHANNEL_NAME = "Session Alerts"

    /**
     * Required for Android 8.0 (Oreo) and above.
     * Channels let users turn off specific types of notifications 
     * (like just alerts) without silencing the whole app.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Reminders for bookmarked sessions"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Shows a simple notification.
     */
    fun showSessionNotification(context: Context, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Standard icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismiss when clicked

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // We use the current time as a unique ID so notifications don't overwrite each other
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}

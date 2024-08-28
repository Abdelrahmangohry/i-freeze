package com.ifreeze.applock.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ifreeze.applock.R

class NotificationHelper(private val context: Context) {
    // Lazy initialization of the notification builder, which constructs the notification
    private val notificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(context, "$NOTIFICATION_ID")
            // Set the title of the notification to the app name
            .setContentTitle(context.getString(R.string.app_name))
            // Disable sound for the notification
            .setSound(null)
            // Set the small icon for the notification
            .setSmallIcon(R.drawable.locked_icon)
            // Automatically remove the notification when the user taps on it
            .setAutoCancel(true)
    }

    // Lazy initialization of the notification manager, which handles displaying notifications
    private val notificationManager by lazy {
        // Retrieve the NotificationManager system service
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    // Function to get the constructed notification
    fun getNotification(): Notification {
        // For Android O and above, create a notification channel before sending a notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createChannel())
        }
        // Return the built notification
        return notificationBuilder.build()
    }

    // Function to update the notification text and display the updated notification
    fun updateNotification(notificationText: String? = null) {
        // If a notification text is provided, update the content text of the notification
        notificationText?.let { notificationBuilder.setContentText(it) }
        // Notify the system to update the notification with the new content
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
    // Function to create a notification channel (required for Android O and above)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() =
        NotificationChannel(
            "$NOTIFICATION_ID", // Channel ID, using the notification ID
            "CHANNEL_NAME",  // Channel name displayed to the user
            NotificationManager.IMPORTANCE_DEFAULT  // Importance level of the notification
        ).apply {
            // Set the description of the channel
            description = "CHANNEL_DESCRIPTION"
            // Disable sound for the channel
            setSound(null, null)
        }

    // Companion object to hold constants related to the NotificationHelper class
    companion object {
        // Constant representing the notification ID
        const val NOTIFICATION_ID = 99
    }
}
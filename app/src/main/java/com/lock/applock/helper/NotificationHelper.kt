package com.lock.applock.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.lock.applock.R

class NotificationHelper(private val context: Context) {

    private val notificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(context, "$NOTIFICATION_ID")
            .setContentTitle(context.getString(R.string.app_name))
            .setSound(null)
            .setSmallIcon(R.drawable.locked_icon)
            .setAutoCancel(true)
    }

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    fun getNotification(): Notification {
        Log.d("islam", "getNotification: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createChannel())
        }

        return notificationBuilder.build()
    }

    fun updateNotification(notificationText: String? = null) {
        notificationText?.let { notificationBuilder.setContentText(it) }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() =
        NotificationChannel(
            "$NOTIFICATION_ID",
            "CHANNEL_NAME",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "CHANNEL_DESCRIPTION"
            setSound(null, null)
        }

    companion object {
        const val NOTIFICATION_ID = 99
    }
}
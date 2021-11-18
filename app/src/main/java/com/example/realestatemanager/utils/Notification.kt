package com.example.realestatemanager.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.realestatemanager.R


class Notification constructor(context: Context) : ContextWrapper(context) {

    val CHANNEL_ID = "channel1"
    var notificationId = 1  // Unique ID for notif // Need to check


    fun buildNotif() {
        var builder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)  //CHANNEL ID for API>=26
                .setSmallIcon(R.drawable.ic_baseline_add_24) //TODO
                .setContentTitle("Adding of real estate")
                .setContentText("The addition of a real estate has been successfully completed")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("The addition of a real estate has been successfully completed")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
            notificationId++
        }
    }


    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
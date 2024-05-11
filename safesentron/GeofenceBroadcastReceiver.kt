package com.group9.safesentron

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) {
            logError(context, "Geofencing Error: ${geofencingEvent.errorCode}")
            return
        }

        when (val geofenceTransition = geofencingEvent?.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                notifyUser(context, "Geofence Entry Detected", "You have entered a designated area.")
                Log.d("GeofenceBroadcastReceiver", "Geofence Entry Triggered")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                notifyUser(context, "Geofence Exit Detected", "You have left a designated area.")
                Log.d("GeofenceBroadcastReceiver", "Geofence Exit Triggered")
            }
            else -> {
                logError(context, "Unknown geofence transition: $geofenceTransition")
            }
        }
    }

    private fun logError(context: Context, message: String) {
        Log.e("GeofenceBroadcastReceiver", message)
        notifyUser(context, "Geofence Error", message)
    }

    private fun notifyUser(context: Context, title: String, content: String) {
        val notificationBuilder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_notification) // Ensure you have this icon in your drawable resources
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(getNotificationId(), notificationBuilder.build())
    }

    private fun getNotificationId(): Int {
        // This can be more sophisticated, e.g., based on specific geofence ids or user actions
        return (System.currentTimeMillis() % 10000).toInt()
    }
}

package com.group9.safesentron

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.database.FirebaseDatabase

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val database = FirebaseDatabase.getInstance().reference  // Get a reference to the database

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
                updateDatabase("Enter", geofencingEvent.triggeringLocation)
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                notifyUser(context, "Geofence Exit Detected", "You have left a designated area.")
                Log.d("GeofenceBroadcastReceiver", "Geofence Exit Triggered")
                updateDatabase("Exit", geofencingEvent.triggeringLocation)
            }
            else -> {
                logError(context, "Unknown geofence transition: $geofenceTransition")
            }
        }
    }

    private fun updateDatabase(transitionType: String, location: Location?) {
        val entry = mapOf(
            "transition" to transitionType,
            "latitude" to location?.latitude,
            "longitude" to location?.longitude,
            "timestamp" to System.currentTimeMillis()
        )
        database.child("geofence_events").push().setValue(entry)
    }

    private fun logError(context: Context, message: String) {
        Log.e("GeofenceBroadcastReceiver", message)
        notifyUser(context, "Geofence Error", message)
    }

    private fun notifyUser(context: Context, title: String, content: String) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("GeofenceBroadcastReceiver", "Notification permission not granted")
            return
        }

        val notificationBuilder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(getNotificationId(), notificationBuilder.build())
    }

    private fun getNotificationId(): Int {
        return (System.currentTimeMillis() % 10000).toInt()
    }
}

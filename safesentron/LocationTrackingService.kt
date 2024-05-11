package com.group9.safesentron

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.google.android.gms.location.*

class LocationTrackingService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onBind(intent: Intent?): IBinder? {
        return null  // No binding provided
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.Builder(10000L)  // Interval set to 10 seconds
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    processLocation(location)
                }
            }
        }
        startLocationUpdates(locationRequest)
    }

    private fun startLocationUpdates(locationRequest: LocationRequest) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null  // Looper
                )
            } catch (e: SecurityException) {
                Log.e("LocationService", "Security Exception: ${e.message}")
            }
        } else {
            Log.e("LocationService", "Location permission not granted")
            // Optionally handle the situation when the permission is not granted
        }
    }

    private fun processLocation(location: Location) {
        Log.d("LocationService", "New location: Lat=${location.latitude}, Lon=${location.longitude}")
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

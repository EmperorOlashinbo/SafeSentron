package com.group9.safesentron

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.database.FirebaseDatabase
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient

class LocationTrackingService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val database = FirebaseDatabase.getInstance().reference  // Firebase database reference

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    if (ActivityCompat.checkSelfPermission(this@LocationTrackingService, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        updateLocationInFirebase(location)  // Update location in Firebase
                    } else {
                        Log.e("LocationService", "Location permission not granted")
                    }
                }
            }
        }

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(10000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            } else {
                Log.e("LocationService", "Location permission not granted")
            }
        } catch (e: SecurityException) {
            Log.e("LocationService", "Security Exception: ${e.message}")
        }
    }

    private fun updateLocationInFirebase(location: Location) {
        val locationData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis()
        )
        database.child("locations").push().setValue(locationData)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

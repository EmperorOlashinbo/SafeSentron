package com.group9.safesentron

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MapsActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isLocationPermissionGranted by mutableStateOf(false)
    private var currentPosition by mutableStateOf(LatLng(-34.0, 151.0)) // Default to Sydney

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                updateCurrentLocation()
            }
            isLocationPermissionGranted = isGranted
        }

        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLocationPermissionGranted -> {
                        MapScreen(currentPosition)
                    }
                    else -> {
                        Button(onClick = { locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
                            Text("Request location permission")
                        }
                    }
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            updateCurrentLocation()
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun updateCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentPosition = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }
}

@Composable
fun MapScreen(currentPosition: LatLng) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPosition, 12f)
    }

    LaunchedEffect(key1 = currentPosition) {
        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentPosition, 12f))
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = currentPosition),
            title = "Current Location",
            snippet = "You are here!"
        )
    }
}

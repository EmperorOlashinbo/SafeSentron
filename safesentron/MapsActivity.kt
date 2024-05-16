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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MapsActivity : ComponentActivity() {
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Update state based on permission result
        isLocationPermissionGranted = isGranted
    }

    // State to keep track of location permission status
    private var isLocationPermissionGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Initial permission check
            isLocationPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLocationPermissionGranted) {
                    MapScreen()
                } else {
                    Button(onClick = {
                        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }) {
                        Text("Request location permission")
                    }
                }
            }
        }
    }
}

@Composable
fun MapScreen() {
    val currentPosition by remember { mutableStateOf(LatLng(-34.0, 151.0)) } // Example coordinates
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPosition, 12f)
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

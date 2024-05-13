package com.group9.safesentron

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast

class PermissionCheckingActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startLocationService()
            } else {
                handlePermissionDenied()
            }
        }

        setContent {
            PermissionRequestScreen()
        }
    }

    @Composable
    fun PermissionRequestScreen() {
        val context = LocalContext.current
        Column {
            Button(onClick = { requestLocationPermission() }) {
                Text("Request Location Permission", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                Toast.makeText(context, "Location permission is necessary for this feature to work.", Toast.LENGTH_LONG).show()
            }) {
                Text("Show Info on Permission Status", fontSize = 18.sp)
            }
        }
    }

    private fun requestLocationPermission() {
        // This will automatically handle checking the permission and requesting if needed
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun startLocationService() {
        // Start your location service here
        startService(Intent(this, LocationTrackingService::class.java))
    }

    private fun handlePermissionDenied() {
        // Display a toast message indicating that the permission was denied
        Toast.makeText(this, "Location permission denied. Some features may not be available.", Toast.LENGTH_LONG).show()
    }
}

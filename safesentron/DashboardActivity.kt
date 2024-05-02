package com.group9.safesentron

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardScreen()
        }
    }
}

@Composable
fun DashboardScreen() {
    val user = FirebaseAuth.getInstance().currentUser
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (user != null) {
            // Displaying the user's email or a welcome message
            Text("Welcome, ${user.email ?: "User"}!")
        } else {
            // This text is shown if no user is found, which shouldn't happen but is good for error handling
            Text("No user is signed in.")
        }
    }
}

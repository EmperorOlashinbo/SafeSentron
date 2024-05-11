package com.group9.safesentron

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun UserProfileScreen() {
    val user = FirebaseAuth.getInstance().currentUser
    val email = remember { mutableStateOf(user?.email ?: "") }
    val displayName = remember { mutableStateOf(user?.displayName ?: "") }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }  // State to manage loading indicator

    Column(modifier = Modifier.padding(16.dp)) {
        Text("User Profile", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = displayName.value,
            onValueChange = { displayName.value = it },
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            enabled = false,  // Disable editing of the email field
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        Button(
            onClick = {
                isLoading = true
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName.value)
                    .build()
                user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Update failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = displayName.value.isNotBlank() && !isLoading  // Disable button when loading or if display name is blank
        ) {
            Text("Update Profile")
        }
    }
}

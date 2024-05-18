package com.group9.safesentron

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
    val context = LocalContext.current as Context
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var showSignOutDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (user != null) {
                Text("Welcome, ${user.email ?: "User"}!", modifier = Modifier.padding(bottom = 8.dp))
                Button(onClick = { showSignOutDialog = true }) {
                    Text("Sign Out")
                }
            } else {
                Text("No user is signed in.")
            }

            if (showSignOutDialog) {
                PasswordDialog(onDismiss = { showSignOutDialog = false }) { password ->
                    reauthenticateUser(user, password, context) {
                        auth.signOut()
                        showSignOutDialog = false
                        (context as? ComponentActivity)?.let {
                            it.startActivity(Intent(it, LoginActivity::class.java))
                            it.finish()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordDialog(onDismiss: () -> Unit, onPasswordConfirm: (String) -> Unit) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Confirm your password to sign out") },
        text = {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )
        },
        confirmButton = {
            Button(onClick = { onPasswordConfirm(password) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

fun reauthenticateUser(user: FirebaseUser?, password: String, context: Context, onSuccess: () -> Unit) {
    val email = user?.email ?: return  // Ensure email is not null
    val credential = EmailAuthProvider.getCredential(email, password)

    user.reauthenticate(credential).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            onSuccess()
        } else {
            Toast.makeText(context, "Password incorrect, please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}

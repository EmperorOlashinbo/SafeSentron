package com.group9.safesentron

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val context = LocalContext.current as Context
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var showSignOutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { showSignOutDialog = true }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserGreetingSection(user)
                QuickActionCard("Edit Profile", Icons.Filled.Edit) {
                    // Placeholder action
                }
                QuickActionCard("Settings", Icons.Filled.Settings) {
                    // Placeholder action
                }
                if (showSignOutDialog) {
                    PasswordDialog(onDismiss = { showSignOutDialog = false }) { password ->
                        reauthenticateUser(user, password, context) {
                            auth.signOut()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            (context as ComponentActivity).finish()
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun UserGreetingSection(user: FirebaseUser?) {
    user?.let {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Image(imageVector = Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(100.dp))
            Text("Welcome, ${user.email ?: "User"}!", fontWeight = FontWeight.Bold)
            Text("Last login: ${formatLastLogin(user.metadata?.lastSignInTimestamp)}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

fun formatLastLogin(timestamp: Long?): String {
    return timestamp?.let {
        val date = Date(it)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        format.format(date)
    } ?: "Recently"
}

@Composable
fun QuickActionCard(action: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(action)
        }
    }
}

@Composable
fun PasswordDialog(onDismiss: () -> Unit, onPasswordConfirm: (String) -> Unit) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm your password to sign out") },
        text = {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility")
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
            Button(onClick = onDismiss) {
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

package com.group9.safesentron

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginForm()
        }
    }
}

@Composable
fun LoginForm() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var loginStatus by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showPasswordResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_safesentron),
            contentDescription = "SafeSentron Logo",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (loginStatus.isNotEmpty()) {
            Text(loginStatus, color = if (loginStatus.startsWith("Login successful")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    isLoading = true
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            loginStatus = "Login successful"
                            context.startActivity(Intent(context, DashboardActivity::class.java))
                            (context as Activity).finish()
                        } else {
                            loginStatus = "Login failed: ${task.exception?.localizedMessage}"
                            Toast.makeText(context, loginStatus, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { showPasswordResetDialog = true }) {
            Text("Forgot password?", fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = {
                context.startActivity(Intent(context, SignUpActivity::class.java))
            }
        ) {
            Text("Don't have an account yet? Sign up here", fontWeight = FontWeight.Medium)
        }

        if (showPasswordResetDialog) {
            PasswordResetDialog(email = email, onDismiss = { showPasswordResetDialog = false }, onEmailChange = { email = it }, auth = auth)
        }
    }
}

@Composable
fun PasswordResetDialog(email: String, onDismiss: () -> Unit, onEmailChange: (String) -> Unit, auth: FirebaseAuth) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Password Reset") },
        text = {
            Column {
                Text("Enter your email address to reset your password.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Reset email sent", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                        onDismiss()
                    }
                }) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

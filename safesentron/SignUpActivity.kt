package com.group9.safesentron

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
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignUpForm()
        }
    }
}

@Composable
fun SignUpForm() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var ssn by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var zip by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var signUpStatus by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Sign Up", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        PasswordField("Password", password, onValueChange = { password = it }, passwordVisibility, onVisibilityChange = { passwordVisibility = it })
        Spacer(modifier = Modifier.height(8.dp))
        PasswordField("Confirm Password", confirmPassword, onValueChange = { confirmPassword = it }, confirmPasswordVisibility, onVisibilityChange = { confirmPasswordVisibility = it })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = ssn,
            onValueChange = { ssn = it },
            label = { Text("Social Security Number (SSN)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = zip,
            onValueChange = { zip = it },
            label = { Text("Zip Code") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (email.isNotEmpty() && password == confirmPassword && password.isNotEmpty()) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            signUpStatus = "Sign up successful. Redirecting..."
                            context.startActivity(Intent(context, DashboardActivity::class.java))
                            (context as ComponentActivity).finish()
                        } else {
                            signUpStatus = "Sign up failed: ${task.exception?.localizedMessage}"
                            Toast.makeText(context, "Signup Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    signUpStatus = "Check your inputs (passwords must match and not be empty)"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(signUpStatus, style = MaterialTheme.typography.bodyLarge, color = if (signUpStatus.startsWith("Sign up successful")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
    }
}

@Composable
fun PasswordField(
    label: String,
    password: String,
    onValueChange: (String) -> Unit,
    passwordVisibility: Boolean,
    onVisibilityChange: (Boolean) -> Unit
) {
    TextField(
        value = password,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onVisibilityChange(!passwordVisibility) }) {
                Icon(
                    imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                )
            }
        }
    )
}

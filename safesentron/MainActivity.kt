package com.group9.safesentron

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainLayout()
        }
    }
}

@Composable
fun MainLayout() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController, drawerState, scope)
        },
    ) {
        NavHost(navController = navController, startDestination = "dashboard") {
            composable("dashboard") { DashboardScreen() }
            composable("profile") { UserProfileScreen() }
            composable("location") { LocationTrackingScreen() }
            composable("settings") { SettingsScreen() }
            composable("mood_tracker") {
                val context = LocalContext.current
                context.startActivity(Intent(context, MoodTrackerActivity::class.java))
            }
            composable("logout") {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login")
            }
        }
    }
}

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    DrawerHeader()
    DrawerItem(icon = Icons.Filled.Person, label = "Profile", onClick = {
        navigateTo(navController, drawerState, scope, "profile")
    })
    DrawerItem(icon = Icons.Filled.Map, label = "Location Tracking", onClick = {
        navigateTo(navController, drawerState, scope, "location")
    })
    DrawerItem(icon = Icons.Filled.Mood, label = "Mood Tracker", onClick = {
        navigateTo(navController, drawerState, scope, "mood_tracker")
    })
    DrawerItem(icon = Icons.Filled.Settings, label = "Settings", onClick = {
        navigateTo(navController, drawerState, scope, "settings")
    })
    DrawerItem(icon = Icons.AutoMirrored.Filled.Logout, label = "Logout", onClick = {
        FirebaseAuth.getInstance().signOut()
        navController.navigate("login") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    })
}

fun navigateTo(navController: NavController, drawerState: DrawerState, scope: CoroutineScope, route: String) {
    scope.launch {
        drawerState.close()
        navController.navigate(route)
    }
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label)
        Spacer(Modifier.width(16.dp))
        Text(text = label)
    }
}

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logo_safesentron),
            contentDescription = "App Logo",
            modifier = Modifier.size(100.dp)
        )
        Text(text = "SafeSentron", style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 28.sp
        ))
    }
}
@Composable
fun LocationTrackingScreen() {
    Text("Location Tracking Screen", modifier = Modifier.fillMaxSize().padding(16.dp))
}

@Composable
fun SettingsScreen() {
    Text("Settings Screen", modifier = Modifier.fillMaxSize().padding(16.dp))
}

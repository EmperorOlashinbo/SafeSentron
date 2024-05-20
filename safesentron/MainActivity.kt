package com.group9.safesentron

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
        }
    ) {
        Scaffold(
            topBar = { TopBar(drawerState, scope) },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    NavHost(navController = navController, startDestination = "dashboard") {
                        composable("dashboard") {
                            DashboardScreen()
                        }
                        composable("profile") { UserProfileScreen() }
                        composable("location") { LocationTrackingScreen() }
                        composable("settings") { SettingsScreen() }
                        composable("mood_tracker") {
                            val context = LocalContext.current
                            context.startActivity(Intent(context, MoodTrackerActivity::class.java))
                        }
                        composable("gamified_learning") {
                            val context = LocalContext.current
                            context.startActivity(Intent(context, GamifiedLearningActivity::class.java))
                        }
                        composable("logout") {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(drawerState: DrawerState, scope: CoroutineScope) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_safesentron),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .height(40.dp)
                        .padding(8.dp)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.Black) // Ensure the icon color is visible
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White, // Ensure the top bar background color is set to white
            titleContentColor = Color.Black, // Ensure the title color is black
            navigationIconContentColor = Color.Black // Ensure the navigation icon color is black
        )
    )
}

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    Column(
        modifier = Modifier
            .width(250.dp) // Set a specific width for the drawer
            .background(Color.White)
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        DrawerHeader()
        Spacer(modifier = Modifier.height(16.dp))
        DrawerItem(icon = Icons.Filled.Person, label = "Profile", onClick = {
            navigateTo(navController, drawerState, scope, "profile")
        })
        DrawerItem(icon = Icons.Filled.Map, label = "Location Tracking", onClick = {
            navigateTo(navController, drawerState, scope, "location")
        })
        DrawerItem(icon = Icons.Filled.Mood, label = "Mood Tracker", onClick = {
            navigateTo(navController, drawerState, scope, "mood_tracker")
        })
        DrawerItem(icon = Icons.Filled.Gamepad, label = "Gamified Learning", onClick = {
            navigateTo(navController, drawerState, scope, "gamified_learning")
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
}

fun navigateTo(navController: NavController, drawerState: DrawerState, scope: CoroutineScope, route: String) {
    scope.launch {
        drawerState.close()
        navController.navigate(route)
    }
}

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_safesentron),
            contentDescription = "App Logo",
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = "SafeSentron", style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 28.sp
            )
        )
    }
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label)
        Spacer(Modifier.width(16.dp))
        Text(text = label)
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

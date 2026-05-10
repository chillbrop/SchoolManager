package com.example.schoolmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.schoolmanager.ui.theme.SchoolManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchoolManagerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SchoolManagerApp()
                }
            }
        }
    }
}

@Composable
fun SchoolManagerApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val dataViewModel: DataViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val onLogout: () -> Unit = {
        authViewModel.logout()
        navController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(onFinished = {
                if (authState is AuthState.Success) {
                    val user = (authState as AuthState.Success).user
                    val dest = when (user.role) {
                        "teacher" -> Screen.TeacherDashboard.route
                        "admin" -> Screen.AdminDashboard.route
                        else -> Screen.StudentDashboard.route
                    }
                    navController.navigate(dest) { popUpTo(Screen.Splash.route) { inclusive = true } }
                } else {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            })
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinished = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { user ->
                    val dest = when (user.role) {
                        "teacher" -> Screen.TeacherDashboard.route
                        "admin" -> Screen.AdminDashboard.route
                        else -> Screen.StudentDashboard.route
                    }
                    navController.navigate(dest) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { user ->
                    val dest = when (user.role) {
                        "teacher" -> Screen.TeacherDashboard.route
                        "admin" -> Screen.AdminDashboard.route
                        else -> Screen.StudentDashboard.route
                    }
                    navController.navigate(dest) { popUpTo(0) { inclusive = true } }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.StudentDashboard.route) {
            val user = currentUser ?: return@composable
            StudentDashboard(user = user, dataViewModel = dataViewModel, onLogout = onLogout)
        }

        composable(Screen.TeacherDashboard.route) {
            val user = currentUser ?: return@composable
            TeacherDashboard(user = user, dataViewModel = dataViewModel, onLogout = onLogout)
        }

        composable(Screen.AdminDashboard.route) {
            val user = currentUser ?: return@composable
            AdminDashboard(user = user, dataViewModel = dataViewModel, onLogout = onLogout)
        }
    }
}

package com.example.schoolmanager

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object StudentDashboard : Screen("student_dashboard")
    object TeacherDashboard : Screen("teacher_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
    object Assignments : Screen("assignments")
    object Announcements : Screen("announcements")
    object Profile : Screen("profile")
}

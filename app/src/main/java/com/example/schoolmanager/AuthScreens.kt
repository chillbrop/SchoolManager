package com.example.schoolmanager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolmanager.ui.theme.*

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (UserProfile) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (val s = authState) {
            is AuthState.Success -> onLoginSuccess(s.user)
            is AuthState.Error  -> { errorMsg = s.message; authViewModel.resetState() }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A237E), Color(0xFF3F51B5), BackgroundLight)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))
            Icon(Icons.Filled.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(8.dp))
            Text("SchoolManager", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Sign in to your account", color = Color.White.copy(0.8f), fontSize = 14.sp)
            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Welcome Back", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Enter your credentials to continue", fontSize = 13.sp, color = TextSecondary)
                    Spacer(Modifier.height(24.dp))

                    if (errorMsg.isNotEmpty()) {
                        Surface(
                            color = ErrorRed.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(errorMsg, color = ErrorRed, fontSize = 13.sp)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMsg = "" },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Filled.Email, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMsg = "" },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) errorMsg = "Please fill in all fields"
                            else authViewModel.login(email.trim(), password)
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = authState !is AuthState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Don't have an account? ", color = TextSecondary, fontSize = 14.sp)
                        Text(
                            "Create one",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: (UserProfile) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("student") }
    var errorMsg by remember { mutableStateOf("") }
    var roleMenuExpanded by remember { mutableStateOf(false) }

    val roles = listOf("student" to "Student", "teacher" to "Teacher", "admin" to "Admin")

    LaunchedEffect(authState) {
        when (val s = authState) {
            is AuthState.Success -> onRegisterSuccess(s.user)
            is AuthState.Error  -> { errorMsg = s.message; authViewModel.resetState() }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A237E), Color(0xFF3F51B5), BackgroundLight)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            Icon(Icons.Filled.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(8.dp))
            Text("SchoolManager", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Create Account", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Join SchoolManager today", fontSize = 13.sp, color = TextSecondary)
                    Spacer(Modifier.height(20.dp))

                    if (errorMsg.isNotEmpty()) {
                        Surface(
                            color = ErrorRed.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(errorMsg, color = ErrorRed, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = fullName, onValueChange = { fullName = it; errorMsg = "" },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Filled.Person, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email, onValueChange = { email = it; errorMsg = "" },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Filled.Email, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password, onValueChange = { password = it; errorMsg = "" },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it; errorMsg = "" },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Filled.LockOpen, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))

                    // Role selector
                    ExposedDropdownMenuBox(
                        expanded = roleMenuExpanded,
                        onExpandedChange = { roleMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = roles.first { it.first == selectedRole }.second,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Role") },
                            leadingIcon = { Icon(Icons.Filled.Badge, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(roleMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = roleMenuExpanded,
                            onDismissRequest = { roleMenuExpanded = false }
                        ) {
                            roles.forEach { (value, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = { selectedRole = value; roleMenuExpanded = false }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            when {
                                fullName.isBlank() || email.isBlank() || password.isBlank() ->
                                    errorMsg = "Please fill in all fields"
                                password != confirmPassword ->
                                    errorMsg = "Passwords do not match"
                                password.length < 6 ->
                                    errorMsg = "Password must be at least 6 characters"
                                else -> authViewModel.register(email.trim(), password, fullName.trim(), selectedRole)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = authState !is AuthState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Already have an account? ", color = TextSecondary, fontSize = 14.sp)
                        Text(
                            "Sign In",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

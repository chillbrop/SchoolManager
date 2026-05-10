package com.example.schoolmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
    object LoggedOut : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                val session = SupabaseClientProvider.client.auth.currentSessionOrNull()
                if (session != null) {
                    val userId = session.user?.id ?: return@launch
                    val profile = SupabaseClientProvider.client
                        .from("profiles")
                        .select { filter { eq("id", userId) } }
                        .decodeSingle<UserProfile>()
                    _currentUser.value = profile
                    _authState.value = AuthState.Success(profile)
                } else {
                    _authState.value = AuthState.LoggedOut
                }
            } catch (_: Exception) {
                _authState.value = AuthState.LoggedOut
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClientProvider.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                val userId: String = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                    ?: throw Exception("No user ID after login")
                val profile = SupabaseClientProvider.client
                    .from("profiles")
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<UserProfile>()
                _currentUser.value = profile
                _authState.value = AuthState.Success(profile)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String, fullName: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClientProvider.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                val userId: String = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                    ?: throw Exception("No user ID after registration")
                val newProfile = NewProfile(
                    id = userId,
                    email = email,
                    fullName = fullName,
                    role = role
                )
                SupabaseClientProvider.client.from("profiles").insert(newProfile)
                val profile = UserProfile(
                    id = userId, email = email, fullName = fullName, role = role
                )
                _currentUser.value = profile
                _authState.value = AuthState.Success(profile)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                SupabaseClientProvider.client.auth.signOut()
            } catch (_: Exception) {}
            _currentUser.value = null
            _authState.value = AuthState.LoggedOut
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

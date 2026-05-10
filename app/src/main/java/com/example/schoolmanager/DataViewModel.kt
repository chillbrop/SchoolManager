package com.example.schoolmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DataState<out T> {
    object Idle : DataState<Nothing>()
    object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
}

class DataViewModel : ViewModel() {

    private val _assignments = MutableStateFlow<DataState<List<Assignment>>>(DataState.Idle)
    val assignments: StateFlow<DataState<List<Assignment>>> = _assignments

    private val _announcements = MutableStateFlow<DataState<List<Announcement>>>(DataState.Idle)
    val announcements: StateFlow<DataState<List<Announcement>>> = _announcements

    private val _actionState = MutableStateFlow<DataState<String>>(DataState.Idle)
    val actionState: StateFlow<DataState<String>> = _actionState

    private val db get() = SupabaseClientProvider.client

    // ─── ASSIGNMENTS ──────────────────────────────────────────────

    fun fetchAssignments() {
        viewModelScope.launch {
            _assignments.value = DataState.Loading
            try {
                val result = db.from("assignments")
                    .select { order("created_at", Order.DESCENDING) }
                    .decodeList<Assignment>()
                _assignments.value = DataState.Success(result)
            } catch (e: Exception) {
                _assignments.value = DataState.Error(e.message ?: "Failed to fetch assignments")
            }
        }
    }

    fun createAssignment(
        title: String,
        description: String,
        subject: String,
        dueDate: String,
        teacherId: String,
        teacherName: String
    ) {
        viewModelScope.launch {
            _actionState.value = DataState.Loading
            try {
                db.from("assignments").insert(
                    NewAssignment(
                        title = title,
                        description = description,
                        subject = subject,
                        dueDate = dueDate,
                        teacherId = teacherId,
                        teacherName = teacherName
                    )
                )
                _actionState.value = DataState.Success("Assignment created!")
                fetchAssignments()
            } catch (e: Exception) {
                _actionState.value = DataState.Error(e.message ?: "Failed to create assignment")
            }
        }
    }

    fun updateAssignment(
        id: String,
        title: String,
        description: String,
        subject: String,
        dueDate: String
    ) {
        viewModelScope.launch {
            _actionState.value = DataState.Loading
            try {
                db.from("assignments").update(
                    {
                        set("title", title)
                        set("description", description)
                        set("subject", subject)
                        set("due_date", dueDate)
                    }
                ) {
                    filter { eq("id", id) }
                }
                _actionState.value = DataState.Success("Assignment updated!")
                fetchAssignments()
            } catch (e: Exception) {
                _actionState.value = DataState.Error(e.message ?: "Failed to update assignment")
            }
        }
    }

    fun deleteAssignment(id: String) {
        viewModelScope.launch {
            _actionState.value = DataState.Loading
            try {
                db.from("assignments").delete { filter { eq("id", id) } }
                _actionState.value = DataState.Success("Assignment deleted!")
                fetchAssignments()
            } catch (e: Exception) {
                _actionState.value = DataState.Error(e.message ?: "Failed to delete assignment")
            }
        }
    }

    // ─── ANNOUNCEMENTS ────────────────────────────────────────────

    fun fetchAnnouncements() {
        viewModelScope.launch {
            _announcements.value = DataState.Loading
            try {
                val result = db.from("announcements")
                    .select { order("created_at", Order.DESCENDING) }
                    .decodeList<Announcement>()
                _announcements.value = DataState.Success(result)
            } catch (e: Exception) {
                _announcements.value = DataState.Error(e.message ?: "Failed to fetch announcements")
            }
        }
    }

    fun createAnnouncement(title: String, content: String, authorId: String, authorName: String) {
        viewModelScope.launch {
            _actionState.value = DataState.Loading
            try {
                db.from("announcements").insert(
                    NewAnnouncement(
                        title = title,
                        content = content,
                        authorId = authorId,
                        authorName = authorName
                    )
                )
                _actionState.value = DataState.Success("Announcement posted!")
                fetchAnnouncements()
            } catch (e: Exception) {
                _actionState.value = DataState.Error(e.message ?: "Failed to post announcement")
            }
        }
    }

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch {
            _actionState.value = DataState.Loading
            try {
                db.from("announcements").delete { filter { eq("id", id) } }
                _actionState.value = DataState.Success("Announcement deleted!")
                fetchAnnouncements()
            } catch (e: Exception) {
                _actionState.value = DataState.Error(e.message ?: "Failed to delete announcement")
            }
        }
    }

    fun resetActionState() {
        _actionState.value = DataState.Idle
    }
}

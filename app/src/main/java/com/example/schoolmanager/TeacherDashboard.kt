package com.example.schoolmanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.schoolmanager.ui.theme.*

@Composable
fun TeacherDashboard(
    user: UserProfile,
    dataViewModel: DataViewModel,
    onLogout: () -> Unit
) {
    val assignmentsState by dataViewModel.assignments.collectAsState()
    val announcementsState by dataViewModel.announcements.collectAsState()
    val actionState by dataViewModel.actionState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateAssignmentDialog by remember { mutableStateOf(false) }
    var showCreateAnnouncementDialog by remember { mutableStateOf(false) }
    var editingAssignment by remember { mutableStateOf<Assignment?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        dataViewModel.fetchAssignments()
        dataViewModel.fetchAnnouncements()
    }

    LaunchedEffect(actionState) {
        when (val s = actionState) {
            is DataState.Success -> { snackbarHostState.showSnackbar(s.data); dataViewModel.resetActionState() }
            is DataState.Error   -> { snackbarHostState.showSnackbar("Error: ${s.message}"); dataViewModel.resetActionState() }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showCreateAssignmentDialog = true
                    else showCreateAnnouncementDialog = true
                },
                containerColor = PrimaryBlue
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AppTopBar(title = "Teacher Dashboard", user = user, onLogout = onLogout)
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    text = { Text("Assignments") },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, null) }
                )
                Tab(
                    selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    text = { Text("Announcements") },
                    icon = { Icon(Icons.Filled.Campaign, null) }
                )
            }
            when (selectedTab) {
                0 -> TeacherAssignmentsTab(
                    state = assignmentsState,
                    onEdit = { editingAssignment = it },
                    onDelete = { dataViewModel.deleteAssignment(it) }
                )
                1 -> TeacherAnnouncementsTab(
                    state = announcementsState,
                    onDelete = { dataViewModel.deleteAnnouncement(it) }
                )
            }
        }
    }

    if (showCreateAssignmentDialog) {
        AssignmentFormDialog(
            title = "Post New Assignment",
            onDismiss = { showCreateAssignmentDialog = false },
            onConfirm = { t, d, s, due ->
                dataViewModel.createAssignment(t, d, s, due, user.id, user.fullName)
                showCreateAssignmentDialog = false
            }
        )
    }

    editingAssignment?.let { assignment ->
        AssignmentFormDialog(
            title = "Edit Assignment",
            initialTitle = assignment.title,
            initialDescription = assignment.description,
            initialSubject = assignment.subject,
            initialDueDate = assignment.dueDate,
            onDismiss = { editingAssignment = null },
            onConfirm = { t, d, s, due ->
                dataViewModel.updateAssignment(assignment.id, t, d, s, due)
                editingAssignment = null
            }
        )
    }

    if (showCreateAnnouncementDialog) {
        AnnouncementFormDialog(
            onDismiss = { showCreateAnnouncementDialog = false },
            onConfirm = { t, c ->
                dataViewModel.createAnnouncement(t, c, user.id, user.fullName)
                showCreateAnnouncementDialog = false
            }
        )
    }
}

@Composable
fun TeacherAssignmentsTab(
    state: DataState<List<Assignment>>,
    onEdit: (Assignment) -> Unit,
    onDelete: (String) -> Unit
) {
    when (state) {
        is DataState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is DataState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
        }
        is DataState.Success -> {
            if (state.data.isEmpty()) {
                EmptyState("No assignments yet. Tap + to add one.", Icons.AutoMirrored.Filled.Assignment)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.data, key = { it.id }) { assignment ->
                        AssignmentCard(assignment = assignment, onEdit = onEdit, onDelete = onDelete)
                    }
                }
            }
        }
        else -> {}
    }
}

@Composable
fun TeacherAnnouncementsTab(
    state: DataState<List<Announcement>>,
    onDelete: (String) -> Unit
) {
    when (state) {
        is DataState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is DataState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
        }
        is DataState.Success -> {
            if (state.data.isEmpty()) {
                EmptyState("No announcements yet. Tap + to post one.", Icons.Filled.Campaign)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.data, key = { it.id }) { ann ->
                        AnnouncementCard(announcement = ann, onDelete = onDelete)
                    }
                }
            }
        }
        else -> {}
    }
}

@Composable
fun AssignmentFormDialog(
    title: String,
    initialTitle: String = "",
    initialDescription: String = "",
    initialSubject: String = "",
    initialDueDate: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var titleField by remember { mutableStateOf(initialTitle) }
    var descriptionField by remember { mutableStateOf(initialDescription) }
    var subjectField by remember { mutableStateOf(initialSubject) }
    var dueDateField by remember { mutableStateOf(initialDueDate) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = titleField, onValueChange = { titleField = it },
                    label = { Text("Title") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = subjectField, onValueChange = { subjectField = it },
                    label = { Text("Subject") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = descriptionField, onValueChange = { descriptionField = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp), maxLines = 4
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = dueDateField, onValueChange = { dueDateField = it },
                    label = { Text("Due Date (e.g. 2025-12-31)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.CalendarToday, null) }
                )
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (titleField.isNotBlank() && subjectField.isNotBlank())
                                onConfirm(titleField, descriptionField, subjectField, dueDateField)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Save") }
                }
            }
        }
    }
}

@Composable
fun AnnouncementFormDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var titleField by remember { mutableStateOf("") }
    var contentField by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Post Announcement", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = titleField, onValueChange = { titleField = it },
                    label = { Text("Title") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = contentField, onValueChange = { contentField = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp), maxLines = 5
                )
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (titleField.isNotBlank() && contentField.isNotBlank())
                                onConfirm(titleField, contentField)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Post") }
                }
            }
        }
    }
}

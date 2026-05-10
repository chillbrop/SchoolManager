package com.example.schoolmanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolmanager.ui.theme.*

@Composable
fun AdminDashboard(
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
            if (selectedTab == 0 || selectedTab == 1) {
                FloatingActionButton(
                    onClick = {
                        if (selectedTab == 0) showCreateAssignmentDialog = true
                        else showCreateAnnouncementDialog = true
                    },
                    containerColor = AdminColor
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AppTopBar(title = "Admin Panel", user = user, onLogout = onLogout)
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    text = { Text("Assignments") },
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, null) }
                )
                Tab(
                    selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    text = { Text("Notices") },
                    icon = { Icon(Icons.Filled.Campaign, null) }
                )
                Tab(
                    selected = selectedTab == 2, onClick = { selectedTab = 2 },
                    text = { Text("Overview") },
                    icon = { Icon(Icons.Filled.Dashboard, null) }
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
                2 -> AdminOverviewTab(assignmentsState, announcementsState)
            }
        }
    }

    if (showCreateAssignmentDialog) {
        AssignmentFormDialog(
            title = "Create Assignment",
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
fun AdminOverviewTab(
    assignmentsState: DataState<List<Assignment>>,
    announcementsState: DataState<List<Announcement>>
) {
    val assignmentCount = (assignmentsState as? DataState.Success)?.data?.size ?: 0
    val announcementCount = (announcementsState as? DataState.Success)?.data?.size ?: 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("School Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("Summary of all school data", fontSize = 13.sp, color = TextSecondary)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    title = "Assignments",
                    value = assignmentCount.toString(),
                    color = TeacherColor,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Filled.Campaign,
                    title = "Announcements",
                    value = announcementCount.toString(),
                    color = AdminColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AdminPanelSettings, null, tint = AdminColor, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Admin Capabilities", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    listOf(
                        Icons.Filled.Add      to "Create assignments and announcements",
                        Icons.Filled.Edit     to "Edit any assignment in the system",
                        Icons.Filled.Delete   to "Delete assignments and announcements",
                        Icons.Filled.Visibility to "View all school data"
                    ).forEach { (icon, text) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AdminColor.copy(0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, null, tint = AdminColor, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(text, fontSize = 14.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.05f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Storage, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Supabase Tables", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                    Spacer(Modifier.height(12.dp))
                    listOf("profiles", "assignments", "announcements").forEach { table ->
                        Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessGreen))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                table,
                                fontSize = 13.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

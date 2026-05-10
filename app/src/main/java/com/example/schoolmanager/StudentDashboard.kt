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
import com.example.schoolmanager.ui.theme.*

@Composable
fun StudentDashboard(
    user: UserProfile,
    dataViewModel: DataViewModel,
    onLogout: () -> Unit
) {
    val assignmentsState by dataViewModel.assignments.collectAsState()
    val announcementsState by dataViewModel.announcements.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        dataViewModel.fetchAssignments()
        dataViewModel.fetchAnnouncements()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = "Student Dashboard", user = user, onLogout = onLogout)
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Assignments") },
                icon = { Icon(Icons.AutoMirrored.Filled.Assignment, null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Announcements") },
                icon = { Icon(Icons.Filled.Campaign, null) }
            )
        }
        when (selectedTab) {
            0 -> AssignmentsTab(assignmentsState)
            1 -> AnnouncementsTab(announcementsState)
        }
    }
}

@Composable
fun AssignmentsTab(state: DataState<List<Assignment>>) {
    when (state) {
        is DataState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is DataState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
        }
        is DataState.Success -> {
            if (state.data.isEmpty()) {
                EmptyState("No assignments yet", Icons.AutoMirrored.Filled.Assignment)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.data) { AssignmentCard(assignment = it) }
                }
            }
        }
        else -> {}
    }
}

@Composable
fun AssignmentCard(
    assignment: Assignment,
    onEdit: ((Assignment) -> Unit)? = null,
    onDelete: ((String) -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(assignment.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Spacer(Modifier.height(4.dp))
                    Surface(color = TeacherColor.copy(0.1f), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            assignment.subject,
                            color = TeacherColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                if (onEdit != null || onDelete != null) {
                    Row {
                        onEdit?.let { edit ->
                            IconButton(onClick = { edit(assignment) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.Edit, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            }
                        }
                        onDelete?.let { del ->
                            IconButton(onClick = { del(assignment.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.Delete, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
            if (assignment.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(assignment.description, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(assignment.teacherName, fontSize = 12.sp, color = TextSecondary)
                }
                if (assignment.dueDate.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarToday, null, tint = AccentGold, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Due: ${assignment.dueDate}", fontSize = 12.sp, color = AccentGold, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementsTab(state: DataState<List<Announcement>>) {
    when (state) {
        is DataState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is DataState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
        }
        is DataState.Success -> {
            if (state.data.isEmpty()) {
                EmptyState("No announcements yet", Icons.Filled.Campaign)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.data) { AnnouncementCard(announcement = it) }
                }
            }
        }
        else -> {}
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    onDelete: ((String) -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(color = AdminColor.copy(0.1f), shape = RoundedCornerShape(50)) {
                        Icon(
                            Icons.Filled.Campaign, null,
                            tint = AdminColor,
                            modifier = Modifier.padding(6.dp).size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(announcement.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                }
                onDelete?.let { del ->
                    IconButton(onClick = { del(announcement.id) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Delete, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(announcement.content, fontSize = 14.sp, color = TextSecondary, lineHeight = 22.sp)
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(announcement.authorName, fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

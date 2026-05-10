package com.example.schoolmanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val gradientColors: List<Color>
)

val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.Filled.School,
        title = "Welcome to SchoolManager",
        description = "A complete school management platform for students, teachers, and administrators.",
        gradientColors = listOf(Color(0xFF1A237E), Color(0xFF3949AB))
    ),
    OnboardingPage(
        icon = Icons.AutoMirrored.Filled.Assignment,
        title = "Manage Assignments",
        description = "Teachers can post assignments and students can view them in real time — all synced with Supabase.",
        gradientColors = listOf(Color(0xFF00695C), Color(0xFF26A69A))
    ),
    OnboardingPage(
        icon = Icons.Filled.Campaign,
        title = "Stay Informed",
        description = "Receive and post announcements instantly. Everyone stays on the same page.",
        gradientColors = listOf(Color(0xFF4A148C), Color(0xFF7B1FA2))
    ),
    OnboardingPage(
        icon = Icons.Filled.AdminPanelSettings,
        title = "Role-Based Access",
        description = "Students, Teachers, and Admins each get their own tailored dashboard with relevant tools.",
        gradientColors = listOf(Color(0xFF880E4F), Color(0xFFE91E63))
    )
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.size - 1

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val pageData = onboardingPages[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(pageData.gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.size(140.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = pageData.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(48.dp))
                    Text(
                        text = pageData.title,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = pageData.description,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(onboardingPages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isLastPage) {
                    TextButton(onClick = onFinished) {
                        Text("Skip", color = Color.White.copy(alpha = 0.75f), fontSize = 16.sp)
                    }
                } else {
                    Spacer(Modifier.width(60.dp))
                }
                Button(
                    onClick = {
                        if (isLastPage) {
                            onFinished()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = onboardingPages[pagerState.currentPage].gradientColors[0]
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text(
                        text = if (isLastPage) "Get Started" else "Next",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

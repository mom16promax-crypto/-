package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.GuidanceViewModel
import com.example.ui.Strings

@Composable
fun DashboardScreen(
    viewModel: GuidanceViewModel,
    onNavigateTab: (String) -> Unit,
    onShowArchitecture: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val surveys by viewModel.surveys.collectAsStateWithLifecycle()
    val responses by viewModel.allResponses.collectAsStateWithLifecycle()
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val complaints by viewModel.complaints.collectAsStateWithLifecycle()
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()

    val isAdmin = currentUser?.role == "ADMIN"
    val activeSurveysCount = surveys.count { it.isActive }
    val totalResponsesCount = responses.size
    val totalAnnouncementsCount = announcements.size
    val activeComplaintsCount = complaints.count { it.status != "Resolved" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("dashboard_screen")
    ) {
        // Hero Welcome Banner
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("welcome_hero_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_guidance_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = Strings.appTitle(isArabic),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = Strings.preparedBy(isArabic),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Role Badge
                    Surface(
                        color = if (isAdmin) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.FamilyRestroom,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAdmin) Strings.adminPortalBadge(isArabic) else Strings.parentPortalBadge(isArabic),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isAdmin) {
                        if (isArabic) "أهلاً بك، الأستاذ جعفر مهدي" else "Welcome, Mr. Jaafar Mahdi"
                    } else {
                        if (isArabic) "أهلاً بك، ${currentUser?.parentName ?: "ولي الأمر الكريم"}" else "Welcome, ${currentUser?.parentName ?: "Valued Parent"}"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = if (isAdmin) {
                        if (isArabic) "المرشد الطلابي ورئيس قسم التوجيه والإرشاد بالمدرسة" else "School Counseling Center Administrator & Guidance Department Head"
                    } else {
                        if (isArabic)
                            "الطالب: ${currentUser?.studentName ?: "رامي الأحمد"} (${currentUser?.grade ?: "الصف الأول ثانوي"})"
                        else
                            "Student: ${currentUser?.studentName ?: "Rami Al-Ahmad"} (${currentUser?.grade ?: "Grade 10"})"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Instant Switcher Ribbon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAdmin) {
                            if (isArabic) "تجربة واجهة ولي الأمر؟" else "Testing Parent flow?"
                        } else {
                            if (isArabic) "تجربة صلاحيات المرشد؟" else "Testing Admin tools?"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedButton(
                        onClick = {
                            if (isAdmin) viewModel.quickSwitchToParent() else viewModel.quickSwitchToAdmin()
                        },
                        modifier = Modifier.testTag("quick_toggle_role_button")
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isAdmin) Strings.switchToParent(isArabic) else Strings.switchToAdmin(isArabic))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Real-Time Statistics Counters
        Text(
            text = Strings.realTimeStats(isArabic),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = Strings.activeSurveys(isArabic),
                value = "$activeSurveysCount",
                icon = Icons.Default.Assignment,
                iconTint = MaterialTheme.colorScheme.primary,
                testTag = "stat_surveys"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = if (isAdmin) Strings.totalResponses(isArabic) else Strings.completedResponses(isArabic),
                value = if (isAdmin) "$totalResponsesCount" else "${responses.count { it.parentPhone == currentUser?.phoneNumber }}",
                icon = Icons.Default.CheckCircle,
                iconTint = Color(0xFF10B981),
                testTag = "stat_responses"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = Strings.announcementsCount(isArabic),
                value = "$totalAnnouncementsCount",
                icon = Icons.Default.Notifications,
                iconTint = MaterialTheme.colorScheme.secondary,
                testTag = "stat_announcements"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = if (isAdmin) Strings.openInquiries(isArabic) else Strings.myConsultations(isArabic),
                value = if (isAdmin) "$activeComplaintsCount" else "${complaints.count { it.parentPhone == currentUser?.phoneNumber }}",
                icon = Icons.Default.ContactSupport,
                iconTint = Color(0xFFEF4444),
                testTag = "stat_complaints"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions Section
        Text(
            text = Strings.quickActions(isArabic),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (isAdmin) {
            // Admin Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "استبانة جديدة" else "New Survey",
                    subtitle = if (isArabic) "بناء استبانة ديناميكية" else "Build dynamic questionnaire",
                    icon = Icons.Default.Assignment,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onNavigateTab("surveys") }
                )
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "نشر تعميم" else "Post Notice",
                    subtitle = if (isArabic) "توجيه لأولياء الأمور" else "Target all or new accounts",
                    icon = Icons.Default.PostAdd,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { onNavigateTab("announcements") }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "متابعة الحالات" else "Review Issues",
                    subtitle = if (isArabic) "لوحة ردود المرشد" else "Counselor response panel",
                    icon = Icons.Default.RateReview,
                    color = Color(0xFF0284C7),
                    onClick = { onNavigateTab("complaints") }
                )
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "التوثيق المعماري" else "Architecture",
                    subtitle = if (isArabic) "مواصفات التصدير والنظام" else "System & export specs",
                    icon = Icons.Default.Description,
                    color = Color(0xFF6B7280),
                    onClick = onShowArchitecture
                )
            }
        } else {
            // Parent Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "المشاركة في استبانة" else "Take Survey",
                    subtitle = if (isArabic) "تقديم رأيك وملاحظاتك" else "Give student feedback",
                    icon = Icons.Default.Assignment,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onNavigateTab("surveys") }
                )
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "الخط المباشر" else "Direct Line",
                    subtitle = if (isArabic) "استشارة سرية مع المرشد" else "File issue or consultation",
                    icon = Icons.Default.HelpOutline,
                    color = Color(0xFFD97706),
                    onClick = { onNavigateTab("complaints") }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "نشرات الإرشاد" else "Notices",
                    subtitle = if (isArabic) "الورش والتعاميم المدرسية" else "School announcements",
                    icon = Icons.Default.Notifications,
                    color = Color(0xFF059669),
                    onClick = { onNavigateTab("announcements") }
                )
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "عن التطبيق" else "About System",
                    subtitle = if (isArabic) "هيكلية النظام والإرشاد" else "Architecture & Counselor",
                    icon = Icons.Default.Description,
                    color = Color(0xFF6366F1),
                    onClick = onShowArchitecture
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Counselor Daily Insight Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.counselorCorner(isArabic),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Strings.counselorQuote(isArabic),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Direct Guidance Office Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Strings.officeHours(isArabic),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = Strings.officeDetails(isArabic),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    testTag: String
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("action_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

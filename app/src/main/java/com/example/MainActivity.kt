package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GuidanceViewModel
import com.example.ui.Strings
import com.example.ui.screens.AnnouncementsScreen
import com.example.ui.screens.ArchitectureDialog
import com.example.ui.screens.ComplaintsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SurveysScreen
import com.example.ui.theme.GuidanceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuidanceAppTheme {
                GuidanceAppMain()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidanceAppMain(viewModel: GuidanceViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val surveys by viewModel.surveys.collectAsStateWithLifecycle()
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showArchitectureDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        if (currentUser == null) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = Strings.appTitle(isArabic),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        actions = {
                            // Language switcher on login screen
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { viewModel.toggleLanguage() }
                                    .testTag("login_lang_toggle")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Language",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "EN" else "عربي",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { viewModel.selectTab("dashboard") }
                    )
                }
            }
        } else {
            val isAdmin = currentUser?.role == "ADMIN"
            val activeSurveysCount = surveys.count { it.isActive }
            val announcementsCount = announcements.size

            BackHandler(enabled = currentTab != "dashboard") {
                viewModel.selectTab("dashboard")
            }

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        navigationIcon = {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_guidance_logo),
                                    contentDescription = "App Icon",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                )
                            }
                        },
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = Strings.appTitle(isArabic),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = Strings.preparedBy(isArabic),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        actions = {
                            // Language Toggle Button
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { viewModel.toggleLanguage() }
                                    .padding(end = 4.dp)
                                    .testTag("topbar_lang_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Change Language",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "EN" else "عربي",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Quick Switcher Button
                            IconButton(
                                onClick = {
                                    if (isAdmin) viewModel.quickSwitchToParent() else viewModel.quickSwitchToAdmin()
                                },
                                modifier = Modifier.testTag("topbar_switch_role_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = Strings.switchRole(isArabic),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Architecture documentation modal
                            IconButton(
                                onClick = { showArchitectureDialog = true },
                                modifier = Modifier.testTag("topbar_info_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Architecture Documentation",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Sign out
                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier.testTag("topbar_logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = Strings.logout(isArabic),
                                    tint = Color(0xFFEF4444)
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == "dashboard",
                            onClick = { viewModel.selectTab("dashboard") },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = Strings.tabHome(isArabic)) },
                            label = { Text(Strings.tabHome(isArabic)) },
                            modifier = Modifier.testTag("nav_tab_dashboard")
                        )

                        NavigationBarItem(
                            selected = currentTab == "surveys",
                            onClick = { viewModel.selectTab("surveys") },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (activeSurveysCount > 0) {
                                            Badge { Text("$activeSurveysCount") }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Assignment, contentDescription = Strings.tabSurveys(isArabic))
                                }
                            },
                            label = { Text(Strings.tabSurveys(isArabic)) },
                            modifier = Modifier.testTag("nav_tab_surveys")
                        )

                        NavigationBarItem(
                            selected = currentTab == "announcements",
                            onClick = { viewModel.selectTab("announcements") },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (announcementsCount > 0) {
                                            Badge { Text("$announcementsCount") }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = Strings.tabAnnouncements(isArabic))
                                }
                            },
                            label = { Text(Strings.tabAnnouncements(isArabic)) },
                            modifier = Modifier.testTag("nav_tab_announcements")
                        )

                        NavigationBarItem(
                            selected = currentTab == "complaints",
                            onClick = { viewModel.selectTab("complaints") },
                            icon = { Icon(Icons.Default.SupportAgent, contentDescription = Strings.tabComplaints(isArabic)) },
                            label = { Text(Strings.tabComplaints(isArabic)) },
                            modifier = Modifier.testTag("nav_tab_complaints")
                        )
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentTab) {
                        "dashboard" -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateTab = { viewModel.selectTab(it) },
                            onShowArchitecture = { showArchitectureDialog = true }
                        )
                        "surveys" -> SurveysScreen(viewModel = viewModel)
                        "announcements" -> AnnouncementsScreen(viewModel = viewModel)
                        "complaints" -> ComplaintsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    if (showArchitectureDialog) {
        ArchitectureDialog(isArabic = isArabic, onDismiss = { showArchitectureDialog = false })
    }
}

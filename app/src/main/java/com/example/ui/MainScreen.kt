package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: FactoryViewModel) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle back button: return to Studio tab if on another tab
    BackHandler(enabled = state.currentTab != NavigationTab.STUDIO) {
        viewModel.selectTab(NavigationTab.STUDIO)
    }

    // Show notifications in snackbar
    LaunchedEffect(state.notificationMessage) {
        state.notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = ObsidianBlack,
                border = BorderStroke(1.dp, Slate800)
            ) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(listOf(CyberTeal, EmeraldMatrix))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PrecisionManufacturing,
                                    contentDescription = null,
                                    tint = Slate950,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "AI FACTORY",
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        letterSpacing = (-0.3).sp,
                                        color = PureWhite
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(EmeraldMatrix.copy(alpha = 0.2f))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "LOCAL AI",
                                            fontFamily = RobotoMonoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = EmeraldLight
                                        )
                                    }
                                }
                                Text(
                                    if (state.currentProject != null) "${state.currentProject?.name} • ${state.currentProject?.formattedSize}" else "100% Offline • Zero API Dependency",
                                    fontFamily = RobotoMonoFontFamily,
                                    fontSize = 10.sp,
                                    color = CyberTeal
                                )
                            }
                        }
                    },
                    actions = {
                        if (state.currentProject != null) {
                            Surface(
                                color = GlassSurfaceElevated,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, CyberTeal.copy(alpha = 0.5f)),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.selectTab(NavigationTab.EXPORTS) },
                                    modifier = Modifier.size(36.dp).testTag("top_export_action")
                                ) {
                                    Icon(Icons.Default.FolderZip, contentDescription = "ZIP Packages", tint = CyberTeal, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = PureWhite
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                color = ObsidianBlack,
                border = BorderStroke(1.dp, Slate800)
            ) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar"),
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    NavigationTab.values().forEach { tab ->
                        val isSelected = state.currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            modifier = Modifier.testTag(tab.testTag),
                            icon = {
                                Icon(
                                    when (tab) {
                                        NavigationTab.STUDIO -> if (isSelected) Icons.Default.EditNote else Icons.Default.EditNote
                                        NavigationTab.CONSOLE -> if (isSelected) Icons.Default.Terminal else Icons.Default.Terminal
                                        NavigationTab.EXPLORER -> if (isSelected) Icons.Default.Code else Icons.Default.Folder
                                        NavigationTab.EXPORTS -> if (isSelected) Icons.Default.FolderZip else Icons.Default.Archive
                                        NavigationTab.HACKATHON_KIT -> if (isSelected) Icons.Default.Timer else Icons.Default.Timer
                                    },
                                    contentDescription = tab.label
                                )
                            },
                            label = {
                                Text(
                                    tab.label,
                                    fontFamily = if (isSelected) RobotoMonoFontFamily else OutfitFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Slate950,
                                selectedTextColor = CyberTeal,
                                indicatorColor = CyberTeal,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = state.currentTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBlack),
            label = "tab_transition"
        ) { tab ->
            when (tab) {
                NavigationTab.STUDIO -> StudioScreen(state = state, viewModel = viewModel)
                NavigationTab.CONSOLE -> ConsoleScreen(state = state, viewModel = viewModel)
                NavigationTab.EXPLORER -> ExplorerScreen(state = state, viewModel = viewModel)
                NavigationTab.EXPORTS -> ExportsScreen(state = state, viewModel = viewModel)
                NavigationTab.HACKATHON_KIT -> HackathonKitScreen(state = state, viewModel = viewModel)
            }
        }
    }
}

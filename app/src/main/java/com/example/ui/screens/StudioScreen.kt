package com.example.ui.screens

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.SampleMarkdownPresets
import com.example.model.EngineMode
import com.example.model.HackathonTheme
import com.example.model.TechStack
import com.example.ui.FactoryUiState
import com.example.ui.FactoryViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    state: FactoryUiState,
    viewModel: FactoryViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showLocalModelSettings by remember { mutableStateOf(false) }

    val haptics = LocalHapticFeedback.current
    val runtime = remember { Runtime.getRuntime() }
    val usedMemMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
    val totalMemMb = runtime.totalMemory() / (1024 * 1024)
    val cpuCores = remember { runtime.availableProcessors() }

    // Pulsing animation for the On-Device AI indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("studio_screen_content"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // --- BESPOKE HERO BANNER WITH GENERATED 3D ARTWORK ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp), spotColor = CyberTeal)
                .clip(RoundedCornerShape(20.dp))
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(listOf(CyberTeal.copy(alpha = 0.6f), EmeraldMatrix.copy(alpha = 0.3f), Color.Transparent))
                    ),
                    RoundedCornerShape(20.dp)
                )
                .testTag("hero_banner_card"),
            colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(190.dp)) {
                // Background 3D Render
                Image(
                    painter = painterResource(id = R.drawable.img_factory_hero_1794243095168),
                    contentDescription = "AI Factory Engine Workshop",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark Gradient Scrim Overlay for high legibility & contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    ObsidianBlack.copy(alpha = 0.7f),
                                    ObsidianBlack.copy(alpha = 0.98f)
                                )
                            )
                        )
                )

                // Hero Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Status HUD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Slate950.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, CyberTeal.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldMatrix.copy(alpha = pulseAlpha))
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "LOCAL MODEL ACTIVE",
                                    fontFamily = RobotoMonoFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                            }
                        }

                        Surface(
                            color = Slate950.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Slate700)
                        ) {
                            Text(
                                "0 API QUOTA • AIR-GAPPED",
                                fontFamily = RobotoMonoFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Bottom Title & Subtitle
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "AI FACTORY",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                letterSpacing = (-0.5).sp,
                                color = PureWhite
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Brush.horizontalGradient(listOf(CyberTeal, EmeraldMatrix)))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "PRO SIH",
                                    fontFamily = RobotoMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Slate950
                                )
                            }
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "Autonomous on-device neural compiler for hackathon-winning architectures & ZIP codebases.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Slate200
                        )
                    }
                }
            }
        }

        // --- SECTION: SIH PROBLEM STATEMENT PRESETS CAROUSEL ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "SIH PROBLEM STATEMENTS",
                        fontFamily = RobotoMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp,
                        color = Slate200
                    )
                }
                Text("1-Tap Spec Load", fontSize = 11.sp, color = CyberTeal)
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("presets_row")
            ) {
                items(SampleMarkdownPresets.presets) { preset ->
                    val isSelected = state.problemStatementId == preset.problemId
                    Surface(
                        modifier = Modifier
                            .width(220.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.applyPreset(preset) }
                            .testTag("preset_chip_${preset.id}"),
                        color = if (isSelected) GlassSurfaceElevated else GlassSurface,
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) CyberTeal else Slate800
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) CyberTeal.copy(alpha = 0.2f) else Slate800)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        preset.problemId,
                                        fontFamily = RobotoMonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (isSelected) CyberTeal else Slate400
                                    )
                                }
                                Text(
                                    preset.theme.label.split(" ").firstOrNull() ?: "",
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                preset.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) PureWhite else Slate200,
                                maxLines = 2,
                                lineHeight = 16.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                preset.recommendedStack.displayName,
                                fontFamily = RobotoMonoFontFamily,
                                fontSize = 10.sp,
                                color = EmeraldLight
                            )
                        }
                    }
                }
            }
        }

        // --- SECTION: PROJECT METADATA CARD ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = GlassSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "PROJECT IDENTITY & TEAM",
                        fontFamily = RobotoMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Slate200
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = state.projectName,
                        onValueChange = { viewModel.updateProjectName(it) },
                        label = { Text("Project Name", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f).testTag("project_name_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberTeal,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = Slate200
                        )
                    )
                    OutlinedTextField(
                        value = state.problemStatementId,
                        onValueChange = { viewModel.updateProblemId(it) },
                        label = { Text("Problem ID", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f).testTag("problem_id_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberTeal,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = Slate200
                        )
                    )
                }

                OutlinedTextField(
                    value = state.authorTeam,
                    onValueChange = { viewModel.updateAuthorTeam(it) },
                    label = { Text("Hackathon Team Name", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("author_team_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = Slate200
                    )
                )
            }
        }

        // --- SECTION: TARGET TECH STACK SELECTION ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Layers, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "TARGET PRODUCTION TECH STACK",
                    fontFamily = RobotoMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Slate200
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.testTag("tech_stack_selector")) {
                TechStack.values().forEach { stack ->
                    val isSelected = state.selectedStack == stack
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.updateTechStack(stack) }
                            .testTag("stack_option_${stack.name}"),
                        color = if (isSelected) GlassSurfaceElevated else GlassSurface,
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) CyberTeal else Slate800
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CyberTeal.copy(alpha = 0.2f) else Slate800),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        when (stack) {
                                            TechStack.ANDROID_COMPOSE -> Icons.Default.PhoneAndroid
                                            TechStack.REACT_TAILWIND -> Icons.Default.Language
                                            TechStack.FASTAPI_AI -> Icons.Default.Psychology
                                            TechStack.NEXTJS_FULLSTACK -> Icons.Default.Web
                                            TechStack.FLUTTER_DART -> Icons.Default.Devices
                                            TechStack.IOT_MICROSERVICES -> Icons.Default.Hub
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) CyberTeal else Slate400,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        stack.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) CyberTeal else PureWhite
                                    )
                                    Text(
                                        stack.iconDescription,
                                        fontFamily = RobotoMonoFontFamily,
                                        fontSize = 10.sp,
                                        color = Slate400
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) CyberTeal else Slate700,
                                        CircleShape
                                    )
                                    .background(if (isSelected) CyberTeal else Color.Transparent)
                            )
                        }
                    }
                }
            }
        }

        // --- SECTION: HACKATHON THEME TRACK ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "HACKATHON THEME DOMAIN",
                fontFamily = RobotoMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Slate200
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HackathonTheme.values().forEach { theme ->
                    val isSelected = state.selectedTheme == theme
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.updateTheme(theme) },
                        color = if (isSelected) CyberTeal.copy(alpha = 0.15f) else GlassSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) CyberTeal else Slate800
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            theme.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyberTeal else Slate200,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // --- SECTION: LOCAL AI MODEL COMMAND CENTER ---
        Surface(
            modifier = Modifier.fillMaxWidth().testTag("local_ai_model_card"),
            color = GlassSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CyberTeal.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLocalModelSettings = !showLocalModelSettings },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "LOCAL AI INFERENCE HUD",
                                    fontFamily = RobotoMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PureWhite
                                )
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldMatrix.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("AIR-GAPPED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                }
                            }
                            Text(
                                state.engineMode.title,
                                fontSize = 11.sp,
                                color = CyberTeal
                            )
                        }
                    }
                    Icon(
                        if (showLocalModelSettings) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Slate400
                    )
                }

                // Live Local Model Telemetry Grid
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate950)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("THROUGHPUT", fontFamily = RobotoMonoFontFamily, fontSize = 8.sp, color = Slate400)
                        Text("~94 tokens/s", fontFamily = RobotoMonoFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                    }
                    Column {
                        Text("CONTEXT WINDOW", fontFamily = RobotoMonoFontFamily, fontSize = 8.sp, color = Slate400)
                        Text("4,096 Tokens", fontFamily = RobotoMonoFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberTeal)
                    }
                    Column {
                        Text("NETWORK CALLS", fontFamily = RobotoMonoFontFamily, fontSize = 8.sp, color = Slate400)
                        Text("0 (100% Offline)", fontFamily = RobotoMonoFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }

                Spacer(Modifier.height(8.dp))
                // Native Android OS Telemetry Bar
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Android, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(5.dp))
                            Text(
                                "Native Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                                fontFamily = RobotoMonoFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate200
                            )
                        }
                        Text(
                            "ART JVM • ${cpuCores} Cores • ${usedMemMb}MB Heap",
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 9.sp,
                            color = CyberTeal
                        )
                    }
                }

                AnimatedVisibility(visible = showLocalModelSettings) {
                    Column(modifier = Modifier.padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        EngineMode.values().forEach { mode ->
                            val isSelected = state.engineMode == mode
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.updateEngineMode(mode) },
                                color = if (isSelected) Slate850 else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.updateEngineMode(mode) },
                                        colors = RadioButtonDefaults.colors(selectedColor = CyberTeal)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(mode.title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = if (isSelected) CyberTeal else PureWhite)
                                        Text(mode.subtitle, fontSize = 10.sp, color = Slate400)
                                    }
                                }
                            }
                        }

                        // Temperature Slider Control
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate950)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Inference Temperature: ${String.format("%.1f", state.localModelTemperature)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberTeal)
                                Text("Deterministic", fontSize = 11.sp, color = Slate400)
                            }
                            Slider(
                                value = state.localModelTemperature,
                                onValueChange = { viewModel.updateLocalModelTemperature(it) },
                                valueRange = 0.0f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = CyberTeal, activeTrackColor = CyberTeal)
                            )
                        }
                    }
                }
            }
        }

        // --- SECTION: PRO MARKDOWN SPEC EDITOR ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = GlassSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // IDE Tab Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "PROBLEM_STATEMENT.md",
                            fontFamily = RobotoMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PureWhite
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${state.markdownInput.length} chars • ${state.markdownInput.lines().size} lines",
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 10.sp,
                            color = Slate400
                        )
                        IconButton(
                            onClick = { viewModel.updateMarkdown("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = Slate400, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Quick Format Snippet Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SuggestionChip(
                        onClick = {
                            viewModel.updateMarkdown(state.markdownInput + "\n\n## 5. Security & Offline Protocol\n- Local Write-Ahead-Log (WAL) storage\n- SHA-256 block hash for telemetry\n- Zero cloud dependencies")
                        },
                        label = { Text("+ Offline Security", fontSize = 10.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Slate950, labelColor = CyberTeal)
                    )
                    SuggestionChip(
                        onClick = {
                            viewModel.updateMarkdown(state.markdownInput + "\n\n## 6. REST API Endpoints\n- POST /api/v1/telemetry/ingest\n- POST /api/v1/diagnose\n- GET /api/v1/health")
                        },
                        label = { Text("+ REST Endpoints", fontSize = 10.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Slate950, labelColor = CyberTeal)
                    )
                    SuggestionChip(
                        onClick = {
                            viewModel.updateMarkdown(state.markdownInput + "\n\n## 7. SIH Jury Defense Points\n- Zero operational cost\n- Instant edge execution on 2GB RAM\n- Vernacular audio advisory")
                        },
                        label = { Text("+ SIH Jury Points", fontSize = 10.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Slate950, labelColor = CyberTeal)
                    )
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = state.markdownInput,
                    onValueChange = { viewModel.updateMarkdown(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp)
                        .testTag("markdown_spec_editor"),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = RobotoMonoFontFamily,
                        fontSize = 11.5.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFFE2E8F0)
                    ),
                    placeholder = { Text("Paste your hackathon problem statement or PRD markdown here...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate950,
                        unfocusedContainerColor = Slate950,
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = Slate800
                    )
                )
            }
        }

        // --- SECTION: DELIVERABLES TOGGLES ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.includePitchKit,
                    onCheckedChange = { viewModel.setIncludePitchKit(it) },
                    colors = CheckboxDefaults.colors(checkedColor = CyberTeal)
                )
                Text("Include SIH Pitch Kit & Jury Q&A", fontSize = 12.sp, color = Slate200)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.includeDocker,
                    onCheckedChange = { viewModel.setIncludeDocker(it) },
                    colors = CheckboxDefaults.colors(checkedColor = CyberTeal)
                )
                Text("Include Docker & CI/CD", fontSize = 12.sp, color = Slate200)
            }
        }

        // --- PRIMARY ACTION: COMPILE COMPLETE PROJECT ZIP ---
        Button(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.startGeneration()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp), spotColor = CyberTeal)
                .testTag("generate_project_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberTeal,
                contentColor = Slate950
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = !state.isGenerating
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "COMPILE COMPLETE PROJECT ZIP",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "100% Native Android APK • Zero WebViews • On-Device AI",
                        fontFamily = RobotoMonoFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

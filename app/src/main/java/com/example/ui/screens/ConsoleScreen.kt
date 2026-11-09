package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.model.GenerationStep
import com.example.ui.FactoryUiState
import com.example.ui.FactoryViewModel
import com.example.ui.NavigationTab
import com.example.ui.theme.*

@Composable
fun ConsoleScreen(
    state: FactoryUiState,
    viewModel: FactoryViewModel,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Auto-scroll to bottom of logs
    LaunchedEffect(state.logs.size) {
        if (state.logs.isNotEmpty()) {
            listState.animateScrollToItem(state.logs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(16.dp)
            .testTag("console_screen_content"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- PIPELINE STEPPER CARD ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .testTag("pipeline_stepper_card"),
            color = GlassSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CardBorderGlow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with Hardware Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (state.isGenerating) CyberTeal else EmeraldMatrix)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "ON-DEVICE COMPILER PIPELINE",
                            fontFamily = RobotoMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PureWhite
                        )
                    }

                    if (state.isGenerating) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SYNTHESIZING...", fontFamily = RobotoMonoFontFamily, fontSize = 10.sp, color = CyberTeal)
                            Spacer(Modifier.width(8.dp))
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = CyberTeal)
                        }
                    } else if (state.currentProject != null) {
                        Text("100% COMPLETE", fontFamily = RobotoMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = EmeraldLight)
                    }
                }

                Spacer(Modifier.height(14.dp))

                GenerationStep.values().forEach { step ->
                    val isCurrent = state.currentStep == step
                    val isCompleted = state.currentProject != null || (state.currentStep != null && state.currentStep.stepNumber > step.stepNumber)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> EmeraldMatrix
                                        isCurrent -> CyberTeal
                                        else -> Slate850
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Slate950, modifier = Modifier.size(14.dp))
                            } else {
                                Text(
                                    "${step.stepNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Slate950 else Slate400
                                )
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        Column {
                            Text(
                                step.title,
                                fontWeight = if (isCurrent || isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 12.sp,
                                color = if (isCurrent) CyberTeal else if (isCompleted) PureWhite else Slate400
                            )
                            Text(
                                step.detail,
                                fontSize = 10.sp,
                                color = Slate400
                            )
                        }
                    }
                }
            }
        }

        // --- SUCCESS CELEBRATION CARD ---
        AnimatedVisibility(visible = state.currentProject != null && !state.isGenerating) {
            state.currentProject?.let { proj ->
                Surface(
                    modifier = Modifier.fillMaxWidth().testTag("generation_success_card"),
                    color = Slate900,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, EmeraldMatrix)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TaskAlt, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "PACKAGE COMPILED & ZIPPED",
                                    fontFamily = RobotoMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EmeraldLight
                                )
                            }
                            Text(
                                proj.formattedSize,
                                fontFamily = RobotoMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = CyberTeal
                            )
                        }

                        Text(
                            "${proj.fileCount} complete files generated with ${proj.totalLinesOfCode} lines of production code. Ready to extract or push to GitHub.",
                            fontSize = 12.sp,
                            color = Slate200
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { viewModel.selectTab(NavigationTab.EXPLORER) },
                                modifier = Modifier.weight(1f).testTag("view_files_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = CyberTeal),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Inspect Files", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.selectTab(NavigationTab.EXPORTS) },
                                modifier = Modifier.weight(1f).testTag("view_zip_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberTeal, contentColor = Slate950),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Download ZIP", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }

        // --- CYBER COMMAND TERMINAL ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("terminal_logs_card"),
            color = Slate950,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                // Terminal Title Bar (Mac-like buttons + session title)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                        Spacer(Modifier.width(6.dp))
                        Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                        Spacer(Modifier.width(6.dp))
                        Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "ai-factory-core ~ local-ast-compiler",
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }

                    Text("${state.logs.size} events", fontFamily = RobotoMonoFontFamily, fontSize = 10.sp, color = CyberTeal)
                }

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = Slate800)
                Spacer(Modifier.height(8.dp))

                if (state.logs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = Slate800, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Terminal ready. Awaiting 'Compile Complete Project ZIP' command.",
                                fontFamily = RobotoMonoFontFamily,
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.logs) { log ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "> [${log.step.name.take(9)}] ",
                                    fontFamily = RobotoMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = CyberTeal
                                )
                                Text(
                                    log.message,
                                    fontFamily = RobotoMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = if (log.message.contains("Error")) MaterialTheme.colorScheme.error else Slate200
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

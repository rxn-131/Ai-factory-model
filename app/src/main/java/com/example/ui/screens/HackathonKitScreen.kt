package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FactoryUiState
import com.example.ui.FactoryViewModel
import com.example.ui.theme.*

@Composable
fun HackathonKitScreen(
    state: FactoryUiState,
    viewModel: FactoryViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var rub1Checked by remember { mutableStateOf(true) }
    var rub2Checked by remember { mutableStateOf(true) }
    var rub3Checked by remember { mutableStateOf(true) }
    var rub4Checked by remember { mutableStateOf(true) }

    val totalScore = (if (rub1Checked) 20 else 0) +
                     (if (rub2Checked) 30 else 0) +
                     (if (rub3Checked) 25 else 0) +
                     (if (rub4Checked) 25 else 0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("pitch_kit_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 4-MINUTE STAGE TIMER CARD ---
        val minutes = state.timerSecondsRemaining / 60
        val seconds = state.timerSecondsRemaining % 60
        val timerColor = when {
            state.timerSecondsRemaining > 120 -> CyberTeal
            state.timerSecondsRemaining > 60 -> Color(0xFFFFBD2E)
            else -> Color(0xFFFF5F56)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = timerColor)
                .testTag("pitch_timer_card"),
            color = GlassSurface,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, timerColor.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = timerColor, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "SIH 4-MINUTE STAGE TIMER",
                            fontFamily = RobotoMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PureWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(timerColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            if (state.isTimerRunning) "LIVE COUNTDOWN" else "STANDBY",
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = timerColor
                        )
                    }
                }

                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontFamily = RobotoMonoFontFamily,
                    fontSize = 62.sp,
                    fontWeight = FontWeight.Bold,
                    color = timerColor,
                    letterSpacing = (-1).sp
                )

                // Current Slide Cue
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Text(
                        when {
                            state.timerSecondsRemaining > 180 -> "Slide 1: Problem Statement & National Impact"
                            state.timerSecondsRemaining > 120 -> "Slide 2: Breakthrough Architecture & Innovation"
                            state.timerSecondsRemaining > 60 -> "Slide 3: LIVE DEMO (Turn Airplane Mode ON!)"
                            state.timerSecondsRemaining > 0 -> "Slide 4: Scalability, Govt Integrations & Q&A Call"
                            else -> "TIME'S UP! Conclude immediately and yield to Jury Q&A."
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PureWhite,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.togglePitchTimer() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isTimerRunning) Color(0xFFFF5F56) else CyberTeal,
                            contentColor = Slate950
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp).testTag("toggle_timer_button")
                    ) {
                        Icon(if (state.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (state.isTimerRunning) "PAUSE" else "START TIMER", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetPitchTimer() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp).testTag("reset_timer_button"),
                        border = BorderStroke(1.dp, Slate700)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = Slate400)
                        Spacer(Modifier.width(6.dp))
                        Text("Reset (4:00)", color = Slate200, fontSize = 13.sp)
                    }
                }
            }
        }

        // --- SIH 100-POINT JURY RUBRIC AUDIT ---
        Surface(
            modifier = Modifier.fillMaxWidth().testTag("evaluation_rubric_card"),
            color = GlassSurface,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "SIH OFFICIAL JURY RUBRIC",
                            fontFamily = RobotoMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PureWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldMatrix.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "$totalScore / 100 PTS",
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight
                        )
                    }
                }

                Text("Audit your pitch deck against official evaluator criteria before presenting:", fontSize = 11.sp, color = Slate400)

                // Criteria 1
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = rub1Checked, onCheckedChange = { rub1Checked = it }, colors = CheckboxDefaults.colors(checkedColor = CyberTeal))
                    Column {
                        Text("1. Novelty & Innovation (20 Pts)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PureWhite)
                        Text("Offline local AI model eliminates cloud latency and per-query costs.", fontSize = 11.sp, color = Slate400)
                    }
                }

                // Criteria 2
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = rub2Checked, onCheckedChange = { rub2Checked = it }, colors = CheckboxDefaults.colors(checkedColor = CyberTeal))
                    Column {
                        Text("2. Technical Feasibility & Architecture (30 Pts)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PureWhite)
                        Text("Modular Clean Architecture, Write-Ahead-Log durability, CRDT peer sync.", fontSize = 11.sp, color = Slate400)
                    }
                }

                // Criteria 3
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = rub3Checked, onCheckedChange = { rub3Checked = it }, colors = CheckboxDefaults.colors(checkedColor = CyberTeal))
                    Column {
                        Text("3. Scalability & National Deployment (25 Pts)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PureWhite)
                        Text("Runs on budget Android devices; integrates with Digital India APIs.", fontSize = 11.sp, color = Slate400)
                    }
                }

                // Criteria 4
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = rub4Checked, onCheckedChange = { rub4Checked = it }, colors = CheckboxDefaults.colors(checkedColor = CyberTeal))
                    Column {
                        Text("4. Social Impact & Inclusiveness (25 Pts)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PureWhite)
                        Text("Vernacular regional language support with voice feedback for rural citizens.", fontSize = 11.sp, color = Slate400)
                    }
                }
            }
        }

        // --- STRATEGIC JURY DEFENSE CARDS ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = GlassSurface,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "DEFENSE REBUTTALS (WINNING ANSWERS)",
                    fontFamily = RobotoMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = EmeraldLight
                )

                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Jury: 'Why not cloud APIs like AWS Bedrock or OpenAI?'", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyberTeal)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Defense: 'Cloud APIs collapse in 2G/disaster dead-zones and incur unsustainable recurring costs. Our on-device model runs 100% offline with zero operational cost and sub-30ms latency.'",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = Slate200
                        )
                    }
                }

                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Jury: 'How will you keep models updated on the edge?'", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyberTeal)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Defense: 'Delta INT8 updates (< 2MB) synced opportunistically whenever a device encounters a Wi-Fi gateway or peer-to-peer supervisor mesh packet.'",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = Slate200
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

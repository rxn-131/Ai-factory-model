package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FactoryUiState
import com.example.ui.FactoryViewModel
import com.example.ui.NavigationTab
import com.example.ui.theme.*

@Composable
fun ExportsScreen(
    state: FactoryUiState,
    viewModel: FactoryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("exports_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val project = state.currentProject
        if (project == null) {
            Surface(
                modifier = Modifier.fillMaxWidth().testTag("no_active_export_card"),
                color = GlassSurface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberTeal.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(30.dp))
                    }
                    Text("No Exported ZIP Archive", fontFamily = OutfitFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PureWhite)
                    Text(
                        "Generate a project from the Spec Studio tab to pack your offline hackathon codebase.",
                        fontSize = 12.sp,
                        color = Slate400,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.selectTab(NavigationTab.STUDIO) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberTeal, contentColor = Slate950),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Open Spec Studio", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Active ZIP Package Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = CyberTeal)
                    .testTag("active_export_card"),
                color = GlassSurface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, CyberTeal.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CyberTeal.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FolderZip, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(24.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "${project.name.lowercase().replace(" ", "_")}_sih.zip",
                                    fontFamily = RobotoMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = PureWhite
                                )
                                Text(
                                    "${project.formattedSize} • ${project.fileCount} Files • ${project.totalLinesOfCode} LOC",
                                    fontFamily = RobotoMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = CyberTeal
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldMatrix.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("READY", fontFamily = RobotoMonoFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                        }
                    }

                    // Checksum Display
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate950)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "SHA-256 INTEGRITY CHECKSUM",
                                fontFamily = RobotoMonoFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate400
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("SHA-256", project.sha256Checksum)
                                    clipboard.setPrimaryClip(clip)
                                },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Hash", tint = CyberTeal, modifier = Modifier.size(13.dp))
                            }
                        }
                        Text(
                            project.sha256Checksum.chunked(32).joinToString("\n"),
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 10.5.sp,
                            color = EmeraldLight
                        )
                    }

                    // Primary Export Action Buttons
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                val shareIntent = viewModel.getShareIntent()
                                if (shareIntent != null) {
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Project ZIP via"))
                                }
                            },
                            modifier = Modifier.weight(1f).height(50.dp).testTag("share_zip_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberTeal, contentColor = Slate950),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Share ZIP", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = { viewModel.exportToDownloads() },
                            modifier = Modifier.weight(1f).height(50.dp).testTag("save_downloads_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = PureWhite),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.SaveAlt, contentDescription = null, modifier = Modifier.size(18.dp), tint = CyberTeal)
                            Spacer(Modifier.width(8.dp))
                            Text("Save Downloads", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // --- SESSION HISTORY ARCHIVE ---
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "GENERATED PACKAGES ARCHIVE",
                fontFamily = RobotoMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Slate200
            )

            if (state.projectHistory.isEmpty()) {
                Text(
                    "No previous exports in this session.",
                    fontSize = 12.sp,
                    color = Slate400
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.testTag("history_list")) {
                    state.projectHistory.forEach { hist ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = GlassSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Slate800)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(hist.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PureWhite)
                                    Text(
                                        "${hist.spec.techStack.displayName} • ${hist.formattedSize} • ${hist.fileCount} files",
                                        fontFamily = RobotoMonoFontFamily,
                                        fontSize = 11.sp,
                                        color = Slate400
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val shareIntent = viewModel.getShareIntent()
                                        if (shareIntent != null) {
                                            context.startActivity(Intent.createChooser(shareIntent, "Share Project ZIP via"))
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = CyberTeal)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- DEPLOYMENT & GITHUB INSTRUCTIONS ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = GlassSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "GITHUB DEPLOYMENT STEPS",
                        fontFamily = RobotoMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PureWhite
                    )
                }

                Text(
                    "1. Export the ZIP to your phone or computer.\n" +
                    "2. Unzip into your project directory.\n" +
                    "3. Run: `git init && git add . && git commit -m 'Initial commit from AI Factory'`\n" +
                    "4. Push to your GitHub repo (CI/CD pipeline already configured in `.github/workflows/ci.yml`).",
                    fontFamily = RobotoMonoFontFamily,
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    color = Slate400
                )
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

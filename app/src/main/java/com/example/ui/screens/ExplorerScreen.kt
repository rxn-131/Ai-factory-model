package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProjectFile
import com.example.ui.FactoryUiState
import com.example.ui.FactoryViewModel
import com.example.ui.NavigationTab
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerScreen(
    state: FactoryUiState,
    viewModel: FactoryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isEditMode by remember { mutableStateOf(false) }

    val project = state.currentProject
    if (project == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianBlack)
                .padding(24.dp)
                .testTag("empty_explorer_state"),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = GlassSurface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Slate800),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CyberTeal.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CodeOff,
                            contentDescription = null,
                            tint = CyberTeal,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        "Codebase Explorer Idle",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = PureWhite
                    )
                    Text(
                        "Head to the Spec Studio tab and hit 'Compile Complete Project ZIP' to inspect real source code files.",
                        fontSize = 13.sp,
                        color = Slate400,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
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
        }
        return
    }

    val filteredFiles = remember(project.files, state.fileSearchQuery) {
        if (state.fileSearchQuery.isBlank()) project.files
        else project.files.filter { it.path.contains(state.fileSearchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(12.dp)
            .testTag("explorer_screen_content"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // --- SEARCH & FILE COUNT HUD ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.fileSearchQuery,
                onValueChange = { viewModel.setFileSearchQuery(it) },
                placeholder = { Text("Filter files (.kt, .tsx, README, etc.)...", fontSize = 11.sp, color = Slate400) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyberTeal) },
                modifier = Modifier.weight(1f).testTag("file_search_input"),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = RobotoMonoFontFamily, color = PureWhite),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Slate950,
                    unfocusedContainerColor = Slate950,
                    focusedBorderColor = CyberTeal,
                    unfocusedBorderColor = Slate800
                )
            )

            Surface(
                color = GlassSurface,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Text(
                    "${project.fileCount} FILES",
                    fontFamily = RobotoMonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTeal,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)
                )
            }
        }

        // --- SPLIT PANE: FILE TREE & SOURCE CODE VIEWER ---
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Left Pane: File List
            Surface(
                modifier = Modifier
                    .width(155.dp)
                    .fillMaxHeight()
                    .testTag("files_list_pane"),
                color = GlassSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    Text(
                        "PROJECT TREE",
                        fontFamily = RobotoMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = CyberTeal
                    )
                    Spacer(Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredFiles) { file ->
                            val isSelected = state.selectedFile?.path == file.path
                            val extColor = when {
                                file.fileName.endsWith(".kt") -> Color(0xFFA78BFA)
                                file.fileName.endsWith(".tsx") || file.fileName.endsWith(".ts") -> Color(0xFF38BDF8)
                                file.fileName.endsWith(".py") -> Color(0xFFFBBF24)
                                file.fileName.endsWith(".md") -> EmeraldLight
                                file.fileName.endsWith(".sql") -> Color(0xFFFB923C)
                                file.fileName.endsWith(".json") -> Color(0xFFFDE047)
                                else -> Slate400
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.selectFile(file)
                                        isEditMode = false
                                    }
                                    .testTag("file_item_${file.fileName}"),
                                color = if (isSelected) Slate800 else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                border = if (isSelected) BorderStroke(1.dp, CyberTeal.copy(alpha = 0.5f)) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(extColor)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            file.fileName,
                                            fontFamily = RobotoMonoFontFamily,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) CyberTeal else PureWhite,
                                            maxLines = 1
                                        )
                                        Text(
                                            if (file.directory.isNotEmpty()) file.directory else "./",
                                            fontSize = 9.sp,
                                            color = Slate400,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Right Pane: Source Code Viewer & Editor
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("code_viewer_pane"),
                color = Slate950,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                val currentFile = state.selectedFile
                if (currentFile == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Select a file to inspect source code",
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        // Header Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    currentFile.path,
                                    fontFamily = RobotoMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = CyberTeal
                                )
                                Text(
                                    "${currentFile.lineCount} lines • ${currentFile.byteSize / 1024.0} KB",
                                    fontFamily = RobotoMonoFontFamily,
                                    fontSize = 9.sp,
                                    color = Slate400
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText(currentFile.fileName, currentFile.content)
                                        clipboard.setPrimaryClip(clip)
                                    },
                                    modifier = Modifier.size(32.dp).testTag("copy_file_button")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberTeal, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = { isEditMode = !isEditMode },
                                    modifier = Modifier.size(32.dp).testTag("toggle_edit_button")
                                ) {
                                    Icon(
                                        if (isEditMode) Icons.Default.Visibility else Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = if (isEditMode) EmeraldLight else Slate200,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                if (isEditMode) {
                                    IconButton(
                                        onClick = {
                                            viewModel.saveCurrentFileEdits()
                                            isEditMode = false
                                        },
                                        modifier = Modifier.size(32.dp).testTag("save_edit_button")
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = "Save", tint = EmeraldMatrix, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate800)

                        if (isEditMode) {
                            OutlinedTextField(
                                value = state.editingFileContent ?: currentFile.content,
                                onValueChange = { viewModel.updateEditingContent(it) },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("code_editor_field"),
                                textStyle = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = RobotoMonoFontFamily,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = Slate200
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate950,
                                    unfocusedContainerColor = Slate950,
                                    focusedBorderColor = CyberTeal,
                                    unfocusedBorderColor = Slate800
                                )
                            )
                        } else {
                            val scrollV = rememberScrollState()
                            val scrollH = rememberScrollState()

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollV)
                                    .horizontalScroll(scrollH)
                                    .testTag("code_display_box")
                            ) {
                                Row {
                                    // Line Numbers
                                    Column(
                                        modifier = Modifier
                                            .background(Slate900.copy(alpha = 0.5f))
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        for (i in 1..currentFile.lineCount) {
                                            Text(
                                                "$i",
                                                fontFamily = RobotoMonoFontFamily,
                                                fontSize = 10.sp,
                                                color = Slate700,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(10.dp))

                                    // Content with Syntax Colors
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        currentFile.content.lines().forEach { line ->
                                            Text(
                                                line,
                                                fontFamily = RobotoMonoFontFamily,
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp,
                                                color = when {
                                                    line.trim().startsWith("#") || line.trim().startsWith("//") || line.trim().startsWith("--") -> CodeComment
                                                    line.trim().startsWith("import ") || line.trim().startsWith("package ") || line.trim().startsWith("from ") -> CodeKeyword
                                                    line.contains("fun ") || line.contains("class ") || line.contains("def ") || line.contains("const ") || line.contains("interface ") -> CodeFunction
                                                    line.contains("val ") || line.contains("var ") || line.contains("let ") -> CodeNumber
                                                    line.contains("\"") -> CodeString
                                                    else -> Slate200
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

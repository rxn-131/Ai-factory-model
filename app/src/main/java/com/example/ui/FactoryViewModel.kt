package com.example.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MarkdownPreset
import com.example.data.SampleMarkdownPresets
import com.example.generator.LocalFactoryEngine
import com.example.generator.LocalLlmClient
import com.example.generator.ZipPackager
import com.example.model.EngineMode
import com.example.model.GeneratedProject
import com.example.model.GenerationStep
import com.example.model.HackathonTheme
import com.example.model.ProjectFile
import com.example.model.ProjectSpec
import com.example.model.TechStack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

enum class NavigationTab(val label: String, val testTag: String) {
    STUDIO("Spec Studio", "tab_studio"),
    CONSOLE("Generation", "tab_console"),
    EXPLORER("Files & Code", "tab_explorer"),
    EXPORTS("ZIP Packages", "tab_exports"),
    HACKATHON_KIT("SIH Pitch Kit", "tab_pitch_kit")
}

data class GenerationLogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val step: GenerationStep,
    val message: String
)

data class FactoryUiState(
    val currentTab: NavigationTab = NavigationTab.STUDIO,
    // Spec Config
    val projectName: String = "KisanDrishti",
    val problemStatementId: String = "SIH-1428",
    val authorTeam: String = "Team Garuda Hackers",
    val selectedStack: TechStack = TechStack.ANDROID_COMPOSE,
    val selectedTheme: HackathonTheme = HackathonTheme.AGRITECH,
    val markdownInput: String = SampleMarkdownPresets.presets.first().markdown,
    val includePitchKit: Boolean = true,
    val includeDocker: Boolean = true,
    // Local AI Model Configuration (100% Offline & Independent of APIs)
    val engineMode: EngineMode = EngineMode.LOCAL_NEURAL_BUILTIN,
    val localModelTemperature: Float = 0.2f,
    val localModelContextTokens: Int = 4096,
    val localGgufFilePath: String = "/sdcard/Download/qwen2.5-coder-1.5b-q4.gguf",
    val localLlmUrl: String = "http://127.0.0.1:11434",
    val localLlmModel: String = "qwen2.5-coder:latest",
    val localLlmTesting: Boolean = false,
    val localLlmStatusMessage: String? = null,
    val tokensGeneratedCount: Int = 0,
    val tokensPerSecond: Float = 88.5f,
    // Generation Process
    val isGenerating: Boolean = false,
    val currentStep: GenerationStep? = null,
    val logs: List<GenerationLogEntry> = emptyList(),
    // Active Project & Files
    val currentProject: GeneratedProject? = null,
    val selectedFile: ProjectFile? = null,
    val fileSearchQuery: String = "",
    val editingFileContent: String? = null,
    // History
    val projectHistory: List<GeneratedProject> = emptyList(),
    // Pitch Kit Timer
    val timerSecondsRemaining: Int = 240, // 4 minutes standard SIH pitch
    val isTimerRunning: Boolean = false,
    // User feedback
    val notificationMessage: String? = null
)

class FactoryViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(FactoryUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Initial state is preloaded with first preset
    }

    fun selectTab(tab: NavigationTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun applyPreset(preset: MarkdownPreset) {
        _uiState.update {
            it.copy(
                projectName = preset.title.substringBefore(':').trim(),
                problemStatementId = preset.problemId,
                selectedStack = preset.recommendedStack,
                selectedTheme = preset.theme,
                markdownInput = preset.markdown,
                notificationMessage = "Loaded preset: ${preset.title}"
            )
        }
    }

    fun updateProjectName(name: String) = _uiState.update { it.copy(projectName = name) }
    fun updateProblemId(id: String) = _uiState.update { it.copy(problemStatementId = id) }
    fun updateAuthorTeam(team: String) = _uiState.update { it.copy(authorTeam = team) }
    fun updateTechStack(stack: TechStack) = _uiState.update { it.copy(selectedStack = stack) }
    fun updateTheme(theme: HackathonTheme) = _uiState.update { it.copy(selectedTheme = theme) }
    fun updateMarkdown(md: String) = _uiState.update { it.copy(markdownInput = md) }
    fun updateEngineMode(mode: EngineMode) = _uiState.update { it.copy(engineMode = mode) }
    fun updateLocalModelTemperature(temp: Float) = _uiState.update { it.copy(localModelTemperature = temp) }
    fun updateLocalGgufPath(path: String) = _uiState.update { it.copy(localGgufFilePath = path) }
    fun updateLocalLlmUrl(url: String) = _uiState.update { it.copy(localLlmUrl = url) }
    fun updateLocalLlmModel(model: String) = _uiState.update { it.copy(localLlmModel = model) }
    fun setIncludePitchKit(inc: Boolean) = _uiState.update { it.copy(includePitchKit = inc) }
    fun setIncludeDocker(inc: Boolean) = _uiState.update { it.copy(includeDocker = inc) }
    fun setFileSearchQuery(query: String) = _uiState.update { it.copy(fileSearchQuery = query) }
    fun clearNotification() = _uiState.update { it.copy(notificationMessage = null) }

    fun selectFile(file: ProjectFile?) {
        _uiState.update {
            it.copy(
                selectedFile = file,
                editingFileContent = file?.content
            )
        }
    }

    fun updateEditingContent(content: String) {
        _uiState.update { it.copy(editingFileContent = content) }
    }

    fun saveCurrentFileEdits() {
        val activeProj = _uiState.value.currentProject ?: return
        val currentFile = _uiState.value.selectedFile ?: return
        val newContent = _uiState.value.editingFileContent ?: return

        val updatedFiles = activeProj.files.map {
            if (it.path == currentFile.path) it.copy(content = newContent) else it
        }

        // Re-package zip with edited files
        val context = getApplication<Application>()
        val result = ZipPackager.createProjectZip(context, activeProj.name, updatedFiles)

        val updatedProj = activeProj.copy(
            files = updatedFiles,
            zipFilePath = result.zipFile.absolutePath,
            zipFileSizeBytes = result.sizeBytes,
            sha256Checksum = result.sha256
        )

        _uiState.update {
            it.copy(
                currentProject = updatedProj,
                selectedFile = currentFile.copy(content = newContent),
                notificationMessage = "Saved edits & re-packaged ZIP!"
            )
        }
    }

    fun testLocalLlm() {
        viewModelScope.launch {
            _uiState.update { it.copy(localLlmTesting = true, localLlmStatusMessage = "Connecting to ${_uiState.value.localLlmUrl}...") }
            val result = LocalLlmClient.testConnection(_uiState.value.localLlmUrl)
            _uiState.update {
                it.copy(
                    localLlmTesting = false,
                    localLlmStatusMessage = result.message
                )
            }
        }
    }

    fun startGeneration() {
        val state = _uiState.value
        val spec = ProjectSpec(
            projectName = state.projectName.ifBlank { "AIFactoryApp" },
            description = "Smart India Hackathon project codebase generated by AI Factory",
            markdownInput = state.markdownInput,
            techStack = state.selectedStack,
            theme = state.selectedTheme,
            authorTeam = state.authorTeam.ifBlank { "Hackathon Team" },
            problemStatementId = state.problemStatementId.ifBlank { "SIH-2026" },
            includePitchKit = state.includePitchKit,
            includeDocker = state.includeDocker
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isGenerating = true,
                    currentTab = NavigationTab.CONSOLE,
                    logs = listOf(GenerationLogEntry(message = "Starting AI Factory autonomous compilation pipeline...", step = GenerationStep.PARSING_SPEC)),
                    currentStep = GenerationStep.PARSING_SPEC
                )
            }

            try {
                val files = LocalFactoryEngine.generateCompleteProject(spec) { step, log ->
                    _uiState.update { current ->
                        current.copy(
                            currentStep = step,
                            logs = current.logs + GenerationLogEntry(step = step, message = log)
                        )
                    }
                }

                // Create the physical ZIP file
                val context = getApplication<Application>()
                val packagingResult = ZipPackager.createProjectZip(context, spec.projectName, files)

                val generatedProject = GeneratedProject(
                    id = "proj_${System.currentTimeMillis()}",
                    name = spec.projectName,
                    spec = spec,
                    files = files,
                    zipFilePath = packagingResult.zipFile.absolutePath,
                    zipFileSizeBytes = packagingResult.sizeBytes,
                    sha256Checksum = packagingResult.sha256
                )

                _uiState.update { current ->
                    current.copy(
                        isGenerating = false,
                        currentStep = null,
                        currentProject = generatedProject,
                        selectedFile = files.firstOrNull { it.fileName == "README.md" } ?: files.firstOrNull(),
                        projectHistory = listOf(generatedProject) + current.projectHistory.take(9),
                        logs = current.logs + GenerationLogEntry(
                            step = GenerationStep.ZIP_PACKAGING,
                            message = "SUCCESS! Generated ${files.size} project files in ${packagingResult.sizeBytes / 1024} KB zip."
                        ),
                        notificationMessage = "Project ZIP successfully generated!"
                    )
                }

            } catch (e: Exception) {
                _uiState.update { current ->
                    current.copy(
                        isGenerating = false,
                        currentStep = null,
                        logs = current.logs + GenerationLogEntry(
                            step = GenerationStep.ZIP_PACKAGING,
                            message = "Error during generation: ${e.localizedMessage}"
                        ),
                        notificationMessage = "Generation failed: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun exportToDownloads() {
        val proj = _uiState.value.currentProject ?: return
        val zipPath = proj.zipFilePath ?: return
        val file = File(zipPath)
        if (!file.exists()) {
            _uiState.update { it.copy(notificationMessage = "ZIP file not found on disk") }
            return
        }

        val result = ZipPackager.exportToPublicDownloads(getApplication(), file, proj.name)
        result.onSuccess { msg ->
            _uiState.update { it.copy(notificationMessage = msg) }
        }.onFailure { err ->
            _uiState.update { it.copy(notificationMessage = "Failed to export: ${err.localizedMessage}") }
        }
    }

    fun getShareIntent(): Intent? {
        val proj = _uiState.value.currentProject ?: return null
        val zipPath = proj.zipFilePath ?: return null
        val file = File(zipPath)
        if (!file.exists()) return null
        return ZipPackager.getShareIntent(getApplication(), file, proj.name)
    }

    // --- PITCH TIMER LOGIC ---

    fun togglePitchTimer() {
        if (_uiState.value.isTimerRunning) {
            pausePitchTimer()
        } else {
            startPitchTimer()
        }
    }

    private fun startPitchTimer() {
        _uiState.update { it.copy(isTimerRunning = true) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isTimerRunning && _uiState.value.timerSecondsRemaining > 0) {
                delay(1000)
                _uiState.update {
                    val next = it.timerSecondsRemaining - 1
                    it.copy(
                        timerSecondsRemaining = next,
                        isTimerRunning = next > 0
                    )
                }
            }
        }
    }

    private fun pausePitchTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }

    fun resetPitchTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerSecondsRemaining = 240, isTimerRunning = false) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

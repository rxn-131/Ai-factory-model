package com.example.model

enum class TechStack(val displayName: String, val category: String, val extension: String, val iconDescription: String) {
    ANDROID_COMPOSE("Android Jetpack Compose", "Mobile", "kt", "Kotlin Native MVVM + Room"),
    REACT_TAILWIND("React + Vite + TypeScript", "Web", "tsx", "Modern SPA with Tailwind CSS"),
    FASTAPI_AI("FastAPI + Python AI", "Backend & ML", "py", "Async REST + PyTorch/ONNX Pipeline"),
    NEXTJS_FULLSTACK("Next.js 14 App Router", "Fullstack", "tsx", "Server Components + Prisma ORM"),
    FLUTTER_DART("Flutter Clean Architecture", "Cross-Platform", "dart", "BLoC/Riverpod Mobile App"),
    IOT_MICROSERVICES("IoT Gateway + Node.js", "Systems & Edge", "js", "MQTT Broker + Realtime Dashboard")
}

enum class HackathonTheme(val label: String, val badgeColorHex: Long) {
    AGRITECH("🌾 Agriculture & Rural Tech", 0xFF10B981),
    HEALTHCARE("🚑 Healthcare & Biomedical", 0xFF00E5FF),
    DISASTER_MGMT("🌊 Disaster Management & Relief", 0xFFF59E0B),
    SMART_CITIES("🚦 Smart Mobility & Cities", 0xFF8B5CF6),
    CYBER_SECURITY("🛡️ Cyber Security & Defense", 0xFFEF4444),
    SMART_EDUCATION("📚 Smart Vernacular Education", 0xFF38BDF8),
    CLEAN_ENERGY("⚡ Clean Energy & Climate", 0xFF10B981),
    OPEN_INNOVATION("🚀 Open Innovation", 0xFFEC4899)
}

enum class EngineMode(val title: String, val subtitle: String, val badge: String) {
    LOCAL_NEURAL_BUILTIN("On-Device Local AI Model", "100% Offline • Zero API • Built-in Neural Generator", "DEFAULT"),
    LOCAL_GGUF_LOADER("Device Storage GGUF / ONNX", "Load local model weights from device storage", "OFFLINE"),
    LOCAL_HOST_DAEMON("Localhost LLM Daemon (Termux/Ollama)", "Connect to local model running on 127.0.0.1:11434", "LOCAL")
}

data class ProjectFile(
    val path: String,
    val content: String,
    val language: String = "text",
    val isCore: Boolean = false
) {
    val fileName: String
        get() = path.substringAfterLast('/')

    val directory: String
        get() = if (path.contains('/')) path.substringBeforeLast('/') else ""

    val lineCount: Int
        get() = content.lines().size

    val byteSize: Int
        get() = content.toByteArray().size
}

data class ProjectSpec(
    val projectName: String,
    val description: String,
    val markdownInput: String,
    val techStack: TechStack = TechStack.ANDROID_COMPOSE,
    val theme: HackathonTheme = HackathonTheme.AGRITECH,
    val authorTeam: String = "SIH Finalists Team",
    val problemStatementId: String = "SIH-2026-AI",
    val targetVersion: String = "1.0.0",
    val includePitchKit: Boolean = true,
    val includeDocker: Boolean = true,
    val includeArchitectureDiagram: Boolean = true
)

data class GeneratedProject(
    val id: String,
    val name: String,
    val spec: ProjectSpec,
    val files: List<ProjectFile>,
    val timestamp: Long = System.currentTimeMillis(),
    val zipFilePath: String? = null,
    val zipFileSizeBytes: Long = 0,
    val sha256Checksum: String = ""
) {
    val totalLinesOfCode: Int
        get() = files.sumOf { it.lineCount }

    val fileCount: Int
        get() = files.size

    val formattedSize: String
        get() {
            if (zipFileSizeBytes <= 0) return "${(files.sumOf { it.byteSize } / 1024)} KB (uncompressed)"
            val kb = zipFileSizeBytes / 1024.0
            return if (kb > 1024) String.format("%.2f MB", kb / 1024.0) else String.format("%.1f KB", kb)
        }
}

enum class GenerationStep(val stepNumber: Int, val title: String, val detail: String) {
    PARSING_SPEC(1, "Parsing Markdown Spec", "Extracting domain entities, user stories & constraints"),
    ARCHITECTING(2, "Synthesizing System Architecture", "Generating microservices, data flow & ER diagrams"),
    SCHEMA_DESIGN(3, "Data Modeling & Contracts", "Designing database schemas, models & OpenAPI specs"),
    CODE_SYNTHESIS(4, "Compiling Source Code", "Writing production components, services & business logic"),
    BUILD_CONFIG(5, "Generating Build & DevOps", "Crafting package.json, Dockerfile & CI/CD workflows"),
    PITCH_ASSEMBLY(6, "Generating Hackathon Pitch Kit", "Creating jury defense notes & evaluation checklists"),
    ZIP_PACKAGING(7, "Packaging Project ZIP", "Compressing files & verifying SHA256 integrity")
}

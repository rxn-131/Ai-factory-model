package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.generator.LocalFactoryEngine
import com.example.generator.ZipPackager
import com.example.model.HackathonTheme
import com.example.model.ProjectSpec
import com.example.model.TechStack
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AI Factory", appName)
    }

    @Test
    fun `test autonomous project generation and zip packaging`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val spec = ProjectSpec(
            projectName = "TestHackProject",
            description = "Test SIH project",
            markdownInput = "# TestHack\n- **FR-1:** Real-time sensor processing\n`SensorData`: id, reading",
            techStack = TechStack.ANDROID_COMPOSE,
            theme = HackathonTheme.AGRITECH,
            authorTeam = "Test Team"
        )

        val files = LocalFactoryEngine.generateCompleteProject(spec) { _, _ -> }
        assertTrue("Generated files should not be empty", files.isNotEmpty())
        assertTrue("Should include README.md", files.any { it.fileName == "README.md" })
        assertTrue("Should include ARCHITECTURE.md", files.any { it.fileName == "ARCHITECTURE.md" })

        val result = ZipPackager.createProjectZip(context, spec.projectName, files)
        assertNotNull(result.zipFile)
        assertTrue("ZIP file should exist", result.zipFile.exists())
        assertTrue("ZIP file should have non-zero size", result.sizeBytes > 0)
        assertTrue("SHA256 checksum should be generated", result.sha256.isNotBlank())
    }
}

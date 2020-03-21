package org.moallemi.gradle.advancedbuildversion

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verifyOrder
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import org.moallemi.gradle.advancedbuildversion.utils.checkAndroidGradleVersion
import org.moallemi.gradle.advancedbuildversion.utils.checkMinimumGradleVersion

class AdvancedBuildVersionPluginTest {

    private val project: Project = mockk()

    private lateinit var plugin: AdvancedBuildVersionPlugin

    @Before
    fun setUp() {
        mockkStatic("org.moallemi.gradle.advancedbuildversion.utils.CompatibilityManagerKt")
        every { checkAndroidGradleVersion(any()) } just runs

        every { project.extensions } returns mockk()
        every {
            project.extensions.create(
                any(),
                AdvancedBuildVersionConfig::class.java,
                any<Project>()
            )
        } returns mockk()

        val afterEvaluateSlot = slot<Action<in Project>>()
        every {
            project.afterEvaluate(capture(afterEvaluateSlot))
        } answers { afterEvaluateSlot.captured.execute(project) }

        val applicationPlugin = mockk<AppPlugin>()
        val pluginsSlot = slot<Action<in Plugin<*>>>()
        every {
            project.plugins.all(capture(pluginsSlot))
        } just runs
        // answers { pluginsSlot.captured.execute(applicationPlugin) }

        every {
            project.extensions.getByType(AppExtension::class.java)
        } returns mockk() {
            every { applicationVariants } returns mockk()
        }

        plugin = AdvancedBuildVersionPlugin()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test plugin applies successfully`() {

        plugin.apply(project)

        verifyOrder {
            checkMinimumGradleVersion()
            checkAndroidGradleVersion(project)
        }
    }
}

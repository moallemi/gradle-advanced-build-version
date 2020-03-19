package org.moallemi.gradle.advancedbuildversion

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verifyOrder
import org.gradle.api.Action
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
        every { project.afterEvaluate(any<Action<in Project>>()) } just runs

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

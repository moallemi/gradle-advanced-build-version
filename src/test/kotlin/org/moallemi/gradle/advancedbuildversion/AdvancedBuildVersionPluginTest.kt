package org.moallemi.gradle.advancedbuildversion

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.FeaturePlugin
import com.android.build.gradle.LibraryPlugin
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
import org.gradle.internal.impldep.org.eclipse.jgit.errors.NotSupportedException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.moallemi.gradle.advancedbuildversion.AdvancedBuildVersionPlugin.Companion.EXTENSION_NAME
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

        plugin = AdvancedBuildVersionPlugin()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `plugin applies successfully on Android Application module`() {

        val applicationPlugin = mockk<AppPlugin>()
        val pluginsSlot = slot<Action<in Plugin<*>>>()
        every {
            project.plugins.all(capture(pluginsSlot))
        } answers { pluginsSlot.captured.execute(applicationPlugin) }

        every {
            project.extensions.create(
                EXTENSION_NAME, AdvancedBuildVersionConfig::class.java, project
            )
        } returns mockk(relaxUnitFun = true)

        every {
            project.extensions.getByType(AppExtension::class.java)
        } returns mockk {
            every { applicationVariants } returns mockk()
        }

        plugin.apply(project)

        verifyOrder {
            checkMinimumGradleVersion()
            checkAndroidGradleVersion(project)
        }
    }

    @Test
    fun `fails on applying to Android Library module`() {

        val libraryPlugin = mockk<LibraryPlugin>()
        val pluginsSlot = slot<Action<in Plugin<*>>>()
        every {
            project.plugins.all(capture(pluginsSlot))
        } answers { pluginsSlot.captured.execute(libraryPlugin) }

        val exception = assertThrows(NotSupportedException::class.java) {
            plugin.apply(project)
        }
        assertEquals(exception.message, "Library module is not supported yet")

        verifyOrder {
            checkMinimumGradleVersion()
            checkAndroidGradleVersion(project)
        }
    }

    @Test
    fun `fails on applying to Android Feature module`() {

        val featurePlugin = mockk<FeaturePlugin>()
        val pluginsSlot = slot<Action<in Plugin<*>>>()
        every {
            project.plugins.all(capture(pluginsSlot))
        } answers { pluginsSlot.captured.execute(featurePlugin) }

        val exception = assertThrows(NotSupportedException::class.java) {
            plugin.apply(project)
        }
        assertEquals(exception.message, "Feature module is not supported")

        verifyOrder {
            checkMinimumGradleVersion()
            checkAndroidGradleVersion(project)
        }
    }
}

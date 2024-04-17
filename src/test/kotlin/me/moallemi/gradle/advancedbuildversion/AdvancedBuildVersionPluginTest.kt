/*
 * Copyright 2020 Reza Moallemi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.moallemi.gradle.advancedbuildversion

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.FeaturePlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verifyOrder
import me.moallemi.gradle.advancedbuildversion.AdvancedBuildVersionPlugin.Companion.EXTENSION_NAME
import me.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import me.moallemi.gradle.advancedbuildversion.utils.ANDROID_GRADLE_PLUGIN_ATTRIBUTE_ID
import me.moallemi.gradle.advancedbuildversion.utils.ANDROID_GRADLE_PLUGIN_GROUP
import me.moallemi.gradle.advancedbuildversion.utils.checkAndroidGradleVersion
import me.moallemi.gradle.advancedbuildversion.utils.checkMinimumGradleVersion
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class AdvancedBuildVersionPluginTest {

    private val project: Project = mockk()

    private lateinit var plugin: AdvancedBuildVersionPlugin

    @Before
    fun setUp() {
        mockkStatic("me.moallemi.gradle.advancedbuildversion.utils.CompatibilityManagerKt")
        every { checkAndroidGradleVersion(any()) } just runs

        every { project.extensions } returns mockk()
        every {
            project.extensions.create(
                any(),
                AdvancedBuildVersionConfig::class.java,
                any<Project>(),
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
                EXTENSION_NAME, AdvancedBuildVersionConfig::class.java, project,
            )
        } returns mockk(relaxUnitFun = true)

        every {
            project.extensions.getByType(AppExtension::class.java)
        } returns mockk {
            every { applicationVariants } returns mockk()
        }

        every {
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        } returns mockk(relaxed = true, relaxUnitFun = true)

        every {
            project.extensions.getByType(BaseAppModuleExtension::class.java)
        } returns mockk(relaxed = true, relaxUnitFun = true)

        mockGetAndroidPlugin()

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

        val exception = assertThrows(IllegalStateException::class.java) {
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

        val exception = assertThrows(IllegalStateException::class.java) {
            plugin.apply(project)
        }
        assertEquals(exception.message, "Feature module is not supported")

        verifyOrder {
            checkMinimumGradleVersion()
            checkAndroidGradleVersion(project)
        }
    }

    private fun mockGetAndroidPlugin() {
        every { project.rootProject } returns mockk {
            every { buildscript } returns mockk {
                every { configurations } returns mockk {
                    every {
                        getByName("classpath").dependencies
                    } returns mockk {
                        every { iterator() } returns mockk()
                        every { iterator().hasNext() } returns true
                        every { iterator().next() } returns mockk {
                            every { group } returns ANDROID_GRADLE_PLUGIN_GROUP
                            every { name } returns ANDROID_GRADLE_PLUGIN_ATTRIBUTE_ID
                            every { version } returns "3.0.1"
                        }
                    }
                }
            }
        }
        every {
            project.buildscript.configurations.getByName("classpath").dependencies
        } returns mockk {
            every { iterator() } returns mockk()
            every { iterator().hasNext() } returns false
        }

        every { project.plugins.hasPlugin(any<String>()) } returns true
    }
}
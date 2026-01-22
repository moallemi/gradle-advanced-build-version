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
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AdvancedBuildVersionPluginTest {

    @get:Rule
    var testProjectRoot = TemporaryFolder()

    private val project: Project = mockk()

    private lateinit var plugin: AdvancedBuildVersionPlugin

    @Before
    fun setUp() {
        mockkStatic("me.moallemi.gradle.advancedbuildversion.utils.CompatibilityManagerKt")
        every { checkAndroidGradleVersion(any()) } just runs

        // Configure project mocks for configuration cache compatible constructor
        every { project.projectDir } returns testProjectRoot.root
        every { project.name } returns "testProject"
        every { project.rootProject } returns mockk {
            every { name } returns "rootProject"
        }
        every { project.providers } returns mockk(relaxed = true)
        every { project.gradle } returns mockk {
            every { startParameter } returns mockk {
                every { taskNames } returns emptyList()
            }
        }

        every { project.extensions } returns mockk()
        every {
            project.extensions.add(EXTENSION_NAME, any<AdvancedBuildVersionConfig>())
        } just runs

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
        val withIdSlot = slot<Action<Plugin<*>>>()
        every {
            project.plugins.withId(eq("com.android.application"), capture(withIdSlot))
        } answers { withIdSlot.captured.execute(mockk<Plugin<*>>()) }

        every {
            project.plugins.withId(eq("com.android.library"), any<Action<Plugin<*>>>())
        } just runs

        every {
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
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
        every {
            project.plugins.withId(eq("com.android.application"), any<Action<Plugin<*>>>())
        } just runs

        val withIdSlot = slot<Action<Plugin<*>>>()
        every {
            project.plugins.withId(eq("com.android.library"), capture(withIdSlot))
        } answers { withIdSlot.captured.execute(mockk<Plugin<*>>()) }

        val exception = assertThrows(IllegalStateException::class.java) {
            plugin.apply(project)
        }
        assertEquals(exception.message, "Library module is not supported yet")

        verifyOrder {
            checkMinimumGradleVersion()
            checkAndroidGradleVersion(project)
        }
    }

    private fun mockGetAndroidPlugin() {
        every { project.rootProject } returns mockk {
            every { name } returns "rootProject"
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
                            every { version } returns "9.0.0"
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
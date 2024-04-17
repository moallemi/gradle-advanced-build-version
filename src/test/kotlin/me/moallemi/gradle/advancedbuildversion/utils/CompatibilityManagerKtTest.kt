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

package me.moallemi.gradle.advancedbuildversion.utils

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.util.GradleVersion
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CompatibilityManagerKtTest {

    private val project: Project = mockk()

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `android gradle plugin is not available in root project and project`() {
        every { project.rootProject } returns project
        every {
            project.buildscript.configurations.getByName("classpath").dependencies
        } returns mockk {
            every { iterator() } returns mockk()
            every { iterator().hasNext() } returns false
        }

        every { project.plugins.hasPlugin(any<String>()) } returns false

        val exception = assertThrows(GradleException::class.java) {
            checkAndroidGradleVersion(project)
        }
        assertEquals(
            exception.message,
            "gradle-advanced-build-version only works with android application modules",
        )
    }

    @Test
    fun `android gradle plugin is available in root project with unsupported version`() {
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
                            every { version } returns "2.0"
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

        val exception = assertThrows(GradleException::class.java) {
            checkAndroidGradleVersion(project)
        }
        assertEquals(
            exception.message,
            "gradle-advanced-build-version does not support Android Gradle plugin 2.0." +
                " Minimum supported version is $ANDROID_GRADLE_MIN_VERSION.",
        )
    }

    @Test
    fun `android gradle plugin is available in root project with unknown version`() {
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
                            every { version } returns null
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

        val exception = assertThrows(GradleException::class.java) {
            checkAndroidGradleVersion(project)
        }
        assertEquals(
            exception.message,
            "gradle-advanced-build-version does not support Android Gradle plugin null." +
                " Minimum supported version is $ANDROID_GRADLE_MIN_VERSION.",
        )
    }

    @Test
    fun `android gradle plugin is available in root project but not applied to root project`() {
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
                            every { version } returns ANDROID_GRADLE_MIN_VERSION
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

        every { project.plugins.hasPlugin(any<String>()) } returns false

        val exception = assertThrows(GradleException::class.java) {
            checkAndroidGradleVersion(project)
        }
        assertEquals(
            exception.message,
            "gradle-advanced-build-version only works with android application modules",
        )
    }

    @Test
    fun `android gradle plugin is available in root project and applied to root project`() {
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
                            every { version } returns ANDROID_GRADLE_MIN_VERSION
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

        checkAndroidGradleVersion(project)
    }

    @Test
    fun `android gradle plugin is available in project with unsupported version`() {
        every {
            project.buildscript.configurations.getByName("classpath").dependencies
        } returns mockk {
            every { iterator() } returns mockk()
            every { iterator().hasNext() } returns true
            every { iterator().next() } returns mockk {
                every { group } returns ANDROID_GRADLE_PLUGIN_GROUP
                every { name } returns ANDROID_GRADLE_PLUGIN_ATTRIBUTE_ID
                every { version } returns "2.0"
            }
        }

        val exception = assertThrows(GradleException::class.java) {
            checkAndroidGradleVersion(project)
        }
        assertEquals(
            exception.message,
            "gradle-advanced-build-version does not support Android Gradle plugin 2.0." +
                " Minimum supported version is $ANDROID_GRADLE_MIN_VERSION.",
        )
    }

    @Test
    fun `android gradle plugin is available in project with unknown version`() {
        every {
            project.buildscript.configurations.getByName("classpath").dependencies
        } returns mockk {
            every { iterator() } returns mockk()
            every { iterator().hasNext() } returns true
            every { iterator().next() } returns mockk {
                every { group } returns ANDROID_GRADLE_PLUGIN_GROUP
                every { name } returns ANDROID_GRADLE_PLUGIN_ATTRIBUTE_ID
                every { version } returns null
            }
        }

        val exception = assertThrows(GradleException::class.java) {
            checkAndroidGradleVersion(project)
        }
        assertEquals(
            exception.message,
            "gradle-advanced-build-version does not support Android Gradle plugin null." +
                " Minimum supported version is $ANDROID_GRADLE_MIN_VERSION.",
        )
    }

    @Test
    fun `android gradle plugin is available in project but not applied to project`() {
        every {
            project.buildscript.configurations.getByName("classpath").dependencies
        } returns mockk {
            every { iterator() } returns mockk()
            every { iterator().hasNext() } returns true
            every { iterator().next() } returns mockk {
                every { group } returns ANDROID_GRADLE_PLUGIN_GROUP
                every { name } returns ANDROID_GRADLE_PLUGIN_ATTRIBUTE_ID
                every { version } returns ANDROID_GRADLE_MIN_VERSION
            }
        }

        every { project.plugins.hasPlugin(any<String>()) } returns false

        val exception = assertThrows(GradleException::class.java) {
            checkAndroidGradleVersion(project)
        }
        assertEquals(
            exception.message,
            "gradle-advanced-build-version only works with android application modules",
        )
    }

    @Test
    fun `android gradle plugin is available in project and applied to project`() {
        every {
            project.buildscript.configurations.getByName("classpath").dependencies
        } returns mockk {
            every { iterator() } returns mockk()
            every { iterator().hasNext() } returns true
            every { iterator().next() } returns mockk {
                every { group } returns ANDROID_GRADLE_PLUGIN_GROUP
                every { name } returns ANDROID_GRADLE_PLUGIN_ATTRIBUTE_ID
                every { version } returns ANDROID_GRADLE_MIN_VERSION
            }
        }

        every { project.plugins.hasPlugin(any<String>()) } returns true

        checkAndroidGradleVersion(project)
    }

    @Test
    fun `minimum Gradle Version is supported`() {
        mockkStatic(GradleVersion::class)

        every { GradleVersion.current() } returns GradleVersion.version("8.4")

        checkMinimumGradleVersion()
    }

    @Test
    fun `minimum Gradle Version is not supported`() {
        mockkStatic(GradleVersion::class)

        every { GradleVersion.current() } returns GradleVersion.version("4.7.8")

        val exception = assertThrows(GradleException::class.java) {
            checkMinimumGradleVersion()
        }
        assertEquals(
            exception.message,
            "\"gradle-advanced-build-version\" plugin requires at least minimum version $GRADLE_MIN_VERSION. " +
                "Detected version ${GradleVersion.version("4.7.8")}.",
        )
    }

    @Test
    fun `minimum Java Version is supported`() {
        mockkStatic(JavaVersion::class)

        every { JavaVersion.current() } returns JavaVersion.VERSION_17

        checkJavaRuntimeVersion()
    }

    @Test
    fun `minimum Java Version is not supported`() {
        mockkStatic(JavaVersion::class)

        every { JavaVersion.current() } returns JavaVersion.VERSION_1_8

        val exception = assertThrows(GradleException::class.java) {
            checkJavaRuntimeVersion()
        }
        assertEquals(
            exception.message,
            "\"gradle-advanced-build-version\" plugin requires this build to run with Java 17+",
        )
    }
}
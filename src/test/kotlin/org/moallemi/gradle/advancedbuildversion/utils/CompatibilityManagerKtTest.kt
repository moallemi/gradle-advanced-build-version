package org.moallemi.gradle.advancedbuildversion.utils

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.gradle.api.GradleException
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

        val exception = assertThrows(IllegalStateException::class.java) {
            checkAndroidGradleVersion(project)
        }
        assertEquals(
            exception.message, "The Android Gradle plugin not found. " +
                "gradle-advanced-build-version only works with Android gradle library."
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
            exception.message, "gradle-advanced-build-version does not support Android Gradle plugin 2.0"
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
            exception.message, "gradle-advanced-build-version does not support Android Gradle plugin null"
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

        every { project.plugins.hasPlugin(any<String>()) } returns false

        val exception = assertThrows(GradleException::class.java) {
            checkAndroidGradleVersion(project)
        }
        assertEquals(
            exception.message, "gradle-advanced-build-version only works with android application modules"
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
            exception.message, "gradle-advanced-build-version does not support Android Gradle plugin 2.0"
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
            exception.message, "gradle-advanced-build-version does not support Android Gradle plugin null"
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
                every { version } returns "3.1.0"
            }
        }

        every { project.plugins.hasPlugin(any<String>()) } returns false

        val exception = assertThrows(GradleException::class.java) {
            checkAndroidGradleVersion(project)
        }
        assertEquals(
            exception.message, "gradle-advanced-build-version only works with android application modules"
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
                every { version } returns "3.1.0"
            }
        }

        every { project.plugins.hasPlugin(any<String>()) } returns true

        checkAndroidGradleVersion(project)
    }

    @Test
    fun `minimum Gradle Version is supported`() {
        mockkStatic(GradleVersion::class)

        every { GradleVersion.current() } returns GradleVersion.version("5.3.1")

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
                "Detected version ${GradleVersion.version("4.7.8")}."
        )
    }
}

package org.moallemi.gradle.advancedbuildversion.integration

import java.io.File
import java.util.concurrent.TimeUnit
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains.containsString
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.moallemi.gradle.advancedbuildversion.utils.GRADLE_MIN_VERSION
import org.moallemi.gradle.advancedbuildversion.utils.ProjectProps

class AdvancedBuildVersionPluginSetUpIntegrationTest {

    @get:Rule
    var testProjectRoot = TemporaryFolder()

    @Test
    fun `low Gradle Version fails build`() {
        writeBuildGradle(
            """plugins {
               id "$PLUGIN_ID" version "$PLUGIN_VERSION"
             }""".trimIndent()
        )

        val exception = assertThrows(UnexpectedBuildFailure::class.java) {
            GradleRunner.create()
                .withProjectDir(testProjectRoot.root)
                .withPluginClasspath()
                .withGradleVersion("4.8")
                .build()
        }
        assertThat(
            exception.message,
            containsString("plugin requires at least minimum version $GRADLE_MIN_VERSION. Detected version Gradle 4.8.")
        )
    }

    @Test
    fun `missing android gradle plugin dependency fails build`() {
        publishToLocalMaven()

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    jcenter()
                    google()
                    mavenLocal()
                  }
                
                  dependencies {
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }
                apply plugin: "$PLUGIN_ID"
                """.trimIndent()
        )

        val exception = assertThrows(UnexpectedBuildFailure::class.java) {
            GradleRunner.create()
                .withProjectDir(testProjectRoot.root)
                .withPluginClasspath()
                .withGradleVersion("5.0")
                .build()
        }
        assertThat(
            exception.message,
            containsString("gradle-advanced-build-version only works with Android gradle library.")
        )
    }

    @Test
    fun `build fails when android gradle plugin is not applied`() {
        publishToLocalMaven()

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    google()
                    jcenter()
                    mavenLocal()
                  }
                
                  dependencies {
                    classpath 'com.android.tools.build:gradle:3.1.0'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }
                apply plugin: "$PLUGIN_ID"
                """.trimIndent()
        )

        val exception = assertThrows(UnexpectedBuildFailure::class.java) {
            GradleRunner.create()
                .withProjectDir(testProjectRoot.root)
                .withPluginClasspath()
                .withGradleVersion("5.0")
                .build()
        }
        assertThat(
            exception.message,
            containsString("gradle-advanced-build-version only works with android application modules")
        )
    }

    @Test
    fun `android plugin and gradle plugin supported`() {
        publishToLocalMaven()

        File("src/test/test-data", "app").copyRecursively(testProjectRoot.root)

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    google()
                    jcenter()
                    mavenLocal()
                  }
                
                  dependencies {
                    classpath 'com.android.tools.build:gradle:3.1.0'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }
                
                apply plugin: 'com.android.application'
                apply plugin: "$PLUGIN_ID"
                
                android {
                  compileSdkVersion 29
                  buildToolsVersion "29.0.2"
                  defaultConfig {
                    applicationId "com.example.myapplication"
                    minSdkVersion 14
                    targetSdkVersion 22
                    versionCode 1
                    versionName "1.0"
                  }
                }
                """.trimIndent()
        )

        val output = GradleRunner.create()
            .withProjectDir(testProjectRoot.root)
            .withPluginClasspath()
            .withGradleVersion("5.0")
            .build()
        assertThat(output.output, containsString("Applying Advanced Build Version Plugin"))
    }

    @Test
    fun `fails if applied on library plugin`() {
        publishToLocalMaven()

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    google()
                    jcenter()
                    mavenLocal()
                  }
                
                  dependencies {
                    classpath 'com.android.tools.build:gradle:3.1.0'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }
                
                apply plugin: 'com.android.library'
                apply plugin: "$PLUGIN_ID"
                
                android {
                  compileSdkVersion 29
                  buildToolsVersion "29.0.2"
                  defaultConfig {
                      minSdkVersion 14
                  }
                }
                """.trimIndent()
        )

        val exception = assertThrows(UnexpectedBuildFailure::class.java) {
            GradleRunner.create()
                .withProjectDir(testProjectRoot.root)
                .withPluginClasspath()
                .withGradleVersion("5.0")
                .build()
        }
        assertThat(
            exception.message,
            containsString("gradle-advanced-build-version only works with android application modules")
        )
    }

    private fun publishToLocalMaven() {
        ProcessBuilder("./gradlew", "publishToMavenLocal")
            .start()
            .apply {
                inputStream.reader(Charsets.UTF_8).use {
                    println(it.readText())
                }
                waitFor(30, TimeUnit.SECONDS)
            }
    }

    private fun writeBuildGradle(build: String) {
        val file = testProjectRoot.newFile("build.gradle")
        file.writeText(build)
    }

    companion object {
        private const val PLUGIN_ID = "org.moallemi.advanced-build-version"
        private const val CLASSPATH = "org.moallemi.gradle.advanced-build-version:gradle-plugin"
        private val PLUGIN_VERSION by lazy {
            ProjectProps.load().advancedBuildPluginVersion
        }
    }
}

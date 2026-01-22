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

package me.moallemi.gradle.advancedbuildversion.integration

import me.moallemi.gradle.advancedbuildversion.utils.GRADLE_MIN_VERSION
import me.moallemi.gradle.advancedbuildversion.utils.ProjectProps
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains.containsString
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.concurrent.TimeUnit

class AdvancedBuildVersionPluginSetUpIntegrationTest {

    @get:Rule
    var testProjectRoot = TemporaryFolder()

    @Test
    fun `low Gradle Version fails build`() {
        publishToLocalMaven()

        writeBuildGradle(
            """plugins {
               id "$PLUGIN_ID" version "$PLUGIN_VERSION"
             }
            """.trimIndent(),
        )

        val exception = assertThrows(UnexpectedBuildFailure::class.java) {
            GradleRunner.create()
                .withProjectDir(testProjectRoot.root)
                .withPluginClasspath()
                .withGradleVersion("8.4")
                .build()
        }
        assertThat(
            exception.message,
            containsString("plugin requires at least minimum version $GRADLE_MIN_VERSION. Detected version Gradle 8.4"),
        )
    }

    @Test
    fun `missing android gradle plugin dependency fails build`() {
        publishToLocalMaven()

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    mavenCentral()
                    google()
                    mavenLocal()
                  }

                  dependencies {
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }
                apply plugin: "$PLUGIN_ID"
            """.trimIndent(),
        )

        val exception = assertThrows(UnexpectedBuildFailure::class.java) {
            GradleRunner.create()
                .withProjectDir(testProjectRoot.root)
                .withPluginClasspath()
                .withGradleVersion(CURRENT_GRADLE_VERSION)
                .build()
        }
        assertThat(
            exception.message,
            containsString("gradle-advanced-build-version only works with android application modules"),
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
                    mavenCentral()
                    mavenLocal()
                  }

                  dependencies {
                    classpath 'com.android.tools.build:gradle:$MIN_AGP_SUPPORTED_VERSION'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }
                apply plugin: "$PLUGIN_ID"
            """.trimIndent(),
        )

        val exception = assertThrows(UnexpectedBuildFailure::class.java) {
            GradleRunner.create()
                .withProjectDir(testProjectRoot.root)
                .withPluginClasspath()
                .withGradleVersion(CURRENT_GRADLE_VERSION)
                .build()
        }
        assertThat(
            exception.message,
            containsString("gradle-advanced-build-version only works with android application modules"),
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
                    mavenCentral()
                    mavenLocal()
                  }

                  dependencies {
                    classpath 'com.android.tools.build:gradle:$MIN_AGP_SUPPORTED_VERSION'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }
                
                apply plugin: 'com.android.application'
                apply plugin: "$PLUGIN_ID"
                
                android {
                  namespace 'com.example.namespace'
                  compileSdkVersion 33
                  defaultConfig {
                    applicationId "com.example.myapplication"
                    minSdkVersion 19
                    targetSdkVersion 33
                    versionCode 1
                    versionName "1.0"
                  }
                }
            """.trimIndent(),
        )

        val output = GradleRunner.create()
            .withProjectDir(testProjectRoot.root)
            .withPluginClasspath()
            .withGradleVersion(CURRENT_GRADLE_VERSION)
            .build()
        assertThat(output.output, containsString("Applying Advanced Build Version Plugin"))
    }

    @Test
    fun `plugin works with android application plugin applied`() {
        publishToLocalMaven()

        File("src/test/test-data", "app").copyRecursively(testProjectRoot.root)

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    google()
                    mavenCentral()
                    mavenLocal()
                  }
                }
                
                plugins {
                    id("com.android.application")
                    id("$PLUGIN_ID")
                }

                android {
                  namespace 'com.example.namespace'
                  compileSdk 35
                  defaultConfig {
                    applicationId "com.example.myapplication"
                    minSdk 21
                    targetSdk 35
                    versionCode 1
                    versionName "1.0"
                  }
                }
            """.trimIndent(),
        )

        val output = GradleRunner.create()
            .withProjectDir(testProjectRoot.root)
            .withPluginClasspath()
            .withGradleVersion(CURRENT_GRADLE_VERSION)
            .build()
        assertThat(output.output, containsString("Applying Advanced Build Version Plugin"))
    }

    @Test
    fun `build fails with unsupported android gradle plugin 8_1_0`() {
        publishToLocalMaven()

        File("src/test/test-data", "app").copyRecursively(testProjectRoot.root)

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    google()
                    mavenCentral()
                    mavenLocal()
                  }

                  dependencies {
                    classpath 'com.android.tools.build:gradle:8.1.0'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }

                repositories {
                   google()
                   mavenCentral()
                }

                apply plugin: 'com.android.application'
                apply plugin: "$PLUGIN_ID"

                advancedVersioning {
                    nameOptions {
                      versionMajor 1
                      versionMinor 3
                      versionPatch 6
                      versionBuild 8
                    }
                  outputOptions {
                      renameOutput true
                      nameFormat 'MyApp-${'$'}{versionName}'
                  }
                }

                android {
                  namespace 'com.example.namespace'
                  compileSdk 33
                  defaultConfig {
                    applicationId "com.example.myapplication"
                    minSdk 19
                    targetSdk 33
                    versionCode 1
                    versionName advancedVersioning.versionName
                  }
                }
            """.trimIndent(),
        )

        val exception = assertThrows(UnexpectedBuildFailure::class.java) {
            GradleRunner.create()
                .withProjectDir(testProjectRoot.root)
                .withPluginClasspath()
                .withGradleVersion(CURRENT_GRADLE_VERSION)
                .build()
        }
        assertThat(
            exception.message,
            containsString("gradle-advanced-build-version does not support Android Gradle plugin 8.1.0"),
        )
    }

    @Test
    fun `outputOptions check versionName and renameOutput is correct for android gradle plugin 9_0_0`() {
        publishToLocalMaven()

        File("src/test/test-data", "app").copyRecursively(testProjectRoot.root)
        writeSettingsGradle()

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    google()
                    mavenCentral()
                    mavenLocal()
                  }

                  dependencies {
                    classpath 'com.android.tools.build:gradle:$MIN_AGP_SUPPORTED_VERSION'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }

                apply plugin: 'com.android.application'
                apply plugin: "$PLUGIN_ID"

                advancedVersioning {
                    nameOptions {
                      versionMajor 1
                      versionMinor 3
                      versionPatch 6
                      versionBuild 8
                    }
                  outputOptions {
                      renameOutput true
                      nameFormat 'MyApp-${'$'}{versionName}'
                  }
                }

                android {
                  namespace 'com.example.namespace'
                  compileSdk 35
                  defaultConfig {
                    applicationId "com.example.myapplication"
                    minSdk 21
                    targetSdk 35
                    versionCode 1
                    versionName advancedVersioning.versionName
                  }
                  lint {
                    abortOnError false
                  }
                }
                advancedVersioning.renameOutputApk()
            """.trimIndent(),
        )

        val output = GradleRunner.create()
            .withProjectDir(testProjectRoot.root)
            .withPluginClasspath()
            .withGradleVersion(CURRENT_GRADLE_VERSION)
            .withArguments("assembleDebug")
            .build()
        assertThat(output.output, containsString("Applying Advanced Build Version Plugin"))
        assertThat(output.output, containsString("outputFileName renamed to MyApp-1.3.6.8.apk"))
    }

    @Test
    fun `fails if applied on library plugin`() {
        publishToLocalMaven()

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    google()
                    mavenCentral()
                    mavenLocal()
                  }

                  dependencies {
                    classpath 'com.android.tools.build:gradle:$MIN_AGP_SUPPORTED_VERSION'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }

                apply plugin: 'com.android.library'
                apply plugin: "$PLUGIN_ID"

                android {
                  namespace 'com.example.namespace'
                  compileSdk 35
                  defaultConfig {
                      minSdk 21
                  }
                }
            """.trimIndent(),
        )

        val exception = assertThrows(UnexpectedBuildFailure::class.java) {
            GradleRunner.create()
                .withProjectDir(testProjectRoot.root)
                .withPluginClasspath()
                .withGradleVersion(CURRENT_GRADLE_VERSION)
                .build()
        }
        assertThat(
            exception.message,
            containsString("gradle-advanced-build-version only works with android application modules"),
        )
    }

    @Test
    fun `plugin works with configuration cache enabled`() {
        publishToLocalMaven()

        File("src/test/test-data", "app").copyRecursively(testProjectRoot.root)
        writeSettingsGradle()

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    google()
                    mavenCentral()
                    mavenLocal()
                  }

                  dependencies {
                    classpath 'com.android.tools.build:gradle:$MIN_AGP_SUPPORTED_VERSION'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }

                apply plugin: 'com.android.application'
                apply plugin: "$PLUGIN_ID"

                advancedVersioning {
                    nameOptions {
                      versionMajor 1
                      versionMinor 3
                      versionPatch 6
                      versionBuild 8
                    }
                  outputOptions {
                      renameOutput true
                      nameFormat 'MyApp-${'$'}{versionName}'
                  }
                }

                android {
                  namespace 'com.example.namespace'
                  compileSdk 35
                  defaultConfig {
                    applicationId "com.example.myapplication"
                    minSdk 21
                    targetSdk 35
                    versionCode 1
                    versionName advancedVersioning.versionName
                  }
                  lint {
                    abortOnError false
                  }
                }
            """.trimIndent(),
        )

        // First run - stores configuration cache
        val firstRun = GradleRunner.create()
            .withProjectDir(testProjectRoot.root)
            .withPluginClasspath()
            .withGradleVersion(CURRENT_GRADLE_VERSION)
            .withArguments("--configuration-cache", "assembleDebug")
            .build()
        assertThat(firstRun.output, containsString("Applying Advanced Build Version Plugin"))
        assertThat(firstRun.output, containsString("outputFileName renamed to MyApp-1.3.6.8.apk"))

        // Second run - reuses configuration cache
        val secondRun = GradleRunner.create()
            .withProjectDir(testProjectRoot.root)
            .withPluginClasspath()
            .withGradleVersion(CURRENT_GRADLE_VERSION)
            .withArguments("--configuration-cache", "assembleDebug")
            .build()
        assertThat(secondRun.output, containsString("Reusing configuration cache"))
    }

    @Test
    fun `GIT_COMMIT_COUNT works with configuration cache enabled`() {
        publishToLocalMaven()

        File("src/test/test-data", "app").copyRecursively(testProjectRoot.root)
        writeSettingsGradle()

        // Set up git repository with commits
        prepareGitCommits()

        writeBuildGradle(
            """
                buildscript {
                  repositories {
                    google()
                    mavenCentral()
                    mavenLocal()
                  }

                  dependencies {
                    classpath 'com.android.tools.build:gradle:$MIN_AGP_SUPPORTED_VERSION'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }

                apply plugin: 'com.android.application'
                apply plugin: "$PLUGIN_ID"

                advancedVersioning {
                    nameOptions {
                      versionMajor 1
                      versionMinor 3
                      versionPatch 6
                      versionBuild 8
                    }
                    codeOptions {
                      versionCodeType 'GIT_COMMIT_COUNT'
                    }
                    outputOptions {
                      renameOutput true
                      nameFormat 'MyApp-${'$'}{versionName}-${'$'}{versionCode}'
                    }
                }

                android {
                  namespace 'com.example.namespace'
                  compileSdk 35
                  defaultConfig {
                    applicationId "com.example.myapplication"
                    minSdk 21
                    targetSdk 35
                    versionCode advancedVersioning.versionCode
                    versionName advancedVersioning.versionName
                  }
                  lint {
                    abortOnError false
                  }
                }
            """.trimIndent(),
        )

        // First run - stores configuration cache
        val firstRun = GradleRunner.create()
            .withProjectDir(testProjectRoot.root)
            .withPluginClasspath()
            .withGradleVersion(CURRENT_GRADLE_VERSION)
            .withArguments("--configuration-cache", "assembleDebug")
            .build()
        assertThat(firstRun.output, containsString("Applying Advanced Build Version Plugin"))
        // prepareGitCommits creates 2 commits, so versionCode should be 2
        assertThat(firstRun.output, containsString("outputFileName renamed to MyApp-1.3.6.8-2.apk"))

        // Second run - reuses configuration cache
        val secondRun = GradleRunner.create()
            .withProjectDir(testProjectRoot.root)
            .withPluginClasspath()
            .withGradleVersion(CURRENT_GRADLE_VERSION)
            .withArguments("--configuration-cache", "assembleDebug")
            .build()
        assertThat(secondRun.output, containsString("Reusing configuration cache"))
    }

    private fun publishToLocalMaven() {
        ProcessBuilder("./gradlew", "publishAdvancedBuildVersionPublicationToMavenLocal")
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

    private fun writeSettingsGradle() {
        val file = testProjectRoot.newFile("settings.gradle")
        file.writeText(
            """
            pluginManagement {
                repositories {
                    google()
                    mavenCentral()
                    mavenLocal()
                }
            }
            dependencyResolutionManagement {
                repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
                repositories {
                    google()
                    mavenCentral()
                    mavenLocal()
                }
            }
            """.trimIndent(),
        )
    }

    private fun prepareGitCommits() {
        testProjectRoot.newFile("first.txt")

        ProcessBuilder("git", "init")
            .directory(testProjectRoot.root)
            .start()
            .waitFor(5, TimeUnit.SECONDS)

        // Configure git user locally for this temp repo (required in CI environments)
        ProcessBuilder("git", "config", "--local", "user.email", "test@test.com")
            .directory(testProjectRoot.root)
            .start()
            .waitFor(5, TimeUnit.SECONDS)

        ProcessBuilder("git", "config", "--local", "user.name", "Test User")
            .directory(testProjectRoot.root)
            .start()
            .waitFor(5, TimeUnit.SECONDS)

        ProcessBuilder("git", "add", ".")
            .directory(testProjectRoot.root)
            .start()
            .waitFor(5, TimeUnit.SECONDS)

        ProcessBuilder("git", "commit", "-m", "first commit")
            .directory(testProjectRoot.root)
            .start()
            .waitFor(5, TimeUnit.SECONDS)

        testProjectRoot.newFile("second.txt")

        ProcessBuilder("git", "add", ".")
            .directory(testProjectRoot.root)
            .start()
            .waitFor(5, TimeUnit.SECONDS)

        ProcessBuilder("git", "commit", "-m", "second commit")
            .directory(testProjectRoot.root)
            .start()
            .waitFor(5, TimeUnit.SECONDS)
    }

    companion object {
        private const val CLASSPATH = "me.moallemi.gradle:advanced-build-version"
        private const val CURRENT_GRADLE_VERSION = "9.1.0"
        private const val MIN_AGP_SUPPORTED_VERSION = "9.0.0"
        private val PLUGIN_ID by lazy {
            ProjectProps.load().advancedBuildPluginId
        }
        private val PLUGIN_VERSION by lazy {
            ProjectProps.load().advancedBuildPluginVersion
        }
    }
}
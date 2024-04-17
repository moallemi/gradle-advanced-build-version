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
                .withGradleVersion("7.2")
                .build()
        }
        assertThat(
            exception.message,
            containsString("plugin requires at least minimum version $GRADLE_MIN_VERSION. Detected version Gradle 7.2"),
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
                    jcenter()
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
                    jcenter()
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
                    jcenter()
                    mavenLocal()
                  }
                }
                
                plugins {
                    id("com.android.application")
                    id("$PLUGIN_ID")
                }
                
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
    fun `build fails with unsupported android gradle plugin 3_1_0`() {
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
                
                repositories {
                   google()
                   jcenter()
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
                  compileSdkVersion 33
                  defaultConfig {
                    applicationId "com.example.myapplication"
                    minSdkVersion 19
                    targetSdkVersion 33
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
            containsString("gradle-advanced-build-version does not support Android Gradle plugin 3.1.0"),
        )
    }

    @Test
    fun `outputOptions check versionName and renameOutput is correct for android gradle plugin 7_0_0`() {
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
                    classpath 'com.android.tools.build:gradle:$MIN_AGP_SUPPORTED_VERSION'
                    classpath '$CLASSPATH:$PLUGIN_VERSION'
                  }
                }

                repositories {
                   google()
                   jcenter()
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
                  compileSdkVersion 33
                  defaultConfig {
                    applicationId "com.example.myapplication"
                    minSdkVersion 19
                    targetSdkVersion 33
                    versionCode 1
                    versionName advancedVersioning.versionName
                  }
                  lintOptions {
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
            .withArguments("assemble")
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
                    jcenter()
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
                  compileSdkVersion 33
                  defaultConfig {
                      minSdkVersion 14
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

    companion object {
        private const val CLASSPATH = "me.moallemi.gradle:advanced-build-version"
        private const val CURRENT_GRADLE_VERSION = "8.4"
        private const val MIN_AGP_SUPPORTED_VERSION = "8.1.0"
        private val PLUGIN_ID by lazy {
            ProjectProps.load().advancedBuildPluginId
        }
        private val PLUGIN_VERSION by lazy {
            ProjectProps.load().advancedBuildPluginVersion
        }
    }
}
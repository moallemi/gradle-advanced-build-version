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

package me.moallemi.gradle.advancedbuildversion.tasks

import com.android.build.api.variant.BuiltArtifact
import com.android.build.api.variant.BuiltArtifacts
import com.android.build.api.variant.BuiltArtifactsLoader
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.file.Directory
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class RenameApkTaskTest {

    @get:Rule
    var testFolder = TemporaryFolder()

    private lateinit var inputDir: File
    private lateinit var outputDir: File
    private lateinit var sourceApk: File

    @Before
    fun setUp() {
        inputDir = testFolder.newFolder("input")
        outputDir = testFolder.newFolder("output")
        sourceApk = File(inputDir, "app-debug.apk")
        sourceApk.writeText("APK content")
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `renameApk copies file with new name`() {
        val project = ProjectBuilder.builder()
            .withProjectDir(testFolder.root)
            .build()

        val task = project.tasks.create("renameApk", RenameApkTask::class.java)

        val inputDirectory: Directory = mockk {
            every { asFile } returns inputDir
        }

        val outputDirectory: Directory = mockk {
            every { file(any<String>()) } answers {
                val fileName = firstArg<String>()
                mockk {
                    every { asFile } returns File(outputDir, fileName)
                }
            }
        }

        val builtArtifact: BuiltArtifact = mockk {
            every { outputFile } returns sourceApk.absolutePath
        }

        val builtArtifacts: BuiltArtifacts = mockk {
            every { elements } returns listOf(builtArtifact)
        }

        val loader: BuiltArtifactsLoader = mockk {
            every { load(any<Directory>()) } returns builtArtifacts
        }

        task.apkFolder.set(inputDirectory)
        task.outputDirectory.set(outputDirectory)
        task.outputFileName.set("MyApp-1.0.0.apk")
        task.builtArtifactsLoader.set(loader)

        task.renameApk()

        val renamedApk = File(outputDir, "MyApp-1.0.0.apk")
        assertTrue("Renamed APK should exist", renamedApk.exists())
        assertEquals("APK content", renamedApk.readText())
    }

    @Test
    fun `renameApk does nothing when builtArtifacts is null`() {
        val project = ProjectBuilder.builder()
            .withProjectDir(testFolder.root)
            .build()

        val task = project.tasks.create("renameApkNull", RenameApkTask::class.java)

        val inputDirectory: Directory = mockk {
            every { asFile } returns inputDir
        }

        val outputDirectory: Directory = mockk()

        val loader: BuiltArtifactsLoader = mockk {
            every { load(any<Directory>()) } returns null
        }

        task.apkFolder.set(inputDirectory)
        task.outputDirectory.set(outputDirectory)
        task.outputFileName.set("MyApp-1.0.0.apk")
        task.builtArtifactsLoader.set(loader)

        // Should not throw any exception
        task.renameApk()

        val renamedApk = File(outputDir, "MyApp-1.0.0.apk")
        assertTrue("Renamed APK should not exist when artifacts are null", !renamedApk.exists())
    }
}
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

package me.moallemi.gradle.advancedbuildversion.gradleextensions

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import me.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.AUTO_INCREMENT_ONE_STEP
import me.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.AUTO_INCREMENT_STEP
import me.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.GIT_COMMIT_COUNT
import me.moallemi.gradle.advancedbuildversion.utils.GitWrapper
import org.gradle.api.GradleException
import org.gradle.api.provider.Provider
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.util.Properties

class VersionCodeConfigTest {

    private val gitWrapper: GitWrapper = mockk()

    private lateinit var versionFile: File

    private var taskNames: List<String> = emptyList()

    private lateinit var versionCodeConfig: VersionCodeConfig

    @Before
    fun setUp() {
        versionFile = File(versionFilePath).apply {
            createNewFile()
        }
        taskNames = emptyList()
        versionCodeConfig = VersionCodeConfig(versionFile, gitWrapper, taskNames)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        versionFile.delete()
    }

    @Test
    fun `versionCodeType is AUTO_INCREMENT_ONE_STEP and file is empty`() {
        versionCodeConfig.versionCodeType(AUTO_INCREMENT_ONE_STEP)

        assertEquals(versionCodeConfig.versionCode, 1)
    }

    @Test
    fun `versionCodeType is AUTO_INCREMENT_ONE_STEP and file is missing`() {
        versionFile.delete()

        val exception = Assert.assertThrows(GradleException::class.java) {
            versionCodeConfig.versionCodeType(AUTO_INCREMENT_ONE_STEP)
            versionCodeConfig.versionCode
        }
        Assert.assertEquals(
            exception.message,
            "Could not read version.properties file in path ${versionFile.absolutePath}." +
                " Please create this file and add it to your VCS (git, svn, ...).",
        )
    }

    @Test
    fun `versionCodeType is GIT_COMMIT_COUNT`() {
        val mockProvider: Provider<Int> = mockk()
        every { mockProvider.get() } returns 5
        every { gitWrapper.getCommitsNumberInBranch() } returns mockProvider

        versionCodeConfig.versionCodeType(GIT_COMMIT_COUNT)
        val actual = versionCodeConfig.versionCode

        assertEquals(5, actual)
    }

    @Test
    fun `versionCodeType is GIT_COMMIT_COUNT and lastLegacyCode = 987650`() {
        val mockProvider: Provider<Int> = mockk()
        every { mockProvider.get() } returns 5
        every { gitWrapper.getCommitsNumberInBranch() } returns mockProvider

        versionCodeConfig.versionCodeType(GIT_COMMIT_COUNT)
        versionCodeConfig.lastLegacyCode(987650)
        val actual = versionCodeConfig.versionCode

        assertEquals(987655, actual)
    }

    @Test
    fun `versionCodeType is AUTO_INCREMENT_ONE_STEP and versionCode is 4`() {
        versionFile.apply {
            val versionProps = Properties()
            versionProps.load(FileInputStream(this))
            versionProps["AI_VERSION_CODE"] = "3"
            versionProps.store(this.writer(), null)
        }

        versionCodeConfig.versionCodeType(AUTO_INCREMENT_ONE_STEP)

        assertEquals(4, versionCodeConfig.versionCode)
    }

    @Test
    fun `versionCodeType is AUTO_INCREMENT_ONE_STEP and versionCode is 4 and lastLegacyCode = 999880`() {
        versionFile.apply {
            val versionProps = Properties()
            versionProps.load(FileInputStream(this))
            versionProps["AI_VERSION_CODE"] = "3"
            versionProps.store(this.writer(), null)
        }

        versionCodeConfig.versionCodeType(AUTO_INCREMENT_ONE_STEP)
        versionCodeConfig.lastLegacyCode(999880)

        assertEquals(999884, versionCodeConfig.versionCode)
    }

    @Test
    fun `increasing version code is possible with default dependsOnTasks`() {
        // Recreate config with the "assembleRelease" task
        versionCodeConfig = VersionCodeConfig(versionFile, gitWrapper, listOf("assembleRelease"))

        val currentVersionCode = versionCodeConfig.versionCode
        versionCodeConfig.increaseVersionCodeIfPossible()

        assertEquals(versionCodeConfig.versionCode, currentVersionCode + 1)
    }

    @Test
    fun `increasing version code is not possible with default dependsOnTask`() {
        // Recreate config with "test" task (doesn't match default "release" dependsOnTasks)
        versionCodeConfig = VersionCodeConfig(versionFile, gitWrapper, listOf("test"))

        val currentVersionCode = versionCodeConfig.versionCode
        versionCodeConfig.increaseVersionCodeIfPossible()

        assertEquals(versionCodeConfig.versionCode, currentVersionCode)
    }

    @Test
    fun `increasing version code is possible with non-default dependsOnTasks`() {
        // Recreate config with "assembleRelease" task
        versionCodeConfig = VersionCodeConfig(versionFile, gitWrapper, listOf("assembleRelease"))
        versionCodeConfig.dependsOnTasks("release", "debug")

        val currentVersionCode = versionCodeConfig.versionCode
        versionCodeConfig.increaseVersionCodeIfPossible()

        assertEquals(versionCodeConfig.versionCode, currentVersionCode + 1)
    }

    @Test
    fun `increasing version code is not possible with non-default dependsOnTask`() {
        // Recreate config with "test" task
        versionCodeConfig = VersionCodeConfig(versionFile, gitWrapper, listOf("test"))
        versionCodeConfig.dependsOnTasks("release", "debug")

        val currentVersionCode = versionCodeConfig.versionCode
        versionCodeConfig.increaseVersionCodeIfPossible()

        assertEquals(versionCodeConfig.versionCode, currentVersionCode)
    }

    @Test
    fun `versionCodeType is AUTO_INCREMENT_STEP and versionCodeStep is 4`() {
        versionFile.apply {
            val versionProps = Properties()
            versionProps.load(FileInputStream(this))
            versionProps["AI_VERSION_CODE"] = "2"
            versionProps.store(this.writer(), null)
        }

        versionCodeConfig.versionCodeType(AUTO_INCREMENT_STEP)
        versionCodeConfig.versionCodeStep(4)

        assertEquals(6, versionCodeConfig.versionCode)
    }

    @Test
    fun `versionCodeType is AUTO_INCREMENT_DATE`() {
        versionCodeConfig.versionCodeType(VersionCodeType.AUTO_INCREMENT_DATE)

        // "The greatest value Google Play allows for versionCode is 2100000000."
        // @see https://developer.android.com/studio/publish/versioning#appversioning
        assert(versionCodeConfig.versionCode <= 2100000000)
    }

    companion object {
        private val versionFilePath = getResourcePath().path + ""

        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        private fun getResourcePath() =
            this::class.java.classLoader.getResource("version.properties").toURI()
    }
}
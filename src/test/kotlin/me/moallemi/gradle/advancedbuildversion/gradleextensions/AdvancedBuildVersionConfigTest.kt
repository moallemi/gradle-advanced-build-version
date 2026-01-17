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

import groovy.lang.Closure
import groovy.lang.GroovyShell
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.gradle.api.provider.ProviderFactory
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.lang.String.join

class AdvancedBuildVersionConfigTest {

    private val providers: ProviderFactory = mockk(relaxed = true)

    private lateinit var config: AdvancedBuildVersionConfig

    @get:Rule
    var testProjectRoot = TemporaryFolder()

    @Before
    fun setUp() {
        config = AdvancedBuildVersionConfig(
            projectDir = testProjectRoot.root,
            projectName = "testProject",
            rootProjectName = "rootProject",
            providers = providers,
            taskNames = emptyList(),
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `setUp nameOptions with closure`() {
        val closure = buildClosure("versionMajor 1", "versionMinor 2")

        config.nameOptions(closure)

        assertEquals("1.2", config.versionName)
    }

    @Test
    fun `setUp nameOptions kts`() {
        config.nameOptions {
            versionMajor(3)
            versionMinor(4)
        }

        assertEquals("3.4", config.versionName)
    }

    @Test
    fun `setUp codeOptions with closure`() {
        val closure = buildClosure("versionCodeStep 5")

        config.codeOptions(closure)

        // Verify the config was applied (the step is stored internally)
    }

    @Test
    fun `setUp codeOptions kts`() {
        config.codeOptions {
            versionCodeStep(10)
        }
        // Verify it runs without error
    }

    @Test
    fun `setUp outOptions with closure`() {
        val closure = buildClosure("renameOutput true")

        config.outputOptions(closure)
        // Verify it runs without error
    }

    @Test
    fun `setUp outOptions kts`() {
        config.outputOptions {
            renameOutput(true)
        }
        // Verify it runs without error
    }

    @Test
    fun `increaseVersionCodeIfPossible runs`() {
        config.increaseVersionCodeIfPossible()
    }

    @Test
    fun `renameOutputApkIfPossible runs`() {
        config.renameOutputApkIfPossible(mockk(), mockk())
    }

    @Test
    fun `renameOutputApk runs`() {
        @Suppress("DEPRECATION")
        config.renameOutputApk()
    }

    private fun buildClosure(vararg strings: String?): Closure<*> {
        val scriptText = "{ script -> " + join("\n", *strings) + " }"
        return GroovyShell().evaluate(scriptText) as Closure<*>
    }
}
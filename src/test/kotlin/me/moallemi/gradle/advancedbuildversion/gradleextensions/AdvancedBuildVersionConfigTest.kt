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

import com.android.build.gradle.AppExtension
import groovy.lang.Closure
import groovy.lang.GroovyShell
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.lang.String.join

class AdvancedBuildVersionConfigTest {

    private val project: Project = mockk(relaxUnitFun = true)

    private lateinit var config: AdvancedBuildVersionConfig

    @get:Rule
    var testProjectRoot = TemporaryFolder()

    @Before
    fun setUp() {
        every { project.buildFile } returns File("file.tmp")

        config = AdvancedBuildVersionConfig(project)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `setUp nameOptions`() {
        givenProject()

        config.nameOptions(buildClosure())

        verify {
            project.configure(any<Any>(), any())
        }
    }

    @Test
    fun `setUp nameOptions kts`() {
        givenProject()

        config.nameOptions(config = mockk())

        verify {
            project.configure(any<Any>(), any())
        }
    }

    @Test
    fun `setUp codeOptions`() {
        givenProject()

        config.codeOptions(buildClosure())

        verify {
            project.configure(any<Any>(), any())
        }
    }

    @Test
    fun `setUp codeOptions kts`() {
        givenProject()

        config.codeOptions(config = mockk())

        verify {
            project.configure(any<Any>(), any())
        }
    }

    @Test
    fun `setUp outOptions`() {
        givenProject()

        config.outputOptions(buildClosure())

        verify {
            project.configure(any<Any>(), any())
        }
    }

    @Test
    fun `setUp outOptions kts`() {
        givenProject()

        config.outputOptions(config = mockk())

        verify {
            project.configure(any<Any>(), any())
        }
    }

    @Test
    fun `increaseVersionCodeIfPossible runs`() {
        every { project.gradle.startParameter.taskNames } returns mockk(relaxed = true)

        config.increaseVersionCodeIfPossible()
    }

    @Test
    fun `renameOutputApkIfPossible runs`() {
        config.renameOutputApkIfPossible(mockk(), mockk())
    }

    @Test
    fun `renameOutputApk runs`() {
        every { project.extensions.findByType(AppExtension::class.java) } returns mockk {
            every { applicationVariants } returns mockk()
        }
        config.renameOutputApk()
    }

    private fun givenProject() {
        every { project.configure(any<Any>(), any()) } returns mockk()
    }

    private fun buildClosure(vararg strings: String?): Closure<*> {
        val scriptText = "{ script -> " + join("\n", *strings) + " }"
        return GroovyShell().evaluate(scriptText) as Closure<*>
    }
}
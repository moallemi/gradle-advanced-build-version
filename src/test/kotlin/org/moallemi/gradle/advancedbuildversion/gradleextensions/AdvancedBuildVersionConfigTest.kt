package org.moallemi.gradle.advancedbuildversion.gradleextensions

import groovy.lang.Closure
import groovy.lang.GroovyShell
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.File
import java.lang.String.join
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

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
    fun `setUp codeOptions`() {
        givenProject()

        config.codeOptions(buildClosure())

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
    fun `increaseVersionCodeIfPossible runs`() {
        every { project.gradle.startParameter.taskNames } returns mockk(relaxed = true)

        config.increaseVersionCodeIfPossible()
    }

    @Test
    fun `renameOutputApkIfPossible runs`() {
        config.renameOutputApkIfPossible(mockk())
    }

    private fun givenProject() {
        every { project.configure(any<Any>(), any()) } returns mockk()
    }

    private fun buildClosure(vararg strings: String?): Closure<*> {
        val scriptText = "{ script -> " + join("\n", *strings) + " }"
        return GroovyShell().evaluate(scriptText) as Closure<*>
    }
}

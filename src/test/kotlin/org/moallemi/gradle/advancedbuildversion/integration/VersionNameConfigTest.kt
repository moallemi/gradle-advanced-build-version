package org.moallemi.gradle.advancedbuildversion.integration

import com.android.build.gradle.AppPlugin
import junit.framework.Assert.assertEquals
import org.gradle.api.Project
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.moallemi.gradle.advancedbuildversion.AdvancedBuildVersionPlugin
import org.moallemi.gradle.advancedbuildversion.AdvancedBuildVersionPlugin.Companion.EXTENSION_NAME
import org.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig

class VersionNameConfigTest {

    @Test
    fun `check version name = 1_0`() {
        val advancedVersioning = givenProject()

        advancedVersioning.versionNameConfig.versionMajor(1)

        assertEquals(advancedVersioning.versionName, "1.0")
    }

    private fun givenProject(): AdvancedBuildVersionConfig {
        val project: Project = ProjectBuilder.builder().build()
        project.repositories.google()

        project.plugins.apply(JavaGradlePluginPlugin::class.java)
        project.buildscript.dependencies.add("classpath", "com.android.tools.build:gradle:3.0.0")

        project.plugins.apply(AppPlugin::class.java)
        project.plugins.apply(AdvancedBuildVersionPlugin::class.java)
        return project.extensions.getByName(EXTENSION_NAME) as AdvancedBuildVersionConfig
    }
}

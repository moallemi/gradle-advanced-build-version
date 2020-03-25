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

import com.android.build.gradle.AppPlugin
import junit.framework.Assert.assertEquals
import me.moallemi.gradle.advancedbuildversion.AdvancedBuildVersionPlugin
import me.moallemi.gradle.advancedbuildversion.AdvancedBuildVersionPlugin.Companion.EXTENSION_NAME
import me.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import org.gradle.api.Project
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

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

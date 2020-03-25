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

package org.moallemi.gradle.advancedbuildversion.integration

import com.android.build.gradle.AppPlugin
import junit.framework.TestCase.assertEquals
import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.lessThan
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.moallemi.gradle.advancedbuildversion.AdvancedBuildVersionPlugin
import org.moallemi.gradle.advancedbuildversion.AdvancedBuildVersionPlugin.Companion.EXTENSION_NAME
import org.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import org.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.AUTO_INCREMENT_DATE
import org.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.DATE
import org.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.GIT_COMMIT_COUNT

class VersionCodeConfigTest {

    @get:Rule
    var testProjectRoot = TemporaryFolder()

    @Test
    fun `Check versionCodeType = DATE`() {
        val advancedVersioning = givenProject()

        advancedVersioning.versionCodeConfig.versionCodeType(DATE)

        assertThat(advancedVersioning.versionCode, lessThan(MAX_VERSION_CODE))
    }

    @Test
    fun `Check versionCodeType = AUTO_INCREMENT_DATE`() {
        val advancedVersioning = givenProject()

        advancedVersioning.versionCodeConfig.versionCodeType(AUTO_INCREMENT_DATE)

        assertThat(advancedVersioning.versionCode, lessThan(MAX_VERSION_CODE))
    }

    @Test
    fun `Check versionCodeType = GIT_COMMIT_COUNT`() {
        givenGitRepo()

        val advancedVersioning = givenProject()

        advancedVersioning.versionCodeConfig.versionCodeType(GIT_COMMIT_COUNT)

        assertEquals(advancedVersioning.versionCode, 1)
    }

    private fun givenProject(): AdvancedBuildVersionConfig {
        val project: Project = ProjectBuilder.builder()
            .withProjectDir(testProjectRoot.root)
            .build()
        project.repositories.google()

        project.plugins.apply(JavaGradlePluginPlugin::class.java)
        project.buildscript.dependencies.add("classpath", "com.android.tools.build:gradle:3.0.0")

        project.plugins.apply(AppPlugin::class.java)
        project.plugins.apply(AdvancedBuildVersionPlugin::class.java)
        return project.extensions.getByName(EXTENSION_NAME) as AdvancedBuildVersionConfig
    }

    private fun givenGitRepo() {
        val git = Git.init().setDirectory(testProjectRoot.root).call()
        git.add().addFilepattern(".").call()
        git.commit().setMessage("Initial commit").setSign(false).call()
        git.close()
    }

    companion object {
        // Based on https://developer.android.com/studio/publish/versioning
        private const val MAX_VERSION_CODE = 2_100_000_000
    }
}

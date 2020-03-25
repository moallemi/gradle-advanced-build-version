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

package org.moallemi.gradle.advancedbuildversion

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.FeaturePlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.eclipse.jgit.errors.NotSupportedException
import org.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import org.moallemi.gradle.advancedbuildversion.utils.checkAndroidGradleVersion
import org.moallemi.gradle.advancedbuildversion.utils.checkMinimumGradleVersion

class AdvancedBuildVersionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        checkMinimumGradleVersion()
        checkAndroidGradleVersion(project)

        println("Applying Advanced Build Version Plugin")

        val advancedBuildVersionPlugin = project.extensions.create(
            EXTENSION_NAME, AdvancedBuildVersionConfig::class.java, project
        )

        project.afterEvaluate {
            project.plugins.all { plugin ->
                when (plugin) {
                    is AppPlugin -> configureAndroid(project, advancedBuildVersionPlugin)
                    is FeaturePlugin -> throw NotSupportedException(
                        "Feature module is not supported"
                    )
                    is LibraryPlugin -> throw NotSupportedException(
                        "Library module is not supported yet"
                    )
                }
            }
        }
    }

    private fun configureAndroid(project: Project, config: AdvancedBuildVersionConfig) {
        config.increaseVersionCodeIfPossible()

        val appExtension = project.extensions.getByType(AppExtension::class.java)
        config.renameOutputApkIfPossible(appExtension.applicationVariants)
    }

    companion object {
        const val EXTENSION_NAME = "advancedVersioning"
    }
}

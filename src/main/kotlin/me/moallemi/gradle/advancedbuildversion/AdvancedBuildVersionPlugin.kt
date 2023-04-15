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

package me.moallemi.gradle.advancedbuildversion

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.FeaturePlugin
import com.android.build.gradle.LibraryPlugin
import me.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import me.moallemi.gradle.advancedbuildversion.utils.checkAndroidGradleVersion
import me.moallemi.gradle.advancedbuildversion.utils.checkJavaRuntimeVersion
import me.moallemi.gradle.advancedbuildversion.utils.checkMinimumGradleVersion
import me.moallemi.gradle.advancedbuildversion.utils.getAndroidPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AdvancedBuildVersionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        checkJavaRuntimeVersion()
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
                    is FeaturePlugin -> throw IllegalStateException("Feature module is not supported")
                    is LibraryPlugin -> throw IllegalStateException("Library module is not supported yet")
                }
            }
        }
    }

    private fun configureAndroid(project: Project, config: AdvancedBuildVersionConfig) {
        config.increaseVersionCodeIfPossible()

        if (getAndroidPlugin(project)?.version?.compareTo("4.1.0") == -1) { // versions prior to 4.1.0
            val appExtension = project.extensions.getByType(AppExtension::class.java)
            config.renameOutputApkIfPossible(appExtension.applicationVariants)
        }
    }

    companion object {
        const val EXTENSION_NAME = "advancedVersioning"
    }
}

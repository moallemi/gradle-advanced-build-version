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

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import me.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import me.moallemi.gradle.advancedbuildversion.utils.checkAndroidGradleVersion
import me.moallemi.gradle.advancedbuildversion.utils.checkJavaRuntimeVersion
import me.moallemi.gradle.advancedbuildversion.utils.checkMinimumGradleVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class AdvancedBuildVersionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        checkJavaRuntimeVersion()
        checkMinimumGradleVersion()
        checkAndroidGradleVersion(project)

        println("Applying Advanced Build Version Plugin")

        // Capture values at configuration time for configuration cache compatibility
        val projectDir = project.projectDir
        val projectName = project.name
        val rootProjectName = project.rootProject.name
        val providers = project.providers
        val taskNames = project.gradle.startParameter.taskNames.toList()

        val advancedBuildVersionPlugin = AdvancedBuildVersionConfig(
            projectDir = projectDir,
            projectName = projectName,
            rootProjectName = rootProjectName,
            providers = providers,
            taskNames = taskNames,
        )
        project.extensions.add(EXTENSION_NAME, advancedBuildVersionPlugin)

        project.plugins.withId("com.android.application") {
            configureAndroid(project, advancedBuildVersionPlugin)
        }
        project.plugins.withId("com.android.library") {
            throw IllegalStateException("Library module is not supported yet")
        }
    }

    private fun configureAndroid(project: Project, config: AdvancedBuildVersionConfig) {
        config.increaseVersionCodeIfPossible()

        val androidComponents = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->
            if (config.shouldRenameOutput()) {
                val versionName = variant.outputs.first().versionName.orNull ?: ""
                val versionCode = variant.outputs.first().versionCode.orNull ?: 0
                val outputFileName = config.generateOutputFileName(variant, versionName, versionCode)

                if (outputFileName != null) {
                    val assembleTaskName = "assemble${variant.name.replaceFirstChar { it.uppercase() }}"
                    val apkDirectory = variant.artifacts.get(SingleArtifact.APK)
                    val artifactsLoader = variant.artifacts.getBuiltArtifactsLoader()

                    // Use afterEvaluate to ensure the assemble task exists
                    project.afterEvaluate {
                        project.tasks.named(assembleTaskName) { assembleTask ->
                            assembleTask.doLast {
                                val builtArtifacts = artifactsLoader.load(apkDirectory.get()) ?: return@doLast
                                builtArtifacts.elements.forEach { artifact ->
                                    val sourceFile = File(artifact.outputFile)
                                    val destFile = File(sourceFile.parentFile, outputFileName)
                                    if (sourceFile.absolutePath != destFile.absolutePath) {
                                        sourceFile.copyTo(destFile, overwrite = true)
                                        println("outputFileName renamed to $outputFileName")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTENSION_NAME = "advancedVersioning"
    }
}
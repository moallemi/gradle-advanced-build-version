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

package me.moallemi.gradle.advancedbuildversion.utils

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.util.GradleVersion

fun checkAndroidGradleVersion(project: Project) {
    val androidGradlePlugin = getAndroidPlugin(project)
    if (androidGradlePlugin == null) {
        throw IllegalStateException(
            "The Android Gradle plugin not found. " +
                "gradle-advanced-build-version only works with Android gradle library."
        )
    } else if (!checkAndroidVersion(androidGradlePlugin.version)) {
        throw GradleException("gradle-advanced-build-version does not support Android Gradle plugin ${androidGradlePlugin.version}")
    } else if (!project.plugins.hasPlugin("com.android.application")) {
        throw GradleException("gradle-advanced-build-version only works with android application modules")
    }
}

fun checkMinimumGradleVersion() {
    if (GRADLE_MIN_VERSION > GradleVersion.current()) {
        throw GradleException("\"gradle-advanced-build-version\" plugin requires at least minimum version $GRADLE_MIN_VERSION. Detected version ${GradleVersion.current()}.")
    }
}

private fun checkAndroidVersion(version: String?) =
    listOf("3.", "4.").any { version?.startsWith(it) ?: false }

fun getAndroidPlugin(project: Project): Dependency? =
    findClassPathDependencyVersion(
        project,
        ANDROID_GRADLE_PLUGIN_GROUP,
        ANDROID_GRADLE_PLUGIN_ATTRIBUTE_ID
    ) ?: findClassPathDependencyVersion(
        project.rootProject,
        ANDROID_GRADLE_PLUGIN_GROUP,
        ANDROID_GRADLE_PLUGIN_ATTRIBUTE_ID
    )

private fun findClassPathDependencyVersion(project: Project, group: String, attributeId: String) =
    project.buildscript.configurations.getByName("classpath").dependencies.find {
        group == it.group && it.name == attributeId
    }

internal val GRADLE_MIN_VERSION: GradleVersion = GradleVersion.version("5.0")
internal const val ANDROID_GRADLE_PLUGIN_GROUP = "com.android.tools.build"
internal const val ANDROID_GRADLE_PLUGIN_ATTRIBUTE_ID = "gradle"

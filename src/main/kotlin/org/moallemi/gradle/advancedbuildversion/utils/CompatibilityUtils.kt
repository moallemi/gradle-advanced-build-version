package org.moallemi.gradle.advancedbuildversion.utils

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.util.GradleVersion

private val GRADLE_MIN_VERSION = GradleVersion.version("5.9")

fun checkAndroidGradleVersion(project: Project) {
    val androidGradlePlugin =
        getAndroidPluginVersion(project)
    if (androidGradlePlugin == null) {
        throw IllegalStateException(
            "The Android Gradle plugin not found. the " +
                "\"gradle-advanced-build-version\" plugin only works with Android gradle library."
        )
    } else if (!checkAndroidVersion(androidGradlePlugin.version)) {
        throw GradleException("gradle-advanced-build-version says: The Android Gradle plugin ${androidGradlePlugin.version} is not supported.")
    }
}

fun checkMinimumGradleVersion() {
    if (GRADLE_MIN_VERSION > GradleVersion.current()) {
        throw GradleException("\"gradle-advanced-build-version\" plugin requires at least minimum version $GRADLE_MIN_VERSION. Detected version ${GradleVersion.current()}.")
    }
}

private fun checkAndroidVersion(version: String?) =
    listOf("3.").any { version?.startsWith(it) ?: false }

private fun getAndroidPluginVersion(project: Project): Dependency? =
    findClassPathDependencyVersion(
        project,
        "com.android.tools.build",
        "gradle"
    ) ?: findClassPathDependencyVersion(
        project.rootProject,
        "com.android.tools.build",
        "gradle"
    )

private fun findClassPathDependencyVersion(project: Project, group: String, attributeId: String) =
    project.buildscript.configurations.getByName("classpath").dependencies.find {
        group == it.group && it.name == attributeId
    }

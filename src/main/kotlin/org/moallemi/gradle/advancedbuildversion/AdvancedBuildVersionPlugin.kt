package org.moallemi.gradle.advancedbuildversion

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig

class AdvancedBuildVersionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val plugin = project.extensions.create(
            "advancedVersioning", AdvancedBuildVersionConfig::class.java, project
        )
        project.afterEvaluate {
            plugin.versionCodeConfig.increaseVersionCodeIfPossible()
        }
    }
}

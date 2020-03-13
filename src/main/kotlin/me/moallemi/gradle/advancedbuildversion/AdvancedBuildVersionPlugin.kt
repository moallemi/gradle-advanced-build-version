package me.moallemi.gradle.advancedbuildversion

import me.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

class AdvancedBuildVersionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val plugin = project.extensions.create("advancedVersioning", AdvancedBuildVersionConfig::class.java, project)
    }
}

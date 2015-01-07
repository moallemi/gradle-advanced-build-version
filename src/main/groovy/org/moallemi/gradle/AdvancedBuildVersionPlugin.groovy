package org.moallemi.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.moallemi.gradle.internal.AdvancedBuildVersionExtension

class AdvancedBuildVersionPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.configure(project) {
            project.extensions.create("advancedVersioning", AdvancedBuildVersionExtension, project)
        }
    }
}

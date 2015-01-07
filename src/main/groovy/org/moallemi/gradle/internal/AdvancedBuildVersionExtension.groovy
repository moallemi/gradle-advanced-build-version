package org.moallemi.gradle.internal

import org.gradle.api.Project

import javax.inject.Inject;


class AdvancedBuildVersionExtension {

    final Project project;
    VersionNameOptions nameOptions = new VersionNameOptions()
    VersionCodeOptions codeOptions = new VersionCodeOptions()

    @Inject
    AdvancedBuildVersionExtension(Project project) {
        this.project = project
    }

    void nameOptions(Closure c) {
        project.configure(nameOptions, c)
    }

    void codeOptions(Closure c) {
        project.configure(codeOptions, c)
    }
}

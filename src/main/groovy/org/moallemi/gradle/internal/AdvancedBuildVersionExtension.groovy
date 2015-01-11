package org.moallemi.gradle.internal

import org.gradle.api.Project

import javax.inject.Inject;


class AdvancedBuildVersionExtension {

    final Project project;
    VersionNameOptions nameOptions
    VersionCodeOptions codeOptions

    @Inject
    AdvancedBuildVersionExtension(Project project) {
        this.project = project
        nameOptions = new VersionNameOptions()
        codeOptions = new VersionCodeOptions(project)
    }

    void nameOptions(Closure c) {
        project.configure(nameOptions, c)
    }

    void codeOptions(Closure c) {
        project.configure(codeOptions, c)
    }

    int getVersionCode() {
        return codeOptions.versionCode
    }

    String getVersionName() {
        return nameOptions.versionName
    }
}

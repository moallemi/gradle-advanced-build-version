package org.moallemi.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.moallemi.gradle.internal.AdvancedBuildVersionExtension
import org.moallemi.gradle.internal.VersionCodeType

class AdvancedBuildVersionPlugin implements Plugin<Project> {

    void apply(Project project) {
        def advancedVersioning = project.extensions.create("advancedVersioning", AdvancedBuildVersionExtension, project)


        project.afterEvaluate {
            for (dependentTask in advancedVersioning.codeOptions.dependsOnTasks) {
                for (String taskName in project.gradle.startParameter.taskNames) {
                    if (taskName.toLowerCase(Locale.ENGLISH).contains(dependentTask) &&
                            advancedVersioning.codeOptions.versionCodeType == VersionCodeType.AUTO_INCREMENT_ONE_STEP) {
                        def versionPropsFile = advancedVersioning.codeOptions.versionFile
                        if (versionPropsFile.canRead()) {
                            def Properties versionProps = new Properties()
                            versionProps.load(new FileInputStream(versionPropsFile))
                            def code = advancedVersioning.versionCode
                            //TODO write signature on file
                            versionProps['AI_VERSION_CODE'] = code.toString()
                            versionProps.store(versionPropsFile.newWriter(), null)
                        }
                    }
                }
            }

        }


    }
}

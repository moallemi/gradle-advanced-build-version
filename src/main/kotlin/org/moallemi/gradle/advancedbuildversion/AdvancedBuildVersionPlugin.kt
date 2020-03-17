package org.moallemi.gradle.advancedbuildversion

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.FeaturePlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.eclipse.jgit.errors.NotSupportedException
import org.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import org.moallemi.gradle.advancedbuildversion.utils.checkAndroidGradleVersion
import org.moallemi.gradle.advancedbuildversion.utils.checkMinimumGradleVersion

class AdvancedBuildVersionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        checkMinimumGradleVersion()
        checkAndroidGradleVersion(project)

        println("Applying Advanced Build Version Plugin")

        val advancedBuildVersionPlugin = project.extensions.create(
            "advancedVersioning", AdvancedBuildVersionConfig::class.java, project
        )

        project.afterEvaluate {
            project.plugins.all { plugin ->
                when (plugin) {
                    is AppPlugin -> configureAndroid(project, advancedBuildVersionPlugin)
                    is LibraryPlugin, is FeaturePlugin -> throw NotSupportedException(
                        "Android library module is not supported yet"
                    )
                }
            }
        }
    }

    private fun configureAndroid(project: Project, config: AdvancedBuildVersionConfig) =
        with(project.extensions.getByType(AppExtension::class.java)) {
            config.versionCodeConfig.increaseVersionCodeIfPossible()
            config.outputConfig.renameOutputApkIfPossible(applicationVariants)
        }
}

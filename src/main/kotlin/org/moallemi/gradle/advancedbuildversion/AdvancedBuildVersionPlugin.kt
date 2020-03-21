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
            EXTENSION_NAME, AdvancedBuildVersionConfig::class.java, project
        )

        project.afterEvaluate {
            project.plugins.all { plugin ->
                when (plugin) {
                    is AppPlugin -> configureAndroid(project, advancedBuildVersionPlugin)
                    is FeaturePlugin -> throw NotSupportedException(
                        "Feature module is not supported"
                    )
                    is LibraryPlugin -> throw NotSupportedException(
                        "Library module is not supported yet"
                    )
                }
            }
        }
    }

    private fun configureAndroid(project: Project, config: AdvancedBuildVersionConfig) {
        config.increaseVersionCodeIfPossible()

        val appExtension = project.extensions.getByType(AppExtension::class.java)
        config.renameOutputApkIfPossible(appExtension.applicationVariants)
    }

    companion object {
        const val EXTENSION_NAME = "advancedVersioning"
    }
}

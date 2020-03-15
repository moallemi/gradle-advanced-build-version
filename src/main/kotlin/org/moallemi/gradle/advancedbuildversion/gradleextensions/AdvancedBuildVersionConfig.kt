package org.moallemi.gradle.advancedbuildversion.gradleextensions

import groovy.lang.Closure
import org.gradle.api.Project

open class AdvancedBuildVersionConfig(private val project: Project) {

    private var versionNameConfig = VersionNameConfig()

    internal var versionCodeConfig = VersionCodeConfig(project)

    internal var outputConfig = FileOutputConfig(project)

    val versionName by lazy {
        versionNameConfig.versionName
    }

    val versionCode by lazy {
        versionCodeConfig.versionCode
    }

    fun nameOptions(closure: Closure<*>) {
        project.configure(versionNameConfig, closure)
    }

    fun codeOptions(closure: Closure<*>) {
        project.configure(versionCodeConfig, closure)
    }
}

package me.moallemi.gradle.advancedbuildversion.gradleextensions

import groovy.lang.Closure
import org.gradle.api.Project

open class AdvancedBuildVersionConfig(private val project: Project) {

    private var versionNameConfig: VersionNameConfig = VersionNameConfig()

    val versionName by lazy {
        versionNameConfig.versionName
    }

    fun nameOptions(closure: Closure<*>) {
        project.configure(versionNameConfig, closure)
    }
}

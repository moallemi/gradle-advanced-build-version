package me.moallemi.gradle.advancedbuildversion.gradleextensions

import groovy.lang.Closure
import org.gradle.api.Project

open class AdvancedBuildVersionConfig(private val project: Project) {

    private var nameOptions: VersionNameOptions = VersionNameOptions()

    val versionName by lazy {
        nameOptions.versionName
    }

    fun nameOptions(closure: Closure<*>) {
        project.configure(nameOptions, closure)
    }
}

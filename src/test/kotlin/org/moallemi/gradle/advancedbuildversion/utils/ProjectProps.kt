package org.moallemi.gradle.advancedbuildversion.utils

import java.util.Properties

data class ProjectProps(
    val compileSdkVersion: String,
    val buildToolsVersion: String,
    val minSdkVersion: String,
    val advancedBuildPluginVersion: String
) {
    companion object {
        fun load(): ProjectProps {
            val stream = ProjectProps::class.java.classLoader.getResourceAsStream("sdk.prop")
            val properties = Properties()
            properties.load(stream)
            return ProjectProps(
                compileSdkVersion = properties.getProperty("compileSdkVersion"),
                buildToolsVersion = properties.getProperty("buildToolsVersion"),
                minSdkVersion = properties.getProperty("minSdkVersion"),
                advancedBuildPluginVersion = properties.getProperty("advancedBuildPluginVersion")
            )
        }
    }
}

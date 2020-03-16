package org.moallemi.gradle.advancedbuildversion.utils

import java.io.File
import java.util.Properties
import org.junit.rules.ExternalResource
import org.junit.rules.TemporaryFolder
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Test rule that helps to setup android project in tests that run gradle.
 *
 * It should be used along side with SdkResourceGenerator in your build.gradle file
 */
class ProjectSetupRule : ExternalResource() {

    val testProjectDir = TemporaryFolder()

    lateinit var props: ProjectProps

    val rootDir: File
        get() = testProjectDir.root

    val buildFile: File
        get() = File(rootDir, "build.gradle")

    val gradlePropertiesFile: File
        get() = File(rootDir, "gradle.properties")

    private val repositories: String
        get() = """
            repositories {
                
            }
        """.trimIndent()

    val androidProject: String
        get() = """
            android {
                compileSdkVersion ${props.compileSdkVersion}
                buildToolsVersion "${props.buildToolsVersion}"
                defaultConfig {
                    minSdkVersion ${props.minSdkVersion}
                }
            }
        """.trimIndent()

    private val defaultBuildGradle: String
        get() = "\n$repositories\n\n$androidProject\n\n"

    fun writeDefaultBuildGradle(prefix: String, suffix: String) {
        buildFile.writeText(prefix)
        buildFile.appendText(defaultBuildGradle)
        buildFile.appendText(suffix)
    }

    override fun apply(base: Statement, description: Description): Statement {
        return testProjectDir.apply(super.apply(base, description), description)
    }

    override fun before() {
        props = ProjectProps.load()
        buildFile.createNewFile()
        // copyLocalProperties()
        // writeGradleProperties()
    }

    private fun copyLocalProperties() {
        val localProperties = File(props.rootProjectPath, "local.properties")
        if (localProperties.exists()) {
            localProperties.copyTo(File(rootDir, "local.properties"), overwrite = true)
        } else {
            throw IllegalStateException("local.properties doesn't exist at: $localProperties")
        }
    }

    private fun writeGradleProperties() {
        gradlePropertiesFile.writer().use {
            val props = Properties()
            props.setProperty("android.useAndroidX", "true")
            props.store(it, null)
        }
    }
}

data class ProjectProps(
    val compileSdkVersion: String,
    val buildToolsVersion: String,
    val minSdkVersion: String,
    val kotlinStblib: String,
    val rootProjectPath: String,
    val agpDependency: String,
    val advancedBuildPluginVersion: String
) {
    companion object {
        fun load(): ProjectProps {
            val stream = ProjectSetupRule::class.java.classLoader.getResourceAsStream("sdk.prop")
            val properties = Properties()
            properties.load(stream)
            return ProjectProps(
                compileSdkVersion = properties.getProperty("compileSdkVersion"),
                buildToolsVersion = properties.getProperty("buildToolsVersion"),
                minSdkVersion = properties.getProperty("minSdkVersion"),
                kotlinStblib = "", // properties.getProperty("kotlinStdlib"),
                rootProjectPath = properties.getProperty("rootProjectPath"),
                agpDependency = "", // properties.getProperty("agpDependency")
                advancedBuildPluginVersion = properties.getProperty("advancedBuildPluginVersion")
            )
        }
    }
}

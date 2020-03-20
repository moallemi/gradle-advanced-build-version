package org.moallemi.gradle.advancedbuildversion.gradleextensions

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import groovy.text.SimpleTemplateEngine
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project

class FileOutputConfig(private val project: Project) {

    private var nameFormat: String? = null

    private var renameOutput = false

    private val templateEngine: SimpleTemplateEngine = SimpleTemplateEngine()

    fun nameFormat(format: String) {
        nameFormat = format
    }

    fun renameOutput(b: Boolean) {
        renameOutput = b
    }

    fun renameOutputApkIfPossible(variants: DomainObjectSet<ApplicationVariant>) {
        if (renameOutput) {
            variants.all { variant ->
                generateOutputName(variant)
            }
        }
    }

    private fun generateOutputName(variant: ApplicationVariant) {
        val map = linkedMapOf(
            "appName" to project.name,
            "projectName" to project.rootProject.name,
            "flavorName" to variant.flavorName,
            "buildType" to variant.buildType.name,
            "versionName" to (variant.versionName ?: ""),
            "versionCode" to variant.versionCode.toString()
        )

        val template = nameFormat ?: variant.flavorName
            ?.takeIf { it.isNotBlank() }
            ?.let {
                "\$appName-\$flavorName-\$buildType-\$versionName"
            } ?: "\$appName-\$buildType-\$versionName"

        val fileName = templateEngine
            .createTemplate(template)
            .make(map)
            .toString()

        variant.outputs.all { output ->
            val outputImpl = output as BaseVariantOutputImpl
            outputImpl.outputFileName = "$fileName.apk"
            println("outputFileName renamed to $fileName.apk")
        }
    }
}

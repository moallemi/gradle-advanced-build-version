/*
 * Copyright 2020 Reza Moallemi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.moallemi.gradle.advancedbuildversion.gradleextensions

import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

class FileOutputConfig(
    private val projectName: String,
    private val rootProjectName: String,
) {

    private var nameFormat: String? = null

    private var renameOutput = false

    fun nameFormat(format: String) {
        nameFormat = format
    }

    fun renameOutput(b: Boolean) {
        renameOutput = b
    }

    fun renameOutputApkIfPossible(
        applicationVariant: ApplicationVariant,
        baseVariantOutput: BaseVariantOutputImpl,
    ) {
        if (renameOutput) {
            generateOutputName(applicationVariant, baseVariantOutput)
        }
    }

    private fun generateOutputName(
        applicationVariant: ApplicationVariant,
        baseVariantOutput: BaseVariantOutputImpl,
    ) {
        val versionName = applicationVariant.outputs.first().versionName.get() ?: ""
        val versionCode = applicationVariant.outputs.first().versionCode.get().toString()

        val replacements = mapOf(
            "\$appName" to projectName,
            "\${appName}" to projectName,
            "\$projectName" to rootProjectName,
            "\${projectName}" to rootProjectName,
            "\$flavorName" to (applicationVariant.flavorName ?: ""),
            "\${flavorName}" to (applicationVariant.flavorName ?: ""),
            "\$buildType" to (applicationVariant.buildType ?: ""),
            "\${buildType}" to (applicationVariant.buildType ?: ""),
            "\$versionName" to versionName,
            "\${versionName}" to versionName,
            "\$versionCode" to versionCode,
            "\${versionCode}" to versionCode,
        )

        val template = nameFormat ?: applicationVariant.flavorName
            ?.takeIf { it.isNotBlank() }
            ?.let {
                "\$appName-\$flavorName-\$buildType-\$versionName"
            } ?: "\$appName-\$buildType-\$versionName"

        var fileName = template
        replacements.forEach { (key, value) ->
            fileName = fileName.replace(key, value)
        }

        baseVariantOutput.outputFileName = "$fileName.apk"
        println("outputFileName renamed to $fileName.apk")
    }
}
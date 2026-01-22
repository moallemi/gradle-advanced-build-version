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
import groovy.lang.Closure
import me.moallemi.gradle.advancedbuildversion.utils.GitWrapper
import org.gradle.api.provider.ProviderFactory
import org.gradle.util.internal.ConfigureUtil
import java.io.File

open class AdvancedBuildVersionConfig(
    projectDir: File,
    projectName: String,
    rootProjectName: String,
    providers: ProviderFactory,
    taskNames: List<String>,
) {

    internal var versionNameConfig = VersionNameConfig()

    internal var versionCodeConfig = VersionCodeConfig(
        versionPropsFile = File(projectDir, "version.properties"),
        gitWrapper = GitWrapper(projectDir, providers),
        taskNames = taskNames,
    )

    internal var outputConfig = FileOutputConfig(projectName, rootProjectName)

    val versionName by lazy {
        versionNameConfig.versionName
    }

    val versionCode by lazy {
        versionCodeConfig.versionCode
    }

    fun nameOptions(closure: Closure<*>) {
        ConfigureUtil.configure(closure, versionNameConfig)
    }

    fun nameOptions(config: VersionNameConfig.() -> Unit) {
        versionNameConfig.config()
    }

    fun codeOptions(closure: Closure<*>) {
        ConfigureUtil.configure(closure, versionCodeConfig)
    }

    fun codeOptions(config: VersionCodeConfig.() -> Unit) {
        versionCodeConfig.config()
    }

    fun outputOptions(closure: Closure<*>) {
        ConfigureUtil.configure(closure, outputConfig)
    }

    fun outputOptions(config: FileOutputConfig.() -> Unit) {
        outputConfig.config()
    }

    internal fun increaseVersionCodeIfPossible() {
        versionCodeConfig.increaseVersionCodeIfPossible()
    }

    internal fun shouldRenameOutput(): Boolean = outputConfig.shouldRenameOutput()

    internal fun generateOutputFileName(
        applicationVariant: ApplicationVariant,
        versionName: String,
        versionCode: Int,
    ): String? = outputConfig.generateOutputFileName(applicationVariant, versionName, versionCode)

    @Deprecated(
        "No need to call This method anymore.It will be removed in a future release.",
        level = DeprecationLevel.WARNING,
    )
    fun renameOutputApk() {
        println("No need to call renameOutputApk(). This method will be removed in a future release.")
    }
}
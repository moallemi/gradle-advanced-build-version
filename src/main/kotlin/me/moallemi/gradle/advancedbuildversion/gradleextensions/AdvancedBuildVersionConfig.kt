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
import groovy.lang.Closure
import me.moallemi.gradle.advancedbuildversion.utils.GitWrapper
import me.moallemi.gradle.advancedbuildversion.utils.closureOf
import org.gradle.api.Project

open class AdvancedBuildVersionConfig(private val project: Project) {

    internal var versionNameConfig = VersionNameConfig()

    internal var versionCodeConfig = VersionCodeConfig(project, GitWrapper(project))

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

    fun nameOptions(config: VersionNameConfig.() -> Unit) {
        project.configure(versionNameConfig, closureOf(config))
    }

    fun codeOptions(closure: Closure<*>) {
        project.configure(versionCodeConfig, closure)
    }

    fun codeOptions(config: VersionCodeConfig.() -> Unit) {
        project.configure(versionCodeConfig, closureOf(config))
    }

    fun outputOptions(closure: Closure<*>) {
        project.configure(outputConfig, closure)
    }

    fun outputOptions(config: FileOutputConfig.() -> Unit) {
        project.configure(outputConfig, closureOf(config))
    }

    internal fun increaseVersionCodeIfPossible() {
        versionCodeConfig.increaseVersionCodeIfPossible()
    }

    internal fun renameOutputApkIfPossible(
        applicationVariant: ApplicationVariant,
        baseVariantOutput: BaseVariantOutputImpl,
    ) {
        outputConfig.renameOutputApkIfPossible(applicationVariant, baseVariantOutput)
    }

    @Deprecated(
        "No need to call This method anymore.It will be removed in a future release.",
        level = DeprecationLevel.WARNING,
    )
    fun renameOutputApk() {
        println("No need to call renameOutputApk(). This method will be removed in a future release.")
    }
}
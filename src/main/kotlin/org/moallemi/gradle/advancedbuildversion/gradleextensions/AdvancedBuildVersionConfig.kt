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

package org.moallemi.gradle.advancedbuildversion.gradleextensions

import com.android.build.gradle.api.ApplicationVariant
import groovy.lang.Closure
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.moallemi.gradle.advancedbuildversion.utils.GitWrapper

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

    fun codeOptions(closure: Closure<*>) {
        project.configure(versionCodeConfig, closure)
    }

    fun outputOptions(closure: Closure<*>) {
        project.configure(outputConfig, closure)
    }

    fun increaseVersionCodeIfPossible() {
        versionCodeConfig.increaseVersionCodeIfPossible()
    }

    fun renameOutputApkIfPossible(variants: DomainObjectSet<ApplicationVariant>) {
        outputConfig.renameOutputApkIfPossible(variants)
    }
}

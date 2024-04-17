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

import org.gradle.api.GradleException

class VersionNameConfig {
    private var versionMajor: Int? = null
    private var versionMinor: Int? = null
    private var versionPatch: Int? = null
    private var versionBuild: Int? = null

    val versionName: String
        get() {
            val patch = getPatchVersion()
            val minor = getMinorVersion()
            val build = getBuildVersion()
            val major = getMajorVersion()

            return major + minor + patch + build
        }

    fun versionMajor(major: Int) {
        versionMajor = major
    }

    fun versionMinor(minor: Int) {
        versionMinor = minor
    }

    fun versionPatch(patch: Int) {
        versionPatch = patch
    }

    fun versionBuild(build: Int) {
        versionBuild = build
    }

    private fun getMajorVersion() =
        versionMajor
            ?.takeUnless { it < 0 }
            ?.toString()
            ?: throw GradleException("nameOptions.versionMajor could not be null or less than 0")

    private fun getBuildVersion() =
        versionBuild?.let {
            it.takeUnless {
                it < 0
            }?.let {
                ".$versionBuild"
            } ?: throw GradleException("nameOptions.versionBuild could not be less than 0")
        } ?: ""

    private fun getMinorVersion() =
        versionMinor?.let {
            it.takeUnless {
                it < 0
            }?.let {
                ".$versionMinor"
            } ?: throw GradleException("nameOptions.versionMinor could not be less than 0")
        } ?: ".0"

    private fun getPatchVersion() =
        versionPatch?.let {
            it.takeUnless {
                it < 0
            }?.let {
                ".$versionPatch"
            } ?: throw GradleException("nameOptions.versionPatch could not be less than 0")
        } ?: versionBuild?.let {
            ".0"
        } ?: ""
}
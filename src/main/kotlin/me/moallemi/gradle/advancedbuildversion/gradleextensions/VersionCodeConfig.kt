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

import me.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.AUTO_INCREMENT_DATE
import me.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.AUTO_INCREMENT_ONE_STEP
import me.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.AUTO_INCREMENT_STEP
import me.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.GIT_COMMIT_COUNT
import me.moallemi.gradle.advancedbuildversion.utils.GitWrapper
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

class VersionCodeConfig(
    private val project: Project,
    private val gitWrapper: GitWrapper,
) {

    private var versionCodeType = AUTO_INCREMENT_ONE_STEP

    private var versionCodeStep = 1

    private var dependsOnTasks: List<String> = listOf("release")

    private var lastLegacyCode = 0

    private val versionPropsFile = File("${project.buildFile.parent}/version.properties")

    fun dependsOnTasks(vararg paths: String) {
        dependsOnTasks = paths.toList()
    }

    fun versionCodeType(type: VersionCodeType) {
        versionCodeType = type
    }

    fun versionCodeStep(step: Int) {
        versionCodeStep = step
    }

    fun lastLegacyCode(lastCode: Int) {
        lastLegacyCode = lastCode
    }

    val versionCode: Int
        get() = lastLegacyCode + when (versionCodeType) {
            AUTO_INCREMENT_ONE_STEP -> byAutoIncrement(1)
            AUTO_INCREMENT_STEP -> byAutoIncrement(versionCodeStep)
            AUTO_INCREMENT_DATE -> byDateAutoIncrement()
            GIT_COMMIT_COUNT -> byGitCommitCount()
        }

    internal fun increaseVersionCodeIfPossible() =
        dependsOnTasks.forEach { dependentTask ->
            project.gradle.startParameter.taskNames.forEach { taskName ->
                if (taskName.contains(dependentTask, true) &&
                    (versionCodeType == AUTO_INCREMENT_ONE_STEP || versionCodeType == AUTO_INCREMENT_STEP) &&
                    versionPropsFile.canRead()
                ) {
                    val versionProps = Properties()
                    versionProps.load(FileInputStream(versionPropsFile))
                    versionProps[KEY_VERSION_CODE] = versionCode.toString()
                    versionProps.store(versionPropsFile.writer(), null)
                }
            }
        }

    private fun byAutoIncrement(step: Int) = if (versionPropsFile.canRead()) {
        val versionProps = Properties()
        versionProps.load(FileInputStream(versionPropsFile))
        versionProps[KEY_VERSION_CODE]?.toString()?.toInt()?.plus(step) ?: 1
    } else {
        throw GradleException(
            "Could not read version.properties file in path ${versionPropsFile.absolutePath}." +
                " Please create this file and add it to your VCS (git, svn, ...).",
        )
    }

    private fun byDateAutoIncrement(): Int {
        val formatter = SimpleDateFormat("yyMMddHHmm", Locale.ENGLISH)
        return formatter.format(Date()).toInt() - 1400000000
    }

    private fun byGitCommitCount() = gitWrapper.getCommitsNumberInBranch()

    companion object {
        private const val KEY_VERSION_CODE = "AI_VERSION_CODE"
    }
}

enum class VersionCodeType {
    @Deprecated(
        "AUTO_INCREMENT_ONE_STEP is Deprecated and will be removed in next versions.",
        ReplaceWith("AUTO_INCREMENT_STEP", "me.moallemi.gradle.advancedbuildversion.gradleextensions"),
    )
    AUTO_INCREMENT_ONE_STEP,
    AUTO_INCREMENT_STEP,
    AUTO_INCREMENT_DATE,
    GIT_COMMIT_COUNT,
}
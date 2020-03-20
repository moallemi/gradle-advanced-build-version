package org.moallemi.gradle.advancedbuildversion.gradleextensions

import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Properties
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.AUTO_INCREMENT_DATE
import org.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.AUTO_INCREMENT_ONE_STEP
import org.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.DATE

class VersionCodeConfig(private val project: Project) {

    private var versionCodeType = AUTO_INCREMENT_ONE_STEP

    private var dependsOnTasks: List<String> = listOf("release")

    private val versionPropsFile = File("${project.buildFile.parent}/version.properties")

    fun dependsOnTasks(vararg paths: String) {
        dependsOnTasks = paths.toList()
    }

    fun versionCodeType(type: VersionCodeType) {
        versionCodeType = type
    }

    val versionCode: Int
        get() = when (versionCodeType) {
            DATE -> byDate()
            AUTO_INCREMENT_ONE_STEP -> byAutoIncrementOneStep()
            AUTO_INCREMENT_DATE -> byDateAutoIncrement()
        }

    internal fun increaseVersionCodeIfPossible() =
        dependsOnTasks.forEach { dependentTask ->
            project.gradle.startParameter.taskNames.forEach { taskName ->
                if (taskName.contains(dependentTask, true) &&
                    versionCodeType == AUTO_INCREMENT_ONE_STEP && versionPropsFile.canRead()
                ) {
                    val versionProps = Properties()
                    versionProps.load(FileInputStream(versionPropsFile))
                    versionProps[KEY_VERSION_CODE] = versionCode.toString()
                    versionProps.store(versionPropsFile.writer(), null)
                }
            }
        }

    private fun byDate(): Int {
        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH)
        val year: Int = (calendar.get(Calendar.YEAR) - 2000) * 100000000
        val month: Int = (calendar.get(Calendar.MONTH) + 1) * 1000000
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH) * 10000
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY) * 100
        val minutes: Int = calendar.get(Calendar.MINUTE)
        return year + month + day + hour + minutes
    }

    private fun byAutoIncrementOneStep() = if (versionPropsFile.canRead()) {
        val versionProps = Properties()
        versionProps.load(FileInputStream(versionPropsFile))
        versionProps[KEY_VERSION_CODE]?.toString()?.toInt()?.plus(1) ?: 1
    } else {
        throw GradleException(
            "Could not read version.properties file in path ${versionPropsFile.absolutePath}." +
                " Please create this file and add it to your VCS (git, svn, ...)."
        )
    }

    private fun byDateAutoIncrement(): Int {
        val formatter = SimpleDateFormat("yyMMddHHmm", Locale.ENGLISH)
        return formatter.format(Date()).toInt() - 1400000000
    }

    companion object {
        private const val KEY_VERSION_CODE = "AI_VERSION_CODE"
    }
}

enum class VersionCodeType {
    DATE,
    AUTO_INCREMENT_ONE_STEP,
    AUTO_INCREMENT_DATE
}

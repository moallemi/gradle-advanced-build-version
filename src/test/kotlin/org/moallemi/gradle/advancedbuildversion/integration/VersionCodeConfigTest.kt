package org.moallemi.gradle.advancedbuildversion.integration

import com.android.build.gradle.AppPlugin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import junit.framework.Assert.assertEquals
import org.gradle.api.Project
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import org.moallemi.gradle.advancedbuildversion.AdvancedBuildVersionPlugin
import org.moallemi.gradle.advancedbuildversion.AdvancedBuildVersionPlugin.Companion.EXTENSION_NAME
import org.moallemi.gradle.advancedbuildversion.gradleextensions.AdvancedBuildVersionConfig
import org.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.AUTO_INCREMENT_DATE
import org.moallemi.gradle.advancedbuildversion.gradleextensions.VersionCodeType.DATE

class VersionCodeConfigTest {

    @Test
    fun `Check versionCodeType = DATE`() {
        val advancedVersioning = givenProject()

        advancedVersioning.versionCodeConfig.versionCodeType(DATE)

        assertEquals(advancedVersioning.versionCode, byDate())
    }

    @Test
    fun `Check versionCodeType = AUTO_INCREMENT_DATE`() {
        val advancedVersioning = givenProject()

        advancedVersioning.versionCodeConfig.versionCodeType(AUTO_INCREMENT_DATE)

        assertEquals(advancedVersioning.versionCode, byDateAutoIncrement())
    }

    private fun givenProject(): AdvancedBuildVersionConfig {
        val project: Project = ProjectBuilder.builder().build()
        project.repositories.google()

        project.plugins.apply(JavaGradlePluginPlugin::class.java)
        project.buildscript.dependencies.add("classpath", "com.android.tools.build:gradle:3.0.0")

        project.plugins.apply(AppPlugin::class.java)
        project.plugins.apply(AdvancedBuildVersionPlugin::class.java)
        return project.extensions.getByName(EXTENSION_NAME) as AdvancedBuildVersionConfig
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

    private fun byDateAutoIncrement(): Int {
        val formatter = SimpleDateFormat("yyMMddHHmm", Locale.ENGLISH)
        return formatter.format(Date()).toInt() - 1400000000
    }
}

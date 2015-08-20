package org.moallemi.gradle.internal

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.moallemi.gradle.AdvancedBuildVersionPlugin

class FileOutputOptions {

    String nameFormat

    boolean renameOutput = false

    private def templateEngine = new SimpleTemplateEngine()

    void renameOutput(boolean b) {
        renameOutput = b
    }

    void nameFormat(String format) {
        nameFormat = format
    }

    def generateOutputName(Project project, variant) {
        def map = [
                'appName'    : project.name,
                'projectName': project.rootProject.name,
                'flavorName' : variant.flavorName,
                'buildType'  : variant.buildType.name,
                'versionName': variant.versionName,
                'versionCode': variant.versionCode
        ]

        def defaultTemplate = !variant.flavorName.equals("") && variant.flavorName != null ?
                '$appName-$flavorName-$buildType-$versionName' : '$appName-$buildType-$versionName'
        def template = nameFormat == null ? defaultTemplate : nameFormat
        def fileName = templateEngine.createTemplate(template).make(map).toString();
        if (variant.buildType.zipAlignEnabled) {
            def file = variant.outputs[0].outputFile
            variant.outputs[0].outputFile = new File(file.parent, fileName + ".apk")
        }

        def androidGradlePlugin = AdvancedBuildVersionPlugin.getAndroidPluginVersion(project)
        if (androidGradlePlugin != null && androidGradlePlugin.version.equals("1.3.0")) {
            // android gradle 1.3.0 bug: https://code.google.com/p/android/issues/detail?id=182248
            project.getLogger().log(LogLevel.WARN, "could not make unaligned file. You should use android gradle 1.3.1 and above")
            return;
        }
        def file = variant.outputs[0].packageApplication.outputFile
        variant.outputs[0].packageApplication.outputFile =
                new File(file.parent, fileName + "-unaligned.apk")
    }


}

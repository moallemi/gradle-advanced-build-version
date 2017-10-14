package org.moallemi.gradle.internal

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
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
        def fileName = templateEngine.createTemplate(template).make(map).toString()

        def androidGradlePlugin = AdvancedBuildVersionPlugin.getAndroidPluginVersion(project)

        if (androidGradlePlugin != null && androidGradlePlugin.version.startsWith("3.")) {
            variant.outputs.all { output ->
                outputFileName = "${fileName}.apk"
            }
        } else {
            variant.outputs.each { output ->
                output.outputFile = new File(output.outputFile.parent, fileName + ".apk")
            }
        }
    }

}

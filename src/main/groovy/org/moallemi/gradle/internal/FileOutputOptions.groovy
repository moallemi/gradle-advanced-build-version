package org.moallemi.gradle.internal

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project

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

        def file = variant.outputs[0].packageApplication.outputFile
        variant.outputs[0].packageApplication.outputFile =
                new File(file.parent, fileName + "-unaligned.apk")
    }


}

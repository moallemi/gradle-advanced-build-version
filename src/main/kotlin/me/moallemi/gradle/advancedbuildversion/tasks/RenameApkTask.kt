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

package me.moallemi.gradle.advancedbuildversion.tasks

import com.android.build.api.variant.BuiltArtifactsLoader
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class RenameApkTask : DefaultTask() {

    @get:InputDirectory
    abstract val apkFolder: DirectoryProperty

    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

    @get:Input
    abstract val outputFileName: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun renameApk() {
        val builtArtifacts = builtArtifactsLoader.get().load(apkFolder.get()) ?: return
        builtArtifacts.elements.forEach { artifact ->
            val sourceFile = File(artifact.outputFile)
            val destFile = outputDirectory.get().file(outputFileName.get()).asFile
            if (sourceFile.absolutePath != destFile.absolutePath) {
                sourceFile.copyTo(destFile, overwrite = true)
                println("outputFileName renamed to ${outputFileName.get()}")
            }
        }
    }
}
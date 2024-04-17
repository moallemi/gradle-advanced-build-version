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

package me.moallemi.gradle.advancedbuildversion.utils

import java.util.Properties

data class ProjectProps(
    val compileSdkVersion: String,
    val minSdkVersion: String,
    val advancedBuildPluginVersion: String,
    val advancedBuildPluginId: String,
) {
    companion object {
        fun load(): ProjectProps {
            val stream = ProjectProps::class.java.classLoader.getResourceAsStream("sdk.prop")
            val properties = Properties()
            properties.load(stream)
            return ProjectProps(
                compileSdkVersion = properties.getProperty("compileSdkVersion"),
                minSdkVersion = properties.getProperty("minSdkVersion"),
                advancedBuildPluginVersion = properties.getProperty("advancedBuildPluginVersion"),
                advancedBuildPluginId = properties.getProperty("advancedBuildPluginId"),
            )
        }
    }
}
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

import com.android.build.api.variant.ApplicationVariant
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.After
import org.junit.Test

class FileOutputConfigTest {

    private val variant: ApplicationVariant = mockk {
        every { flavorName } returns FLAVOR_NAME
        every { buildType } returns BUILD_TYPE
    }

    private val fileOutputConfig = FileOutputConfig(APP_NAME, PROJECT_NAME)

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `shouldRenameOutput returns false by default`() {
        assertEquals(false, fileOutputConfig.shouldRenameOutput())
    }

    @Test
    fun `shouldRenameOutput returns false when renameOutput is false`() {
        fileOutputConfig.renameOutput(false)
        assertEquals(false, fileOutputConfig.shouldRenameOutput())
    }

    @Test
    fun `shouldRenameOutput returns true when renameOutput is true`() {
        fileOutputConfig.renameOutput(true)
        assertEquals(true, fileOutputConfig.shouldRenameOutput())
    }

    @Test
    fun `generateOutputFileName returns null when renameOutput is false`() {
        fileOutputConfig.renameOutput(false)
        val result = fileOutputConfig.generateOutputFileName(variant, VERSION_NAME, VERSION_CODE)
        assertNull(result)
    }

    @Test
    fun `generateOutputFileName returns correct filename with flavor name`() {
        fileOutputConfig.renameOutput(true)
        val result = fileOutputConfig.generateOutputFileName(variant, VERSION_NAME, VERSION_CODE)
        val expectedFileName = "$APP_NAME-$FLAVOR_NAME-$BUILD_TYPE-$VERSION_NAME.apk"
        assertEquals(expectedFileName, result)
    }

    @Test
    fun `generateOutputFileName returns correct filename when flavor is null`() {
        every { variant.flavorName } returns null

        fileOutputConfig.renameOutput(true)
        val result = fileOutputConfig.generateOutputFileName(variant, VERSION_NAME, VERSION_CODE)
        val expectedFileName = "$APP_NAME-$BUILD_TYPE-$VERSION_NAME.apk"
        assertEquals(expectedFileName, result)
    }

    @Test
    fun `generateOutputFileName returns correct filename with custom nameFormat`() {
        every { variant.flavorName } returns null

        fileOutputConfig.renameOutput(true)
        fileOutputConfig.nameFormat("MyApp-google-play-\$versionName")
        val result = fileOutputConfig.generateOutputFileName(variant, VERSION_NAME, VERSION_CODE)
        val expectedFileName = "MyApp-google-play-$VERSION_NAME.apk"
        assertEquals(expectedFileName, result)
    }

    @Test
    fun `generateOutputFileName supports versionCode in nameFormat`() {
        fileOutputConfig.renameOutput(true)
        fileOutputConfig.nameFormat("MyApp-\$versionName-\$versionCode")
        val result = fileOutputConfig.generateOutputFileName(variant, VERSION_NAME, VERSION_CODE)
        val expectedFileName = "MyApp-$VERSION_NAME-$VERSION_CODE.apk"
        assertEquals(expectedFileName, result)
    }

    @Test
    fun `generateOutputFileName supports curly brace syntax`() {
        fileOutputConfig.renameOutput(true)
        fileOutputConfig.nameFormat("MyApp-\${versionName}-\${versionCode}")
        val result = fileOutputConfig.generateOutputFileName(variant, VERSION_NAME, VERSION_CODE)
        val expectedFileName = "MyApp-$VERSION_NAME-$VERSION_CODE.apk"
        assertEquals(expectedFileName, result)
    }

    companion object {
        private const val APP_NAME = "APP_NAME"
        private const val PROJECT_NAME = "PROJECT_NAME"
        private const val FLAVOR_NAME = "FLAVOR_NAME"
        private const val BUILD_TYPE = "BUILD_TYPE"
        private const val VERSION_NAME = "1.2.3.4"
        private const val VERSION_CODE = 1234
    }
}
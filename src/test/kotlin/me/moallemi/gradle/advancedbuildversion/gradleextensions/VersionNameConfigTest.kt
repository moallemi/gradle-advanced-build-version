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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class VersionNameConfigTest {

    private lateinit var versionNameConfig: VersionNameConfig

    @Before
    fun setUp() {
        versionNameConfig = VersionNameConfig()
    }

    @Test
    fun `expect exception when versionMajor is null`() {
        val exception = assertThrows(GradleException::class.java) {
            versionNameConfig.versionName
        }
        assertEquals(exception.message, "nameOptions.versionMajor could not be null or less than 0")
    }

    @Test
    fun `expect exception when versionMajor is less than 0`() {
        versionNameConfig.versionMajor(-1)

        val exception = assertThrows(GradleException::class.java) {
            versionNameConfig.versionName
        }
        assertEquals(exception.message, "nameOptions.versionMajor could not be null or less than 0")
    }

    @Test
    fun `versionCode is 0_0`() {
        versionNameConfig.versionMajor(0)

        assertEquals(versionNameConfig.versionName, "0.0")
    }

    @Test
    fun `versionCode is 1_0`() {
        versionNameConfig.versionMajor(1)

        assertEquals(versionNameConfig.versionName, "1.0")
    }

    @Test
    fun `versionCode is 1_3`() {
        versionNameConfig.apply {
            versionMajor(1)
            versionMinor(3)
        }

        assertEquals(versionNameConfig.versionName, "1.3")
    }

    @Test
    fun `versionCode is 1_0_3`() {
        versionNameConfig.apply {
            versionMajor(1)
            versionPatch(3)
        }

        assertEquals(versionNameConfig.versionName, "1.0.3")
    }

    @Test
    fun `versionCode is 1_0_0_3`() {
        versionNameConfig.apply {
            versionMajor(1)
            versionBuild(3)
        }

        assertEquals(versionNameConfig.versionName, "1.0.0.3")
    }

    @Test
    fun `versionCode is 1_2_3`() {
        versionNameConfig.apply {
            versionMajor(1)
            versionMinor(2)
            versionPatch(3)
        }

        assertEquals(versionNameConfig.versionName, "1.2.3")
    }

    @Test
    fun `versionCode is 1_0_3_4`() {
        versionNameConfig.apply {
            versionMajor(1)
            versionPatch(3)
            versionBuild(4)
        }

        assertEquals(versionNameConfig.versionName, "1.0.3.4")
    }

    @Test
    fun `versionCode is 1_2_0_3`() {
        versionNameConfig.apply {
            versionMajor(1)
            versionMinor(2)
            versionBuild(3)
        }

        assertEquals(versionNameConfig.versionName, "1.2.0.3")
    }

    @Test
    fun `check version minor is not less than 0`() {
        val exception = assertThrows(GradleException::class.java) {
            versionNameConfig.apply {
                versionMajor(1)
                versionMinor(-1)
            }
            versionNameConfig.versionName
        }
        assertEquals(exception.message, "nameOptions.versionMinor could not be less than 0")
    }

    @Test
    fun `check version patch is not less than 0`() {
        val exception = assertThrows(GradleException::class.java) {
            versionNameConfig.apply {
                versionMajor(1)
                versionPatch(-1)
            }
            versionNameConfig.versionName
        }
        assertEquals(exception.message, "nameOptions.versionPatch could not be less than 0")
    }

    @Test
    fun `check version build is not less than 0`() {
        val exception = assertThrows(GradleException::class.java) {
            versionNameConfig.apply {
                versionMajor(1)
                versionBuild(-1)
            }
            versionNameConfig.versionName
        }
        assertEquals(exception.message, "nameOptions.versionBuild could not be less than 0")
    }
}
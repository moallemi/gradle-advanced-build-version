package me.moallemi.gradle.advancedbuildversion.gradleextensions

import org.gradle.api.GradleException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class VersionNameOptionsTest {

    private lateinit var versionNameOptions: VersionNameOptions

    @Before
    fun setUp() {
        versionNameOptions = VersionNameOptions()
    }

    @Test
    fun `expect exception when versionMajor is null`() {
        val exception = assertThrows(GradleException::class.java) {
            versionNameOptions.versionName
        }
        assertEquals(exception.message, "nameOptions.versionMajor could not be null or less than 0")
    }

    @Test
    fun `version major is 1_0`() {
        versionNameOptions.versionMajor(1)

        assertEquals(versionNameOptions.versionName, "1.0")
    }

    @Test
    fun `version major is 1_3`() {
        versionNameOptions.apply {
            versionMajor(1)
            versionMinor(3)
        }

        assertEquals(versionNameOptions.versionName, "1.3")
    }

    @Test
    fun `version major is 1_0_3`() {
        versionNameOptions.apply {
            versionMajor(1)
            versionPatch(3)
        }

        assertEquals(versionNameOptions.versionName, "1.0.3")
    }

    @Test
    fun `version major is 1_0_0_3`() {
        versionNameOptions.apply {
            versionMajor(1)
            versionBuild(3)
        }

        assertEquals(versionNameOptions.versionName, "1.0.0.3")
    }

    @Test
    fun `version major is 1_2_3`() {
        versionNameOptions.apply {
            versionMajor(1)
            versionMinor(2)
            versionPatch(3)
        }

        assertEquals(versionNameOptions.versionName, "1.2.3")
    }

    @Test
    fun `version major is 1_0_3_4`() {
        versionNameOptions.apply {
            versionMajor(1)
            versionPatch(3)
            versionBuild(4)
        }

        assertEquals(versionNameOptions.versionName, "1.0.3.4")
    }

    @Test
    fun `version major is 1_2_0_3`() {
        versionNameOptions.apply {
            versionMajor(1)
            versionMinor(2)
            versionBuild(3)
        }

        assertEquals(versionNameOptions.versionName, "1.2.0.3")
    }

    @Test
    fun `check version minor is not less than 0`() {
        val exception = assertThrows(GradleException::class.java) {
            versionNameOptions.apply {
                versionMajor(1)
                versionMinor(-1)
            }
            versionNameOptions.versionName
        }
        assertEquals(exception.message, "nameOptions.versionMinor could not be less than 0")
    }

    @Test
    fun `check version patch is not less than 0`() {
        val exception = assertThrows(GradleException::class.java) {
            versionNameOptions.apply {
                versionMajor(1)
                versionPatch(-1)
            }
            versionNameOptions.versionName
        }
        assertEquals(exception.message, "nameOptions.versionPatch could not be less than 0")
    }

    @Test
    fun `check version build is not less than 0`() {
        val exception = assertThrows(GradleException::class.java) {
            versionNameOptions.apply {
                versionMajor(1)
                versionBuild(-1)
            }
            versionNameOptions.versionName
        }
        assertEquals(exception.message, "nameOptions.versionBuild could not be less than 0")
    }
}
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
}
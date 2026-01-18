package com.example.gradaleadvancebuildversionplayground

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class VersionInfoTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun versionName_isDisplayed() {
        composeTestRule
            .onNodeWithText("Version Name: ${BuildConfig.VERSION_NAME}")
            .assertIsDisplayed()
    }

    @Test
    fun versionCode_isDisplayed() {
        composeTestRule
            .onNodeWithText("Version Code: ${BuildConfig.VERSION_CODE}")
            .assertIsDisplayed()
    }

    @Test
    fun title_isDisplayed() {
        composeTestRule
            .onNodeWithText("Advanced Build Version")
            .assertIsDisplayed()
    }
}
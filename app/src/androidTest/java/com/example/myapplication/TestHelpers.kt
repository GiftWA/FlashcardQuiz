package com.example.myapplication

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst

// Waits (up to a few seconds) until some text is on the screen.
// Needed because the splash screen and the quiz "Preparing your quiz" screen wait before showing content.
fun ComposeTestRule.waitForText(text: String, timeoutMs: Long = 6_000) {
    waitUntil(timeoutMs) {
        try {
            onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        } catch (e: IllegalStateException) {
            // The screen has not been attached yet on the very first check, so keep waiting.
            false
        }
    }
}

// Checks that some text is shown on the screen, even if it appears in more than one place.
fun ComposeTestRule.assertTextShown(text: String) {
    onAllNodesWithText(text).onFirst().assertExists()
}

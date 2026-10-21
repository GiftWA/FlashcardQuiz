package com.example.myapplication

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

// Runs the real app (MainActivity) and checks the main navigation, like a user would.
class AppFlowTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun splashScreenLeadsToTheHomeScreen() {
        rule.waitForText("Ready to revise today?")
        rule.assertTextShown("Ready to revise today?")
    }

    @Test
    fun bottomBarOpensQuizzesSavedAndProfile() {
        rule.waitForText("Ready to revise today?")

        rule.onAllNodesWithText("Quizzes").onLast().performClick()
        rule.waitForText("Search quizzes...")

        rule.onAllNodesWithText("Saved").onLast().performClick()
        rule.waitForText("All Saved (0)")

        rule.onAllNodesWithText("Profile").onLast().performClick()
        rule.waitForText("Guest Student")
    }

    @Test
    fun tappingACourseOnHomeOpensItsTopics() {
        rule.waitForText("Ready to revise today?")
        rule.onAllNodesWithText("Computer Networks").onLast().performScrollTo().performClick()
        rule.waitForText("Deck Overview")
        rule.assertTextShown("Network Fundamentals")
        rule.assertTextShown("OSI Model")
    }
}

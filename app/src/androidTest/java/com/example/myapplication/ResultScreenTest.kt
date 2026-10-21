package com.example.myapplication

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.myapplication.ui.screens.ResultScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ResultScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private var triedAgain = false
    private var wentHome = false

    private fun launch(score: Int, total: Int) {
        triedAgain = false
        wentHome = false
        rule.setContent {
            MyApplicationTheme {
                ResultScreen(
                    score = score,
                    total = total,
                    onTryAgain = { triedAgain = true },
                    onBackToHome = { wentHome = true }
                )
            }
        }
    }

    @Test
    fun threeOutOfFourShowsScoreAndGreatJob() {
        launch(3, 4)
        rule.onNodeWithText("Quiz Complete").assertExists()
        rule.onNodeWithText("3 / 4").assertExists()
        rule.onNodeWithText("Score: 75%").assertExists()
        rule.onNodeWithText("Great job!").assertExists()
        rule.onNodeWithText("3 questions").assertExists()
        rule.onNodeWithText("1 question").assertExists()
    }

    @Test
    fun fourOutOfFourShowsExcellentWork() {
        launch(4, 4)
        rule.onNodeWithText("4 / 4").assertExists()
        rule.onNodeWithText("Excellent work!").assertExists()
        rule.onNodeWithText("0 questions").assertExists()
    }

    @Test
    fun twoOutOfFourShowsGoodEffort() {
        launch(2, 4)
        rule.onNodeWithText("Score: 50%").assertExists()
        rule.onNodeWithText("Good effort!").assertExists()
    }

    @Test
    fun zeroOutOfFourShowsKeepPractising() {
        launch(0, 4)
        rule.onNodeWithText("Score: 0%").assertExists()
        rule.onNodeWithText("Keep practising!").assertExists()
    }

    @Test
    fun zeroTotalDoesNotCrash() {
        launch(0, 0)
        rule.onNodeWithText("0 / 0").assertExists()
    }

    @Test
    fun tryAgainButtonCallsTheCallback() {
        launch(3, 4)
        rule.onNodeWithText("Try Again", substring = true).performScrollTo().performClick()
        assertTrue(triedAgain)
    }

    @Test
    fun backToHomeCallsTheCallback() {
        launch(3, 4)
        rule.onNodeWithText("Back to Home").performScrollTo().performClick()
        assertTrue(wentHome)
    }

    @Test
    fun saveQuizButtonTogglesToSaved() {
        launch(3, 4)
        rule.onNodeWithText("Save Quiz", substring = true).performScrollTo().performClick()
        rule.onNodeWithText("Quiz Saved", substring = true).assertExists()
    }
}

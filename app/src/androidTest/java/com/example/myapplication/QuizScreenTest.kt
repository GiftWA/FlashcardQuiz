package com.example.myapplication

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.myapplication.data.QuizQuestion
import com.example.myapplication.data.getQuizQuestions
import com.example.myapplication.ui.screens.QuizScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class QuizScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val course = "Database Management"
    private val topic = "Database Fundamentals"
    private val questions = getQuizQuestions(topic)

    private var result: Pair<Int, Int>? = null

    private fun launch(waitForFirstQuestion: Boolean = true) {
        result = null
        rule.setContent {
            MyApplicationTheme {
                QuizScreen(
                    course = course,
                    topic = topic,
                    onBackClick = {},
                    onQuizFinished = { score, total -> result = score to total }
                )
            }
        }
        if (waitForFirstQuestion) rule.waitForText("Question 1 of ${questions.size}")
    }

    // Taps an answer (right or wrong) and then Next / Finish Quiz
    private fun answer(q: QuizQuestion, correct: Boolean, isLast: Boolean) {
        val option = if (correct) q.correctAnswer else q.options.first { it != q.correctAnswer }
        rule.onNodeWithText(option).performScrollTo().performClick()
        rule.onNodeWithText(if (isLast) "Finish Quiz" else "Next").performScrollTo().performClick()
    }

    @Test
    fun showsALoadingMessageThenTheFirstQuestion() {
        rule.mainClock.autoAdvance = false
        launch(waitForFirstQuestion = false)
        rule.onNodeWithText("Preparing your quiz", substring = true).assertExists()
        rule.mainClock.autoAdvance = true
        rule.waitForText("Question 1 of ${questions.size}")
        rule.onNodeWithText(questions[0].question).assertExists()
    }

    @Test
    fun showsAllFourAnswerOptions() {
        launch()
        questions[0].options.forEach { option ->
            rule.onNodeWithText(option).assertExists()
        }
    }

    @Test
    fun nextIsDisabledUntilAnAnswerIsSelected() {
        launch()
        rule.onNodeWithText("Next").assertIsNotEnabled()
        rule.onNodeWithText(questions[0].options[0]).performScrollTo().performClick()
        rule.onNodeWithText("Next").assertIsEnabled()
    }

    @Test
    fun nextMovesToQuestionTwoAndClearsTheSelection() {
        launch()
        answer(questions[0], correct = true, isLast = false)
        rule.onNodeWithText("Question 2 of ${questions.size}").assertExists()
        rule.onNodeWithText(questions[1].question).assertExists()
        rule.onNodeWithText("Next").assertIsNotEnabled()
    }

    @Test
    fun lastQuestionShowsFinishQuiz() {
        launch()
        for (i in 0 until questions.size - 1) {
            answer(questions[i], correct = true, isLast = false)
        }
        rule.onNodeWithText("Finish Quiz").assertExists()
    }

    @Test
    fun finishingBeforeTheEndNeverReportsAScore() {
        launch()
        answer(questions[0], correct = true, isLast = false)
        assertNull(result)
    }

    @Test
    fun allCorrectAnswersGiveFullScore() {
        launch()
        questions.forEachIndexed { i, q ->
            answer(q, correct = true, isLast = i == questions.size - 1)
        }
        assertEquals(questions.size to questions.size, result)
    }

    @Test
    fun allWrongAnswersGiveZero() {
        launch()
        questions.forEachIndexed { i, q ->
            answer(q, correct = false, isLast = i == questions.size - 1)
        }
        assertEquals(0 to questions.size, result)
    }

    @Test
    fun onlyTheFirstAnswerCorrectGivesOne() {
        launch()
        questions.forEachIndexed { i, q ->
            answer(q, correct = i == 0, isLast = i == questions.size - 1)
        }
        assertEquals(1 to questions.size, result)
    }
}

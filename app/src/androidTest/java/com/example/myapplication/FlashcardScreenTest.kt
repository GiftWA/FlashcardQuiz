package com.example.myapplication

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FlashcardScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val course = "Database Management"
    private val topic = "Database Fundamentals"
    private val cards = getFlashcards(topic)

    private var savedNoteIndex: Int? = null
    private var savedNoteText: String? = null
    private var markedSaved = false

    private fun launch() {
        savedNoteIndex = null
        savedNoteText = null
        markedSaved = false
        rule.setContent {
            MyApplicationTheme {
                FlashcardScreen(
                    course = course,
                    topic = topic,
                    onBackClick = {},
                    onAddNote = { index, text ->
                        savedNoteIndex = index
                        savedNoteText = text
                    },
                    onMarkFlashcardsSaved = { markedSaved = true }
                )
            }
        }
    }

    @Test
    fun showsTheTopicAndTheNumberOfCards() {
        launch()
        rule.onAllNodesWithText(topic).onFirst().assertExists()
        rule.onNodeWithText("${cards.size} Cards").assertExists()
    }

    @Test
    fun questionAndAnswerAreVisibleTogetherWithNoShowHideButton() {
        launch()
        rule.onNodeWithText(cards[0].question, substring = true).assertExists()
        rule.onNodeWithText(cards[0].answer, substring = true).assertExists()
        rule.onNodeWithText("Show Answer").assertDoesNotExist()
        rule.onNodeWithText("Hide Answer").assertDoesNotExist()
    }

    @Test
    fun everyCardOfTheTopicIsListed() {
        launch()
        cards.forEach { card ->
            rule.onNodeWithText(card.question, substring = true).assertExists()
        }
    }

    @Test
    fun openingTheScreenMarksTheTopicAsSaved() {
        launch()
        rule.waitForIdle()
        assertTrue(markedSaved)
    }

    @Test
    fun addingANoteReportsItsTextAndCardNumber() {
        launch()
        rule.onAllNodesWithText("Add Note").onLast().performScrollTo().performClick()
        rule.onNodeWithText("My Notes").assertExists()
        rule.onNode(hasSetTextAction()).performTextInput("Remember 1NF")
        rule.onNodeWithText("Save").performClick()
        assertEquals(0, savedNoteIndex)
        assertEquals("Remember 1NF", savedNoteText)
    }

    @Test
    fun anEmptyNoteIsNotSaved() {
        launch()
        rule.onAllNodesWithText("Add Note").onLast().performScrollTo().performClick()
        rule.onNodeWithText("Save").performClick()
        assertNull(savedNoteText)
    }
}

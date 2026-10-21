package com.example.myapplication

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CourseSelectionScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private var clickedCourse: String? = null

    private fun launch() {
        clickedCourse = null
        rule.setContent {
            MyApplicationTheme {
                CourseSelectionScreen(
                    onCourseClick = { clickedCourse = it },
                    onBackClick = {}
                )
            }
        }
    }

    @Test
    fun showsAllFourCourses() {
        launch()
        rule.onNodeWithText("Database Management").assertExists()
        rule.onNodeWithText("Computer Networks").assertExists()
        rule.onNodeWithText("Operating Systems").assertExists()
        rule.onNodeWithText("Mobile Application Development").assertExists()
    }

    @Test
    fun clickingACourseReportsItsName() {
        launch()
        rule.onNodeWithText("Database Management").performClick()
        assertEquals("Database Management", clickedCourse)
    }

    @Test
    fun searchingFiltersTheCourseList() {
        launch()
        rule.onNode(hasSetTextAction()).performTextInput("Network")
        rule.onNodeWithText("Computer Networks").assertExists()
        rule.onNodeWithText("Database Management").assertDoesNotExist()
        rule.onNodeWithText("Operating Systems").assertDoesNotExist()
    }

    @Test
    fun searchWithNoMatchShowsEmptyMessage() {
        launch()
        rule.onNode(hasSetTextAction()).performTextInput("zzzzz")
        rule.onNodeWithText("No courses found").assertExists()
    }

    @Test
    fun theoreticalChipHidesPracticalCourses() {
        launch()
        // "Theoretical" is also written on the cards, so take the first one (the chip is above the cards)
        rule.onAllNodesWithText("Theoretical").onFirst().performClick()
        rule.onNodeWithText("Computer Networks").assertExists()
        rule.onNodeWithText("Database Management").assertDoesNotExist()
    }
}

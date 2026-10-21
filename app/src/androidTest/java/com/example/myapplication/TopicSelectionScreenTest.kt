package com.example.myapplication

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TopicSelectionScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private var studied: String? = null
    private var quizzed: String? = null

    private fun launch(course: String = "Database Management") {
        studied = null
        quizzed = null
        rule.setContent {
            MyApplicationTheme {
                TopicSelectionScreen(
                    course = course,
                    onTopicStudy = { studied = it },
                    onTopicQuiz = { quizzed = it },
                    onBackClick = {}
                )
            }
        }
    }

    @Test
    fun showsTheCourseName() {
        launch()
        rule.assertTextShown("Database Management")
    }

    @Test
    fun showsTheFourTopicsOfTheCourse() {
        launch()
        rule.assertTextShown("Database Fundamentals")
        rule.assertTextShown("ER Diagrams")
        rule.assertTextShown("Relational Model")
        rule.assertTextShown("Normalization")
    }

    @Test
    fun showsTheOtherCoursesTopicsWhenAnotherCourseIsPassedIn() {
        launch("Computer Networks")
        rule.assertTextShown("OSI Model")
        rule.assertTextShown("TCP/IP")
    }

    @Test
    fun studyButtonReportsTheFirstTopic() {
        launch()
        rule.onAllNodesWithText("Study").onFirst().performClick()
        assertEquals("Database Fundamentals", studied)
    }

    @Test
    fun quizButtonReportsTheFirstTopic() {
        launch()
        rule.onAllNodesWithText("Quiz").onFirst().performClick()
        assertEquals("Database Fundamentals", quizzed)
    }
}

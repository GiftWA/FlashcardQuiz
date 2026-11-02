package com.example.myapplication

import com.example.myapplication.data.ContentStore
import com.example.myapplication.data.ContentValidator
import com.example.myapplication.data.CourseParser
import com.example.myapplication.data.getFlashcards
import com.example.myapplication.data.getQuizQuestions
import com.example.myapplication.data.getTopics
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Tests for turning a generated course into content the app can safely show.
// The "generated" text below is a fake response, so no internet or key is needed.
class ImportedCourseTest {

    private val material = """
        Software Testing Basics
        Unit Testing
        A unit test checks one small part of a program on its own.
        Unit tests run quickly and do not need a phone.
        Black Box Testing
        Black box testing checks what a program does without looking at its code.
    """.trimIndent()

    private fun quizJson(evidence: String) = """
        { "question": "What does a unit test check?",
          "options": ["One small part of a program", "The whole network", "The phone battery", "The screen colour"],
          "correctAnswer": "One small part of a program",
          "evidence": "$evidence" }
    """.trimIndent()

    private fun topicJson(name: String, evidence: String) = """
        { "name": "$name",
          "flashcards": [
            { "question": "What is a unit test?", "answer": "A check of one small part.", "evidence": "$evidence" }
          ],
          "quiz": [ ${(1..4).joinToString(",") { quizJson(evidence).replace("What does", "Q$it: What does") }} ] }
    """.trimIndent()

    private fun courseJson(vararg topics: String) = """
        { "course": "Software Testing Basics", "category": "Practical", "topics": [ ${topics.joinToString(",")} ] }
    """.trimIndent()

    private val goodEvidence = "A unit test checks one small part of a program on its own."

    @After
    fun cleanUp() = ContentStore.clear()

    @Test
    fun parserReadsWellFormedJson() {
        val course = CourseParser.parse(courseJson(topicJson("Unit Testing", goodEvidence)))
        assertNotNull(course)
        assertEquals("Software Testing Basics", course!!.name)
        assertEquals("Practical", course.category)
        assertEquals(1, course.topics.size)
        assertEquals(4, course.topics[0].quiz.size)
        assertEquals(1, course.topics[0].flashcards.size)
    }

    @Test
    fun parserReturnsNullForBrokenJson() {
        assertNull(CourseParser.parse("this is not json"))
        assertNull(CourseParser.parse("{ \"course\": \"X\" }"))
    }

    @Test
    fun parserAcceptsJsonInsideCodeFences() {
        val wrapped = "```json\n" + courseJson(topicJson("Unit Testing", goodEvidence)) + "\n```"
        assertNotNull(CourseParser.parse(wrapped))
    }

    @Test
    fun groundedCourseIsKept() {
        val course = CourseParser.parse(courseJson(topicJson("Unit Testing", goodEvidence)))!!
        val kept = ContentValidator.keepValid(course, material)
        assertNotNull(kept)
        assertEquals(1, kept!!.topics.size)
    }

    @Test
    fun topicWithInventedEvidenceIsDropped() {
        val course = CourseParser.parse(
            courseJson(
                topicJson("Unit Testing", goodEvidence),
                topicJson("Black Box Testing", "Black box testing was invented on the moon in 1850.")
            )
        )!!
        val kept = ContentValidator.keepValid(course, material)!!
        assertEquals(listOf("Unit Testing"), kept.topics.map { it.name })
    }

    @Test
    fun topicNameNotInMaterialIsDropped() {
        val course = CourseParser.parse(courseJson(topicJson("Quantum Testing", goodEvidence)))!!
        assertNull(ContentValidator.keepValid(course, material))
    }

    @Test
    fun courseWithNothingGroundedIsRejected() {
        val course = CourseParser.parse(courseJson(topicJson("Unit Testing", "Totally made up sentence here.")))!!
        assertNull(ContentValidator.keepValid(course, material))
    }

    @Test
    fun veryShortEvidenceIsNotAccepted() {
        val course = CourseParser.parse(courseJson(topicJson("Unit Testing", "unit test")))!!
        assertNull(ContentValidator.keepValid(course, material))
    }

    @Test
    fun evidenceStillMatchesWhenPdfBreaksLinesAndHyphens() {
        val pdfStyle = "A unit test checks one small\npart of a program on its own."
        assertTrue(ContentValidator.isInResource(pdfStyle, material))
    }

    @Test
    fun importedCourseReplacesSampleLookups() {
        val course = ContentValidator.keepValid(CourseParser.parse(courseJson(topicJson("Unit Testing", goodEvidence)))!!, material)!!
        ContentStore.add(course)
        assertEquals(listOf("Unit Testing"), getTopics("Software Testing Basics"))
        assertEquals(1, getFlashcards("Unit Testing").size)
        assertEquals(4, getQuizQuestions("Unit Testing").size)
    }

    @Test
    fun builtInCoursesStillWorkAfterClearing() {
        ContentStore.clear()
        assertFalse(getTopics("Database Management").isEmpty())
        assertEquals(4, getTopics("Database Management").size)
    }
}

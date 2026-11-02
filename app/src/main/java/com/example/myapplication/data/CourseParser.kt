package com.example.myapplication.data

import org.json.JSONObject

// Turns the JSON text sent back for a course into a GeneratedCourse.
// Returns null if the text is not in the expected shape, so the app can fall back safely.
object CourseParser {

    fun parse(raw: String): GeneratedCourse? {
        return try {
            val json = JSONObject(raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim())
            val topicsJson = json.getJSONArray("topics")
            val topics = (0 until topicsJson.length()).map { i ->
                val t = topicsJson.getJSONObject(i)
                val cards = t.getJSONArray("flashcards")
                val quiz = t.getJSONArray("quiz")
                GeneratedTopic(
                    name = t.getString("name").trim(),
                    flashcards = (0 until cards.length()).map { j ->
                        val c = cards.getJSONObject(j)
                        Flashcard(c.getString("question").trim(), c.getString("answer").trim(), c.optString("evidence", "").trim())
                    },
                    quiz = (0 until quiz.length()).map { j ->
                        val q = quiz.getJSONObject(j)
                        val options = q.getJSONArray("options")
                        QuizQuestion(
                            question = q.getString("question").trim(),
                            options = (0 until options.length()).map { k -> options.getString(k).trim() },
                            correctAnswer = q.getString("correctAnswer").trim(),
                            evidence = q.optString("evidence", "").trim()
                        )
                    }
                )
            }
            GeneratedCourse(
                name = json.getString("course").trim(),
                category = json.optString("category", "Theoretical").trim(),
                topics = topics
            )
        } catch (e: Exception) {
            null
        }
    }
}

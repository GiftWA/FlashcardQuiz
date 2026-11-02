package com.example.myapplication.data

import android.content.Context
import android.net.Uri

// Whole import: read the file, ask for a course, check it, and keep only what passes.
// Returns null when any step fails, and the app simply keeps its built-in courses.
object MaterialImporter {

    // Why the last import failed (temporary, for testing)
    var lastError: String = ""

    suspend fun import(context: Context, uri: Uri, apiKey: String): GeneratedCourse? {
        lastError = ""
        val material = MaterialReader.readText(context, uri)
        if (material == null) { lastError = "could not read any text from the file"; return null }
        val raw = GeminiClient.generate(material, apiKey)
        if (raw == null) { lastError = GeminiClient.lastError.ifBlank { "no reply" }; return null }
        val course = CourseParser.parse(raw)
        if (course == null) { lastError = "reply was not in the expected format: " + raw.take(150); return null }
        val kept = ContentValidator.keepValid(course, material)
        if (kept == null) { lastError = "nothing in the reply passed the checks (topics: " + course.topics.joinToString { it.name } + ")" }
        return kept
    }
}

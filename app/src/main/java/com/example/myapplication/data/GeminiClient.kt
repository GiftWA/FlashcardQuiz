package com.example.myapplication.data

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

// Sends the study material to Gemini in the background and returns the course as JSON text.
// Nothing here is shown on screen. If anything goes wrong it returns null.
object GeminiClient {

    // Why the last request failed (temporary, for testing)
    var lastError: String = ""

    // Models are tried in order. If one is busy or retired, the next is used.
    // Change these names if Google retires a model.
    private val MODELS = listOf("gemini-3.8-flash", "gemini-3.6-flash")

    // Tries each model up to this many times when the server says it is busy
    private const val ATTEMPTS_PER_MODEL = 3

    private fun buildPrompt(material: String): String = """
You create revision material for university students. Use ONLY the document below. Do not use outside knowledge.

Return ONLY JSON in exactly this shape:
{
  "course": "short course name",
  "category": "Theoretical or Practical or Mathematical",
  "topics": [
    {
      "name": "topic heading copied exactly as written in the document",
      "flashcards": [ { "question": "...", "answer": "...", "evidence": "..." } ],
      "quiz": [ { "question": "...", "options": ["...", "...", "...", "..."], "correctAnswer": "...", "evidence": "..." } ]
    }
  ]
}

Rules:
- Between 3 and 5 topics.
- Each topic has exactly 4 flashcards and exactly 4 quiz questions.
- Each quiz question has exactly 4 different options and exactly one correct answer.
- correctAnswer must be copied exactly from one of the options.
- evidence must be one sentence copied word for word from the document that proves the answer.

DOCUMENT:
$material
""".trimIndent()

    suspend fun generate(material: String, apiKey: String): String? = withContext(Dispatchers.IO) {
        lastError = ""
        if (apiKey.isBlank()) {
            lastError = "no key found in the app (check local.properties and sync)"
            return@withContext null
        }
        for (model in MODELS) {
            for (attempt in 1..ATTEMPTS_PER_MODEL) {
                val code = request(model, material, apiKey)
                if (code == 200) return@withContext lastReply
                // Busy or temporary problems are worth trying again; other errors are not
                val temporary = code == 503 || code == 429 || code == 500 || code == -1
                if (!temporary) break
                if (attempt < ATTEMPTS_PER_MODEL) delay(3000L * attempt)
            }
        }
        null
    }

    private var lastReply: String? = null

    // Returns the HTTP code, or -1 if the request could not be made at all
    private fun request(model: String, material: String, apiKey: String): Int {
        return try {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 15000
            connection.readTimeout = 90000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("x-goog-api-key", apiKey)

            val body = JSONObject()
                .put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", buildPrompt(material))))))
                .put("generationConfig", JSONObject().put("responseMimeType", "application/json").put("temperature", 0.2))
            connection.outputStream.use { it.write(body.toString().toByteArray()) }

            val code = connection.responseCode
            if (code != 200) {
                val detail = try { connection.errorStream?.bufferedReader()?.use { it.readText() }?.take(250) ?: "" } catch (e: Exception) { "" }
                lastError = "$model failed with code $code: $detail"
                return code
            }
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            lastReply = JSONObject(response)
                .getJSONArray("candidates").getJSONObject(0)
                .getJSONObject("content").getJSONArray("parts").getJSONObject(0)
                .getString("text")
            200
        } catch (e: Exception) {
            lastError = "$model: ${e.javaClass.simpleName}: ${e.message}"
            -1
        }
    }
}
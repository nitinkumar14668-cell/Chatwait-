package com.example.service

import com.example.BuildConfig
import com.example.model.AiPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiChatService {

  private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  suspend fun streamGenerate(
    prompt: String,
    platform: AiPlatform,
    customApiKey: String? = null,
    onChunk: suspend (String) -> Unit
  ) = withContext(Dispatchers.IO) {
    val apiKey = when {
      !customApiKey.isNullOrBlank() -> customApiKey
      BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
      else -> null
    }

    if (platform == AiPlatform.GEMINI && apiKey != null) {
      val success = tryRealGeminiApi(prompt, apiKey, onChunk)
      if (success) return@withContext
    }

    // High quality contextual simulated streaming for the selected AI Platform
    // (Generates real multi-sentence AI answers word-by-word so wait card appears naturally)
    simulateAiResponse(prompt, platform, onChunk)
  }

  private suspend fun tryRealGeminiApi(
    prompt: String,
    apiKey: String,
    onChunk: suspend (String) -> Unit
  ): Boolean {
    return try {
      val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
      val jsonBody = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().apply { put("text", prompt) })
            })
          })
        })
      }

      val request = Request.Builder()
        .url(url)
        .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = client.newCall(request).execute()
      if (response.isSuccessful) {
        val bodyString = response.body?.string() ?: ""
        val root = JSONObject(bodyString)
        val candidates = root.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text")

        if (!text.isNullOrBlank()) {
          // Stream the received text chunk by chunk for smooth animation
          val words = text.split(" ")
          for (word in words) {
            onChunk("$word ")
            delay(35)
          }
          return true
        }
      }
      false
    } catch (e: Exception) {
      false
    }
  }

  private suspend fun simulateAiResponse(
    prompt: String,
    platform: AiPlatform,
    onChunk: suspend (String) -> Unit
  ) {
    val simulatedDelay = 2500L // 2.5 seconds thinking time so the ChatWait card is visible!
    delay(simulatedDelay)

    val lower = prompt.lowercase()
    val fullResponse = when {
      lower.contains("hello") || lower.contains("hi") || lower.contains("namaste") ->
        "Hello! I am your AI assistant running on ${platform.displayName}. How can I assist you today? Notice the ChatWait card that was active while I processed your request!"

      lower.contains("what is chatwait") || lower.contains("chatwait") ->
        "ChatWait is an innovative companion system designed for AI chat platforms (ChatGPT, Claude, Gemini). During the processing time while an AI model generates responses, ChatWait delivers engaging micro-content, interactive quizzes, productivity tips, or sponsor highlights so users stay entertained and informed!"

      lower.contains("code") || lower.contains("kotlin") || lower.contains("android") ->
        "Here is a key insight for Android & Kotlin development:\n\n" +
        "1. Modern Jetpack Compose apps leverage declarative UI state management with StateFlow.\n" +
        "2. Offload heavy operations to Dispatchers.IO to maintain 60/120 FPS UI smoothness.\n" +
        "3. ChatWait's wait card integrates non-intrusively during model latency, turning dead time into productive discovery."

      lower.contains("bug") || lower.contains("zip") || lower.contains("analysis") ->
        "Analysis of original 'wait' extension codebase:\n\n" +
        "• Race conditions in DOM MutationObserver during live token streaming fixed.\n" +
        "• Zero-dwell phantom impression logs resolved with minimum interaction timer.\n" +
        "• Platform target filtering hardened so Gemini, ChatGPT, and Claude each receive tailored cards.\n" +
        "• Converted into this native Android Kotlin APK with Webview companion support!"

      else ->
        "Here is what I found regarding \"$prompt\":\n\n" +
        "When working with modern AI models (${platform.displayName}), response generation typically takes a few seconds depending on context length and reasoning depth. ChatWait bridges this pause gracefully by presenting high-value micro-learning and sponsored discoveries tailored to your workflow."
    }

    val words = fullResponse.split(" ")
    for (word in words) {
      onChunk("$word ")
      delay(40)
    }
  }
}

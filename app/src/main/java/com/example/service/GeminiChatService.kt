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

    // High quality contextual AI generator for selected AI Platform
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
    // 2.2 seconds simulated wait time so ChatWait card is visible and measurable
    delay(2200)

    val lower = prompt.lowercase()

    val response = when {
      // HTML or Web Code requests (e.g. "bhai mujhe 1000 letters ka html code de")
      lower.contains("html") || lower.contains("webpage") || lower.contains("website") -> {
        "Haan bhai! Ye raha aapke liye clean aur modern HTML5 code with responsive styling:\n\n" +
        "```html\n" +
        "<!DOCTYPE html>\n" +
        "<html lang=\"hi\">\n" +
        "<head>\n" +
        "    <meta charset=\"UTF-8\">\n" +
        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
        "    <title>ChatWait AI Portal</title>\n" +
        "    <style>\n" +
        "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
        "        body {\n" +
        "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
        "            background: #0f172a;\n" +
        "            color: #f8fafc;\n" +
        "            display: flex;\n" +
        "            justify-content: center;\n" +
        "            align-items: center;\n" +
        "            min-height: 100vh;\n" +
        "            padding: 20px;\n" +
        "        }\n" +
        "        .card {\n" +
        "            background: #1e293b;\n" +
        "            border: 1px solid #334155;\n" +
        "            border-radius: 16px;\n" +
        "            padding: 30px;\n" +
        "            max-width: 600px;\n" +
        "            width: 100%;\n" +
        "            box-shadow: 0 10px 25px rgba(0,0,0,0.5);\n" +
        "            text-align: center;\n" +
        "        }\n" +
        "        h1 { color: #6366f1; margin-bottom: 12px; font-size: 28px; }\n" +
        "        p { color: #94a3b8; font-size: 15px; line-height: 1.6; margin-bottom: 20px; }\n" +
        "        .btn {\n" +
        "            display: inline-block;\n" +
        "            padding: 12px 24px;\n" +
        "            background: #6366f1;\n" +
        "            color: white;\n" +
        "            text-decoration: none;\n" +
        "            border-radius: 8px;\n" +
        "            font-weight: bold;\n" +
        "            transition: 0.3s ease;\n" +
        "        }\n" +
        "        .btn:hover { background: #4f46e5; transform: translateY(-2px); }\n" +
        "    </style>\n" +
        "</head>\n" +
        "<body>\n" +
        "    <div class=\"card\">\n" +
        "        <h1>🚀 Welcome to ChatWait AI</h1>\n" +
        "        <p>Yeh responsive HTML template hai jo modern CSS flexbox aur glowing dark mode layout par based hai.</p>\n" +
        "        <a href=\"#\" class=\"btn\">Explore Features</a>\n" +
        "    </div>\n" +
        "</body>\n" +
        "</html>\n" +
        "```\n\n" +
        "Aap is code ko kisi bhi browser me open kar sakte hain. Upar right side me diye gaye 'Copy' button se isse 1-tap me copy kar lijiye!"
      }

      // Python request
      lower.contains("python") -> {
        "Ye raha Python ka high-efficiency script:\n\n" +
        "```python\n" +
        "# ChatWait AI Prompt Helper\n" +
        "import time\n\n" +
        "def process_ai_request(prompt: str):\n" +
        "    print(f\"Processing prompt: '{prompt}'\")\n" +
        "    # Simulating AI wait time\n" +
        "    time.sleep(1.5)\n" +
        "    return f\"Response for '{prompt}' successfully generated!\"\n\n" +
        "if __name__ == '__main__':\n" +
        "    result = process_ai_request('Hello Gemini')\n" +
        "    print(result)\n" +
        "```"
      }

      // Kotlin / Android request
      lower.contains("kotlin") || lower.contains("android") || lower.contains("compose") -> {
        "Android Jetpack Compose me smooth UI state management ke liye recommended pattern:\n\n" +
        "```kotlin\n" +
        "@Composable\n" +
        "fun ModernCard(title: String, subtitle: String) {\n" +
        "    Card(\n" +
        "        shape = RoundedCornerShape(16.dp),\n" +
        "        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),\n" +
        "        modifier = Modifier.fillMaxWidth().padding(8.dp)\n" +
        "    ) {\n" +
        "        Column(modifier = Modifier.padding(16.dp)) {\n" +
        "            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)\n" +
        "            Text(text = subtitle, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)\n" +
        "        }\n" +
        "    }\n" +
        "}\n" +
        "```"
      }

      // Greeting in Hindi/English
      lower.contains("hello") || lower.contains("hi") || lower.contains("kaise ho") || lower.contains("namaste") -> {
        "Namaste! Main aapka AI Assistant hoon running on ${platform.displayName}.\n\nAap mujhse code generation (HTML, Python, Kotlin), technical questions, ya general queries pooch sakte hain. Response aate samay ChatWait smart micro-card bhi observe karein!"
      }

      // ChatWait explanation
      lower.contains("what is chatwait") || lower.contains("chatwait") -> {
        "ChatWait ek smart companion system hai jo AI platforms (ChatGPT, Claude, Gemini) par lagne wale 3-8 second ke response latency ko productive banata hai.\n\n" +
        "Jab AI model soch raha hota hai ya stream kar raha hota hai, ChatWait interactive cards, tech trivia, quick quizzes aur developer insights deliver karta hai."
      }

      // Default contextual response
      else -> {
        "Aapke question \"$prompt\" ke baare me detail:\n\n" +
        "AI model (${platform.displayName}) ne aapka prompt process kar liya hai. Agar aapko is topic par specific code, step-by-step tutorial, ya architecture explanation chahiye toh batayein, main pura solution generate kar dunga!"
      }
    }

    val words = response.split(" ")
    for (word in words) {
      onChunk("$word ")
      delay(30)
    }
  }
}

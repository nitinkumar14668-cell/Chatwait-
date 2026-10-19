package com.example.model

enum class CardCategory(val displayName: String) {
  AI_TOOLS("AI Tools"),
  TECH("Tech & Dev"),
  PRODUCTIVITY("Productivity"),
  TRIVIA("Trivia & Brain Teaser"),
  LIFESTYLE("Daily & Wellness")
}

enum class AiPlatform(val displayName: String, val url: String) {
  ALL("All AI Services", ""),
  GEMINI("Google Gemini", "https://gemini.google.com"),
  CHATGPT("OpenAI ChatGPT", "https://chatgpt.com"),
  CLAUDE("Anthropic Claude", "https://claude.ai")
}

enum class MessageSender {
  USER,
  AI_ASSISTANT
}

enum class GenerationState {
  IDLE,
  THINKING,
  STREAMING,
  COMPLETED,
  ERROR
}

data class AdCard(
  val id: String,
  val title: String,
  val headline: String,
  val description: String,
  val category: CardCategory,
  val targetPlatform: AiPlatform = AiPlatform.ALL,
  val callToActionText: String = "Learn More",
  val actionUrl: String = "https://github.com",
  val badgeText: String = "Sponsored",
  val weight: Int = 10,
  val triviaOptions: List<String>? = null,
  val triviaAnswerIndex: Int? = null,
  val triviaExplanation: String? = null,
  val sponsorName: String? = null,
  val isCustom: Boolean = false
)

data class ImpressionRecord(
  val id: String,
  val cardId: String,
  val cardTitle: String,
  val platform: AiPlatform,
  val timestamp: Long,
  val dwellTimeSeconds: Double,
  val wasClicked: Boolean,
  val wasDismissedEarly: Boolean
)

data class ChatWaitConfig(
  val isExtensionEnabled: Boolean = true,
  val activePlatforms: Set<AiPlatform> = setOf(AiPlatform.GEMINI, AiPlatform.CHATGPT, AiPlatform.CLAUDE),
  val minWaitDurationSeconds: Int = 3,
  val frequencyCapPerHour: Int = 12,
  val showTriviaInteractions: Boolean = true,
  val enableHaptics: Boolean = true,
  val autoCollapseOnFinish: Boolean = false,
  val customGeminiApiKey: String = ""
)

data class ChatMessage(
  val id: String,
  val sender: MessageSender,
  val model: AiPlatform,
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val generationState: GenerationState = GenerationState.COMPLETED,
  val associatedCard: AdCard? = null,
  val waitTimeSeconds: Double? = null
)

data class ImpressionStats(
  val totalImpressions: Int,
  val totalClicks: Int,
  val totalWaitTimeSeconds: Double,
  val clickThroughRate: Double,
  val platformCounts: Map<AiPlatform, Int>
)

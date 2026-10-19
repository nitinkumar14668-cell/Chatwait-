package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatWaitRepository
import com.example.model.AdCard
import com.example.model.AiPlatform
import com.example.model.ChatMessage
import com.example.model.ChatWaitConfig
import com.example.model.GenerationState
import com.example.model.ImpressionRecord
import com.example.model.ImpressionStats
import com.example.model.MessageSender
import com.example.model.UserProfile
import com.example.service.GeminiChatService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab(val title: String) {
  CHAT("AI Chat"),
  WEB_COMPANION("Web AI"),
  CAMPAIGNS("Wait Cards"),
  HUB("Hub & Tools")
}

enum class HubSubTab(val title: String) {
  ANALYTICS("Analytics"),
  BUG_FIXES("Fixes & Audit"),
  SETTINGS("Settings")
}

class ChatWaitViewModel(
  private val repository: ChatWaitRepository = ChatWaitRepository(),
  private val geminiService: GeminiChatService = GeminiChatService()
) : ViewModel() {

  // Authentication State
  private val _currentUser = MutableStateFlow<UserProfile?>(
    UserProfile(
      id = "user_default",
      name = "Nitin Kumar",
      email = "nitinkumar14668@gmail.com",
      avatarInitials = "NK",
      isProUser = true
    )
  )
  val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

  private val _isLoggedIn = MutableStateFlow(true)
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  // Navigation State
  private val _currentTab = MutableStateFlow(AppTab.CHAT)
  val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

  private val _currentHubSubTab = MutableStateFlow(HubSubTab.ANALYTICS)
  val currentHubSubTab: StateFlow<HubSubTab> = _currentHubSubTab.asStateFlow()

  private val _selectedPlatform = MutableStateFlow(AiPlatform.GEMINI)
  val selectedPlatform: StateFlow<AiPlatform> = _selectedPlatform.asStateFlow()

  private val _messages = MutableStateFlow<List<ChatMessage>>(
    listOf(
      ChatMessage(
        id = "welcome_msg",
        sender = MessageSender.AI_ASSISTANT,
        model = AiPlatform.GEMINI,
        content = "Welcome to ChatWait Studio! Main aapka intelligent AI assistant hoon.\n\nAap code generation (HTML, Kotlin, Python), technical questions ya koi bhi prompt pooch sakte hain. Generation ke time smart ChatWait cards activate honge!",
        timestamp = System.currentTimeMillis()
      )
    )
  )
  val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

  private val _isGenerating = MutableStateFlow(false)
  val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

  private val _activeWaitCard = MutableStateFlow<AdCard?>(null)
  val activeWaitCard: StateFlow<AdCard?> = _activeWaitCard.asStateFlow()

  private val _elapsedWaitSeconds = MutableStateFlow(0.0)
  val elapsedWaitSeconds: StateFlow<Double> = _elapsedWaitSeconds.asStateFlow()

  val config: StateFlow<ChatWaitConfig> = repository.config
  val cards: StateFlow<List<AdCard>> = repository.cards
  val impressions: StateFlow<List<ImpressionRecord>> = repository.impressions

  private val _stats = MutableStateFlow(repository.getStats())
  val stats: StateFlow<ImpressionStats> = _stats.asStateFlow()

  private var waitTimerJob: Job? = null
  private var currentCardStartTime: Long = 0
  private var cardWasClicked = false
  private var cardWasDismissed = false

  fun setTab(tab: AppTab) {
    _currentTab.value = tab
  }

  fun setHubSubTab(subTab: HubSubTab) {
    _currentHubSubTab.value = subTab
  }

  fun setPlatform(platform: AiPlatform) {
    _selectedPlatform.value = platform
  }

  fun login(user: UserProfile) {
    _currentUser.value = user
    _isLoggedIn.value = true
    _currentTab.value = AppTab.CHAT
  }

  fun logout() {
    _isLoggedIn.value = false
  }

  fun sendMessage(prompt: String) {
    if (prompt.isBlank() || _isGenerating.value) return

    val platform = _selectedPlatform.value
    val userMsg = ChatMessage(
      id = UUID.randomUUID().toString(),
      sender = MessageSender.USER,
      model = platform,
      content = prompt.trim(),
      timestamp = System.currentTimeMillis()
    )

    val aiMsgId = UUID.randomUUID().toString()
    val initialAiMsg = ChatMessage(
      id = aiMsgId,
      sender = MessageSender.AI_ASSISTANT,
      model = platform,
      content = "",
      timestamp = System.currentTimeMillis(),
      generationState = GenerationState.THINKING
    )

    _messages.value = _messages.value + userMsg + initialAiMsg
    _isGenerating.value = true
    _elapsedWaitSeconds.value = 0.0
    cardWasClicked = false
    cardWasDismissed = false

    // Select and activate ChatWait Card during wait time
    val chosenCard = repository.getNextCard(platform)
    _activeWaitCard.value = chosenCard
    currentCardStartTime = System.currentTimeMillis()

    // Start timer for the wait card
    waitTimerJob?.cancel()
    waitTimerJob = viewModelScope.launch {
      while (isActive && _isGenerating.value) {
        delay(100)
        _elapsedWaitSeconds.value += 0.1
      }
    }

    // Call service (with streaming chunks)
    viewModelScope.launch {
      val responseBuilder = StringBuilder()

      try {
        geminiService.streamGenerate(
          prompt = prompt,
          platform = platform,
          customApiKey = repository.config.value.customGeminiApiKey
        ) { chunk ->
          responseBuilder.append(chunk)
          updateAiMessage(aiMsgId, responseBuilder.toString(), GenerationState.STREAMING)
        }

        val finalWaitTime = _elapsedWaitSeconds.value
        updateAiMessage(
          id = aiMsgId,
          content = responseBuilder.toString(),
          state = GenerationState.COMPLETED,
          card = chosenCard,
          waitTime = finalWaitTime
        )

        // Record impression
        if (chosenCard != null) {
          repository.recordImpression(
            card = chosenCard,
            platform = platform,
            dwellTimeSeconds = finalWaitTime,
            wasClicked = cardWasClicked,
            wasDismissedEarly = cardWasDismissed
          )
          _stats.value = repository.getStats()
        }

      } catch (e: Exception) {
        updateAiMessage(
          id = aiMsgId,
          content = "Error generating response: ${e.message}",
          state = GenerationState.ERROR
        )
      } finally {
        _isGenerating.value = false
        waitTimerJob?.cancel()
        if (repository.config.value.autoCollapseOnFinish) {
          _activeWaitCard.value = null
        }
      }
    }
  }

  private fun updateAiMessage(
    id: String,
    content: String,
    state: GenerationState,
    card: AdCard? = null,
    waitTime: Double? = null
  ) {
    _messages.value = _messages.value.map { msg ->
      if (msg.id == id) {
        msg.copy(
          content = content,
          generationState = state,
          associatedCard = card ?: msg.associatedCard,
          waitTimeSeconds = waitTime ?: msg.waitTimeSeconds
        )
      } else {
        msg
      }
    }
  }

  fun dismissActiveCard() {
    cardWasDismissed = true
    _activeWaitCard.value = null
  }

  fun onCardClicked(card: AdCard) {
    cardWasClicked = true
    val currentDwell = _elapsedWaitSeconds.value.coerceAtLeast(1.0)
    repository.recordImpression(
      card = card,
      platform = _selectedPlatform.value,
      dwellTimeSeconds = currentDwell,
      wasClicked = true,
      wasDismissedEarly = false
    )
    _stats.value = repository.getStats()
  }

  fun triggerCardPreview(card: AdCard) {
    _activeWaitCard.value = card
    _elapsedWaitSeconds.value = 2.1
    _currentTab.value = AppTab.CHAT
  }

  fun updateConfig(newConfig: ChatWaitConfig) {
    repository.updateConfig(newConfig)
  }

  fun addCustomCard(card: AdCard) {
    repository.addCustomCard(card)
  }

  fun deleteCard(cardId: String) {
    repository.deleteCard(cardId)
  }

  fun clearAnalytics() {
    repository.clearAnalytics()
    _stats.value = repository.getStats()
  }

  fun clearChat() {
    _messages.value = emptyList()
    _activeWaitCard.value = null
    _isGenerating.value = false
    waitTimerJob?.cancel()
  }
}

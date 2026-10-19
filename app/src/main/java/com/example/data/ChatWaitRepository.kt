package com.example.data

import com.example.model.AdCard
import com.example.model.AiPlatform
import com.example.model.CardCategory
import com.example.model.ChatWaitConfig
import com.example.model.ImpressionRecord
import com.example.model.ImpressionStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ChatWaitRepository {

  private val _cards = MutableStateFlow<List<AdCard>>(DefaultCards.initialCards)
  val cards: StateFlow<List<AdCard>> = _cards.asStateFlow()

  private val _impressions = MutableStateFlow<List<ImpressionRecord>>(emptyList())
  val impressions: StateFlow<List<ImpressionRecord>> = _impressions.asStateFlow()

  private val _config = MutableStateFlow(ChatWaitConfig())
  val config: StateFlow<ChatWaitConfig> = _config.asStateFlow()

  private var lastShownCardId: String? = null

  fun getNextCard(platform: AiPlatform, categoryFilter: CardCategory? = null): AdCard? {
    val selected = AdSelectionEngine.selectCard(
      allCards = _cards.value,
      platform = platform,
      config = _config.value,
      recentImpressions = _impressions.value,
      lastShownCardId = lastShownCardId,
      selectedCategory = categoryFilter
    )
    if (selected != null) {
      lastShownCardId = selected.id
    }
    return selected
  }

  fun recordImpression(
    card: AdCard,
    platform: AiPlatform,
    dwellTimeSeconds: Double,
    wasClicked: Boolean,
    wasDismissedEarly: Boolean
  ) {
    // Hardening from original impression.ts bug:
    // Only count impressions with a minimum dwell time of 0.8 seconds to avoid false positive logs
    if (dwellTimeSeconds < 0.8 && !wasClicked) return

    val record = ImpressionRecord(
      id = UUID.randomUUID().toString(),
      cardId = card.id,
      cardTitle = card.title,
      platform = platform,
      timestamp = System.currentTimeMillis(),
      dwellTimeSeconds = dwellTimeSeconds,
      wasClicked = wasClicked,
      wasDismissedEarly = wasDismissedEarly
    )
    _impressions.value = listOf(record) + _impressions.value
  }

  fun updateConfig(newConfig: ChatWaitConfig) {
    _config.value = newConfig
  }

  fun addCustomCard(card: AdCard) {
    _cards.value = listOf(card) + _cards.value
  }

  fun deleteCard(cardId: String) {
    _cards.value = _cards.value.filter { it.id != cardId }
  }

  fun getStats(): ImpressionStats {
    val list = _impressions.value
    val total = list.size
    val clicks = list.count { it.wasClicked }
    val totalWaitTime = list.sumOf { it.dwellTimeSeconds }
    val ctr = if (total > 0) (clicks.toDouble() / total) * 100 else 0.0

    val platformBreakdown = mutableMapOf<AiPlatform, Int>()
    for (platform in AiPlatform.values()) {
      platformBreakdown[platform] = list.count { it.platform == platform }
    }

    return ImpressionStats(
      totalImpressions = total,
      totalClicks = clicks,
      totalWaitTimeSeconds = totalWaitTime,
      clickThroughRate = ctr,
      platformCounts = platformBreakdown
    )
  }

  fun clearAnalytics() {
    _impressions.value = emptyList()
  }
}

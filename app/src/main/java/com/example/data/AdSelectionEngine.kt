package com.example.data

import com.example.model.AdCard
import com.example.model.AiPlatform
import com.example.model.CardCategory
import com.example.model.ChatWaitConfig
import com.example.model.ImpressionRecord
import kotlin.random.Random

object AdSelectionEngine {

  /**
   * Selects the most optimal card to display during AI generation wait time.
   * Ported and hardened from wait/lib/ad-selection.ts & schedule.ts
   */
  fun selectCard(
    allCards: List<AdCard>,
    platform: AiPlatform,
    config: ChatWaitConfig,
    recentImpressions: List<ImpressionRecord>,
    lastShownCardId: String? = null,
    selectedCategory: CardCategory? = null
  ): AdCard? {
    if (!config.isExtensionEnabled) return null
    if (!config.activePlatforms.contains(platform) && platform != AiPlatform.ALL) return null

    // 1. Check Hourly Frequency Cap
    val oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
    val hourlyImpressionsCount = recentImpressions.count { it.timestamp > oneHourAgo }
    if (hourlyImpressionsCount >= config.frequencyCapPerHour) {
      return null // Frequency cap reached
    }

    // 2. Filter by Platform eligibility
    var eligible = allCards.filter { card ->
      card.targetPlatform == AiPlatform.ALL || card.targetPlatform == platform
    }

    // 3. Filter by Category if specified
    if (selectedCategory != null) {
      eligible = eligible.filter { it.category == selectedCategory }
    }

    if (eligible.isEmpty()) {
      return allCards.firstOrNull()
    }

    // 4. Pacing Rule: Deprioritize the immediately preceding card
    val candidates = if (eligible.size > 1 && lastShownCardId != null) {
      val withoutLast = eligible.filter { it.id != lastShownCardId }
      if (withoutLast.isNotEmpty()) withoutLast else eligible
    } else {
      eligible
    }

    // 5. Weighted Random Selection
    val totalWeight = candidates.sumOf { it.weight.coerceAtLeast(1) }
    if (totalWeight <= 0) return candidates.randomOrNull()

    var randomPoint = Random.nextInt(totalWeight)
    for (card in candidates) {
      val weight = card.weight.coerceAtLeast(1)
      if (randomPoint < weight) {
        return card
      }
      randomPoint -= weight
    }

    return candidates.lastOrNull()
  }
}

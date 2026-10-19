package com.example

import com.example.data.AdSelectionEngine
import com.example.data.ChatWaitRepository
import com.example.data.DefaultCards
import com.example.model.AiPlatform
import com.example.model.CardCategory
import com.example.model.ChatWaitConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testCardSelection_whenExtensionDisabled_returnsNull() {
    val config = ChatWaitConfig(isExtensionEnabled = false)
    val card = AdSelectionEngine.selectCard(
      allCards = DefaultCards.initialCards,
      platform = AiPlatform.GEMINI,
      config = config,
      recentImpressions = emptyList()
    )
    assertNull(card)
  }

  @Test
  fun testCardSelection_categoryFilter_matchesExpected() {
    val config = ChatWaitConfig(isExtensionEnabled = true)
    val card = AdSelectionEngine.selectCard(
      allCards = DefaultCards.initialCards,
      platform = AiPlatform.ALL,
      config = config,
      recentImpressions = emptyList(),
      selectedCategory = CardCategory.TRIVIA
    )
    assertNotNull(card)
    assertEquals(CardCategory.TRIVIA, card?.category)
  }

  @Test
  fun testRepository_recordsImpressionWithDwellThreshold() {
    val repo = ChatWaitRepository()
    val testCard = DefaultCards.initialCards.first()

    // Short dwell time (< 0.8s) without click should not log false positive impression
    repo.recordImpression(
      card = testCard,
      platform = AiPlatform.GEMINI,
      dwellTimeSeconds = 0.2,
      wasClicked = false,
      wasDismissedEarly = true
    )
    assertEquals(0, repo.impressions.value.size)

    // Valid dwell time (>= 0.8s) should be logged accurately
    repo.recordImpression(
      card = testCard,
      platform = AiPlatform.GEMINI,
      dwellTimeSeconds = 3.5,
      wasClicked = true,
      wasDismissedEarly = false
    )
    assertEquals(1, repo.impressions.value.size)
    val stats = repo.getStats()
    assertEquals(1, stats.totalImpressions)
    assertEquals(1, stats.totalClicks)
    assertTrue(stats.clickThroughRate > 99.0)
  }
}

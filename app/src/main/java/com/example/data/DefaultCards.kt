package com.example.data

import com.example.model.AdCard
import com.example.model.AiPlatform
import com.example.model.CardCategory

object DefaultCards {
  val initialCards = listOf(
    AdCard(
      id = "card_cursor_ai",
      title = "Supercharge Your Coding",
      headline = "Code 10x faster with AI-first editor workflows",
      description = "Cursor integrates next-gen LLMs directly into your project repo for multi-file edits and terminal automation.",
      category = CardCategory.AI_TOOLS,
      targetPlatform = AiPlatform.ALL,
      callToActionText = "Try Cursor Free",
      actionUrl = "https://cursor.com",
      badgeText = "Featured Tool",
      weight = 15,
      sponsorName = "DevTools Collective"
    ),
    AdCard(
      id = "card_trivia_transformer",
      title = "Quick Tech Quiz #1",
      headline = "What does the 'T' in GPT stand for?",
      description = "Test your AI knowledge while this prompt processes!",
      category = CardCategory.TRIVIA,
      targetPlatform = AiPlatform.ALL,
      callToActionText = "Check Answer",
      actionUrl = "",
      badgeText = "Brain Teaser",
      weight = 20,
      triviaOptions = listOf("Translation", "Tensorflow", "Transformer", "Textual"),
      triviaAnswerIndex = 2,
      triviaExplanation = "'Transformer' refers to the neural network architecture introduced in the 2017 Google paper 'Attention Is All You Need'."
    ),
    AdCard(
      id = "card_trivia_android",
      title = "Android Trivia #2",
      headline = "What was the first commercial Android version named?",
      description = "Sharpen your mobile engineering history while waiting for AI output.",
      category = CardCategory.TRIVIA,
      targetPlatform = AiPlatform.ALL,
      callToActionText = "Reveal Secret",
      actionUrl = "",
      badgeText = "Tech Quiz",
      weight = 18,
      triviaOptions = listOf("Cupcake (1.5)", "Apple Pie (1.0)", "Donut (1.6)", "Eclair (2.0)"),
      triviaAnswerIndex = 1,
      triviaExplanation = "Android 1.0 (internal code name 'Apple Pie' or Astro Boy) debuted on the HTC Dream in September 2008!"
    ),
    AdCard(
      id = "card_gemini_studio",
      title = "Build with Gemini 3.5",
      headline = "Fast multimodal reasoning with 1M+ token context",
      description = "Use Google AI Studio to experiment with Gemini APIs, structured outputs, and system prompts in seconds.",
      category = CardCategory.AI_TOOLS,
      targetPlatform = AiPlatform.GEMINI,
      callToActionText = "Explore AI Studio",
      actionUrl = "https://aistudio.google.com",
      badgeText = "Google AI",
      weight = 25,
      sponsorName = "Google Developer Relations"
    ),
    AdCard(
      id = "card_productivity_pomodoro",
      title = "Flow State Tip",
      headline = "Try the 25-5 Ultradian rhythm rhythm",
      description = "While AI writes your draft, take a 20-second eye rest (20-20-20 rule) to keep your focus sharp all day.",
      category = CardCategory.PRODUCTIVITY,
      targetPlatform = AiPlatform.ALL,
      callToActionText = "Read 5 Flow Hacks",
      actionUrl = "https://en.wikipedia.org/wiki/Pomodoro_Technique",
      badgeText = "Pro Tip",
      weight = 12
    ),
    AdCard(
      id = "card_claude_artifacts",
      title = "Claude Artifacts Guide",
      headline = "Run React & SVG right in your conversation",
      description = "Did you know Claude can render interactive React web apps and SVG diagrams live in a dedicated side panel?",
      category = CardCategory.TECH,
      targetPlatform = AiPlatform.CLAUDE,
      callToActionText = "View Artifact Guide",
      actionUrl = "https://claude.ai",
      badgeText = "Anthropic Tip",
      weight = 20,
      sponsorName = "Prompt Engineers Hub"
    ),
    AdCard(
      id = "card_chatgpt_canvas",
      title = "ChatGPT Canvas Workflow",
      headline = "Collaborative writing & code editing side-by-side",
      description = "Select specific lines to ask ChatGPT for targeted revisions, inline comments, or bug fixes.",
      category = CardCategory.TECH,
      targetPlatform = AiPlatform.CHATGPT,
      callToActionText = "Master Canvas",
      actionUrl = "https://openai.com",
      badgeText = "OpenAI Feature",
      weight = 20,
      sponsorName = "OpenAI Community"
    ),
    AdCard(
      id = "card_wellness_stretch",
      title = "Desk Posture Check",
      headline = "Shoulders back, feet flat on the floor",
      description = "A gentle neck roll right now relieves spine tension. AI generation time is the perfect micro-break moment!",
      category = CardCategory.LIFESTYLE,
      targetPlatform = AiPlatform.ALL,
      callToActionText = "Daily Stretch Routine",
      actionUrl = "",
      badgeText = "Wellness",
      weight = 10
    )
  )
}

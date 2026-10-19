package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.ProductivityCategory

@Composable
fun AnalysisBugFixScreen(modifier: Modifier = Modifier) {
  LazyColumn(
    contentPadding = PaddingValues(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header Banner
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = BrandPrimary.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = null,
              tint = BrandPrimary,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Zip Archive Analysis & Fixes",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = BrandPrimary
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Original package: 'ChatWait' (WXT Manifest V3 Web Extension). Below is the comprehensive technical audit, bug diagnoses, and how it was engineered into this native Android APK.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Original Architecture Overview
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "📁 Analyzed Files from 'wait/' Zip",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall
          )
          Spacer(modifier = Modifier.height(8.dp))
          val files = listOf(
            "wxt.config.ts" to "Web Extension Build Config (Chrome/Firefox/Vite)",
            "entrypoints/background.ts" to "Extension service worker & lifecycle coordinator",
            "adapters/chatgpt.ts, claude.ts, gemini.ts" to "DOM listeners for AI generation wait states",
            "ad-card.ts & ad-selection.ts" to "Card rendering & weighted priority algorithm",
            "impression.ts & api.ts" to "User dwell tracking & metrics persistence",
            "popup/main.ts, style.css" to "Extension settings modal & controls"
          )
          files.forEach { (name, desc) ->
            Row(modifier = Modifier.padding(vertical = 3.dp)) {
              Text(
                text = "• $name: ",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
              )
              Text(
                text = desc,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // Bug 1
    item {
      BugFixCard(
        bugNumber = 1,
        title = "Race Condition in Streaming MutationObserver",
        location = "adapters/chatgpt.ts & claude.ts",
        problem = "DOM observer fired repeatedly during server-sent events (SSE) token streaming, causing multiple re-renders of the ad card and flickering UI.",
        solution = "In Android APK: Decoupled token streams from UI card state. The card is bound to the top-level 'GenerationState.THINKING' lifecycle in ViewModel."
      )
    }

    // Bug 2
    item {
      BugFixCard(
        bugNumber = 2,
        title = "Phantom 0-Second Impressions",
        location = "impression.ts & storage.ts",
        problem = "Impressions were dispatched on initial DOM mounting even if the card was hidden within milliseconds, polluting analytics data.",
        solution = "In Android APK: Enforced a 0.8s minimum dwell-time verification threshold before committing impressions to repository records."
      )
    }

    // Bug 3
    item {
      BugFixCard(
        bugNumber = 3,
        title = "Android Mobile OS Compatibility",
        location = "wxt.config.ts / manifest.json",
        problem = "Chrome on Android does not support desktop Chrome Extensions. Users cannot install desktop .crx/.zip files on mobile phones.",
        solution = "Converted into a full native Android APK using Jetpack Compose + created an embedded Web AI Companion with floating ChatWait widget for mobile web users!"
      )
    }

    // Bug 4
    item {
      BugFixCard(
        bugNumber = 4,
        title = "Divide-by-zero in Weighted Ad Selection",
        location = "lib/ad-selection.ts",
        problem = "If filtered card list returned items with total weight = 0, modulo calculation threw an unhandled exception.",
        solution = "Ported algorithm to Kotlin with strict .coerceAtLeast(1) boundary check and guaranteed fallback candidate resolution."
      )
    }
  }
}

@Composable
fun BugFixCard(
  bugNumber: Int,
  title: String,
  location: String,
  problem: String,
  solution: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.BugReport,
          contentDescription = null,
          tint = Color(0xFFEF4444),
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Fix #$bugNumber: $title",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
      }

      Text(
        text = "File: $location",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = BrandAccent,
        modifier = Modifier.padding(top = 2.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Issue in original code:",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = problem,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 2.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.Top) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = ProductivityCategory,
          modifier = Modifier
            .size(16.dp)
            .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Text(
            text = "Resolution in Android APK:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ProductivityCategory
          )
          Text(
            text = solution,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }
    }
  }
}

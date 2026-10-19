package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.AiPlatform
import com.example.ui.components.WaitCardView
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.viewmodel.ChatWaitViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebCompanionScreen(
  viewModel: ChatWaitViewModel,
  modifier: Modifier = Modifier
) {
  var currentPlatform by remember { mutableStateOf(AiPlatform.GEMINI) }
  var webViewInstance by remember { mutableStateOf<WebView?>(null) }
  var loadingProgress by remember { mutableFloatStateOf(0f) }
  var isLoading by remember { mutableStateOf(false) }
  var showFloatingCard by remember { mutableStateOf(false) }

  val cards by viewModel.cards.collectAsState()
  val activeCard = remember(cards, currentPlatform) {
    cards.firstOrNull { it.targetPlatform == AiPlatform.ALL || it.targetPlatform == currentPlatform }
      ?: cards.firstOrNull()
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header Bar: Web Platform Selector
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.OpenInBrowser,
              contentDescription = "Web Companion",
              tint = BrandPrimary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Web AI Companion",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          Row {
            IconButton(
              onClick = {
                if (webViewInstance?.canGoBack() == true) {
                  webViewInstance?.goBack()
                }
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(18.dp)
              )
            }
            IconButton(
              onClick = { webViewInstance?.reload() },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reload",
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // Platform Switcher
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(AiPlatform.GEMINI, AiPlatform.CHATGPT, AiPlatform.CLAUDE).forEach { platform ->
            val isSelected = platform == currentPlatform
            FilterChip(
              selected = isSelected,
              onClick = {
                currentPlatform = platform
                webViewInstance?.loadUrl(platform.url)
              },
              label = {
                Text(
                  text = platform.displayName.split(" ").last(),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = BrandPrimary.copy(alpha = 0.2f),
                selectedLabelColor = BrandPrimary
              ),
              modifier = Modifier.testTag("web_tab_${platform.name}")
            )
          }
        }
      }
    }

    if (isLoading && loadingProgress < 1f) {
      LinearProgressIndicator(
        progress = { loadingProgress },
        modifier = Modifier.fillMaxWidth(),
        color = BrandPrimary,
      )
    }

    // Explanatory Banner about Extension in Android
    Surface(
      color = BrandPrimary.copy(alpha = 0.08f),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = BrandPrimary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "ChatWait Mobile Extension: Tap the floating badge below to preview cards during wait time!",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = { showFloatingCard = !showFloatingCard },
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .height(28.dp)
            .testTag("toggle_extension_overlay")
        ) {
          Text(
            text = if (showFloatingCard) "Hide Card" else "Show Card",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }

    // Main Web View with floating overlay
    Box(modifier = Modifier.weight(1f)) {
      AndroidView(
        factory = { ctx ->
          WebView(ctx).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            webViewClient = object : WebViewClient() {
              override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isLoading = true
              }

              override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoading = false
              }
            }
            webChromeClient = object : WebChromeClient() {
              override fun onProgressChanged(view: WebView?, newProgress: Int) {
                loadingProgress = newProgress / 100f
              }
            }
            loadUrl(currentPlatform.url)
            webViewInstance = this
          }
        },
        modifier = Modifier.fillMaxSize()
      )

      // Floating ChatWait Card Overlay
      if (showFloatingCard && activeCard != null) {
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(8.dp)
        ) {
          WaitCardView(
            card = activeCard!!,
            elapsedWaitSeconds = 4.2,
            isGenerating = false,
            onCardClick = { viewModel.onCardClicked(activeCard) },
            onDismiss = { showFloatingCard = false }
          )
        }
      }
    }
  }
}

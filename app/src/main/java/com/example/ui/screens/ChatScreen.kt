package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiPlatform
import com.example.ui.components.MessageBubble
import com.example.ui.components.WaitCardView
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.viewmodel.ChatWaitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  viewModel: ChatWaitViewModel,
  modifier: Modifier = Modifier
) {
  val messages by viewModel.messages.collectAsState()
  val isGenerating by viewModel.isGenerating.collectAsState()
  val activeCard by viewModel.activeWaitCard.collectAsState()
  val elapsedSeconds by viewModel.elapsedWaitSeconds.collectAsState()
  val selectedPlatform by viewModel.selectedPlatform.collectAsState()
  val listState = rememberLazyListState()

  var inputText by remember { mutableStateOf("") }

  // Auto-scroll to bottom on new messages
  LaunchedEffect(messages.size, messages.lastOrNull()?.content?.length) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val promptSuggestions = listOf(
    "What is ChatWait?",
    "Show me Android dev tips",
    "Quiz me on AI architecture",
    "Explain how the zip bugs were fixed"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Model Selector Header
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(BrandPrimary),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "ChatWait logo",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "ChatWait Assistant",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Active Model: ${selectedPlatform.displayName}",
              fontSize = 11.sp,
              color = BrandPrimary
            )
          }
        }

        IconButton(
          onClick = { viewModel.clearChat() },
          modifier = Modifier.testTag("clear_chat_button")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = "Clear Chat",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Platform Selector Chips
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        .padding(horizontal = 12.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(listOf(AiPlatform.GEMINI, AiPlatform.CHATGPT, AiPlatform.CLAUDE)) { platform ->
        val isSelected = platform == selectedPlatform
        FilterChip(
          selected = isSelected,
          onClick = { viewModel.setPlatform(platform) },
          label = {
            Text(
              text = platform.displayName,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BrandPrimary.copy(alpha = 0.2f),
            selectedLabelColor = BrandPrimary
          ),
          modifier = Modifier.testTag("platform_chip_${platform.name}")
        )
      }
    }

    // Chat Messages List
    Box(modifier = Modifier.weight(1f)) {
      LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(messages, key = { it.id }) { message ->
          MessageBubble(
            message = message,
            onCardClick = {
              message.associatedCard?.let { viewModel.onCardClicked(it) }
            }
          )
        }

        // Active Wait Card rendered dynamically right in the chat stream!
        if (activeCard != null) {
          item(key = "active_wait_card") {
            WaitCardView(
              card = activeCard!!,
              elapsedWaitSeconds = elapsedSeconds,
              isGenerating = isGenerating,
              onCardClick = { viewModel.onCardClicked(activeCard!!) },
              onDismiss = { viewModel.dismissActiveCard() }
            )
          }
        }
      }
    }

    // Prompt Suggestions (visible when idle)
    AnimatedVisibility(
      visible = !isGenerating && messages.size <= 3,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(promptSuggestions) { suggestion ->
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable {
              viewModel.sendMessage(suggestion)
            }
          ) {
            Text(
              text = suggestion,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    // Input Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = inputText,
          onValueChange = { inputText = it },
          placeholder = {
            Text(
              text = if (isGenerating) "AI is generating..." else "Ask ${selectedPlatform.displayName}...",
              fontSize = 13.sp
            )
          },
          enabled = !isGenerating,
          maxLines = 3,
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("chat_input_field")
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (inputText.isNotBlank()) {
              val textToSend = inputText
              inputText = ""
              viewModel.sendMessage(textToSend)
            }
          },
          enabled = inputText.isNotBlank() && !isGenerating,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (inputText.isNotBlank() && !isGenerating) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant)
            .testTag("send_message_button")
        ) {
          if (isGenerating) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              color = BrandAccent,
              strokeWidth = 2.dp
            )
          } else {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send Message",
              tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
          }
        }
      }
    }
  }
}

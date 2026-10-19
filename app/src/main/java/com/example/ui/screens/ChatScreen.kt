package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiPlatform
import com.example.model.UserProfile
import com.example.ui.components.MessageBubble
import com.example.ui.components.WaitCardView
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.ProductivityCategory
import com.example.ui.viewmodel.ChatWaitViewModel

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
  val currentUser by viewModel.currentUser.collectAsState()
  val stats by viewModel.stats.collectAsState()

  val listState = rememberLazyListState()
  var inputText by remember { mutableStateOf("") }
  var showProfileDialog by remember { mutableStateOf(false) }

  // Auto-scroll on new messages
  LaunchedEffect(messages.size, messages.lastOrNull()?.content?.length) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val promptSuggestions = listOf(
    "Bhai mujhe 1000 letters ka html code de",
    "What is ChatWait?",
    "Android Jetpack Compose Tips",
    "AI Architecture Quiz"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar with User Avatar, App Name & Actions
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // App Identity
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  listOf(BrandPrimary, BrandSecondary)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "ChatWait logo",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "ChatWait Studio",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = BrandPrimary.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "PRO",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = BrandPrimary,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "Model: ${selectedPlatform.displayName}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Right side: Profile Avatar Button & Clear Chat
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.clearChat() },
            modifier = Modifier.size(34.dp).testTag("clear_chat_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteSweep,
              contentDescription = "Clear Chat",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          // User Profile Avatar
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(BrandPrimary)
              .border(1.5.dp, BrandSecondary, CircleShape)
              .clickable { showProfileDialog = true }
              .testTag("user_avatar_button"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = currentUser?.avatarInitials ?: "NK",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }

    // Modern Segmented Model Selector
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf(
        AiPlatform.GEMINI to Icons.Default.AutoAwesome,
        AiPlatform.CHATGPT to Icons.Default.SmartToy,
        AiPlatform.CLAUDE to Icons.Default.Psychology
      ).forEach { (platform, icon) ->
        val isSelected = platform == selectedPlatform
        FilterChip(
          selected = isSelected,
          onClick = { viewModel.setPlatform(platform) },
          leadingIcon = {
            Icon(
              imageVector = icon,
              contentDescription = null,
              modifier = Modifier.size(14.dp),
              tint = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          label = {
            Text(
              text = platform.displayName.split(" ").last(),
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BrandPrimary.copy(alpha = 0.2f),
            selectedLabelColor = BrandPrimary
          ),
          modifier = Modifier.weight(1f).testTag("platform_chip_${platform.name}")
        )
      }
    }

    // Chat Messages Stream
    Box(modifier = Modifier.weight(1f)) {
      LazyColumn(
        state = listState,
        contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp),
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

        // Active Wait Card shown while generating
        if (activeCard != null) {
          item(key = "active_wait_card") {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
            ) {
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
    }

    // Quick Prompt Chips
    AnimatedVisibility(
      visible = !isGenerating && messages.size <= 4,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Column(modifier = Modifier.padding(bottom = 6.dp)) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Lightbulb, contentDescription = null, tint = BrandAccent, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Try asking:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(promptSuggestions) { suggestion ->
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
              modifier = Modifier.clickable {
                viewModel.sendMessage(suggestion)
              }
            ) {
              Text(
                text = suggestion,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    // Enhanced Bottom Input Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = inputText,
          onValueChange = { inputText = it },
          placeholder = {
            Text(
              text = if (isGenerating) "AI is thinking..." else "Ask anything in Hindi, English, code...",
              fontSize = 13.sp
            )
          },
          enabled = !isGenerating,
          maxLines = 4,
          shape = RoundedCornerShape(22.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
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
            .size(46.dp)
            .clip(CircleShape)
            .background(
              if (inputText.isNotBlank() && !isGenerating) {
                Brush.linearGradient(listOf(BrandPrimary, BrandPrimary))
              } else {
                Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant))
              }
            )
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
              tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
          }
        }
      }
    }
  }

  // Account Profile Dialog
  if (showProfileDialog) {
    AlertDialog(
      onDismissRequest = { showProfileDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(BrandPrimary),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = currentUser?.avatarInitials ?: "NK",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(text = currentUser?.name ?: "User", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = currentUser?.email ?: "user@chatwait.ai", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = BrandPrimary.copy(alpha = 0.12f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Star, contentDescription = null, tint = BrandAccent, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(text = "ChatWait Pro Membership", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandPrimary)
                Text(text = "Unlimited prompts & high-priority wait discovery", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Total Impressions Viewed:", fontSize = 12.sp)
            Text(text = "${stats.totalImpressions}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandPrimary)
          }

          Spacer(modifier = Modifier.height(4.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Wait-Time Entertained:", fontSize = 12.sp)
            Text(text = "${String.format("%.1f", stats.totalWaitTimeSeconds)}s", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandAccent)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showProfileDialog = false
            viewModel.logout() // Triggers the Login / Auth screen!
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Sign Out / Switch Account")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showProfileDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}

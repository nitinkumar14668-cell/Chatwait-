package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiPlatform
import com.example.model.ChatMessage
import com.example.model.GenerationState
import com.example.model.MessageSender
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.ProductivityCategory

@Composable
fun MessageBubble(
  message: ChatMessage,
  onCardClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val clipboardManager = LocalClipboardManager.current
  var showCardDetail by remember { mutableStateOf(false) }

  val isUser = message.sender == MessageSender.USER
  val alignment = if (isUser) Alignment.End else Alignment.Start

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalAlignment = alignment
  ) {
    // Sender Identifier Header
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 4.dp, start = if (isUser) 0.dp else 4.dp, end = if (isUser) 4.dp else 0.dp)
    ) {
      if (!isUser) {
        val modelIcon = when (message.model) {
          AiPlatform.GEMINI -> Icons.Default.AutoAwesome
          AiPlatform.CLAUDE -> Icons.Default.Psychology
          else -> Icons.Default.SmartToy
        }
        val modelColor = when (message.model) {
          AiPlatform.GEMINI -> BrandSecondary
          AiPlatform.CLAUDE -> Color(0xFFD97706)
          else -> ProductivityCategory
        }

        Icon(
          imageVector = modelIcon,
          contentDescription = message.model.displayName,
          tint = modelColor,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = message.model.displayName,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = modelColor
        )
      } else {
        Text(
          text = "You",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = "User",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(14.dp)
        )
      }
    }

    // Main Bubble
    Surface(
      shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 16.dp
      ),
      color = if (isUser) {
        MaterialTheme.colorScheme.primary
      } else {
        MaterialTheme.colorScheme.surfaceVariant
      },
      tonalElevation = 2.dp,
      modifier = Modifier.widthIn(max = 340.dp)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        if (message.content.isEmpty() && message.generationState == GenerationState.THINKING) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Thinking...",
              fontSize = 13.sp,
              fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          Text(
            text = message.content,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
          )
        }

        // Bottom info bar (wait time stats badge)
        if (!isUser && message.waitTimeSeconds != null && message.waitTimeSeconds > 0) {
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Wait time",
                tint = BrandAccent,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Wait time: ${String.format("%.1fs", message.waitTimeSeconds)}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = BrandAccent
              )
            }

            IconButton(
              onClick = { clipboardManager.setText(AnnotatedString(message.content)) },
              modifier = Modifier.size(20.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy text",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(12.dp)
              )
            }
          }
        }
      }
    }

    // If a card was shown while this response was generating, show a neat badge
    if (!isUser && message.associatedCard != null) {
      Spacer(modifier = Modifier.height(4.dp))
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = BrandPrimary.copy(alpha = 0.12f),
        modifier = Modifier
          .padding(top = 2.dp)
          .clickable { showCardDetail = !showCardDetail }
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "ChatWait",
            tint = BrandPrimary,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "ChatWait Card: ${message.associatedCard.title}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = BrandPrimary
          )
        }
      }
    }
  }
}

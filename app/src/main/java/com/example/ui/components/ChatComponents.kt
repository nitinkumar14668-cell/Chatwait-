package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
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
  var copied by remember { mutableStateOf(false) }

  val isUser = message.sender == MessageSender.USER
  val alignment = if (isUser) Alignment.End else Alignment.Start

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalAlignment = alignment
  ) {
    // Header Label: Model Badge or User tag
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

        Box(
          modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(modelColor.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = modelIcon,
            contentDescription = message.model.displayName,
            tint = modelColor,
            modifier = Modifier.size(12.dp)
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = message.model.displayName,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = modelColor
        )
      } else {
        Text(
          text = "You",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
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

    // Main Bubble Body
    Surface(
      shape = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 18.dp,
        bottomStart = if (isUser) 18.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 18.dp
      ),
      color = if (isUser) {
        BrandPrimary
      } else {
        MaterialTheme.colorScheme.surfaceVariant
      },
      tonalElevation = if (isUser) 4.dp else 2.dp,
      modifier = Modifier
        .widthIn(max = 340.dp)
        .border(
          width = 1.dp,
          color = if (isUser) BrandPrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
          shape = RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = if (isUser) 18.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 18.dp
          )
        )
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        if (message.content.isEmpty() && message.generationState == GenerationState.THINKING) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = BrandAccent,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Generating response...",
              fontSize = 13.sp,
              fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          // Check for code blocks in content
          RenderMessageContent(
            content = message.content,
            isUser = isUser
          )
        }

        // Bottom stats & copy row for AI messages
        if (!isUser && message.content.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            if (message.waitTimeSeconds != null && message.waitTimeSeconds > 0) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .background(BrandAccent.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Timer,
                  contentDescription = "Wait time",
                  tint = BrandAccent,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Wait time: ${String.format("%.1fs", message.waitTimeSeconds)}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BrandAccent
                )
              }
            } else {
              Spacer(modifier = Modifier.width(1.dp))
            }

            IconButton(
              onClick = {
                clipboardManager.setText(AnnotatedString(message.content))
                copied = true
              },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                contentDescription = "Copy text",
                tint = if (copied) ProductivityCategory else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(13.dp)
              )
            }
          }
        }
      }
    }

    // Associated Wait Card Badge (if a card was shown during response)
    if (!isUser && message.associatedCard != null) {
      Spacer(modifier = Modifier.height(4.dp))
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = BrandPrimary.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.3f)),
        modifier = Modifier
          .padding(top = 2.dp)
          .clickable { onCardClick() }
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
            fontWeight = FontWeight.SemiBold,
            color = BrandPrimary
          )
        }
      }
    }
  }
}

@Composable
fun RenderMessageContent(content: String, isUser: Boolean) {
  val clipboardManager = LocalClipboardManager.current

  // Split content by code fences ```
  val parts = content.split("```")

  if (parts.size <= 1) {
    // Normal text message
    Text(
      text = content,
      style = MaterialTheme.typography.bodyMedium,
      color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
      lineHeight = 21.sp
    )
  } else {
    // Mixed content with code snippets
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      parts.forEachIndexed { index, part ->
        if (index % 2 == 0) {
          // Regular text
          if (part.isNotBlank()) {
            Text(
              text = part.trim(),
              style = MaterialTheme.typography.bodyMedium,
              color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
              lineHeight = 21.sp
            )
          }
        } else {
          // Code block! Extract language name if present (e.g. "html\n<!DOCTYPE html>...")
          val lines = part.trim().lines()
          val firstLine = lines.firstOrNull()?.trim() ?: ""
          val hasLang = firstLine.isNotEmpty() && !firstLine.contains(" ") && lines.size > 1
          val language = if (hasLang) firstLine.uppercase() else "CODE"
          val codeBody = if (hasLang) lines.drop(1).joinToString("\n") else part.trim()

          var codeCopied by remember { mutableStateOf(false) }

          // Code Block Container
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
          ) {
            Column {
              // Code Block Header
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFF1E293B))
                  .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Code, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = language,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandSecondary
                  )
                }

                Row(
                  modifier = Modifier
                    .clickable {
                      clipboardManager.setText(AnnotatedString(codeBody))
                      codeCopied = true
                    }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = if (codeCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy code",
                    tint = if (codeCopied) ProductivityCategory else Color(0xFF94A3B8),
                    modifier = Modifier.size(11.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = if (codeCopied) "Copied!" else "Copy",
                    fontSize = 10.sp,
                    color = if (codeCopied) ProductivityCategory else Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                  )
                }
              }

              // Code Body
              Text(
                text = codeBody,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color(0xFFE2E8F0),
                lineHeight = 16.sp,
                modifier = Modifier.padding(10.dp)
              )
            }
          }
        }
      }
    }
  }
}

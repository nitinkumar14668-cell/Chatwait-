package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdCard
import com.example.ui.theme.AiToolsCategory
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.ProductivityCategory
import com.example.ui.theme.TechCategory
import com.example.ui.theme.TriviaCategory

@Composable
fun WaitCardView(
  card: AdCard,
  elapsedWaitSeconds: Double,
  isGenerating: Boolean,
  onCardClick: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var isExpanded by remember { mutableStateOf(true) }
  var selectedTriviaOptionIndex by remember { mutableStateOf<Int?>(null) }

  // Infinite pulsing animation for wait indicator
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseAlpha"
  )

  val categoryColor = when (card.category.name) {
    "AI_TOOLS" -> AiToolsCategory
    "TECH" -> TechCategory
    "PRODUCTIVITY" -> ProductivityCategory
    "TRIVIA" -> TriviaCategory
    else -> BrandAccent
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(16.dp))
      .border(
        width = 1.5.dp,
        brush = Brush.horizontalGradient(
          listOf(categoryColor.copy(alpha = 0.8f), BrandPrimary.copy(alpha = 0.5f))
        ),
        shape = RoundedCornerShape(16.dp)
      )
      .testTag("wait_card_${card.id}")
      .animateContentSize(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Header: Status Badge + Live Timer + Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Pulsing Glow Dot
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (isGenerating) BrandAccent.copy(alpha = pulseAlpha) else ProductivityCategory)
          )
          Spacer(modifier = Modifier.width(6.dp))

          // Badge
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = categoryColor.copy(alpha = 0.2f),
            modifier = Modifier.padding(end = 6.dp)
          ) {
            Text(
              text = card.badgeText.uppercase(),
              color = categoryColor,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          if (isGenerating) {
            Text(
              text = "While AI generates...",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Timer & Actions
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = "Wait Timer",
            modifier = Modifier.size(14.dp),
            tint = BrandAccent
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = String.format("%.1fs", elapsedWaitSeconds),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandAccent
          )

          IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
              .size(28.dp)
              .testTag("toggle_expand_card")
          ) {
            Icon(
              imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
              contentDescription = if (isExpanded) "Collapse" else "Expand",
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(28.dp)
              .testTag("dismiss_card")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Dismiss Card",
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Title & Headline
      Text(
        text = card.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = card.headline,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = categoryColor,
        modifier = Modifier.padding(top = 2.dp)
      )

      // Collapsible Content
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
          Text(
            text = card.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
          )

          // Interactive Trivia Quiz section if present
          if (!card.triviaOptions.isNullOrEmpty() && card.triviaAnswerIndex != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Tap to answer:",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = TriviaCategory
            )
            Spacer(modifier = Modifier.height(4.dp))

            card.triviaOptions.forEachIndexed { index, option ->
              val isSelected = selectedTriviaOptionIndex == index
              val isCorrect = index == card.triviaAnswerIndex
              val optionBgColor by animateColorAsState(
                targetValue = when {
                  selectedTriviaOptionIndex == null -> MaterialTheme.colorScheme.surface
                  isSelected && isCorrect -> ProductivityCategory.copy(alpha = 0.25f)
                  isSelected && !isCorrect -> Color(0xFFEF4444).copy(alpha = 0.25f)
                  isCorrect -> ProductivityCategory.copy(alpha = 0.2f)
                  else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                },
                label = "optionBg"
              )

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = optionBgColor,
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) categoryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 3.dp)
                  .clickable {
                    selectedTriviaOptionIndex = index
                    onCardClick()
                  }
                  .testTag("trivia_option_$index")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${('A' + index)}. $option",
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                  )
                  if (selectedTriviaOptionIndex != null) {
                    if (isCorrect) {
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Correct",
                        tint = ProductivityCategory,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }
              }
            }

            if (selectedTriviaOptionIndex != null && !card.triviaExplanation.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = card.triviaExplanation,
                fontSize = 11.sp,
                color = ProductivityCategory,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                  .background(ProductivityCategory.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                  .padding(6.dp)
              )
            }
          }

          // Call To Action Button (if actionUrl is set)
          if (card.actionUrl.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (card.sponsorName != null) {
                Text(
                  text = "by ${card.sponsorName}",
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                  modifier = Modifier.padding(end = 12.dp)
                )
              }

              Button(
                onClick = {
                  onCardClick()
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(card.actionUrl))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    // Fallback handled safely
                  }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = categoryColor),
                modifier = Modifier
                  .height(34.dp)
                  .testTag("card_cta_button")
              ) {
                Text(
                  text = card.callToActionText,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                  contentDescription = "Open Link",
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

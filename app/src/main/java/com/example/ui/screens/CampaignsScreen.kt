package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.AdCard
import com.example.model.AiPlatform
import com.example.model.CardCategory
import com.example.ui.theme.AiToolsCategory
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.ProductivityCategory
import com.example.ui.theme.TechCategory
import com.example.ui.theme.TriviaCategory
import com.example.ui.viewmodel.ChatWaitViewModel
import java.util.UUID

@Composable
fun CampaignsScreen(
  viewModel: ChatWaitViewModel,
  modifier: Modifier = Modifier
) {
  val cards by viewModel.cards.collectAsState()
  var selectedCategory by remember { mutableStateOf<CardCategory?>(null) }
  var showCreateDialog by remember { mutableStateOf(false) }

  val filteredCards = remember(cards, selectedCategory) {
    if (selectedCategory == null) cards else cards.filter { it.category == selectedCategory }
  }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showCreateDialog = true },
        containerColor = BrandPrimary,
        contentColor = Color.White,
        modifier = Modifier.testTag("add_card_fab")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Create Card")
      }
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Header
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Wait Card Campaigns",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Manage micro-content & sponsored snippets displayed during AI latency",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Categories Horizontal Filter
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          FilterChip(
            selected = selectedCategory == null,
            onClick = { selectedCategory = null },
            label = { Text("All (${cards.size})", fontSize = 12.sp) },
            modifier = Modifier.testTag("filter_all_categories")
          )
        }
        items(CardCategory.values()) { cat ->
          val count = cards.count { it.category == cat }
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { selectedCategory = cat },
            label = { Text("${cat.displayName} ($count)", fontSize = 12.sp) },
            modifier = Modifier.testTag("filter_category_${cat.name}")
          )
        }
      }

      // Cards List
      LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredCards, key = { it.id }) { card ->
          CampaignCardItem(
            card = card,
            onTest = { viewModel.triggerCardPreview(card) },
            onDelete = if (card.isCustom) {
              { viewModel.deleteCard(card.id) }
            } else null
          )
        }
      }
    }
  }

  if (showCreateDialog) {
    CreateCardDialog(
      onDismiss = { showCreateDialog = false },
      onCreate = { newCard ->
        viewModel.addCustomCard(newCard)
        showCreateDialog = false
      }
    )
  }
}

@Composable
fun CampaignCardItem(
  card: AdCard,
  onTest: () -> Unit,
  onDelete: (() -> Unit)?,
  modifier: Modifier = Modifier
) {
  val catColor = when (card.category) {
    CardCategory.AI_TOOLS -> AiToolsCategory
    CardCategory.TECH -> TechCategory
    CardCategory.PRODUCTIVITY -> ProductivityCategory
    CardCategory.TRIVIA -> TriviaCategory
    else -> BrandAccent
  }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = catColor.copy(alpha = 0.2f)
        ) {
          Text(
            text = card.badgeText.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = catColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Weight: ${card.weight}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          if (onDelete != null) {
            IconButton(
              onClick = onDelete,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Card",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = card.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )

      Text(
        text = card.headline,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = catColor,
        modifier = Modifier.padding(top = 2.dp)
      )

      Text(
        text = card.description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        modifier = Modifier.padding(top = 4.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Target: ${card.targetPlatform.displayName}",
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
          onClick = onTest,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
          modifier = Modifier.height(30.dp)
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Test in Chat", fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
fun CreateCardDialog(
  onDismiss: () -> Unit,
  onCreate: (AdCard) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var headline by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var category by remember { mutableStateOf(CardCategory.AI_TOOLS) }
  var platform by remember { mutableStateOf(AiPlatform.ALL) }
  var ctaText by remember { mutableStateOf("Explore Now") }
  var ctaUrl by remember { mutableStateOf("https://google.com") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create Wait Card", fontWeight = FontWeight.Bold) },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Card Title") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = headline,
          onValueChange = { headline = it },
          label = { Text("Catchy Headline") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Body Description") },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = ctaUrl,
          onValueChange = { ctaUrl = it },
          label = { Text("CTA Destination URL") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            val card = AdCard(
              id = "custom_" + UUID.randomUUID().toString(),
              title = title,
              headline = if (headline.isBlank()) title else headline,
              description = description,
              category = category,
              targetPlatform = platform,
              callToActionText = ctaText,
              actionUrl = ctaUrl,
              badgeText = "Custom",
              weight = 20,
              isCustom = true
            )
            onCreate(card)
          }
        },
        enabled = title.isNotBlank()
      ) {
        Text("Save Card")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

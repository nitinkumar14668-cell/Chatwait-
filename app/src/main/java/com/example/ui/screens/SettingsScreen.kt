package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiPlatform
import com.example.model.ChatWaitConfig
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.viewmodel.ChatWaitViewModel

@Composable
fun SettingsScreen(
  viewModel: ChatWaitViewModel,
  modifier: Modifier = Modifier
) {
  val config by viewModel.config.collectAsState()
  var apiKeyInput by remember(config.customGeminiApiKey) { mutableStateOf(config.customGeminiApiKey) }

  LazyColumn(
    contentPadding = PaddingValues(14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
    item {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "ChatWait Configuration",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Fine-tune card trigger timing, pacing rules, and API keys",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Master Switch
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Enable ChatWait Engine",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleSmall
            )
            Text(
              text = "Show smart micro-cards during AI generation wait intervals",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Switch(
            checked = config.isExtensionEnabled,
            onCheckedChange = { viewModel.updateConfig(config.copy(isExtensionEnabled = it)) },
            colors = SwitchDefaults.colors(checkedThumbColor = BrandPrimary),
            modifier = Modifier.testTag("master_switch")
          )
        }
      }
    }

    // Active Platforms Section
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Active AI Platforms",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall
          )
          Text(
            text = "Select which AI chats receive ChatWait cards",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))

          listOf(AiPlatform.GEMINI, AiPlatform.CHATGPT, AiPlatform.CLAUDE).forEach { platform ->
            val isChecked = config.activePlatforms.contains(platform)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Checkbox(
                checked = isChecked,
                onCheckedChange = { checked ->
                  val newSet = if (checked) {
                    config.activePlatforms + platform
                  } else {
                    config.activePlatforms - platform
                  }
                  viewModel.updateConfig(config.copy(activePlatforms = newSet))
                },
                modifier = Modifier.testTag("checkbox_${platform.name}")
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = platform.displayName, fontSize = 13.sp)
            }
          }
        }
      }
    }

    // Pacing & Frequency Cap Sliders
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Speed, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Frequency Capping & Timing",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleSmall
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Hourly Cap
          Text(
            text = "Max cards per hour: ${config.frequencyCapPerHour}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
          Slider(
            value = config.frequencyCapPerHour.toFloat(),
            onValueChange = {
              viewModel.updateConfig(config.copy(frequencyCapPerHour = it.toInt()))
            },
            valueRange = 2f..30f,
            steps = 28,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Auto collapse toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Auto-hide on generation complete", fontSize = 12.sp, fontWeight = FontWeight.Medium)
              Text(
                text = "Collapse card once AI finishes typing",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Switch(
              checked = config.autoCollapseOnFinish,
              onCheckedChange = { viewModel.updateConfig(config.copy(autoCollapseOnFinish = it)) }
            )
          }
        }
      }
    }

    // Optional API Key Config
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Key, contentDescription = null, tint = BrandAccent, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Google Gemini API Key (Optional)",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleSmall
            )
          }
          Text(
            text = "Configure custom Gemini key for direct live Google AI API streaming. If empty, the app runs on intelligent offline AI engine.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
          )

          OutlinedTextField(
            value = apiKeyInput,
            onValueChange = {
              apiKeyInput = it
              viewModel.updateConfig(config.copy(customGeminiApiKey = it.trim()))
            },
            placeholder = { Text("AIzaSy...") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("api_key_input")
          )
        }
      }
    }

    // Reset Defaults
    item {
      Button(
        onClick = {
          viewModel.updateConfig(ChatWaitConfig())
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Reset to Default Settings", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

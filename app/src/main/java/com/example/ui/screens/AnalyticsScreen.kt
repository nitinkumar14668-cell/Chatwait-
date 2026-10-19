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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiPlatform
import com.example.model.ImpressionRecord
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.ProductivityCategory
import com.example.ui.viewmodel.ChatWaitViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
  viewModel: ChatWaitViewModel,
  modifier: Modifier = Modifier
) {
  val stats by viewModel.stats.collectAsState()
  val impressions by viewModel.impressions.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Impression Analytics",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Wait-time metrics, user dwell time & engagement",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(
          onClick = { viewModel.clearAnalytics() },
          modifier = Modifier.testTag("clear_analytics_button")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Clear History",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    LazyColumn(
      contentPadding = PaddingValues(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      // 4 Metric Highlight Cards in Grid
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MetricTile(
            title = "Total Views",
            value = "${stats.totalImpressions}",
            icon = Icons.Default.Visibility,
            color = BrandPrimary,
            modifier = Modifier.weight(1f)
          )
          MetricTile(
            title = "Total Clicks",
            value = "${stats.totalClicks}",
            icon = Icons.Default.Mouse,
            color = ProductivityCategory,
            modifier = Modifier.weight(1f)
          )
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MetricTile(
            title = "Wait Time Saved",
            value = "${String.format("%.1f", stats.totalWaitTimeSeconds)}s",
            icon = Icons.Default.QueryBuilder,
            color = BrandAccent,
            modifier = Modifier.weight(1f)
          )
          MetricTile(
            title = "CTR %",
            value = "${String.format("%.1f", stats.clickThroughRate)}%",
            icon = Icons.Default.BarChart,
            color = BrandSecondary,
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Platform Breakdown
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Impressions by AI Model",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(10.dp))

            listOf(AiPlatform.GEMINI, AiPlatform.CHATGPT, AiPlatform.CLAUDE).forEach { p ->
              val count = stats.platformCounts[p] ?: 0
              val percent = if (stats.totalImpressions > 0) (count.toFloat() / stats.totalImpressions) * 100 else 0f

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = p.displayName, fontSize = 12.sp)
                Text(
                  text = "$count views (${String.format("%.0f", percent)}%)",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BrandPrimary
                )
              }
            }
          }
        }
      }

      // Recent Impressions Log
      item {
        Text(
          text = "Real-Time Impression Event Log (${impressions.size})",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.padding(top = 8.dp)
        )
      }

      if (impressions.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No impressions recorded yet. Send a prompt in the AI Chat tab to trigger wait cards!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(impressions, key = { it.id }) { record ->
          ImpressionLogItem(record = record)
        }
      }
    }
  }
}

@Composable
fun MetricTile(
  title: String,
  value: String,
  icon: ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(16.dp)
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = color
      )
    }
  }
}

@Composable
fun ImpressionLogItem(
  record: ImpressionRecord,
  modifier: Modifier = Modifier
) {
  val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
  val timeStr = dateFormat.format(Date(record.timestamp))

  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = record.cardTitle,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp
        )
        Text(
          text = "${record.platform.displayName} • ${String.format("%.1fs", record.dwellTimeSeconds)} dwell • $timeStr",
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      if (record.wasClicked) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = ProductivityCategory.copy(alpha = 0.2f)
        ) {
          Text(
            text = "CLICKED",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = ProductivityCategory,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      } else {
        Text(
          text = "Viewed",
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

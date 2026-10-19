package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPrimary
import com.example.ui.viewmodel.ChatWaitViewModel
import com.example.ui.viewmodel.HubSubTab

@Composable
fun HubScreen(
  viewModel: ChatWaitViewModel,
  modifier: Modifier = Modifier
) {
  val currentSubTab by viewModel.currentHubSubTab.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Hub Segmented Switcher
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      TabRow(
        selectedTabIndex = currentSubTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = BrandPrimary,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[currentSubTab.ordinal]),
            color = BrandPrimary,
            height = 3.dp
          )
        }
      ) {
        HubSubTab.values().forEach { tab ->
          val isSelected = tab == currentSubTab
          val icon = when (tab) {
            HubSubTab.ANALYTICS -> Icons.Default.BarChart
            HubSubTab.BUG_FIXES -> Icons.Default.BugReport
            HubSubTab.SETTINGS -> Icons.Default.Settings
          }

          Tab(
            selected = isSelected,
            onClick = { viewModel.setHubSubTab(tab) },
            icon = {
              Icon(
                imageVector = icon,
                contentDescription = tab.title,
                tint = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
              )
            },
            text = {
              Text(
                text = tab.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          )
        }
      }
    }

    Box(modifier = Modifier.weight(1f)) {
      when (currentSubTab) {
        HubSubTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
        HubSubTab.BUG_FIXES -> AnalysisBugFixScreen()
        HubSubTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
      }
    }
  }
}

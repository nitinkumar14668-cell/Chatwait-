package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AnalysisBugFixScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CampaignsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WebCompanionScreen
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.ChatWaitViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: ChatWaitViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainScreen(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun MainScreen(viewModel: ChatWaitViewModel) {
  val currentTab by viewModel.currentTab.collectAsState()

  // BackHandler: If user is on any secondary screen, navigating back returns to Chat
  BackHandler(enabled = currentTab != AppTab.CHAT) {
    viewModel.setTab(AppTab.CHAT)
  }

  Scaffold(
    bottomBar = {
      NavigationBar(
        modifier = Modifier.testTag("bottom_navigation_bar")
      ) {
        NavigationBarItem(
          selected = currentTab == AppTab.CHAT,
          onClick = { viewModel.setTab(AppTab.CHAT) },
          icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "AI Chat") },
          label = { Text("Chat", fontSize = 10.sp) },
          colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandPrimary),
          modifier = Modifier.testTag("nav_chat")
        )

        NavigationBarItem(
          selected = currentTab == AppTab.WEB_COMPANION,
          onClick = { viewModel.setTab(AppTab.WEB_COMPANION) },
          icon = { Icon(Icons.Default.OpenInBrowser, contentDescription = "Web AI") },
          label = { Text("Web AI", fontSize = 10.sp) },
          colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandPrimary),
          modifier = Modifier.testTag("nav_web")
        )

        NavigationBarItem(
          selected = currentTab == AppTab.CAMPAIGNS,
          onClick = { viewModel.setTab(AppTab.CAMPAIGNS) },
          icon = { Icon(Icons.Default.ViewAgenda, contentDescription = "Cards") },
          label = { Text("Cards", fontSize = 10.sp) },
          colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandPrimary),
          modifier = Modifier.testTag("nav_campaigns")
        )

        NavigationBarItem(
          selected = currentTab == AppTab.ANALYTICS,
          onClick = { viewModel.setTab(AppTab.ANALYTICS) },
          icon = { Icon(Icons.Default.BarChart, contentDescription = "Analytics") },
          label = { Text("Stats", fontSize = 10.sp) },
          colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandPrimary),
          modifier = Modifier.testTag("nav_analytics")
        )

        NavigationBarItem(
          selected = currentTab == AppTab.ANALYSIS_BUGS,
          onClick = { viewModel.setTab(AppTab.ANALYSIS_BUGS) },
          icon = { Icon(Icons.Default.BugReport, contentDescription = "Bug Fixes") },
          label = { Text("Fixes", fontSize = 10.sp) },
          colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandPrimary),
          modifier = Modifier.testTag("nav_bugs")
        )

        NavigationBarItem(
          selected = currentTab == AppTab.SETTINGS,
          onClick = { viewModel.setTab(AppTab.SETTINGS) },
          icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
          label = { Text("Config", fontSize = 10.sp) },
          colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandPrimary),
          modifier = Modifier.testTag("nav_settings")
        )
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .widthIn(max = 600.dp),
      contentAlignment = Alignment.Center
    ) {
      when (currentTab) {
        AppTab.CHAT -> ChatScreen(viewModel = viewModel)
        AppTab.WEB_COMPANION -> WebCompanionScreen(viewModel = viewModel)
        AppTab.CAMPAIGNS -> CampaignsScreen(viewModel = viewModel)
        AppTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
        AppTab.ANALYSIS_BUGS -> AnalysisBugFixScreen()
        AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
      }
    }
  }
}

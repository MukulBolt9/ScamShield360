package com.example.ui

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lan
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ScamAcademyScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.ThreatGraphScreen
import com.example.ui.screens.ThreatVaultScreen
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DevModeAmber
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScamShieldApp(
  viewModel: ScamShieldViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val colors = MaterialTheme.colorScheme
  val isDark = colors.background.red < 0.5f

  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val inputText by viewModel.inputText.collectAsStateWithLifecycle()
  val inputUrl by viewModel.inputUrl.collectAsStateWithLifecycle()
  val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
  val analysisProgress by viewModel.analysisProgress.collectAsStateWithLifecycle()
  val currentResult by viewModel.currentResult.collectAsStateWithLifecycle()
  val savedReports by viewModel.savedReports.collectAsStateWithLifecycle()

  val isDevMode by viewModel.isDevMode.collectAsStateWithLifecycle()
  val isBackgroundGuardian by viewModel.isBackgroundGuardian.collectAsStateWithLifecycle()
  val isLiveChipActive by viewModel.isLiveChipActive.collectAsStateWithLifecycle()
  val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsStateWithLifecycle()
  val isNotificationBannerDismissed by viewModel.isNotificationBannerDismissed.collectAsStateWithLifecycle()
  val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()
  val isAutomationMode by viewModel.isAutomationMode.collectAsStateWithLifecycle()
  val isBankingPrivacyActive by viewModel.isBankingPrivacyActive.collectAsStateWithLifecycle()
  val latestAutoScanResult by viewModel.latestAutoScanResult.collectAsStateWithLifecycle()
  val isAccessibilityEnabled by viewModel.isAccessibilityEnabled.collectAsStateWithLifecycle()
  val isAccessibilityBannerDismissed by viewModel.isAccessibilityBannerDismissed.collectAsStateWithLifecycle()

  val defangInput by viewModel.defangInput.collectAsStateWithLifecycle()
  val defangOutput by viewModel.defangOutput.collectAsStateWithLifecycle()
  val typosquatInput by viewModel.typosquatInput.collectAsStateWithLifecycle()
  val typosquatResult by viewModel.typosquatResult.collectAsStateWithLifecycle()
  val userQuizAnswers by viewModel.userQuizAnswers.collectAsStateWithLifecycle()

  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    viewModel.onNotificationPermissionResult(isGranted, context)
  }

  if (selectedTab != 0) {
    BackHandler {
      viewModel.selectTab(0)
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = colors.background,
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "ScamShield",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = colors.onBackground
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "360",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = AccentBlue
            )
          }
        },
        actions = {
          // Light / Dark Theme Toggle Button
          IconButton(
            onClick = { viewModel.toggleTheme() },
            modifier = Modifier.testTag("theme_toggle_button")
          ) {
            Icon(
              imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = if (isDark) "Switch to Light Theme" else "Switch to Dark Theme",
              tint = colors.onSurface
            )
          }

          // Assistant Quick Scan Screen Button (Pops up over ANY app!)
          IconButton(
            onClick = { viewModel.launchAssistantOverlayOverAnyApp(context) },
            modifier = Modifier.testTag("appbar_quick_scan_button")
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Quick Screen Scan Over Any App",
              tint = AccentBlue
            )
          }

          // Dev Mode / Normal Mode Toggle Pill
          Box(
            modifier = Modifier
              .padding(end = 12.dp)
              .clip(RoundedCornerShape(8.dp))
              .border(
                1.dp,
                if (isDevMode) DevModeAmber else colors.outline,
                RoundedCornerShape(8.dp)
              )
              .background(if (isDevMode) DevModeAmber.copy(alpha = 0.15f) else colors.surface)
              .clickable { viewModel.toggleDevMode() }
              .padding(horizontal = 8.dp, vertical = 5.dp)
              .testTag("dev_mode_toggle")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = if (isDevMode) DevModeAmber else colors.onSurfaceVariant,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isDevMode) "Dev Mode" else "Normal",
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDevMode) DevModeAmber else colors.onSurfaceVariant
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = colors.background,
          titleContentColor = colors.onBackground
        )
      )
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars)
          .testTag("bottom_nav_bar"),
        containerColor = colors.surface,
        contentColor = colors.onSurface,
        tonalElevation = 8.dp
      ) {
        NavigationBarItem(
          selected = selectedTab == 0,
          onClick = { viewModel.selectTab(0) },
          icon = {
            Icon(
              imageVector = if (selectedTab == 0) Icons.Filled.Security else Icons.Outlined.Security,
              contentDescription = "Scanner"
            )
          },
          label = {
            Text("Scanner", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Medium)
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = colors.surface,
            selectedTextColor = AccentBlue,
            indicatorColor = AccentBlue,
            unselectedIconColor = colors.onSurfaceVariant,
            unselectedTextColor = colors.onSurfaceVariant
          )
        )

        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { viewModel.selectTab(1) },
          icon = {
            Icon(
              imageVector = if (selectedTab == 1) Icons.Filled.Lan else Icons.Outlined.Lan,
              contentDescription = "Inspection"
            )
          },
          label = {
            Text("Inspection", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Medium)
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = colors.surface,
            selectedTextColor = AccentBlue,
            indicatorColor = AccentBlue,
            unselectedIconColor = colors.onSurfaceVariant,
            unselectedTextColor = colors.onSurfaceVariant
          )
        )

        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { viewModel.selectTab(2) },
          icon = {
            Icon(
              imageVector = if (selectedTab == 2) Icons.Filled.School else Icons.Outlined.School,
              contentDescription = "Academy"
            )
          },
          label = {
            Text("Academy", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Medium)
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = colors.surface,
            selectedTextColor = AccentBlue,
            indicatorColor = AccentBlue,
            unselectedIconColor = colors.onSurfaceVariant,
            unselectedTextColor = colors.onSurfaceVariant
          )
        )

        NavigationBarItem(
          selected = selectedTab == 3,
          onClick = { viewModel.selectTab(3) },
          icon = {
            Icon(
              imageVector = if (selectedTab == 3) Icons.Filled.History else Icons.Outlined.History,
              contentDescription = "Vault"
            )
          },
          label = {
            Text("Vault", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Medium)
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = colors.surface,
            selectedTextColor = AccentBlue,
            indicatorColor = AccentBlue,
            unselectedIconColor = colors.onSurfaceVariant,
            unselectedTextColor = colors.onSurfaceVariant
          )
        )
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(colors.background),
      contentAlignment = Alignment.TopCenter
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 700.dp)
      ) {
        when (selectedTab) {
          0 -> ScannerScreen(
            inputText = inputText,
            inputUrl = inputUrl,
            isAnalyzing = isAnalyzing,
            analysisProgress = analysisProgress,
            currentResult = currentResult,
            presets = viewModel.presets,
            isDevMode = isDevMode,
            isPrivacyMode = isPrivacyMode,
            isAutomationMode = isAutomationMode,
            isBankingPrivacyActive = isBankingPrivacyActive,
            isBackgroundGuardian = isBackgroundGuardian,
            isLiveChipActive = isLiveChipActive,
            hasNotificationPermission = hasNotificationPermission,
            isNotificationBannerDismissed = isNotificationBannerDismissed,
            isAccessibilityEnabled = isAccessibilityEnabled,
            isAccessibilityBannerDismissed = isAccessibilityBannerDismissed,
            latestAutoScanResult = latestAutoScanResult,
            onRequestNotificationPermission = {
              if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
              } else {
                viewModel.onNotificationPermissionResult(true, context)
              }
            },
            onDismissNotificationBanner = { viewModel.dismissNotificationBanner() },
            onDismissAccessibilityBanner = { viewModel.dismissAccessibilityBanner() },
            onInputTextChanged = viewModel::onInputTextChanged,
            onInputUrlChanged = viewModel::onInputUrlChanged,
            onClearInputs = viewModel::clearInputs,
            onLoadPreset = viewModel::loadPreset,
            onAnalyze = viewModel::analyzeCurrentPayload,
            onOpenAssistantOverlay = { viewModel.launchAssistantOverlayOverAnyApp(context) },
            onTogglePrivacyMode = viewModel::togglePrivacyMode,
            onToggleAutomationMode = viewModel::toggleAutomationMode,
            onToggleBackgroundGuardian = { viewModel.toggleBackgroundGuardian(context) },
            onOpenPromotionSettings = { viewModel.openPromotionSettings(context) },
            contentPadding = paddingValues
          )

          1 -> ThreatGraphScreen(
            currentResult = currentResult,
            defangInput = defangInput,
            defangOutput = defangOutput,
            typosquatInput = typosquatInput,
            typosquatResult = typosquatResult,
            isDevMode = isDevMode,
            onDefangInputChanged = viewModel::updateDefangInput,
            onDefangText = viewModel::defangText,
            onRefangText = viewModel::refangText,
            onTyposquatInputChanged = viewModel::updateTyposquatInput,
            contentPadding = paddingValues
          )

          2 -> ScamAcademyScreen(
            questions = viewModel.academyQuiz,
            userAnswers = userQuizAnswers,
            onAnswerSelected = viewModel::answerQuiz,
            contentPadding = paddingValues
          )

          3 -> ThreatVaultScreen(
            reports = savedReports,
            radarAlerts = viewModel.radarAlerts,
            onSelectReport = viewModel::selectReport,
            onDeleteReport = viewModel::deleteReport,
            onClearAll = viewModel::clearAllReports,
            contentPadding = paddingValues
          )
        }
      }
    }
  }
}

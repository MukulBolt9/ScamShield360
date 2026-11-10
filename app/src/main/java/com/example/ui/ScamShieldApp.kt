package com.example.ui

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import com.example.data.model.ThreatAnalysisResult
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
  val isSamsungNowbarEnabled by viewModel.isSamsungNowbarEnabled.collectAsStateWithLifecycle()
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

  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWideScreen = maxWidth >= 720.dp

    if (isWideScreen) {
      // Wide Screen (Tablet / Foldable Unfolded / Landscape) Layout with Side NavigationRail
      Row(
        modifier = Modifier
          .fillMaxSize()
          .background(colors.background)
      ) {
        NavigationRail(
          modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("wide_nav_rail"),
          containerColor = colors.surface,
          contentColor = colors.onSurface,
          header = {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(AccentBlue.copy(alpha = 0.15f))
                  .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.Security,
                  contentDescription = "ScamShield 360",
                  tint = AccentBlue,
                  modifier = Modifier.size(26.dp)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "ScamShield",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = colors.onSurface
              )
            }
          }
        ) {
          Spacer(modifier = Modifier.height(8.dp))

          NavigationRailItem(
            selected = selectedTab == 0,
            onClick = { viewModel.selectTab(0) },
            icon = {
              Icon(
                imageVector = if (selectedTab == 0) Icons.Filled.Security else Icons.Outlined.Security,
                contentDescription = "Scanner",
                modifier = Modifier.size(22.dp)
              )
            },
            label = {
              Text("Scanner", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            },
            colors = NavigationRailItemDefaults.colors(
              selectedIconColor = colors.surface,
              selectedTextColor = AccentBlue,
              indicatorColor = AccentBlue,
              unselectedIconColor = colors.onSurfaceVariant,
              unselectedTextColor = colors.onSurfaceVariant
            )
          )

          NavigationRailItem(
            selected = selectedTab == 1,
            onClick = { viewModel.selectTab(1) },
            icon = {
              Icon(
                imageVector = if (selectedTab == 1) Icons.Filled.Lan else Icons.Outlined.Lan,
                contentDescription = "Inspection",
                modifier = Modifier.size(22.dp)
              )
            },
            label = {
              Text("Inspection", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            },
            colors = NavigationRailItemDefaults.colors(
              selectedIconColor = colors.surface,
              selectedTextColor = AccentBlue,
              indicatorColor = AccentBlue,
              unselectedIconColor = colors.onSurfaceVariant,
              unselectedTextColor = colors.onSurfaceVariant
            )
          )

          NavigationRailItem(
            selected = selectedTab == 2,
            onClick = { viewModel.selectTab(2) },
            icon = {
              Icon(
                imageVector = if (selectedTab == 2) Icons.Filled.School else Icons.Outlined.School,
                contentDescription = "Academy",
                modifier = Modifier.size(22.dp)
              )
            },
            label = {
              Text("Academy", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            },
            colors = NavigationRailItemDefaults.colors(
              selectedIconColor = colors.surface,
              selectedTextColor = AccentBlue,
              indicatorColor = AccentBlue,
              unselectedIconColor = colors.onSurfaceVariant,
              unselectedTextColor = colors.onSurfaceVariant
            )
          )

          NavigationRailItem(
            selected = selectedTab == 3,
            onClick = { viewModel.selectTab(3) },
            icon = {
              Icon(
                imageVector = if (selectedTab == 3) Icons.Filled.History else Icons.Outlined.History,
                contentDescription = "Vault",
                modifier = Modifier.size(22.dp)
              )
            },
            label = {
              Text("Vault", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            },
            colors = NavigationRailItemDefaults.colors(
              selectedIconColor = colors.surface,
              selectedTextColor = AccentBlue,
              indicatorColor = AccentBlue,
              unselectedIconColor = colors.onSurfaceVariant,
              unselectedTextColor = colors.onSurfaceVariant
            )
          )

          Spacer(modifier = Modifier.weight(1f))

          // Theme toggle at bottom of rail
          IconButton(
            onClick = { viewModel.toggleTheme() },
            modifier = Modifier
              .padding(bottom = 16.dp)
              .size(48.dp)
          ) {
            Icon(
              imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = "Toggle Theme",
              tint = colors.onSurfaceVariant
            )
          }
        }

        // Screen content beside the rail
        Scaffold(
          modifier = Modifier.weight(1f),
          containerColor = colors.background,
          topBar = {
            TopAppBar(
              title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = when (selectedTab) {
                      0 -> "ScamShield Threat Scanner"
                      1 -> "Deep Threat Inspection"
                      2 -> "Scam Defense Academy"
                      3 -> "Encrypted Threat Vault"
                      else -> "ScamShield 360"
                    },
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.onBackground
                  )
                }
              },
              actions = {
                IconButton(
                  onClick = { viewModel.launchAssistantOverlayOverAnyApp(context) },
                  modifier = Modifier
                    .size(48.dp)
                    .testTag("appbar_quick_scan_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Quick Screen Scan Over Any App",
                    tint = AccentBlue,
                    modifier = Modifier.size(22.dp)
                  )
                }

                Box(
                  modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                      1.dp,
                      if (isDevMode) DevModeAmber else colors.outline,
                      RoundedCornerShape(8.dp)
                    )
                    .background(if (isDevMode) DevModeAmber.copy(alpha = 0.15f) else colors.surface)
                    .clickable { viewModel.toggleDevMode() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("dev_mode_toggle")
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Build,
                      contentDescription = null,
                      tint = if (isDevMode) DevModeAmber else colors.onSurfaceVariant,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (isDevMode) "Dev Mode" else "Normal Mode",
                      fontFamily = JetBrainsMono,
                      fontSize = 11.sp,
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
                .widthIn(max = 760.dp)
            ) {
              AppTabContent(
                selectedTab = selectedTab,
                viewModel = viewModel,
                context = context,
                paddingValues = paddingValues,
                inputText = inputText,
                inputUrl = inputUrl,
                isAnalyzing = isAnalyzing,
                analysisProgress = analysisProgress,
                currentResult = currentResult,
                savedReports = savedReports,
                isDevMode = isDevMode,
                isPrivacyMode = isPrivacyMode,
                isAutomationMode = isAutomationMode,
                isBankingPrivacyActive = isBankingPrivacyActive,
                isBackgroundGuardian = isBackgroundGuardian,
                isLiveChipActive = isLiveChipActive,
                isSamsungNowbarEnabled = isSamsungNowbarEnabled,
                hasNotificationPermission = hasNotificationPermission,
                isNotificationBannerDismissed = isNotificationBannerDismissed,
                isAccessibilityEnabled = isAccessibilityEnabled,
                isAccessibilityBannerDismissed = isAccessibilityBannerDismissed,
                latestAutoScanResult = latestAutoScanResult,
                defangInput = defangInput,
                defangOutput = defangOutput,
                typosquatInput = typosquatInput,
                typosquatResult = typosquatResult,
                userQuizAnswers = userQuizAnswers,
                notificationPermissionLauncher = notificationPermissionLauncher
              )
            }
          }
        }
      }
    } else {
      // Standard Phone Layout with Responsive TopBar and Bottom NavigationBar
      Scaffold(
        modifier = Modifier.fillMaxSize(),
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
              // Light / Dark Theme Toggle Button with 48dp touch target
              IconButton(
                onClick = { viewModel.toggleTheme() },
                modifier = Modifier
                  .size(48.dp)
                  .testTag("theme_toggle_button")
              ) {
                Icon(
                  imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                  contentDescription = if (isDark) "Switch to Light Theme" else "Switch to Dark Theme",
                  tint = colors.onSurface,
                  modifier = Modifier.size(20.dp)
                )
              }

              // Assistant Quick Scan Screen Button (Overlay over any app)
              IconButton(
                onClick = { viewModel.launchAssistantOverlayOverAnyApp(context) },
                modifier = Modifier
                  .size(48.dp)
                  .testTag("appbar_quick_scan_button")
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = "Quick Screen Scan Over Any App",
                  tint = AccentBlue,
                  modifier = Modifier.size(20.dp)
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
                  .padding(horizontal = 8.dp, vertical = 6.dp)
                  .testTag("dev_mode_toggle")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = if (isDevMode) DevModeAmber else colors.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (isDevMode) "Dev" else "Normal",
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
                  contentDescription = "Scanner",
                  modifier = Modifier.size(22.dp)
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
                  contentDescription = "Inspection",
                  modifier = Modifier.size(22.dp)
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
                  contentDescription = "Academy",
                  modifier = Modifier.size(22.dp)
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
                  contentDescription = "Vault",
                  modifier = Modifier.size(22.dp)
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
            AppTabContent(
              selectedTab = selectedTab,
              viewModel = viewModel,
              context = context,
              paddingValues = paddingValues,
              inputText = inputText,
              inputUrl = inputUrl,
              isAnalyzing = isAnalyzing,
              analysisProgress = analysisProgress,
              currentResult = currentResult,
              savedReports = savedReports,
              isDevMode = isDevMode,
              isPrivacyMode = isPrivacyMode,
              isAutomationMode = isAutomationMode,
              isBankingPrivacyActive = isBankingPrivacyActive,
              isBackgroundGuardian = isBackgroundGuardian,
              isLiveChipActive = isLiveChipActive,
              isSamsungNowbarEnabled = isSamsungNowbarEnabled,
              hasNotificationPermission = hasNotificationPermission,
              isNotificationBannerDismissed = isNotificationBannerDismissed,
              isAccessibilityEnabled = isAccessibilityEnabled,
              isAccessibilityBannerDismissed = isAccessibilityBannerDismissed,
              latestAutoScanResult = latestAutoScanResult,
              defangInput = defangInput,
              defangOutput = defangOutput,
              typosquatInput = typosquatInput,
              typosquatResult = typosquatResult,
              userQuizAnswers = userQuizAnswers,
              notificationPermissionLauncher = notificationPermissionLauncher
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AppTabContent(
  selectedTab: Int,
  viewModel: ScamShieldViewModel,
  context: android.content.Context,
  paddingValues: PaddingValues,
  inputText: String,
  inputUrl: String,
  isAnalyzing: Boolean,
  analysisProgress: String,
  currentResult: ThreatAnalysisResult?,
  savedReports: List<ThreatAnalysisResult>,
  isDevMode: Boolean,
  isPrivacyMode: Boolean,
  isAutomationMode: Boolean,
  isBankingPrivacyActive: Boolean,
  isBackgroundGuardian: Boolean,
  isLiveChipActive: Boolean,
  isSamsungNowbarEnabled: Boolean,
  hasNotificationPermission: Boolean,
  isNotificationBannerDismissed: Boolean,
  isAccessibilityEnabled: Boolean,
  isAccessibilityBannerDismissed: Boolean,
  latestAutoScanResult: ThreatAnalysisResult?,
  defangInput: String,
  defangOutput: String,
  typosquatInput: String,
  typosquatResult: com.example.data.analyzer.DomainAnalysis?,
  userQuizAnswers: Map<Int, Int>,
  notificationPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
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
      isSamsungNowbarEnabled = isSamsungNowbarEnabled,
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
      onToggleSamsungNowbar = { viewModel.toggleSamsungNowbar(context) },
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

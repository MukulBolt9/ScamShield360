package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScamPreset
import com.example.data.model.ThreatAnalysisResult
import com.example.data.model.ThreatLevel
import com.example.ui.components.AiFindingsCard
import com.example.ui.components.AttackSimulationCard
import com.example.ui.components.InfrastructureGraphView
import com.example.ui.components.NotificationPermissionCard
import com.example.ui.components.OverlayPermissionCard
import com.example.ui.components.ProtectionSettingsSheet
import com.example.ui.components.ScamLessonCard
import com.example.ui.components.ThreatScoreGauge
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DevModeAmber
import com.example.ui.theme.DevModeCyan
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SamsungNowbarPurple
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSuspicious
import com.example.ui.components.EvidenceMatrixCard
import com.example.ui.components.IdentityConsistencyCard
import com.example.ui.components.MaxCheckTelemetryCard
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
  inputText: String,
  inputUrl: String,
  isAnalyzing: Boolean,
  analysisProgress: String,
  currentResult: ThreatAnalysisResult?,
  presets: List<ScamPreset>,
  isDevMode: Boolean,
  isPrivacyMode: Boolean,
  isAutomationMode: Boolean = true,
  isBankingPrivacyActive: Boolean = false,
  isBackgroundGuardian: Boolean,
  isLiveChipActive: Boolean = true,
  isSamsungNowbarEnabled: Boolean = true,
  hasNotificationPermission: Boolean,
  isNotificationBannerDismissed: Boolean,
  isAccessibilityEnabled: Boolean,
  isAccessibilityBannerDismissed: Boolean,
  latestAutoScanResult: ThreatAnalysisResult? = null,
  onRequestNotificationPermission: () -> Unit,
  onDismissNotificationBanner: () -> Unit,
  onDismissAccessibilityBanner: () -> Unit,
  onInputTextChanged: (String) -> Unit,
  onInputUrlChanged: (String) -> Unit,
  onClearInputs: () -> Unit,
  onLoadPreset: (ScamPreset) -> Unit,
  onAnalyze: () -> Unit,
  onOpenAssistantOverlay: () -> Unit,
  onTogglePrivacyMode: () -> Unit,
  onToggleAutomationMode: () -> Unit = {},
  onToggleBackgroundGuardian: () -> Unit,
  onToggleSamsungNowbar: () -> Unit = {},
  onOpenPromotionSettings: () -> Unit = {},
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val colors = MaterialTheme.colorScheme

  var showProtectionSettingsSheet by remember { mutableStateOf(false) }
  var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

  if (showProtectionSettingsSheet) {
    ProtectionSettingsSheet(
      onDismiss = { showProtectionSettingsSheet = false },
      isBackgroundGuardian = isBackgroundGuardian,
      isSamsungNowbarEnabled = isSamsungNowbarEnabled,
      isPrivacyMode = isPrivacyMode,
      isAutomationMode = isAutomationMode,
      isBankingPrivacyShield = true,
      isBankingAppCurrentlyActive = isBankingPrivacyActive,
      detectedBankingAppName = if (isBankingPrivacyActive) "Banking Platform / App" else null,
      onToggleBackgroundGuardian = onToggleBackgroundGuardian,
      onToggleSamsungNowbar = onToggleSamsungNowbar,
      onOpenPromotionSettings = onOpenPromotionSettings,
      onTogglePrivacyMode = onTogglePrivacyMode,
      onToggleAutomationMode = onToggleAutomationMode,
      onToggleBankingPrivacyShield = {}
    )
  }

  if (showPrivacyPolicyDialog) {
    val scrollState = rememberScrollState()
    AlertDialog(
      onDismissRequest = { showPrivacyPolicyDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "ScamShield 360 Privacy Policy",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = colors.onSurface
          )
        }
      },
      text = {
        Column(
          modifier = Modifier
            .verticalScroll(scrollState)
            .padding(vertical = 4.dp)
        ) {
          Text(
            text = "Last Updated: November 2026\n",
            fontFamily = JetBrainsMono,
            fontSize = 10.sp,
            color = colors.onSurfaceVariant
          )

          Text(
            text = "1. Financial Privacy Guard (Banking Protection)\nScamShield 360 automatically detects verified financial, UPI, and banking apps. When active, all screen reading and text processing is immediately paused to ensure user balances, credentials, PINs, and personal financial transactions are NEVER captured or processed.\n",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurface,
            lineHeight = 16.sp
          )

          Text(
            text = "2. Zero-PII Redaction Engine\nBefore querying threat intelligence APIs (VirusTotal, PhishTank, urlscan), phone numbers, email addresses, names, and session tokens embedded within extracted text are automatically redacted locally on your device.\n",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurface,
            lineHeight = 16.sp
          )

          Text(
            text = "3. Accessibility Service Usage\nThe optional ScamShield Accessibility Service reads on-screen text solely to extract web links (URLs) for proactive phishing analysis. It never logs keystrokes, personal messages, or user passwords.\n",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurface,
            lineHeight = 16.sp
          )

          Text(
            text = "4. Data Storage & Local Encrypted Vault\nSecurity reports and threat scores are stored locally in an encrypted database on your device. You can clear or delete your audit history at any time from the Vault screen.\n",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurface,
            lineHeight = 16.sp
          )

          Text(
            text = "5. Contact Information\nFor privacy inquiries or questions, contact: mukulbolt9@gmail.com",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = AccentBlue,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showPrivacyPolicyDialog = false
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/privacypolicyforscamshield360/home"))
            context.startActivity(intent)
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = colors.surface)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Open Web Policy", fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
          }
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = { showPrivacyPolicyDialog = false }
        ) {
          Text("Close", fontFamily = SpaceGrotesk, fontSize = 12.sp)
        }
      },
      containerColor = colors.surface,
      shape = RoundedCornerShape(16.dp)
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(contentPadding)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Clean Minimal Header & Protection Bar
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(colors.surface)
          .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
          .padding(18.dp)
      ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
          val isCompactWidth = maxWidth < 420.dp

          if (isCompactWidth) {
            // Stacked clean layout for compact mobile screens
            Column(modifier = Modifier.fillMaxWidth()) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
              ) {
                Image(
                  painter = painterResource(id = R.drawable.ic_scamshield_minimal_logo),
                  contentDescription = "ScamShield Minimal Logo",
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(5.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isBankingPrivacyActive) StatusSafe else AccentBlue)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (isBankingPrivacyActive) "PRIVACY SHIELD PAUSED" else "PROTECTION ACTIVE",
                      fontFamily = JetBrainsMono,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isBankingPrivacyActive) StatusSafe else AccentBlue
                    )
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "ScamShield 360",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.onSurface
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedButton(
                  onClick = onOpenAssistantOverlay,
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("quick_screen_scan_button"),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Screen Scan",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                }

                IconButton(
                  onClick = { showProtectionSettingsSheet = true },
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceVariant)
                    .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
                    .testTag("protection_settings_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Protection Settings",
                    tint = colors.onSurface,
                    modifier = Modifier.size(20.dp)
                  )
                }

                IconButton(
                  onClick = { showPrivacyPolicyDialog = true },
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceVariant)
                    .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
                    .testTag("privacy_policy_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Privacy Policy",
                    tint = AccentBlue,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          } else {
            // Horizontal layout for wider mobile/tablet displays
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Image(
                  painter = painterResource(id = R.drawable.ic_scamshield_minimal_logo),
                  contentDescription = "ScamShield Minimal Logo",
                  modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(5.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isBankingPrivacyActive) StatusSafe else AccentBlue)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (isBankingPrivacyActive) "PRIVACY SHIELD PAUSED" else "PROTECTION ACTIVE",
                      fontFamily = JetBrainsMono,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isBankingPrivacyActive) StatusSafe else AccentBlue
                    )
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "ScamShield 360",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = colors.onSurface
                  )
                }
              }

              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedButton(
                  onClick = onOpenAssistantOverlay,
                  modifier = Modifier
                    .height(44.dp)
                    .testTag("quick_screen_scan_button"),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface),
                  contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Screen Scan",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                }

                IconButton(
                  onClick = { showProtectionSettingsSheet = true },
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceVariant)
                    .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
                    .testTag("protection_settings_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Protection Settings",
                    tint = colors.onSurface,
                    modifier = Modifier.size(20.dp)
                  )
                }

                IconButton(
                  onClick = { showPrivacyPolicyDialog = true },
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceVariant)
                    .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
                    .testTag("privacy_policy_button_wide")
                ) {
                  Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Privacy Policy",
                    tint = AccentBlue,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          }
        }

        if (isBankingPrivacyActive) {
          Spacer(modifier = Modifier.height(14.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(StatusSafe.copy(alpha = 0.15f))
              .border(1.dp, StatusSafe.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = StatusSafe,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "FINANCIAL PRIVACY GUARD ACTIVE",
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = StatusSafe
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Auto-scanning safely paused inside verified banking/UPI app or website.",
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                color = colors.onSurface
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Adaptive Protection summary indicator pills with dedicated icons and status
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
          val isCompact = maxWidth < 400.dp

          if (isCompact) {
            // 2x2 grid on compact devices so labels and icons have generous breathing room
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ProtectionPill(
                  title = "Background",
                  statusLabel = if (isBackgroundGuardian) "ON" else "OFF",
                  icon = Icons.Default.NotificationsActive,
                  active = isBackgroundGuardian,
                  activeColor = AccentBlue,
                  onClick = { showProtectionSettingsSheet = true },
                  modifier = Modifier.weight(1f)
                )
                ProtectionPill(
                  title = "Live Chip",
                  statusLabel = if (isLiveChipActive) "ACTIVE" else "OFF",
                  icon = Icons.Default.Smartphone,
                  active = isLiveChipActive,
                  activeColor = AccentBlue,
                  onClick = { showProtectionSettingsSheet = true },
                  modifier = Modifier.weight(1f)
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ProtectionPill(
                  title = "Auto Guard",
                  statusLabel = if (isAutomationMode) "READY" else "OFF",
                  icon = Icons.Default.AutoAwesome,
                  active = isAutomationMode,
                  activeColor = DevModeCyan,
                  onClick = { showProtectionSettingsSheet = true },
                  modifier = Modifier.weight(1f)
                )
                ProtectionPill(
                  title = "Zero PII",
                  statusLabel = if (isPrivacyMode) "SECURE" else "OFF",
                  icon = Icons.Default.Lock,
                  active = isPrivacyMode,
                  activeColor = StatusSafe,
                  onClick = { showProtectionSettingsSheet = true },
                  modifier = Modifier.weight(1f)
                )
              }
            }
          } else {
            // 1 row of 4 on standard screens
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              ProtectionPill(
                title = "Background",
                statusLabel = if (isBackgroundGuardian) "ON" else "OFF",
                icon = Icons.Default.NotificationsActive,
                active = isBackgroundGuardian,
                activeColor = AccentBlue,
                onClick = { showProtectionSettingsSheet = true },
                modifier = Modifier.weight(1f)
              )
              ProtectionPill(
                title = "Live Chip",
                statusLabel = if (isLiveChipActive) "ACTIVE" else "OFF",
                icon = Icons.Default.Smartphone,
                active = isLiveChipActive,
                activeColor = AccentBlue,
                onClick = { showProtectionSettingsSheet = true },
                modifier = Modifier.weight(1f)
              )
              ProtectionPill(
                title = "Auto Guard",
                statusLabel = if (isAutomationMode) "READY" else "OFF",
                icon = Icons.Default.AutoAwesome,
                active = isAutomationMode,
                activeColor = DevModeCyan,
                onClick = { showProtectionSettingsSheet = true },
                modifier = Modifier.weight(1f)
              )
              ProtectionPill(
                title = "Zero PII",
                statusLabel = if (isPrivacyMode) "SECURE" else "OFF",
                icon = Icons.Default.Lock,
                active = isPrivacyMode,
                activeColor = StatusSafe,
                onClick = { showProtectionSettingsSheet = true },
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }

    // Proactive Auto-Guard Alert Banner (if link intercepted on screen)
    if (latestAutoScanResult != null) {
      item {
        val alertResult = latestAutoScanResult
        val alertBorder = if (alertResult.threatLevel == ThreatLevel.DANGER) StatusDanger else StatusSuspicious
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(alertBorder.copy(alpha = 0.1f))
            .border(1.5.dp, alertBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = alertBorder,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "PROACTIVE SCREEN AUTO-GUARD ALERT",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = alertBorder
              )
            }
            Text(
              text = "${alertResult.overallScore}/100",
              fontFamily = JetBrainsMono,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = alertBorder
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = alertResult.inputUrl,
            fontFamily = JetBrainsMono,
            fontSize = 11.sp,
            color = colors.onSurface,
            maxLines = 1
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = alertResult.verdictSummary,
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              onInputUrlChanged(alertResult.inputUrl)
              onInputTextChanged(alertResult.inputMessage)
              onAnalyze()
            },
            colors = ButtonDefaults.buttonColors(containerColor = alertBorder, contentColor = colors.surface),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text("Review Evidence Matrix", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Optional Notification Permission Card if not granted
    if (!hasNotificationPermission && !isNotificationBannerDismissed) {
      item {
        NotificationPermissionCard(
          onRequestPermission = onRequestNotificationPermission,
          onDismiss = onDismissNotificationBanner
        )
      }
    }

    // Optional Screen Scan Accessibility Permission Card if not granted
    if (!isAccessibilityEnabled && !isAccessibilityBannerDismissed) {
      item {
        OverlayPermissionCard(
          onDismiss = onDismissAccessibilityBanner
        )
      }
    }

    // 2. Preset Scenarios Carousel
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Try Example Scenarios",
            fontFamily = SpaceGrotesk,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
          )
          Text(
            text = "Tap to test",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(presets) { preset ->
            PresetChip(
              preset = preset,
              onClick = { onLoadPreset(preset) }
            )
          }
        }
      }
    }

    // 3. Simple Input Card
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(colors.surface)
          .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Enter text or link to test",
            fontFamily = SpaceGrotesk,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
          )

          if (inputText.isNotBlank() || inputUrl.isNotBlank()) {
            Row(
              modifier = Modifier
                .clickable { onClearInputs() }
                .padding(4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear",
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Clear",
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                color = colors.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message input
        OutlinedTextField(
          value = inputText,
          onValueChange = onInputTextChanged,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("message_input"),
          label = {
            Text(
              "Suspicious message or SMS",
              fontFamily = SpaceGrotesk,
              fontSize = 13.sp
            )
          },
          placeholder = {
            Text(
              "e.g. 'USPS: Package held due to incorrect address...'",
              fontFamily = SpaceGrotesk,
              fontSize = 12.sp,
              color = colors.onSurfaceVariant
            )
          },
          minLines = 2,
          maxLines = 4,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = colors.outline,
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface,
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant
          ),
          shape = RoundedCornerShape(12.dp),
          trailingIcon = {
            IconButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val item = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                if (!item.isNullOrBlank()) {
                  onInputTextChanged(item)
                  Toast.makeText(context, "Pasted from clipboard", Toast.LENGTH_SHORT).show()
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.ContentPaste,
                contentDescription = "Paste message",
                tint = AccentBlue,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // URL input
        OutlinedTextField(
          value = inputUrl,
          onValueChange = onInputUrlChanged,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("url_input"),
          label = {
            Text(
              "Link URL",
              fontFamily = SpaceGrotesk,
              fontSize = 13.sp
            )
          },
          placeholder = {
            Text(
              "https://example.com/link...",
              fontFamily = SpaceGrotesk,
              fontSize = 12.sp,
              color = colors.onSurfaceVariant
            )
          },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = colors.outline,
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface,
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant
          ),
          shape = RoundedCornerShape(12.dp),
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Link,
              contentDescription = null,
              tint = AccentBlue,
              modifier = Modifier.size(18.dp)
            )
          }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Analyze Action Button
        Button(
          onClick = onAnalyze,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("analyze_safely_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = AccentBlue,
            contentColor = colors.surface
          ),
          enabled = !isAnalyzing && (inputText.isNotBlank() || inputUrl.isNotBlank())
        ) {
          if (isAnalyzing) {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              color = colors.surface,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Checking link safely...",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          } else {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Check Link Safely",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }

        if (isAnalyzing && analysisProgress.isNotBlank()) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = analysisProgress,
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = AccentBlue
          )
        }
      }
    }

    // 4. Results Section
    if (currentResult != null && !isAnalyzing) {
      val result = currentResult

      // Plain NLP Verdict & Action Card
      item {
        val bannerBorder = when (result.threatLevel) {
          ThreatLevel.DANGER -> StatusDanger
          ThreatLevel.SUSPICIOUS -> StatusSuspicious
          ThreatLevel.SAFE -> StatusSafe
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(1.5.dp, bannerBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("verdict_banner")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(bannerBorder.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = when (result.threatLevel) {
                    ThreatLevel.DANGER -> Icons.Default.Warning
                    ThreatLevel.SUSPICIOUS -> Icons.Default.Shield
                    ThreatLevel.SAFE -> Icons.Default.CheckCircle
                  },
                  contentDescription = null,
                  tint = bannerBorder,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = result.threatLevel.simpleTitle,
                  fontFamily = SpaceGrotesk,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp,
                  color = bannerBorder
                )
                Text(
                  text = "Target: ${result.attackVector}",
                  fontFamily = SpaceGrotesk,
                  fontSize = 12.sp,
                  color = colors.onSurfaceVariant
                )
              }
            }

            IconButton(
              onClick = {
                val reportSummary = buildShareableReport(result)
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(Intent.EXTRA_TEXT, reportSummary)
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share ScamShield Report"))
              },
              modifier = Modifier.size(48.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share Report",
                tint = bannerBorder,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = result.verdictSummary,
            fontFamily = SpaceGrotesk,
            fontSize = 12.sp,
            color = colors.onSurface,
            lineHeight = 17.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Recommended Action Box
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(colors.surfaceVariant)
              .padding(12.dp)
          ) {
            Text(
              text = "What should you do?",
              fontFamily = SpaceGrotesk,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = bannerBorder
            )
            Spacer(modifier = Modifier.height(4.dp))
            result.recommendedActions.forEach { action ->
              Text(
                text = "• $action",
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                color = colors.onSurface,
                lineHeight = 15.sp
              )
            }
          }
        }
      }

      // Identity Consistency Check (Claimed Brand vs. Destination Domain)
      if (result.identityConsistency != null) {
        item {
          IdentityConsistencyCard(check = result.identityConsistency)
        }
      }

      // Max Check Active Sandbox Honey-Trap Telemetry
      if (result.maxCheckTelemetry != null) {
        item {
          MaxCheckTelemetryCard(telemetry = result.maxCheckTelemetry)
        }
      }

      // Multi-Source Evidence Matrix Table (PhishTank, urlscan, VirusTotal, etc.)
      if (result.evidenceMatrix.isNotEmpty()) {
        item {
          EvidenceMatrixCard(evidenceList = result.evidenceMatrix)
        }
      }

      // Threat Score Gauge & Multi-layer Risk Matrix
      item {
        ThreatScoreGauge(
          score = result.overallScore,
          threatLevel = result.threatLevel,
          layerScores = result.layerScores,
          isDevMode = isDevMode
        )
      }

      // Infrastructure & Connection Path
      item {
        InfrastructureGraphView(
          nodes = result.infrastructurePath,
          isDevMode = isDevMode
        )
      }

      // "What Would Happen?" Attack Simulation
      item {
        AttackSimulationCard(
          steps = result.predictedAttackPath,
          threatLevel = result.threatLevel,
          isDevMode = isDevMode
        )
      }

      // AI Decision Explainer
      item {
        AiFindingsCard(
          findings = result.aiFindings,
          isDevMode = isDevMode
        )
      }

      // Privacy Policy & Zero-PII Compliance Card
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
            .clickable { showPrivacyPolicyDialog = true }
            .padding(16.dp)
            .testTag("privacy_policy_footer_card")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(AccentBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Description,
                  contentDescription = null,
                  tint = AccentBlue,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Official Privacy Policy",
                  fontFamily = SpaceGrotesk,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = colors.onSurface
                )
                Text(
                  text = "Financial Privacy Guard & Zero-PII Guarantee",
                  fontFamily = SpaceGrotesk,
                  fontSize = 11.sp,
                  color = AccentBlue,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            Icon(
              imageVector = Icons.Default.OpenInNew,
              contentDescription = "Read Privacy Policy",
              tint = AccentBlue,
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "ScamShield 360 automatically pauses screen reading inside banking apps and redacts phone numbers, emails, and tokens before querying threat APIs. Tap to read the full policy.",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant,
            lineHeight = 15.sp
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun PresetChip(
  preset: ScamPreset,
  onClick: () -> Unit
) {
  val colors = MaterialTheme.colorScheme
  val chipColor = when (preset.expectedRisk) {
    ThreatLevel.DANGER -> StatusDanger
    ThreatLevel.SUSPICIOUS -> StatusSuspicious
    ThreatLevel.SAFE -> StatusSafe
  }

  val scenarioIcon = when (preset.expectedRisk) {
    ThreatLevel.DANGER -> Icons.Default.Warning
    ThreatLevel.SUSPICIOUS -> Icons.Default.Shield
    ThreatLevel.SAFE -> Icons.Default.CheckCircle
  }

  Column(
    modifier = Modifier
      .width(185.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(colors.surface)
      .border(1.dp, colors.outline, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = preset.category,
        fontFamily = SpaceGrotesk,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = AccentBlue
      )

      Box(
        modifier = Modifier
          .size(20.dp)
          .clip(CircleShape)
          .background(chipColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = scenarioIcon,
          contentDescription = null,
          tint = chipColor,
          modifier = Modifier.size(12.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = preset.title,
      fontFamily = SpaceGrotesk,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = colors.onSurface,
      maxLines = 1
    )

    Spacer(modifier = Modifier.height(3.dp))

    Text(
      text = preset.snippet,
      fontFamily = SpaceGrotesk,
      fontSize = 10.sp,
      color = colors.onSurfaceVariant,
      maxLines = 2,
      lineHeight = 14.sp
    )
  }
}

private fun buildShareableReport(result: ThreatAnalysisResult): String {
  return """
    🛡️ SCAMSHIELD 360 REPORT
    Verdict: ${result.threatLevel.simpleTitle} (${result.overallScore}/100 Risk)
    Target: ${result.attackVector}
    
    Link: ${result.inputUrl.ifBlank { "N/A" }}
    Summary: ${result.verdictSummary}
    
    Recommendations:
    ${result.recommendedActions.joinToString("\n") { "• $it" }}
    
    “Don't click to discover. Analyze before you interact.”
  """.trimIndent()
}

@Composable
private fun ProtectionPill(
  title: String,
  statusLabel: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  active: Boolean,
  activeColor: androidx.compose.ui.graphics.Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colorScheme
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (active) activeColor.copy(alpha = 0.12f) else colors.surfaceVariant)
      .border(
        1.dp,
        if (active) activeColor.copy(alpha = 0.35f) else colors.outline.copy(alpha = 0.4f),
        RoundedCornerShape(8.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 7.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = if (active) activeColor else colors.onSurfaceVariant.copy(alpha = 0.6f),
      modifier = Modifier.size(15.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontFamily = SpaceGrotesk,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = colors.onSurface,
        maxLines = 1
      )
      Text(
        text = statusLabel,
        fontFamily = JetBrainsMono,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = if (active) activeColor else colors.onSurfaceVariant.copy(alpha = 0.6f)
      )
    }
  }
}


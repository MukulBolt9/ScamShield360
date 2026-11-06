package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
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
  isNowbarMeterMode: Boolean,
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
  onToggleNowbarMeterMode: () -> Unit,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val colors = MaterialTheme.colorScheme

  var showProtectionSettingsSheet by remember { mutableStateOf(false) }

  if (showProtectionSettingsSheet) {
    ProtectionSettingsSheet(
      onDismiss = { showProtectionSettingsSheet = false },
      isBackgroundGuardian = isBackgroundGuardian,
      isNowbarMeterMode = isNowbarMeterMode,
      isPrivacyMode = isPrivacyMode,
      isAutomationMode = isAutomationMode,
      isBankingPrivacyShield = true,
      isBankingAppCurrentlyActive = isBankingPrivacyActive,
      detectedBankingAppName = if (isBankingPrivacyActive) "Banking Platform / App" else null,
      onToggleBackgroundGuardian = onToggleBackgroundGuardian,
      onToggleNowbarMeterMode = onToggleNowbarMeterMode,
      onTogglePrivacyMode = onTogglePrivacyMode,
      onToggleAutomationMode = onToggleAutomationMode,
      onToggleBankingPrivacyShield = {}
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
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(4.dp)
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

          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Screen Scan Button (Gemini style overlay without floating icon)
            OutlinedButton(
              onClick = onOpenAssistantOverlay,
              modifier = Modifier.testTag("quick_screen_scan_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Screen Scan",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }

            // Protection Settings Gear/Security Icon
            IconButton(
              onClick = { showProtectionSettingsSheet = true },
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surfaceVariant)
                .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
                .testTag("protection_settings_button")
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Protection Settings",
                tint = colors.onSurface,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        if (isBankingPrivacyActive) {
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(StatusSafe.copy(alpha = 0.15f))
              .border(1.dp, StatusSafe.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = StatusSafe,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "FINANCIAL PRIVACY GUARD ACTIVE",
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = StatusSafe
              )
              Text(
                text = "Auto-scanning paused inside verified banking/UPI app or website.",
                fontFamily = SpaceGrotesk,
                fontSize = 10.sp,
                color = colors.onSurface
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Protection summary indicator pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ProtectionPill(
            title = "Background",
            active = isBackgroundGuardian,
            activeColor = AccentBlue,
            modifier = Modifier.weight(1f)
          )
          ProtectionPill(
            title = "Now Bar",
            active = isNowbarMeterMode,
            activeColor = SamsungNowbarPurple,
            modifier = Modifier.weight(1f)
          )
          ProtectionPill(
            title = "Auto Guard",
            active = isAutomationMode,
            activeColor = DevModeCyan,
            modifier = Modifier.weight(1f)
          )
          ProtectionPill(
            title = "Zero PII",
            active = isPrivacyMode,
            activeColor = StatusSafe,
            modifier = Modifier.weight(1f)
          )
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
            Column(modifier = Modifier.weight(1f)) {
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

            IconButton(
              onClick = {
                val reportSummary = buildShareableReport(result)
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(Intent.EXTRA_TEXT, reportSummary)
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share ScamShield Report"))
              }
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share Report",
                tint = bannerBorder
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

      // Human Shield / Scam Lesson
      item {
        ScamLessonCard(lessons = result.scamLessons)
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

  Column(
    modifier = Modifier
      .width(180.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(colors.surface)
      .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(10.dp)
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
          .size(6.dp)
          .clip(CircleShape)
          .background(chipColor)
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = preset.title,
      fontFamily = SpaceGrotesk,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = colors.onSurface,
      maxLines = 1
    )

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = preset.snippet,
      fontFamily = SpaceGrotesk,
      fontSize = 10.sp,
      color = colors.onSurfaceVariant,
      maxLines = 2,
      lineHeight = 13.sp
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
  active: Boolean,
  activeColor: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colorScheme
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(if (active) activeColor.copy(alpha = 0.12f) else colors.surfaceVariant)
      .border(0.5.dp, if (active) activeColor.copy(alpha = 0.3f) else colors.outline.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
      .padding(horizontal = 6.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(if (active) activeColor else colors.onSurfaceVariant.copy(alpha = 0.4f))
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = title,
      fontFamily = SpaceGrotesk,
      fontSize = 9.sp,
      fontWeight = FontWeight.SemiBold,
      color = if (active) activeColor else colors.onSurfaceVariant
    )
  }
}


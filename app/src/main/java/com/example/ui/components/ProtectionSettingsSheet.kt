package com.example.ui.components

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DevModeCyan
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SamsungNowbarPurple
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusSafe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectionSettingsSheet(
  onDismiss: () -> Unit,
  isBackgroundGuardian: Boolean,
  isSamsungNowbarEnabled: Boolean = true,
  isPrivacyMode: Boolean,
  isAutomationMode: Boolean,
  isBankingPrivacyShield: Boolean,
  isBankingAppCurrentlyActive: Boolean,
  detectedBankingAppName: String?,
  onToggleBackgroundGuardian: () -> Unit,
  onToggleSamsungNowbar: () -> Unit = {},
  onOpenPromotionSettings: () -> Unit,
  onTogglePrivacyMode: () -> Unit,
  onToggleAutomationMode: () -> Unit,
  onToggleBankingPrivacyShield: () -> Unit,
  sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
  val context = LocalContext.current
  val colors = MaterialTheme.colorScheme
  var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = colors.surface,
    scrimColor = colors.onSurface.copy(alpha = 0.32f),
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .testTag("protection_settings_sheet")
    ) {
      // Sheet Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Protection Settings",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = colors.onSurface
          )
        }

        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = colors.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 1. Live Background Link Guardian
      SettingToggleRow(
        icon = Icons.Default.NotificationsActive,
        iconTint = if (isBackgroundGuardian) AccentBlue else colors.onSurfaceVariant,
        title = "Live Background Protection",
        subtitle = if (isBackgroundGuardian) "Auditing links silently in status bar" else "Disabled (Tap switch to enable)",
        badgeText = if (isBackgroundGuardian) "Active" else "Off",
        badgeColor = if (isBackgroundGuardian) AccentBlue else colors.onSurfaceVariant,
        checked = isBackgroundGuardian,
        onCheckedChange = { onToggleBackgroundGuardian() }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Android 16 Live Update & Samsung Now Bar
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(colors.surfaceVariant)
          .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.Smartphone,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Android 16 Live Update & Now Bar",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = colors.onSurface,
                modifier = Modifier.weight(1f, fill = false),
                maxLines = 2
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(AccentBlue.copy(alpha = 0.15f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "Native Chip",
                  fontFamily = JetBrainsMono,
                  fontSize = 8.sp,
                  color = AccentBlue,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  softWrap = false
                )
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Live Updates natively power the Pixel Status Bar Chip and Samsung One UI Now Bar across lock screen, status bar, and notifications using standard Android Platform APIs.",
              fontFamily = SpaceGrotesk,
              fontSize = 11.sp,
              color = colors.onSurfaceVariant,
              lineHeight = 15.sp
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(AccentBlue.copy(alpha = 0.12f))
              .clickable { onOpenPromotionSettings() }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Settings",
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                color = AccentBlue,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "Promotion Settings",
                tint = AccentBlue,
                modifier = Modifier.size(12.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Banking & UPI Privacy Shield
      SettingToggleRow(
        icon = Icons.Default.Security,
        iconTint = if (isBankingPrivacyShield) StatusSafe else colors.onSurfaceVariant,
        title = "Banking & UPI Privacy Shield",
        subtitle = "Pauses screen reading in bank suites and UPI apps (Google Pay, PhonePe, YONO, Axis, HDFC, SBIC) to keep PINs & balances private",
        badgeText = if (isBankingPrivacyShield) "Shield Active" else "Disabled",
        badgeColor = if (isBankingPrivacyShield) StatusSafe else colors.onSurfaceVariant,
        checked = isBankingPrivacyShield,
        onCheckedChange = { onToggleBankingPrivacyShield() },
        extraNote = if (isBankingAppCurrentlyActive) "🔒 Shielding ${detectedBankingAppName ?: "Banking Suite"} (Screen reading paused)" else null
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 5. Automation Mode (Screen Auto-Guard)
      SettingToggleRow(
        icon = Icons.Default.AutoAwesome,
        iconTint = if (isAutomationMode) DevModeCyan else colors.onSurfaceVariant,
        title = "Proactive Screen Auto-Guard",
        subtitle = "Scans on-screen links automatically across SMS, WhatsApp & Chrome before you tap",
        badgeText = if (isAutomationMode) "Auto-Scan" else "Manual",
        badgeColor = if (isAutomationMode) DevModeCyan else colors.onSurfaceVariant,
        checked = isAutomationMode,
        onCheckedChange = { onToggleAutomationMode() }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 7. Privacy Policy & Compliance
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(colors.surfaceVariant)
          .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
          .clickable { showPrivacyPolicyDialog = true }
          .padding(12.dp)
          .testTag("privacy_policy_row")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = null,
              tint = AccentBlue,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Official Privacy Policy",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colors.onSurface
              )
              Text(
                text = "Zero-PII guarantee, Financial Privacy Guard rules, and data handling disclosure",
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                color = colors.onSurfaceVariant,
                lineHeight = 15.sp
              )
            }
          }

          Icon(
            imageVector = Icons.Default.OpenInNew,
            contentDescription = "Read Policy",
            tint = AccentBlue,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
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
  }
}

@Composable
private fun SettingToggleRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: androidx.compose.ui.graphics.Color,
  title: String,
  subtitle: String,
  badgeText: String,
  badgeColor: androidx.compose.ui.graphics.Color,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  extraNote: String? = null
) {
  val colors = MaterialTheme.colorScheme

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(colors.surfaceVariant)
      .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = title,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = colors.onSurface,
            modifier = Modifier.weight(1f, fill = false),
            maxLines = 2
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(badgeColor.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = badgeText,
              fontFamily = JetBrainsMono,
              fontSize = 8.sp,
              color = badgeColor,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              softWrap = false
            )
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          fontFamily = SpaceGrotesk,
          fontSize = 11.sp,
          color = colors.onSurfaceVariant,
          lineHeight = 15.sp
        )
        if (extraNote != null) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = extraNote,
            fontFamily = JetBrainsMono,
            fontSize = 10.sp,
            color = StatusSafe,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
          checkedThumbColor = colors.surface,
          checkedTrackColor = iconTint,
          uncheckedThumbColor = colors.onSurfaceVariant,
          uncheckedTrackColor = colors.outline
        )
      )
    }
  }
}

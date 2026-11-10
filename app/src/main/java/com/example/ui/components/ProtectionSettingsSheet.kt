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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DevModeCyan
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusSafe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectionSettingsSheet(
  onDismiss: () -> Unit,
  isBackgroundGuardian: Boolean,
  isPrivacyMode: Boolean,
  isAutomationMode: Boolean,
  isBankingPrivacyShield: Boolean,
  isBankingAppCurrentlyActive: Boolean,
  detectedBankingAppName: String?,
  onToggleBackgroundGuardian: () -> Unit,
  onOpenPromotionSettings: () -> Unit,
  onTogglePrivacyMode: () -> Unit,
  onToggleAutomationMode: () -> Unit,
  onToggleBankingPrivacyShield: () -> Unit,
  sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
  val colors = MaterialTheme.colorScheme

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

      // 2. Android 16 Live Update Status Bar Chip
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Smartphone,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Android 16 Status Bar Live Chip",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colors.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(AccentBlue.copy(alpha = 0.15f))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "Native Chip",
                  fontFamily = JetBrainsMono,
                  fontSize = 8.sp,
                  color = AccentBlue,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Live Update notification formatted with ProgressStyle and short critical text for Android 16 status bar chip & lock screen.",
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

      // 3. Banking & UPI Privacy Shield
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

      // 4. Automation Mode (Screen Auto-Guard)
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

      // 5. Zero-PII Privacy Mode
      SettingToggleRow(
        icon = Icons.Default.Lock,
        iconTint = if (isPrivacyMode) StatusSafe else colors.onSurfaceVariant,
        title = "Zero-PII Token Redaction",
        subtitle = "Auto-redacts phone numbers, emails, and auth tokens before intelligence queries",
        badgeText = if (isPrivacyMode) "Redacted" else "Plaintext",
        badgeColor = if (isPrivacyMode) StatusSafe else colors.onSurfaceVariant,
        checked = isPrivacyMode,
        onCheckedChange = { onTogglePrivacyMode() }
      )

      Spacer(modifier = Modifier.height(24.dp))
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = title,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = colors.onSurface
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(badgeColor.copy(alpha = 0.15f))
              .padding(horizontal = 5.dp, vertical = 1.dp)
          ) {
            Text(
              text = badgeText,
              fontFamily = JetBrainsMono,
              fontSize = 8.sp,
              color = badgeColor,
              fontWeight = FontWeight.Bold
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

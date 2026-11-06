package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusDangerContainerDark
import com.example.ui.theme.StatusDangerContainerLight
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSafeContainerDark
import com.example.ui.theme.StatusSafeContainerLight
import com.example.ui.theme.StatusSuspicious
import com.example.ui.theme.StatusSuspiciousContainerDark
import com.example.ui.theme.StatusSuspiciousContainerLight

enum class ThreatLevel(
  val label: String,
  val color: Color,
  val containerColorDark: Color,
  val containerColorLight: Color,
  val badgeText: String,
  val simpleTitle: String,
  val simpleSubtitle: String
) {
  SAFE(
    label = "SAFE",
    color = StatusSafe,
    containerColorDark = StatusSafeContainerDark,
    containerColorLight = StatusSafeContainerLight,
    badgeText = "SAFE",
    simpleTitle = "Looks Safe to Open",
    simpleSubtitle = "No active threat indicators or redirects observed. Always verify credentials."
  ),
  SUSPICIOUS(
    label = "SUSPICIOUS",
    color = StatusSuspicious,
    containerColorDark = StatusSuspiciousContainerDark,
    containerColorLight = StatusSuspiciousContainerLight,
    badgeText = "SUSPICIOUS",
    simpleTitle = "Be Careful: Suspicious Signals",
    simpleSubtitle = "This message or link uses unexpected redirects, urgent language, or unverified hosts."
  ),
  DANGER(
    label = "DANGER",
    color = StatusDanger,
    containerColorDark = StatusDangerContainerDark,
    containerColorLight = StatusDangerContainerLight,
    badgeText = "SCAM DETECTED",
    simpleTitle = "Dangerous Scam: Do Not Open",
    simpleSubtitle = "Unauthorized lookalike domain designed to steal credentials, money, or personal data."
  );

  fun getContainerColor(isDark: Boolean): Color = if (isDark) containerColorDark else containerColorLight

  companion object {
    fun fromScore(score: Int): ThreatLevel = when {
      score >= 70 -> DANGER
      score >= 45 -> SUSPICIOUS
      else -> SAFE
    }
  }
}

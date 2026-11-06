package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiFinding
import com.example.data.model.FindingSeverity
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSuspicious

@Composable
fun AiFindingsCard(
  findings: List<AiFinding>,
  isDevMode: Boolean,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colorScheme

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(colors.surface)
      .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
      .padding(18.dp)
      .testTag("ai_findings_card")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Psychology,
        contentDescription = null,
        tint = AccentBlue,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "Why was this flagged?",
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = colors.onSurface
        )
        Text(
          text = "Clear breakdown of warning signals detected",
          fontFamily = SpaceGrotesk,
          fontSize = 11.sp,
          color = colors.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (findings.isEmpty()) {
      Text(
        text = "No warning signs observed. Domain and links match normal legitimate behavior.",
        fontFamily = SpaceGrotesk,
        fontSize = 12.sp,
        color = colors.onSurfaceVariant
      )
    } else {
      findings.forEach { finding ->
        FindingItemRow(finding, isDevMode)
        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}

@Composable
private fun FindingItemRow(finding: AiFinding, isDevMode: Boolean) {
  val colors = MaterialTheme.colorScheme
  val isDark = colors.background.red < 0.5f

  val (sevColor, sevBg) = when (finding.severity) {
    FindingSeverity.CRITICAL -> Pair(StatusDanger, StatusDanger.copy(alpha = if (isDark) 0.18f else 0.12f))
    FindingSeverity.HIGH -> Pair(StatusDanger, StatusDanger.copy(alpha = if (isDark) 0.15f else 0.10f))
    FindingSeverity.MEDIUM -> Pair(StatusSuspicious, StatusSuspicious.copy(alpha = if (isDark) 0.15f else 0.10f))
    FindingSeverity.LOW -> Pair(colors.onSurfaceVariant, colors.surface)
    FindingSeverity.INFO -> Pair(StatusSafe, StatusSafe.copy(alpha = if (isDark) 0.18f else 0.12f))
  }

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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Icon(
          imageVector = if (finding.severity == FindingSeverity.INFO) Icons.Default.Info else Icons.Default.ReportProblem,
          contentDescription = null,
          tint = sevColor,
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = finding.title,
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = colors.onSurface
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(sevBg)
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = finding.severity.name,
          fontFamily = JetBrainsMono,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = sevColor
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = finding.description,
      fontFamily = SpaceGrotesk,
      fontSize = 11.sp,
      color = colors.onSurfaceVariant,
      lineHeight = 15.sp
    )

    if (isDevMode) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Category: ${finding.category.uppercase()}",
        fontFamily = JetBrainsMono,
        fontSize = 9.sp,
        color = AccentBlue
      )
    }
  }
}

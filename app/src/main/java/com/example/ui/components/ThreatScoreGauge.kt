package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LayerScores
import com.example.data.model.ThreatLevel
import com.example.ui.theme.DevModeAmber
import com.example.ui.theme.DevModeCyan
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSuspicious

@Composable
fun ThreatScoreGauge(
  score: Int,
  threatLevel: ThreatLevel,
  layerScores: LayerScores,
  isDevMode: Boolean,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colorScheme
  val isDark = colors.background.red < 0.5f // detect dark vs light

  val animatedScore = remember { Animatable(0f) }
  LaunchedEffect(score) {
    animatedScore.animateTo(
      targetValue = score.toFloat(),
      animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
  }

  val gaugeColor = when (threatLevel) {
    ThreatLevel.DANGER -> StatusDanger
    ThreatLevel.SUSPICIOUS -> StatusSuspicious
    ThreatLevel.SAFE -> StatusSafe
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(colors.surface)
      .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
      .padding(18.dp)
      .testTag("threat_score_gauge")
  ) {
    // Top Clean Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = when (threatLevel) {
            ThreatLevel.DANGER -> Icons.Default.ReportProblem
            ThreatLevel.SUSPICIOUS -> Icons.Default.Warning
            ThreatLevel.SAFE -> Icons.Default.CheckCircle
          },
          contentDescription = null,
          tint = gaugeColor,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = threatLevel.simpleTitle,
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = colors.onSurface
        )
      }

      // Clean Score Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(threatLevel.getContainerColor(isDark))
          .border(1.dp, gaugeColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Text(
          text = "${animatedScore.value.toInt()}/100 Risk",
          fontFamily = JetBrainsMono,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = gaugeColor
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Simple Progress Bar
    LinearProgressIndicator(
      progress = { animatedScore.value / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(CircleShape),
      color = gaugeColor,
      trackColor = colors.surfaceVariant
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Natural Language Summary
    Text(
      text = threatLevel.simpleSubtitle,
      fontFamily = SpaceGrotesk,
      fontSize = 13.sp,
      color = colors.onSurfaceVariant,
      lineHeight = 18.sp
    )

    // DEV MODE: Unlocks Technical Multi-Layer Risk Matrix
    if (isDevMode) {
      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "POWER USER TELEMETRY MATRIX",
          fontFamily = JetBrainsMono,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = DevModeCyan
        )
        Text(
          text = "Dev Mode Active",
          fontFamily = JetBrainsMono,
          fontSize = 9.sp,
          color = DevModeAmber
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      DevLayerRow("Message Social Engineering", layerScores.messageRisk)
      Spacer(modifier = Modifier.height(6.dp))
      DevLayerRow("URL & Domain Entropy", layerScores.linkRisk)
      Spacer(modifier = Modifier.height(6.dp))
      DevLayerRow("Sandbox Web & Redirect Probe", layerScores.websiteRisk)
      Spacer(modifier = Modifier.height(6.dp))
      DevLayerRow("Behavioral Impersonation Pattern", layerScores.behaviorRisk)
    }
  }
}

@Composable
private fun DevLayerRow(name: String, score: Int) {
  val colors = MaterialTheme.colorScheme
  val barColor = when {
    score >= 70 -> StatusDanger
    score >= 35 -> StatusSuspicious
    else -> StatusSafe
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(colors.surfaceVariant)
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = name,
        fontFamily = JetBrainsMono,
        fontSize = 10.sp,
        color = colors.onSurfaceVariant
      )
      Text(
        text = "$score/100",
        fontFamily = JetBrainsMono,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = barColor
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    LinearProgressIndicator(
      progress = { score / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
        .clip(RoundedCornerShape(2.dp)),
      color = barColor,
      trackColor = colors.outline.copy(alpha = 0.3f)
    )
  }
}

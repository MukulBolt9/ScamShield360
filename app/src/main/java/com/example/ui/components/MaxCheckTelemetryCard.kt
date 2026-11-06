package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.analyzer.MaxCheckTelemetry
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DevModeAmber
import com.example.ui.theme.DevModeCyan
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSuspicious

@Composable
fun MaxCheckTelemetryCard(
  telemetry: MaxCheckTelemetry,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colorScheme
  var isExpanded by remember { mutableStateOf(true) }

  val headerColor = when {
    telemetry.threatScoreBoost >= 40 || telemetry.formFieldsDetected.isNotEmpty() -> StatusDanger
    telemetry.isUnreachableHost -> StatusSuspicious
    else -> StatusSafe
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(colors.surface)
      .border(1.5.dp, headerColor, RoundedCornerShape(18.dp))
      .padding(16.dp)
      .testTag("max_check_telemetry_card")
  ) {
    // Header Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { isExpanded = !isExpanded },
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(headerColor.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Science,
            contentDescription = null,
            tint = headerColor,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "MAX CHECK: Active Sandbox Probe",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = headerColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(headerColor.copy(alpha = 0.15f))
                .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
              Text(
                text = "Honey-Trap",
                fontFamily = JetBrainsMono,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = headerColor
              )
            }
          }
          Text(
            text = "Isolated Navigation & Synthetic Canary Injection",
            fontFamily = SpaceGrotesk,
            fontSize = 10.sp,
            color = colors.onSurfaceVariant
          )
        }
      }

      Icon(
        imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
        contentDescription = "Expand/Collapse",
        tint = colors.onSurfaceVariant
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Verdict Badge
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(headerColor.copy(alpha = 0.12f))
        .border(1.dp, headerColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
        .padding(10.dp)
    ) {
      Text(
        text = telemetry.sandboxVerdict,
        fontFamily = JetBrainsMono,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = headerColor
      )
    }

    AnimatedVisibility(visible = isExpanded) {
      Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {

        // Captured Telemetry Items
        if (telemetry.syntheticCanaryInjected != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(colors.surfaceVariant)
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.BugReport,
              contentDescription = null,
              tint = DevModeAmber,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Synthetic Canary Credentials Injected",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = colors.onSurface
              )
              Text(
                text = telemetry.syntheticCanaryInjected,
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                color = DevModeAmber
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }

        if (telemetry.exfiltrationEndpointCaptured != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(colors.surfaceVariant)
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Terminal,
              contentDescription = null,
              tint = StatusDanger,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Captured Exfiltration Endpoint",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = colors.onSurface
              )
              Text(
                text = telemetry.exfiltrationEndpointCaptured,
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                color = StatusDanger
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }

        // Terminal Log
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surfaceVariant.copy(alpha = 0.7f))
            .border(1.dp, colors.outline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(10.dp)
        ) {
          Text(
            text = "SANDBOX EXECUTION LOG",
            fontFamily = JetBrainsMono,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = AccentBlue
          )
          Spacer(modifier = Modifier.height(6.dp))
          telemetry.telemetryLog.forEach { logLine ->
            Text(
              text = logLine,
              fontFamily = JetBrainsMono,
              fontSize = 10.sp,
              color = colors.onSurface,
              lineHeight = 14.sp
            )
          }
        }
      }
    }
  }
}

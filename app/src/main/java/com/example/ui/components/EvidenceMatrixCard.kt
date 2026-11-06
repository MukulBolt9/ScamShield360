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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
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
import com.example.data.model.EvidenceItem
import com.example.data.model.EvidenceStatus
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSuspicious

@Composable
fun EvidenceMatrixCard(
  evidenceList: List<EvidenceItem>,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colorScheme
  val isDark = colors.background.red < 0.5f

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(colors.surface)
      .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
      .padding(16.dp)
      .testTag("evidence_matrix_card")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Layers,
          contentDescription = null,
          tint = AccentBlue,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "Multi-Source Evidence Matrix",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = colors.onSurface
          )
          Text(
            text = "AI is the reasoning layer, not the source of truth",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(AccentBlue.copy(alpha = if (isDark) 0.2f else 0.1f))
          .padding(horizontal = 8.dp, vertical = 2.dp)
      ) {
        Text(
          text = "${evidenceList.size} Signals",
          fontFamily = JetBrainsMono,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = AccentBlue
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Evidence Items List / Table
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(colors.surfaceVariant)
        .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
    ) {
      evidenceList.forEachIndexed { index, item ->
        val statusColor = when (item.status) {
          EvidenceStatus.DANGER -> StatusDanger
          EvidenceStatus.WARNING -> StatusSuspicious
          EvidenceStatus.VERIFIED -> StatusSafe
          EvidenceStatus.NEUTRAL -> colors.onSurfaceVariant
        }

        val statusBg = when (item.status) {
          EvidenceStatus.DANGER -> StatusDanger.copy(alpha = if (isDark) 0.18f else 0.12f)
          EvidenceStatus.WARNING -> StatusSuspicious.copy(alpha = if (isDark) 0.18f else 0.12f)
          EvidenceStatus.VERIFIED -> StatusSafe.copy(alpha = if (isDark) 0.18f else 0.12f)
          EvidenceStatus.NEUTRAL -> colors.outline.copy(alpha = 0.5f)
        }

        val statusLabel = when (item.status) {
          EvidenceStatus.DANGER -> "DANGER"
          EvidenceStatus.WARNING -> "WARNING"
          EvidenceStatus.VERIFIED -> "VERIFIED"
          EvidenceStatus.NEUTRAL -> "UNLISTED"
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
        ) {
          // Top row: Indicator title + Source pill on left, Status badge on right
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.indicator,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colors.onSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Source: ${item.source}",
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                color = colors.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(statusBg)
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = statusLabel,
                fontFamily = JetBrainsMono,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Result line
          Text(
            text = item.result,
            fontFamily = JetBrainsMono,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor
          )

          Spacer(modifier = Modifier.height(3.dp))

          // Detail text
          Text(
            text = item.detail,
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant,
            lineHeight = 15.sp
          )
        }

        if (index < evidenceList.size - 1) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(colors.outline.copy(alpha = 0.5f))
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Golden Security Principle Callout
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(colors.surfaceVariant.copy(alpha = 0.8f))
        .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
        .padding(10.dp),
      verticalAlignment = Alignment.Top
    ) {
      Icon(
        imageVector = Icons.Default.Info,
        contentDescription = null,
        tint = AccentBlue,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Rule: 'Not listed' must NEVER mean 'safe'. PhishTank and vendor databases only contain previously reported attacks. Brand, infrastructure, and page consistency determine genuine safety.",
        fontFamily = SpaceGrotesk,
        fontSize = 11.sp,
        color = colors.onSurfaceVariant,
        lineHeight = 15.sp
      )
    }
  }
}

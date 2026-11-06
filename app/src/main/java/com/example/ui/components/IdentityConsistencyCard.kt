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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.model.IdentityConsistencyCheck
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe

@Composable
fun IdentityConsistencyCard(
  check: IdentityConsistencyCheck,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colorScheme
  val isDark = colors.background.red < 0.5f

  val statusColor = if (check.isDomainMatched) StatusSafe else StatusDanger
  val containerBg = if (check.isDomainMatched) {
    StatusSafe.copy(alpha = if (isDark) 0.15f else 0.08f)
  } else {
    StatusDanger.copy(alpha = if (isDark) 0.15f else 0.08f)
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(colors.surface)
      .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
      .padding(16.dp)
      .testTag("identity_consistency_card")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Fingerprint,
          contentDescription = null,
          tint = statusColor,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "Identity vs. Infrastructure",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = colors.onSurface
          )
          Text(
            text = "Does the claimed identity match reality?",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(containerBg)
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(
          text = if (check.isDomainMatched) "MATCHED" else "MISMATCH",
          fontFamily = JetBrainsMono,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = statusColor
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Comparison Table Card
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(colors.surfaceVariant)
        .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      // 1. Claimed Organization
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "CLAIMED ORGANIZATION",
          fontFamily = SpaceGrotesk,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = colors.onSurfaceVariant
        )
        Text(
          text = check.claimedBrand,
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = colors.onSurface
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.outline.copy(alpha = 0.5f)))
      Spacer(modifier = Modifier.height(8.dp))

      // 2. Destination Domain
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "DESTINATION HOST",
          fontFamily = SpaceGrotesk,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = colors.onSurfaceVariant
        )
        Text(
          text = check.destinationDomain,
          fontFamily = JetBrainsMono,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = if (check.isDomainMatched) StatusSafe else StatusDanger
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.outline.copy(alpha = 0.5f)))
      Spacer(modifier = Modifier.height(8.dp))

      // 3. Visual Claim vs Infrastructure Reality
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "VISUAL APPEARANCE",
          fontFamily = SpaceGrotesk,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = check.visualSimilarityClaim,
          fontFamily = SpaceGrotesk,
          fontSize = 11.sp,
          color = colors.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "INFRASTRUCTURE REALITY",
          fontFamily = SpaceGrotesk,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = check.infrastructureReality,
          fontFamily = SpaceGrotesk,
          fontSize = 11.sp,
          color = statusColor
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Innovation Slogan & Core Principle
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(containerBg)
        .padding(10.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Warning,
          contentDescription = null,
          tint = statusColor,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "VISUAL SIMILARITY ≠ LEGITIMACY",
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = statusColor
        )
      }

      Spacer(modifier = Modifier.height(3.dp))

      Text(
        text = "“Don't trust what it looks like. Verify what it is.”",
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = colors.onSurface
      )
    }
  }
}

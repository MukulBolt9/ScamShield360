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
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InfrastructureNode
import com.example.data.model.NodeType
import com.example.data.model.ThreatLevel
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DevModeAmber
import com.example.ui.theme.DevModeCyan
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSuspicious

@Composable
fun InfrastructureGraphView(
  nodes: List<InfrastructureNode>,
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
      .testTag("infrastructure_graph_view")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Lan,
          contentDescription = null,
          tint = if (isDevMode) DevModeCyan else AccentBlue,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = if (isDevMode) "NETWORK & ROUTING GRAPH (DEV)" else "Where does this link take you?",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colors.onSurface
          )
          Text(
            text = if (isDevMode) "Observable DNS, BGP ASN, and HTTP redirect telemetry" else "The path this link follows before reaching the destination",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (!isDevMode) {
      // NORMAL MODE: Friendly, minimal step-by-step summary
      SimplePathSummary(nodes)
    } else {
      // DEV MODE: Full power user telemetry nodes
      nodes.forEachIndexed { index, node ->
        DevThreatNodeItem(
          node = node,
          isLast = index == nodes.size - 1
        )
      }
    }
  }
}

@Composable
private fun SimplePathSummary(nodes: List<InfrastructureNode>) {
  val colors = MaterialTheme.colorScheme
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(colors.surfaceVariant)
      .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    nodes.take(4).forEachIndexed { idx, node ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val badgeColor = when (node.riskLevel) {
          ThreatLevel.DANGER -> StatusDanger
          ThreatLevel.SUSPICIOUS -> StatusSuspicious
          ThreatLevel.SAFE -> StatusSafe
        }

        Box(
          modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(badgeColor.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${idx + 1}",
            fontFamily = JetBrainsMono,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = badgeColor
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = node.label.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = colors.onSurface
          )
          Text(
            text = node.details,
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = colors.onSurfaceVariant,
            maxLines = 1
          )
        }
      }
    }
  }
}

@Composable
private fun DevThreatNodeItem(
  node: InfrastructureNode,
  isLast: Boolean
) {
  val colors = MaterialTheme.colorScheme
  val icon = getNodeIcon(node.type)
  val nodeColor = when (node.riskLevel) {
    ThreatLevel.DANGER -> StatusDanger
    ThreatLevel.SUSPICIOUS -> StatusSuspicious
    ThreatLevel.SAFE -> StatusSafe
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.Top
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(30.dp)
      ) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(nodeColor.copy(alpha = 0.15f))
            .border(1.dp, nodeColor, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = nodeColor,
            modifier = Modifier.size(13.dp)
          )
        }

        if (!isLast) {
          Box(
            modifier = Modifier
              .width(1.5.dp)
              .height(36.dp)
              .background(colors.outline)
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(colors.surfaceVariant)
          .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
          .padding(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = node.label,
            fontFamily = JetBrainsMono,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = nodeColor
          )

          if (node.protocolOrStatus != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(colors.surface)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = node.protocolOrStatus,
                fontFamily = JetBrainsMono,
                fontSize = 9.sp,
                color = colors.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = node.details,
          fontFamily = JetBrainsMono,
          fontSize = 11.sp,
          color = colors.onSurface,
          lineHeight = 15.sp
        )

        if (node.ipAddress != null || node.countryOrRegion != null) {
          Spacer(modifier = Modifier.height(4.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (node.ipAddress != null) {
              Text(
                text = "IP: ${node.ipAddress}",
                fontFamily = JetBrainsMono,
                fontSize = 9.sp,
                color = colors.onSurfaceVariant
              )
            }
            if (node.countryOrRegion != null) {
              Text(
                text = "GEO: [${node.countryOrRegion}]",
                fontFamily = JetBrainsMono,
                fontSize = 9.sp,
                color = DevModeCyan
              )
            }
          }
        }
      }
    }

    if (!isLast) {
      Spacer(modifier = Modifier.height(4.dp))
    }
  }
}

private fun getNodeIcon(type: NodeType): ImageVector = when (type) {
  NodeType.ORIGIN_MESSAGE -> Icons.AutoMirrored.Filled.Message
  NodeType.ORIGIN_URL -> Icons.Default.Link
  NodeType.DNS_HOSTING -> Icons.Default.Dns
  NodeType.NETWORK_ASN -> Icons.Default.Router
  NodeType.REDIRECT_HOP -> Icons.AutoMirrored.Filled.AltRoute
  NodeType.FINAL_DESTINATION -> Icons.Default.Public
  NodeType.CREDENTIAL_FORM -> Icons.Default.LockOpen
}

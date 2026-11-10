package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.ThreatAnalysisResult
import com.example.data.model.ThreatLevel
import com.example.data.repository.RadarTrend
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSuspicious
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ThreatVaultScreen(
  reports: List<ThreatAnalysisResult>,
  radarAlerts: List<RadarTrend>,
  onSelectReport: (ThreatAnalysisResult) -> Unit,
  onDeleteReport: (String) -> Unit,
  onClearAll: () -> Unit,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colorScheme
  var selectedFilter by remember { mutableStateOf<ThreatLevel?>(null) }
  var searchQuery by remember { mutableStateOf("") }

  val filteredReports = reports.filter { report ->
    (selectedFilter == null || report.threatLevel == selectedFilter) &&
    (searchQuery.isBlank() || report.inputUrl.contains(searchQuery, ignoreCase = true) || report.inputMessage.contains(searchQuery, ignoreCase = true))
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(contentPadding)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.FolderOpen,
              contentDescription = null,
              tint = AccentBlue,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Threat Vault",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = colors.onSurface
            )
          }

          if (reports.isNotEmpty()) {
            IconButton(
              onClick = onClearAll,
              modifier = Modifier.size(48.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DeleteSweep,
                contentDescription = "Clear all vault records",
                tint = StatusDanger,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Encrypted local vault storing threat intelligence reports and security audits.",
          fontFamily = SpaceGrotesk,
          fontSize = 12.sp,
          color = colors.onSurfaceVariant,
          lineHeight = 16.sp
        )
      }
    }

    // Filter Chips
    // Filter Chips in smooth horizontal LazyRow
    item {
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          FilterChip(
            selected = selectedFilter == null,
            onClick = { selectedFilter = null },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
              )
            },
            label = { Text("All (${reports.size})", fontFamily = SpaceGrotesk, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = AccentBlue,
              selectedLabelColor = colors.surface,
              containerColor = colors.surface,
              labelColor = colors.onSurfaceVariant
            )
          )
        }

        item {
          FilterChip(
            selected = selectedFilter == ThreatLevel.DANGER,
            onClick = { selectedFilter = ThreatLevel.DANGER },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
              )
            },
            label = { Text("Scams", fontFamily = SpaceGrotesk, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = StatusDanger,
              selectedLabelColor = colors.surface,
              containerColor = colors.surface,
              labelColor = StatusDanger
            )
          )
        }

        item {
          FilterChip(
            selected = selectedFilter == ThreatLevel.SUSPICIOUS,
            onClick = { selectedFilter = ThreatLevel.SUSPICIOUS },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
              )
            },
            label = { Text("Suspicious", fontFamily = SpaceGrotesk, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = StatusSuspicious,
              selectedLabelColor = colors.surface,
              containerColor = colors.surface,
              labelColor = StatusSuspicious
            )
          )
        }

        item {
          FilterChip(
            selected = selectedFilter == ThreatLevel.SAFE,
            onClick = { selectedFilter = ThreatLevel.SAFE },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
              )
            },
            label = { Text("Safe", fontFamily = SpaceGrotesk, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = StatusSafe,
              selectedLabelColor = colors.surface,
              containerColor = colors.surface,
              labelColor = StatusSafe
            )
          )
        }
      }
    }

    // Vault Items
    if (filteredReports.isEmpty()) {
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
            .padding(28.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.FolderOpen,
            contentDescription = null,
            tint = colors.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "No saved scans in this category.",
            fontFamily = SpaceGrotesk,
            fontSize = 13.sp,
            color = colors.onSurfaceVariant
          )
        }
      }
    } else {
      items(filteredReports, key = { it.id }) { report ->
        VaultReportItem(
          report = report,
          onSelect = { onSelectReport(report) },
          onDelete = { onDeleteReport(report.id) }
        )
      }
    }

    // Live Threat Radar Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Radar,
          contentDescription = null,
          tint = StatusDanger,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Active Scam Trends",
          fontFamily = SpaceGrotesk,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = colors.onSurface
        )
      }
    }

    items(radarAlerts, key = { it.id }) { alert ->
      RadarAlertCard(alert = alert)
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun VaultReportItem(
  report: ThreatAnalysisResult,
  onSelect: () -> Unit,
  onDelete: () -> Unit
) {
  val colors = MaterialTheme.colorScheme
  val isDark = colors.background.red < 0.5f
  val levelColor = when (report.threatLevel) {
    ThreatLevel.DANGER -> StatusDanger
    ThreatLevel.SUSPICIOUS -> StatusSuspicious
    ThreatLevel.SAFE -> StatusSafe
  }

  val dateStr = remember(report.timestamp) {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    sdf.format(Date(report.timestamp))
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(colors.surface)
      .border(1.dp, levelColor.copy(alpha = if (isDark) 0.4f else 0.25f), RoundedCornerShape(16.dp))
      .clickable(onClick = onSelect)
      .padding(14.dp)
      .testTag("vault_item_${report.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(levelColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = report.threatLevel.simpleTitle,
          fontFamily = SpaceGrotesk,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = levelColor
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "${report.overallScore}/100",
          fontFamily = JetBrainsMono,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = levelColor
        )
        Spacer(modifier = Modifier.width(6.dp))
        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(44.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = colors.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    if (report.inputUrl.isNotBlank()) {
      Text(
        text = report.inputUrl,
        fontFamily = JetBrainsMono,
        fontSize = 11.sp,
        color = AccentBlue,
        maxLines = 1
      )
      Spacer(modifier = Modifier.height(2.dp))
    }

    Text(
      text = report.inputMessage.take(80) + if (report.inputMessage.length > 80) "..." else "",
      fontFamily = SpaceGrotesk,
      fontSize = 11.sp,
      color = colors.onSurfaceVariant,
      maxLines = 2,
      lineHeight = 15.sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = dateStr,
        fontFamily = SpaceGrotesk,
        fontSize = 10.sp,
        color = colors.onSurfaceVariant
      )
      Text(
        text = "Tap to review →",
        fontFamily = SpaceGrotesk,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = AccentBlue
      )
    }
  }
}

@Composable
private fun RadarAlertCard(alert: RadarTrend) {
  val colors = MaterialTheme.colorScheme

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(colors.surface)
      .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = alert.title,
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = colors.onSurface
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.TrendingUp,
          contentDescription = null,
          tint = StatusDanger,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = alert.surgePercentage,
          fontFamily = SpaceGrotesk,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = StatusDanger
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = alert.description,
      fontFamily = SpaceGrotesk,
      fontSize = 11.sp,
      color = colors.onSurfaceVariant,
      lineHeight = 15.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(colors.surfaceVariant)
        .border(1.dp, colors.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
        .padding(8.dp),
      verticalAlignment = Alignment.Top
    ) {
      Text(
        text = "Rule: ",
        fontFamily = SpaceGrotesk,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = StatusSafe
      )
      Text(
        text = alert.prevention,
        fontFamily = SpaceGrotesk,
        fontSize = 10.sp,
        color = colors.onSurface,
        lineHeight = 14.sp
      )
    }
  }
}

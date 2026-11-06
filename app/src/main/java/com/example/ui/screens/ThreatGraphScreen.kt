package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.analyzer.DomainAnalysis
import com.example.data.model.ThreatAnalysisResult
import com.example.ui.components.EvidenceMatrixCard
import com.example.ui.components.IdentityConsistencyCard
import com.example.ui.components.InfrastructureGraphView
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DevModeAmber
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSuspicious

@Composable
fun ThreatGraphScreen(
  currentResult: ThreatAnalysisResult?,
  defangInput: String,
  defangOutput: String,
  typosquatInput: String,
  typosquatResult: DomainAnalysis?,
  isDevMode: Boolean,
  onDefangInputChanged: (String) -> Unit,
  onDefangText: () -> Unit,
  onRefangText: () -> Unit,
  onTyposquatInputChanged: (String) -> Unit,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val colors = MaterialTheme.colorScheme
  val isDark = colors.background.red < 0.5f

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(contentPadding)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Title Card
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isDevMode) Icons.Default.Build else Icons.Default.Lan,
            contentDescription = null,
            tint = if (isDevMode) DevModeAmber else AccentBlue,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isDevMode) "POWER USER DEV TOOLS" else "Link Inspection Tools",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = colors.onSurface
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (isDevMode)
            "Advanced network telemetry, multi-vendor intelligence, DNS lookups, BGP ASN routing, and string defangers."
          else
            "Tools to test lookalike website names and safely share suspicious links without clicking them.",
          fontFamily = SpaceGrotesk,
          fontSize = 12.sp,
          color = colors.onSurfaceVariant,
          lineHeight = 16.sp
        )
      }
    }

    // Active Investigation: Identity vs. Infrastructure Card
    if (currentResult != null && currentResult.identityConsistency != null) {
      item {
        IdentityConsistencyCard(check = currentResult.identityConsistency)
      }
    }

    // Active Investigation: Multi-Source Evidence Matrix Card
    if (currentResult != null && currentResult.evidenceMatrix.isNotEmpty()) {
      item {
        EvidenceMatrixCard(evidenceList = currentResult.evidenceMatrix)
      }
    }

    // Active Investigation Threat Graph
    if (currentResult != null) {
      item {
        InfrastructureGraphView(
          nodes = currentResult.infrastructurePath,
          isDevMode = isDevMode
        )
      }
    }

    // Tool 1: URL Defanger & Sanitizer
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(colors.surface)
          .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
          .padding(18.dp)
          .testTag("url_defanger_card")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Make Links Safe to Share (Defanger)",
            fontFamily = SpaceGrotesk,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
          )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Turns clickable danger links into safe non-clickable text (hxxp://evil[.]com) so you can safely send it for reporting.",
          fontFamily = SpaceGrotesk,
          fontSize = 12.sp,
          color = colors.onSurfaceVariant,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = defangInput,
          onValueChange = onDefangInputChanged,
          modifier = Modifier.fillMaxWidth(),
          label = { Text("Input URL", fontFamily = SpaceGrotesk, fontSize = 12.sp) },
          placeholder = { Text("https://suspicious-site.com", fontFamily = SpaceGrotesk, fontSize = 11.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = colors.outline,
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface,
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onDefangText,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
              containerColor = AccentBlue,
              contentColor = colors.surface
            ),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Defang [ . ]", fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onRefangText,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface)
          ) {
            Text("Restore URL", fontFamily = SpaceGrotesk, fontSize = 12.sp)
          }
        }

        if (defangOutput.isNotBlank()) {
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(colors.surfaceVariant)
              .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = defangOutput,
              fontFamily = JetBrainsMono,
              fontSize = 12.sp,
              color = AccentBlue,
              modifier = Modifier.weight(1f)
            )
            IconButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Defanged URL", defangOutput)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Copied safe link text", Toast.LENGTH_SHORT).show()
              }
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = AccentBlue,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }

    // Tool 2: Domain Homoglyph & Fake Brand Detector
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(colors.surface)
          .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
          .padding(18.dp)
          .testTag("typosquat_checker_card")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Dns,
            contentDescription = null,
            tint = StatusSuspicious,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Fake Lookalike Domain Checker",
            fontFamily = SpaceGrotesk,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
          )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Checks if a domain is disguising letters (like Cyrillic 'а' or '0' for 'o') to pretend to be a real brand.",
          fontFamily = SpaceGrotesk,
          fontSize = 12.sp,
          color = colors.onSurfaceVariant,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = typosquatInput,
          onValueChange = onTyposquatInputChanged,
          modifier = Modifier.fillMaxWidth(),
          label = { Text("Enter Website Address", fontFamily = SpaceGrotesk, fontSize = 12.sp) },
          placeholder = { Text("e.g. paypa1.com, sbi-kyc.top, chase-security.xyz", fontFamily = SpaceGrotesk, fontSize = 11.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = StatusSuspicious,
            unfocusedBorderColor = colors.outline,
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface,
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant
          ),
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = StatusSuspicious,
              modifier = Modifier.size(18.dp)
            )
          }
        )

        if (typosquatResult != null) {
          val result = typosquatResult
          val resultColor = when {
            result.riskScore > 40 -> StatusDanger
            result.riskScore > 15 -> StatusSuspicious
            else -> StatusSafe
          }

          Spacer(modifier = Modifier.height(12.dp))

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(colors.surfaceVariant)
              .border(1.dp, resultColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${result.riskScore}/100 Risk",
                fontFamily = SpaceGrotesk,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = resultColor
              )

              if (result.targetBrandImpersonated != null) {
                Text(
                  text = "Impersonating: ${result.targetBrandImpersonated}",
                  fontFamily = SpaceGrotesk,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = StatusDanger
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (result.redFlags.isEmpty()) {
              Text(
                text = "✓ No lookalike characters or brand spoofing detected.",
                fontFamily = SpaceGrotesk,
                fontSize = 12.sp,
                color = StatusSafe
              )
            } else {
              result.redFlags.forEach { flag ->
                Text(
                  text = "• $flag",
                  fontFamily = SpaceGrotesk,
                  fontSize = 12.sp,
                  color = resultColor,
                  modifier = Modifier.padding(vertical = 1.dp)
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.analyzer.SafeAnalysisEngine
import com.example.data.analyzer.TyposquattingDetector
import com.example.data.model.ThreatAnalysisResult
import com.example.data.model.ThreatLevel
import com.example.service.ScreenLinkAccessibilityService
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DevModeAmber
import com.example.ui.theme.DevModeCyan
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSuspicious
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

data class ScannedLinkEntry(
  val url: String,
  val result: ThreatAnalysisResult
)

@Composable
fun AssistantOverlaySheet(
  onDismiss: () -> Unit,
  onOpenInFullApp: (String) -> Unit,
  modifier: Modifier = Modifier,
  initialSharedText: String = ""
) {
  val context = LocalContext.current
  val colors = MaterialTheme.colorScheme
  val isDark = colors.background.red < 0.5f
  val coroutineScope = rememberCoroutineScope()

  var isScanning by remember { mutableStateOf(true) }
  var scannedEntries by remember { mutableStateOf<List<ScannedLinkEntry>>(emptyList()) }
  var manualInputText by remember { mutableStateOf("") }
  var rawScannedSnippet by remember { mutableStateOf("") }
  var showManualInput by remember { mutableStateOf(false) }

  var isAccessibilityActive by remember {
    mutableStateOf(ScreenLinkAccessibilityService.isAccessibilityServiceEnabled(context))
  }
  var screenScanSource by remember { mutableStateOf("Screen & Clipboard") }

  // Perform real link discovery and analysis without fake placeholders
  fun performScan(textToScan: String, directUrls: List<String> = emptyList()) {
    coroutineScope.launch {
      isScanning = true
      rawScannedSnippet = textToScan.trim().take(120)

      val combinedUrls = if (directUrls.isNotEmpty()) {
        directUrls.distinct()
      } else if (textToScan.isNotBlank()) {
        SafeAnalysisEngine.extractAllUrls(textToScan).distinct()
      } else {
        emptyList()
      }

      if (combinedUrls.isEmpty()) {
        scannedEntries = emptyList()
        isScanning = false
        showManualInput = true
        return@launch
      }

      // Analyze every detected URL in isolated safe sandbox
      val results = combinedUrls.map { url ->
        async {
          val res = SafeAnalysisEngine.analyze("Screen link scan", url)
          ScannedLinkEntry(url = url, result = res)
        }
      }.awaitAll()

      scannedEntries = results
      isScanning = false
    }
  }

  // Initial scan on launch: checks visible screen nodes first, then shared intent
  LaunchedEffect(Unit) {
    isAccessibilityActive = ScreenLinkAccessibilityService.isAccessibilityServiceEnabled(context)

    // 1. Scan links directly off active screen via Accessibility Service
    val visibleScreenLinks = ScreenLinkAccessibilityService.getCurrentlyVisibleScreenLinks()

    when {
      visibleScreenLinks.isNotEmpty() -> {
        screenScanSource = "Visible Screen (${visibleScreenLinks.size} on-screen link${if (visibleScreenLinks.size > 1) "s" else ""} detected)"
        performScan(textToScan = "", directUrls = visibleScreenLinks)
      }
      initialSharedText.isNotBlank() -> {
        screenScanSource = "Shared App Content"
        manualInputText = initialSharedText
        performScan(initialSharedText)
      }
      else -> {
        // No links on current screen: do NOT sneakily scan clipboard
        isScanning = false
        showManualInput = true
      }
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "gemini_glow")
  val glowOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow"
  )

  // Backdrop Scrim
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.6f))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onDismiss
      ),
    contentAlignment = Alignment.BottomCenter
  ) {
    // Floating Gemini-style Bottom Sheet
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
        .background(colors.surface)
        .border(1.dp, colors.outline, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = {}
        )
        .padding(horizontal = 20.dp, vertical = 16.dp)
        .testTag("assistant_overlay_sheet")
    ) {
      // Gemini Gradient Glow Top Indicator Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp)
          .clip(CircleShape)
          .background(
            Brush.horizontalGradient(
              listOf(
                AccentBlue,
                DevModeCyan,
                DevModeAmber,
                AccentBlue
              )
            )
          )
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Header row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
            painter = painterResource(id = R.drawable.ic_scamshield_logo),
            contentDescription = "ScamShield Logo",
            modifier = Modifier
              .size(24.dp)
              .clip(RoundedCornerShape(6.dp))
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "ScamShield Screen Scanner",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = colors.onSurface
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clipItem = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
              if (clipItem.isNotBlank()) {
                manualInputText = clipItem
                performScan(clipItem)
                Toast.makeText(context, "Scanning clipboard...", Toast.LENGTH_SHORT).show()
              } else {
                Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Re-scan Clipboard",
              tint = AccentBlue,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = colors.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (isScanning) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          LinearProgressIndicator(
            modifier = Modifier
              .fillMaxWidth(0.7f)
              .height(4.dp)
              .clip(CircleShape),
            color = AccentBlue,
            trackColor = colors.surfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Extracting and analyzing all links safely...",
            fontFamily = SpaceGrotesk,
            fontSize = 13.sp,
            color = colors.onSurfaceVariant
          )
        }
      } else if (scannedEntries.isNotEmpty()) {
        // Multi-link finding results header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Link,
              contentDescription = null,
              tint = AccentBlue,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Found ${scannedEntries.size} link${if (scannedEntries.size > 1) "s" else ""}",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = colors.onSurface
            )
          }

          val maxScore = scannedEntries.maxOfOrNull { it.result.overallScore } ?: 0
          val topLevel = when {
            maxScore >= 70 -> ThreatLevel.DANGER
            maxScore >= 35 -> ThreatLevel.SUSPICIOUS
            else -> ThreatLevel.SAFE
          }
          val topColor = when (topLevel) {
            ThreatLevel.DANGER -> StatusDanger
            ThreatLevel.SUSPICIOUS -> StatusSuspicious
            ThreatLevel.SAFE -> StatusSafe
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(topLevel.getContainerColor(isDark))
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = "Peak Risk: $maxScore/100",
              fontFamily = JetBrainsMono,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = topColor
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dynamic list of ALL scanned links
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 340.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(scannedEntries) { entry ->
            val result = entry.result
            val levelColor = when (result.threatLevel) {
              ThreatLevel.DANGER -> StatusDanger
              ThreatLevel.SUSPICIOUS -> StatusSuspicious
              ThreatLevel.SAFE -> StatusSafe
            }

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceVariant)
                .border(1.dp, levelColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(12.dp)
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
                    text = result.threatLevel.simpleTitle,
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = levelColor
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(result.threatLevel.getContainerColor(isDark))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "${result.overallScore}/100",
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = levelColor
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = entry.url,
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "${result.attackVector} • ${result.threatLevel.simpleSubtitle}",
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                color = colors.onSurfaceVariant,
                lineHeight = 15.sp
              )

              if (result.identityConsistency != null) {
                Spacer(modifier = Modifier.height(4.dp))
                val isMatch = result.identityConsistency.isDomainMatched
                val matchColor = if (isMatch) StatusSafe else StatusDanger
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(matchColor.copy(alpha = 0.12f))
                    .border(1.dp, matchColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = if (isMatch) "✓ Identity Matched: ${result.identityConsistency.claimedBrand}"
                    else "⚠️ Identity Mismatch: Claims ${result.identityConsistency.claimedBrand} but destination is ${result.identityConsistency.destinationDomain}",
                    fontFamily = SpaceGrotesk,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = matchColor
                  )
                }
              }

              // Multi-source intelligence sources badge
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                val dangerCount = result.evidenceMatrix.count { it.status == com.example.data.model.EvidenceStatus.DANGER }
                val badgeColor = if (dangerCount > 0) StatusDanger else StatusSafe
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor.copy(alpha = 0.12f))
                    .border(0.5.dp, badgeColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "Sources: PhishTank • VirusTotal • DNS Root • urlscan (${if (dangerCount > 0) "$dangerCount Threats Flagged" else "Verified Clean"})",
                    fontFamily = JetBrainsMono,
                    fontSize = 8.sp,
                    color = badgeColor,
                    fontWeight = FontWeight.Medium
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = {
                    onOpenInFullApp(entry.url)
                    onDismiss()
                  },
                  modifier = Modifier.weight(1f),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor = colors.surface
                  ),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Deep Audit", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                  onClick = {
                    val defanged = TyposquattingDetector.defangUrl(entry.url)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Sanitized Link", defanged)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied safe link text", Toast.LENGTH_SHORT).show()
                  },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface)
                ) {
                  Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Copy Safe Text", fontFamily = SpaceGrotesk, fontSize = 11.sp)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Toggle to scan other text
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showManualInput = !showManualInput }
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (showManualInput) "Hide text input" else "Scan another message or text",
            fontFamily = SpaceGrotesk,
            fontSize = 12.sp,
            color = AccentBlue,
            fontWeight = FontWeight.SemiBold
          )
        }
      } else {
        // Zero links detected state - honest and informative without placeholders
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceVariant)
            .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.WarningAmber,
            contentDescription = null,
            tint = DevModeAmber,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "No Links Detected on Current Screen",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = colors.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "The visible screen contains no external web addresses or suspicious links.",
            fontFamily = SpaceGrotesk,
            fontSize = 12.sp,
            color = colors.onSurfaceVariant,
            lineHeight = 16.sp
          )

          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
          val clipItem = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
          if (clipItem.isNotBlank() && (clipItem.contains("http") || clipItem.contains(".com") || clipItem.contains("."))) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
              onClick = {
                manualInputText = clipItem
                performScan(clipItem)
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue)
            ) {
              Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Analyze Copied Clipboard Link", fontFamily = SpaceGrotesk, fontSize = 12.sp)
            }
          }
        }
      }

      // Collapsible Manual Input & Paste Bar
      AnimatedVisibility(visible = showManualInput || scannedEntries.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
        ) {
          OutlinedTextField(
            value = manualInputText,
            onValueChange = { manualInputText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Paste message or text with links", fontFamily = SpaceGrotesk, fontSize = 12.sp) },
            placeholder = { Text("e.g. Check tracking at https://xyz.site or verify...", fontFamily = SpaceGrotesk, fontSize = 11.sp) },
            minLines = 2,
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AccentBlue,
              unfocusedBorderColor = colors.outline,
              focusedContainerColor = colors.surfaceVariant,
              unfocusedContainerColor = colors.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
              IconButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clipItem = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                  if (clipItem.isNotBlank()) {
                    manualInputText = clipItem
                    Toast.makeText(context, "Pasted from clipboard", Toast.LENGTH_SHORT).show()
                  }
                }
              ) {
                Icon(
                  imageVector = Icons.Default.ContentPaste,
                  contentDescription = "Paste",
                  tint = AccentBlue,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          )

          Spacer(modifier = Modifier.height(8.dp))

          Button(
            onClick = {
              if (manualInputText.isNotBlank()) {
                performScan(manualInputText)
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = AccentBlue,
              contentColor = colors.surface
            ),
            enabled = manualInputText.isNotBlank() && !isScanning
          ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Find & Scan All Links", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
    }
  }
}

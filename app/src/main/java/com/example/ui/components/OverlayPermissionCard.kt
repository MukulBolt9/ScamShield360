package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ScreenSearchDesktop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.SpaceGrotesk

@Composable
fun OverlayPermissionCard(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val colors = MaterialTheme.colorScheme

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(colors.surface)
      .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
      .padding(16.dp)
      .testTag("screen_scan_accessibility_card")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.ScreenSearchDesktop,
          contentDescription = null,
          tint = AccentBlue,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Enable Live Screen Scanning",
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = colors.onSurface
        )
      }

      IconButton(
        onClick = onDismiss,
        modifier = Modifier.size(24.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Dismiss",
          tint = colors.onSurfaceVariant,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "Turn on ScamShield Screen Reader in Accessibility Settings to automatically extract and verify incoming links directly off your screen inside WhatsApp, Messages, or Chrome.",
      fontFamily = SpaceGrotesk,
      fontSize = 12.sp,
      color = colors.onSurfaceVariant,
      lineHeight = 16.sp
    )

    Spacer(modifier = Modifier.height(12.dp))

    Button(
      onClick = {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .testTag("enable_accessibility_button"),
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = AccentBlue,
        contentColor = colors.surface
      )
    ) {
      Text(
        text = "Open Accessibility Settings",
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
    }
  }
}

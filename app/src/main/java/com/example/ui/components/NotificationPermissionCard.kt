package com.example.ui.components

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
import androidx.compose.material.icons.filled.NotificationsActive
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.SpaceGrotesk

@Composable
fun NotificationPermissionCard(
  onRequestPermission: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colorScheme

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(colors.surfaceVariant)
      .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
      .padding(16.dp)
      .testTag("notification_permission_card")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.NotificationsActive,
          contentDescription = null,
          tint = AccentBlue,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Enable Live Protection & Now Bar",
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
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
      text = "Allow notifications so ScamShield can show live link scanning alerts and the Samsung Now Bar live meter on your lockscreen.",
      fontFamily = SpaceGrotesk,
      fontSize = 12.sp,
      color = colors.onSurfaceVariant,
      lineHeight = 16.sp
    )

    Spacer(modifier = Modifier.height(12.dp))

    Button(
      onClick = onRequestPermission,
      modifier = Modifier
        .fillMaxWidth()
        .height(42.dp)
        .testTag("grant_notification_permission_button"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = AccentBlue,
        contentColor = colors.surface
      )
    ) {
      Text(
        text = "Allow Live Notifications",
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
    }
  }
}

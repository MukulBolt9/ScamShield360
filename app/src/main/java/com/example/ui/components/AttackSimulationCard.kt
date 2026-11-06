package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttackStep
import com.example.data.model.ThreatLevel
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuspicious

@Composable
fun AttackSimulationCard(
  steps: List<AttackStep>,
  threatLevel: ThreatLevel,
  isDevMode: Boolean,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(true) }
  val colors = MaterialTheme.colorScheme

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(colors.surface)
      .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
      .padding(18.dp)
      .testTag("attack_simulation_card")
  ) {
    // Slogan Banner
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(colors.surfaceVariant)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Shield,
        contentDescription = null,
        tint = AccentBlue,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "“You don't need to find out by clicking it.”",
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic,
        fontSize = 12.sp,
        color = colors.onSurface
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Header Toggle
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { isExpanded = !isExpanded },
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "What would happen if you opened this?",
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = colors.onSurface
        )
        Text(
          text = "Predicted step-by-step path based on safe testing",
          fontFamily = SpaceGrotesk,
          fontSize = 11.sp,
          color = colors.onSurfaceVariant
        )
      }

      Icon(
        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
        contentDescription = if (isExpanded) "Collapse" else "Expand",
        tint = colors.onSurfaceVariant
      )
    }

    AnimatedVisibility(
      visible = isExpanded,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp)
      ) {
        steps.forEachIndexed { index, step ->
          SimpleStepItem(
            step = step,
            isLast = index == steps.size - 1,
            isDevMode = isDevMode
          )
        }
      }
    }
  }
}

@Composable
private fun SimpleStepItem(
  step: AttackStep,
  isLast: Boolean,
  isDevMode: Boolean
) {
  val colors = MaterialTheme.colorScheme
  val stageColor = when (step.stage) {
    "Bait" -> StatusSuspicious
    "Routing" -> AccentBlue
    "Impersonation" -> StatusDanger
    "Theft" -> StatusDanger
    else -> colors.onSurface
  }

  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.width(26.dp)
    ) {
      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(CircleShape)
          .background(stageColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "${step.stepNumber}",
          fontFamily = SpaceGrotesk,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = stageColor
        )
      }

      if (!isLast) {
        Box(
          modifier = Modifier
            .width(1.dp)
            .height(42.dp)
            .background(colors.outline)
        )
      }
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(colors.surfaceVariant)
        .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
        .padding(10.dp)
    ) {
      Text(
        text = step.title,
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = colors.onSurface
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = step.description,
        fontFamily = SpaceGrotesk,
        fontSize = 11.sp,
        color = colors.onSurfaceVariant,
        lineHeight = 15.sp
      )

      if (isDevMode) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Dev Trace: ${step.technicalIndicator}",
          fontFamily = JetBrainsMono,
          fontSize = 9.sp,
          color = AccentBlue
        )
      }
    }
  }

  if (!isLast) {
    Spacer(modifier = Modifier.height(4.dp))
  }
}

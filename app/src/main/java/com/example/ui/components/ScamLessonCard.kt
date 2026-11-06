package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
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
import com.example.data.model.ScamLesson
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.SpaceGrotesk

@Composable
fun ScamLessonCard(
  lessons: List<ScamLesson>,
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
      .testTag("scam_lesson_card")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(AccentBlue.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.School,
          contentDescription = null,
          tint = AccentBlue,
          modifier = Modifier.size(16.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "Quick Scam Lesson",
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = colors.onSurface
        )
        Text(
          text = "How you can spot this yourself next time",
          fontFamily = SpaceGrotesk,
          fontSize = 11.sp,
          color = colors.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    lessons.forEachIndexed { index, lesson ->
      LessonItemRow(lesson = lesson, index = index + 1)
      if (index < lessons.size - 1) {
        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}

@Composable
private fun LessonItemRow(lesson: ScamLesson, index: Int) {
  val colors = MaterialTheme.colorScheme

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(colors.surfaceVariant)
      .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Rule #$index: ${lesson.redFlagTitle}",
        fontFamily = SpaceGrotesk,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = colors.onSurface
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = lesson.explanation,
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
        .background(colors.surface)
        .border(1.dp, colors.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
        .padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.Top
    ) {
      Icon(
        imageVector = Icons.Default.Lightbulb,
        contentDescription = null,
        tint = AccentBlue,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Pro Tip: ${lesson.proTip}",
        fontFamily = SpaceGrotesk,
        fontSize = 11.sp,
        color = AccentBlue,
        lineHeight = 15.sp
      )
    }
  }
}

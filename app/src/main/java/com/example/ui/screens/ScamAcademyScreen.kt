package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
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
import com.example.ui.QuizQuestion
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSafe
import com.example.ui.theme.StatusSuspicious

@Composable
fun ScamAcademyScreen(
  questions: List<QuizQuestion>,
  userAnswers: Map<Int, Int>,
  onAnswerSelected: (Int, Int) -> Unit,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier
) {
  val colors = MaterialTheme.colorScheme

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
          .clip(RoundedCornerShape(18.dp))
          .background(colors.surface)
          .border(1.dp, colors.outline, RoundedCornerShape(18.dp))
          .padding(18.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(AccentBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.School,
              contentDescription = null,
              tint = AccentBlue,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Scam Defense Academy",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = colors.onSurface
            )
            Text(
              text = "Human Shield Training",
              fontFamily = SpaceGrotesk,
              fontSize = 11.sp,
              color = AccentBlue,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Learn how modern scams work and test your instincts so you can spot deceptive red flags before tapping.",
          fontFamily = SpaceGrotesk,
          fontSize = 12.sp,
          color = colors.onSurfaceVariant,
          lineHeight = 17.sp
        )
      }
    }

    // Academy Pillar 1: Anatomy of Modern Multi-Stage Scams
    item {
      CyberAcademyPillar(
        pillarNumber = "1",
        title = "The Multi-Step Trap",
        subtitle = "Message → Link → Redirect → Fake Site → Credential Theft",
        description = "Modern scammers rarely send direct links to fake pages because filters block them. Instead, they bounce your browser across URL shorteners or intermediate domains before dropping you onto a fake login form."
      )
    }

    // Academy Pillar 2: The Art of Root Domain Inspection
    item {
      CyberAcademyPillar(
        pillarNumber = "2",
        title = "How to Spot the Real Website",
        subtitle = "Don't be fooled by names in the beginning of a link",
        description = "Look immediately to the left of the FIRST single slash '/' in the web address. If the link says 'https://chase.com.security-verify.xyz/login', the real website is 'security-verify.xyz', NOT Chase Bank."
      )
    }

    // Academy Pillar 3: Urgency Manipulation
    item {
      CyberAcademyPillar(
        pillarNumber = "3",
        title = "The Fake Emergency",
        subtitle = "Why scammers create 24-hour deadlines",
        description = "Panic makes people act before thinking. If a text claims your package is returning in 2 hours or your bank account is frozen, pause. True banks never give short countdowns over SMS."
      )
    }

    // Academy Pillar 4: Identity Consistency (RSBVP Core Principle)
    item {
      CyberAcademyPillar(
        pillarNumber = "4",
        title = "Identity Consistency",
        subtitle = "Visual similarity ≠ legitimacy",
        description = "Even if a website's logo, colors, and layout look 100% genuine like SBI or Chase, check where the link goes. If the domain doesn't match official cryptographic registration, it is an impersonation trap."
      )
    }

    // Interactive Quiz Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Quick Quiz: Test Your Instincts",
          fontFamily = SpaceGrotesk,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = colors.onSurface
        )
        Text(
          text = "${userAnswers.size}/${questions.size} Answered",
          fontFamily = JetBrainsMono,
          fontSize = 11.sp,
          color = AccentBlue,
          fontWeight = FontWeight.Bold
        )
      }
    }

    questions.forEachIndexed { qIndex, question ->
      item {
        QuizQuestionCard(
          questionIndex = qIndex,
          question = question,
          selectedAnswer = userAnswers[qIndex],
          onSelectOption = { onAnswerSelected(qIndex, it) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun CyberAcademyPillar(
  pillarNumber: String,
  title: String,
  subtitle: String,
  description: String
) {
  val colors = MaterialTheme.colorScheme

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(colors.surface)
      .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
      .padding(16.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(20.dp)
          .clip(CircleShape)
          .background(AccentBlue.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = pillarNumber,
          fontFamily = JetBrainsMono,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = AccentBlue
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Pillar $pillarNumber: $title",
        fontFamily = SpaceGrotesk,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = AccentBlue
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = subtitle,
      fontFamily = SpaceGrotesk,
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      color = colors.onSurface
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = description,
      fontFamily = SpaceGrotesk,
      fontSize = 12.sp,
      color = colors.onSurfaceVariant,
      lineHeight = 17.sp
    )
  }
}

@Composable
private fun QuizQuestionCard(
  questionIndex: Int,
  question: QuizQuestion,
  selectedAnswer: Int?,
  onSelectOption: (Int) -> Unit
) {
  val colors = MaterialTheme.colorScheme
  val hasAnswered = selectedAnswer != null
  val isCorrect = selectedAnswer == question.correctIndex

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(colors.surface)
      .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
      .padding(16.dp)
      .testTag("quiz_card_$questionIndex")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "QUESTION ${questionIndex + 1}",
        fontFamily = JetBrainsMono,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = StatusSuspicious
      )
      if (hasAnswered) {
        Text(
          text = if (isCorrect) "VERIFIED" else "FAILED",
          fontFamily = JetBrainsMono,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = if (isCorrect) StatusSafe else StatusDanger
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = question.question,
      fontFamily = SpaceGrotesk,
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      color = colors.onSurface,
      lineHeight = 18.sp
    )

    Spacer(modifier = Modifier.height(12.dp))

    question.options.forEachIndexed { optIndex, optionText ->
      val isThisOptionSelected = selectedAnswer == optIndex
      val isThisOptionCorrect = optIndex == question.correctIndex

      val optBg = when {
        !hasAnswered -> colors.surfaceVariant
        isThisOptionCorrect -> StatusSafe.copy(alpha = 0.15f)
        isThisOptionSelected -> StatusDanger.copy(alpha = 0.15f)
        else -> colors.surfaceVariant
      }

      val optBorder = when {
        !hasAnswered && isThisOptionSelected -> AccentBlue
        !hasAnswered -> colors.outline
        isThisOptionCorrect -> StatusSafe
        isThisOptionSelected -> StatusDanger
        else -> colors.outline
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(optBg)
          .border(1.dp, optBorder, RoundedCornerShape(12.dp))
          .clickable(enabled = !hasAnswered) { onSelectOption(optIndex) }
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(1.5.dp, if (isThisOptionSelected) AccentBlue else colors.outline, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          if (isThisOptionSelected) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (hasAnswered && isCorrect) StatusSafe else AccentBlue)
            )
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
          text = optionText,
          fontFamily = SpaceGrotesk,
          fontSize = 12.sp,
          color = colors.onSurface,
          lineHeight = 16.sp,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
    }

    if (hasAnswered) {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(if (isCorrect) StatusSafe.copy(alpha = 0.12f) else StatusDanger.copy(alpha = 0.12f))
          .border(1.dp, if (isCorrect) StatusSafe.copy(alpha = 0.3f) else StatusDanger.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
          .padding(12.dp),
        verticalAlignment = Alignment.Top
      ) {
        Icon(
          imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Warning,
          contentDescription = null,
          tint = if (isCorrect) StatusSafe else StatusDanger,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = (if (isCorrect) "Correct! " else "Incorrect. ") + question.explanation,
          fontFamily = SpaceGrotesk,
          fontSize = 11.sp,
          color = colors.onSurface,
          lineHeight = 16.sp
        )
      }
    }
  }
}

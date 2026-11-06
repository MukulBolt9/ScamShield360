package com.example.data.model

enum class FindingSeverity(val label: String) {
  CRITICAL("CRITICAL"),
  HIGH("HIGH"),
  MEDIUM("MEDIUM"),
  LOW("LOW"),
  INFO("INFO")
}

data class AiFinding(
  val title: String,
  val description: String,
  val severity: FindingSeverity,
  val category: String, // "Message", "Link", "Network", "Behavior"
  val iconType: String = "warning"
)

data class AttackStep(
  val stepNumber: Int,
  val title: String,
  val description: String,
  val technicalIndicator: String,
  val stage: String // "Bait", "Routing", "Impersonation", "Theft"
)

data class ScamLesson(
  val id: Int,
  val redFlagTitle: String,
  val explanation: String,
  val proTip: String
)

data class ScamPreset(
  val id: String,
  val title: String,
  val category: String,
  val snippet: String,
  val message: String,
  val url: String,
  val expectedRisk: ThreatLevel
)

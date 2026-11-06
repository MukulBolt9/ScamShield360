package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ThreatLevel

@Entity(tableName = "threat_reports")
data class ThreatReportEntity(
  @PrimaryKey
  val id: String,
  val timestamp: Long,
  val inputMessage: String,
  val inputUrl: String,
  val overallScore: Int,
  val threatLevel: ThreatLevel,
  val verdictTitle: String,
  val verdictSummary: String,
  val attackVector: String,
  val messageScore: Int,
  val linkScore: Int,
  val websiteScore: Int,
  val behaviorScore: Int,
  val findingsJson: String,
  val attackStepsJson: String,
  val infraNodesJson: String,
  val redirectChainJson: String,
  val lessonsJson: String,
  val actionsJson: String,
  val isBookmarked: Boolean = false
)

package com.example.data.model

data class LayerScores(
  val messageRisk: Int,    // 0..100
  val linkRisk: Int,       // 0..100
  val websiteRisk: Int,    // 0..100
  val behaviorRisk: Int    // 0..100
)

data class ThreatAnalysisResult(
  val id: String,
  val timestamp: Long,
  val inputMessage: String,
  val inputUrl: String,
  val overallScore: Int,
  val threatLevel: ThreatLevel,
  val verdictTitle: String,
  val verdictSummary: String,
  val attackVector: String, // e.g. "Credential Harvesting & Brand Spoofing", "Delivery Smishing", "Cryptocurrency Task Fraud"
  val layerScores: LayerScores,
  val infrastructurePath: List<InfrastructureNode>,
  val redirectChain: List<RedirectHop>,
  val aiFindings: List<AiFinding>,
  val predictedAttackPath: List<AttackStep>,
  val scamLessons: List<ScamLesson>,
  val recommendedActions: List<String>,
  val evidenceMatrix: List<EvidenceItem> = emptyList(),
  val identityConsistency: IdentityConsistencyCheck? = null,
  val maxCheckTelemetry: com.example.data.analyzer.MaxCheckTelemetry? = null,
  val isPrivacyModeActive: Boolean = true,
  val isLiveProbed: Boolean = false,
  val rawTelemetry: String = ""
)

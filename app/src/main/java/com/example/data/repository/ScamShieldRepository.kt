package com.example.data.repository

import com.example.data.analyzer.SafeAnalysisEngine
import com.example.data.local.Converters
import com.example.data.local.ThreatReportDao
import com.example.data.local.ThreatReportEntity
import com.example.data.model.LayerScores
import com.example.data.model.ThreatAnalysisResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScamShieldRepository(private val dao: ThreatReportDao) {

  val savedReports: Flow<List<ThreatAnalysisResult>> = dao.getAllReports().map { entities ->
    entities.map { entityToResult(it) }
  }

  suspend fun analyze(message: String, url: String, isPrivacyMode: Boolean = true): ThreatAnalysisResult {
    val result = SafeAnalysisEngine.analyze(message, url, isPrivacyMode)
    dao.insertReport(resultToEntity(result))
    return result
  }

  suspend fun saveReport(result: ThreatAnalysisResult) {
    dao.insertReport(resultToEntity(result))
  }

  suspend fun deleteReport(id: String) {
    val entity = dao.getReportById(id)
    if (entity != null) {
      dao.deleteReport(entity)
    }
  }

  suspend fun clearAllReports() {
    dao.deleteAll()
  }

  suspend fun getReportCount(): Int = dao.getReportCount()

  private fun resultToEntity(result: ThreatAnalysisResult): ThreatReportEntity {
    return ThreatReportEntity(
      id = result.id,
      timestamp = result.timestamp,
      inputMessage = result.inputMessage,
      inputUrl = result.inputUrl,
      overallScore = result.overallScore,
      threatLevel = result.threatLevel,
      verdictTitle = result.verdictTitle,
      verdictSummary = result.verdictSummary,
      attackVector = result.attackVector,
      messageScore = result.layerScores.messageRisk,
      linkScore = result.layerScores.linkRisk,
      websiteScore = result.layerScores.websiteRisk,
      behaviorScore = result.layerScores.behaviorRisk,
      findingsJson = Converters.serializeFindings(result.aiFindings),
      attackStepsJson = Converters.serializeAttackSteps(result.predictedAttackPath),
      infraNodesJson = Converters.serializeInfraNodes(result.infrastructurePath),
      redirectChainJson = Converters.serializeRedirectChain(result.redirectChain),
      lessonsJson = Converters.serializeLessons(result.scamLessons),
      actionsJson = Converters.serializeFindings(emptyList()), // reusable or simple
      isBookmarked = false
    )
  }

  private fun entityToResult(entity: ThreatReportEntity): ThreatAnalysisResult {
    return ThreatAnalysisResult(
      id = entity.id,
      timestamp = entity.timestamp,
      inputMessage = entity.inputMessage,
      inputUrl = entity.inputUrl,
      overallScore = entity.overallScore,
      threatLevel = entity.threatLevel,
      verdictTitle = entity.verdictTitle,
      verdictSummary = entity.verdictSummary,
      attackVector = entity.attackVector,
      layerScores = LayerScores(
        messageRisk = entity.messageScore,
        linkRisk = entity.linkScore,
        websiteRisk = entity.websiteScore,
        behaviorRisk = entity.behaviorScore
      ),
      infrastructurePath = Converters.deserializeInfraNodes(entity.infraNodesJson),
      redirectChain = Converters.deserializeRedirectChain(entity.redirectChainJson),
      aiFindings = Converters.deserializeFindings(entity.findingsJson),
      predictedAttackPath = Converters.deserializeAttackSteps(entity.attackStepsJson),
      scamLessons = Converters.deserializeLessons(entity.lessonsJson),
      recommendedActions = listOf(
        "Do NOT tap or provide passwords on this link.",
        "Block sender number and report to spam registry.",
        "Review official account settings via direct browser entry."
      ),
      isLiveProbed = true,
      rawTelemetry = "Saved incident investigation report."
    )
  }
}

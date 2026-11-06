package com.example.data.model

enum class NodeType {
  ORIGIN_MESSAGE,
  ORIGIN_URL,
  DNS_HOSTING,
  NETWORK_ASN,
  REDIRECT_HOP,
  FINAL_DESTINATION,
  CREDENTIAL_FORM
}

data class InfrastructureNode(
  val type: NodeType,
  val label: String,
  val details: String,
  val riskLevel: ThreatLevel,
  val countryOrRegion: String? = null,
  val ipAddress: String? = null,
  val protocolOrStatus: String? = null
)

data class RedirectHop(
  val hopNumber: Int,
  val fromUrl: String,
  val toUrl: String,
  val statusCode: Int,
  val latencyMs: Long = 0,
  val serverType: String? = null
)

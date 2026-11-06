package com.example.data.model

enum class EvidenceStatus {
  DANGER,    // Red / High Risk flag
  WARNING,   // Amber / Suspicious anomaly
  NEUTRAL,   // Gray / Not Listed (explicitly not safe)
  VERIFIED   // Green / Legitimately verified
}

data class EvidenceItem(
  val indicator: String,         // e.g. "Phishing Database Check"
  val result: String,            // e.g. "Listed (Confirmed Phish)" or "Not Listed (Unlisted ≠ Safe)"
  val source: String,            // e.g. "PhishTank / Cisco Talos"
  val status: EvidenceStatus,
  val detail: String             // e.g. "Verified zero-day phishing target or blacklisted domain"
)

data class IdentityConsistencyCheck(
  val claimedBrand: String,            // e.g. "State Bank of India (SBI)" or "USPS"
  val destinationDomain: String,       // e.g. "sbi-kyc-update.xyz"
  val isDomainMatched: Boolean,        // false
  val visualSimilarityClaim: String,   // e.g. "Visually mimics official SBI banking portal"
  val infrastructureReality: String,   // e.g. "Hosted on disposable VPS; unverified TLS registrar"
  val consistencyVerdict: String       // "MISMATCH: High-confidence Impersonation Trap"
)

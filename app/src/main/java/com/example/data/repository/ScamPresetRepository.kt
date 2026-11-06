package com.example.data.repository

import com.example.data.model.ScamPreset
import com.example.data.model.ThreatLevel

object ScamPresetRepository {
  val PRESETS = listOf(
    ScamPreset(
      id = "sbi_kyc_mismatch",
      title = "SBI Banking KYC Trap",
      category = "Identity Mismatch",
      snippet = "SBI: Your account will be blocked. Verify immediately...",
      message = "State Bank of India Alert: Your SBI account has been scheduled for suspension due to pending KYC mandate. Verify your credentials immediately to avoid card blockage: https://sbi-kyc-verify.xyz/portal",
      url = "https://sbi-kyc-verify.xyz/portal",
      expectedRisk = ThreatLevel.DANGER
    ),
    ScamPreset(
      id = "usps_smish",
      title = "USPS Delivery Smishing",
      category = "Package / Postal",
      snippet = "USPS: Package #US-8829 is held due to incomplete address...",
      message = "USPS ALERT: Your parcel #US-940292 has arrived at our sorting facility but is held due to an incorrect street address. Please update your details within 12 hours at usps-redelivery-fees.xyz/tracking to prevent automatic return to sender.",
      url = "https://usps-redelivery-fees.xyz/tracking",
      expectedRisk = ThreatLevel.DANGER
    ),
    ScamPreset(
      id = "chase_alert",
      title = "Chase Bank Security Alert",
      category = "Banking / Credential Theft",
      snippet = "CHASE: Suspicious $842.10 charge detected in TX. Verify...",
      message = "CHASE FRAUD ALERT: We detected an unusual card charge of $842.10 at BestBuy in Austin, TX. If this was not you, verify your identity immediately to prevent card cancellation: https://chase-secure-portal.com-auth.net/login",
      url = "https://chase-secure-portal.com-auth.net/login",
      expectedRisk = ThreatLevel.DANGER
    ),
    ScamPreset(
      id = "whatsapp_crypto",
      title = "WhatsApp Crypto Task Scam",
      category = "Job / Investment",
      snippet = "Hi! We're hiring online assistants for remote app reviews...",
      message = "Hi! I am Emma from Global Talent HR. We are hiring remote assistants for daily online merchant rating tasks. Work 30-45 mins daily from your phone and earn $300-$800 daily payout in USDT. Add our mentor on Telegram: t.me/global_crypto_rewards",
      url = "https://t.me/global_crypto_rewards",
      expectedRisk = ThreatLevel.DANGER
    ),
    ScamPreset(
      id = "geeksquad_invoice",
      title = "GeekSquad $499 Renewal Scam",
      category = "Tech Support / Invoice",
      snippet = "Geek Squad: Thank you for renewing your 3-year subscription...",
      message = "Geek Squad Total Tech: Thank you for renewing your Total Protection 3-year plan for $499.99 via auto-debit. If you did not authorize this subscription renewal, click to dispute immediately: https://support-geeksquad-refund.site/cancel",
      url = "https://support-geeksquad-refund.site/cancel",
      expectedRisk = ThreatLevel.DANGER
    ),
    ScamPreset(
      id = "irs_tax_refund",
      title = "IRS Tax Refund Deposit",
      category = "Government / Identity",
      snippet = "IRS.gov: You have a pending tax refund of $1,420.50...",
      message = "Internal Revenue Service: We found an overpayment calculation in your 2024 filing. A pending direct deposit refund of $1,420.50 is waiting. Confirm your banking routing number before midnight at: https://irs-taxpayer-refund-direct.online",
      url = "https://irs-taxpayer-refund-direct.online",
      expectedRisk = ThreatLevel.DANGER
    ),
    ScamPreset(
      id = "google_legit",
      title = "Google Security Key Alert (Safe)",
      category = "Legitimate Control",
      snippet = "Google: New 2-Step Verification security key was added...",
      message = "Google Account: A new Passkey was registered for your account. If this was you, no action is needed. Review your devices anytime at https://myaccount.google.com/security",
      url = "https://myaccount.google.com/security",
      expectedRisk = ThreatLevel.SAFE
    )
  )

  // Real-world Scam Trends for Live Radar
  val LIVE_SCAM_RADAR = listOf(
    RadarTrend(
      id = "radar_1",
      title = "Toll Road 'Unpaid Toll' Smishing Wave",
      surgePercentage = "+310% this week",
      severity = "CRITICAL",
      description = "SMS texts claiming 'E-ZPass / SunPass / Fastrak: Unpaid toll of $4.50 will incur $50 penalty. Pay now at [random-tolls-pay.com]'. Massive surge nationwide.",
      prevention = "Never pay road tolls via SMS links. Use the official state turnpike toll website directly."
    ),
    RadarTrend(
      id = "radar_2",
      title = "Quishing (QR Code Phishing) in Parking & Cafes",
      surgePercentage = "+145% surge",
      severity = "HIGH",
      description = "Scammers paste physical adhesive stickers over public parking meter QR codes and cafe menus, redirecting mobile scanners to malicious lookalike payment forms.",
      prevention = "Inspect physical QR stickers for tampering; verify the displayed browser address bar before paying."
    ),
    RadarTrend(
      id = "radar_3",
      title = "AI Voice Clone Emergency Grandparent Scam",
      surgePercentage = "+85% surge",
      severity = "HIGH",
      description = "Attackers scrape 3 seconds of audio from social media videos to clone a relative's voice, calling family members claiming an urgent car crash or bail bond.",
      prevention = "Establish a secret family emergency passphrase; hang up and call the family member back on their known number."
    ),
    RadarTrend(
      id = "radar_4",
      title = "Multi-Hop Shortener Redirection Traps",
      surgePercentage = "+220% surge",
      severity = "ELEVATED",
      description = "Scammers chain 3 to 4 free URL shorteners (bit.ly -> tinyurl -> cutt.ly -> malicious landing page) to trick automated link scanners.",
      prevention = "Use ScamShield 360's isolated safe analyzer to unmask the entire redirect chain."
    )
  )
}

data class RadarTrend(
  val id: String,
  val title: String,
  val surgePercentage: String,
  val severity: String,
  val description: String,
  val prevention: String
)

package com.example

import com.example.data.analyzer.SafeAnalysisEngine
import com.example.data.analyzer.TyposquattingDetector
import com.example.data.model.ThreatLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun typosquattingDetector_flagsDeceptiveBrandDomains() {
    val analysis = TyposquattingDetector.analyzeDomain("https://chase-security-login.xyz")
    assertTrue(analysis.riskScore > 30)
    assertTrue(analysis.hasSuspiciousTld)
  }

  @Test
  fun typosquattingDetector_flagsSbiPanTrap() {
    val analysis = TyposquattingDetector.analyzeDomain("https://onlinesbi-pan-kyc.xyz")
    assertTrue(analysis.riskScore >= 45)
    assertTrue(analysis.hasSuspiciousTld)
    assertTrue(analysis.targetBrandImpersonated?.contains("State Bank of India") == true)
  }

  @Test
  fun defanger_correctlyNeutralizesUrls() {
    val defanged = TyposquattingDetector.defangUrl("https://evil.com/login")
    assertEquals("hxxps://evil[.]com/login", defanged)
  }

  @Test
  fun threatLevels_haveClearHumanReadableTitles() {
    assertEquals("Dangerous Scam: Do Not Open", ThreatLevel.DANGER.simpleTitle)
    assertEquals("Looks Safe to Open", ThreatLevel.SAFE.simpleTitle)
    assertEquals("Be Careful: Suspicious Signals", ThreatLevel.SUSPICIOUS.simpleTitle)
  }

  @Test
  fun extractAllUrls_findsMultipleDistinctLinks() {
    val message = "Urgent: Your parcel https://usps-parcel-fees.xyz/track is held. Also contact https://postal-help.site for support."
    val extracted = SafeAnalysisEngine.extractAllUrls(message)
    assertEquals(2, extracted.size)
    assertTrue(extracted.any { it.contains("usps-parcel-fees.xyz") })
    assertTrue(extracted.any { it.contains("postal-help.site") })
  }

  @Test
  fun extractAllUrls_extractsRbiBankInAndBareDomainsWithoutHttps() {
    val text = "Browser address bar: yonoretail.sbi.bank.in/registration and contact at axis.bank.in"
    val extracted = SafeAnalysisEngine.extractAllUrls(text)
    assertEquals(2, extracted.size)
    assertTrue(extracted.any { it.contains("yonoretail.sbi.bank.in") })
    assertTrue(extracted.any { it.contains("axis.bank.in") })
  }

  @Test
  fun sanitizeUrlForPrivacy_redactsSensitiveTokens() {
    val raw = "https://tracker.xyz/auth?token=Secret12345&email=target@example.com"
    val sanitized = SafeAnalysisEngine.sanitizeUrlForPrivacy(raw)
    assertTrue(sanitized.contains("[REDACTED_PII]"))
    assertTrue(!sanitized.contains("Secret12345"))
    assertTrue(!sanitized.contains("target@example.com"))
  }

  @Test
  fun typosquattingDetector_flagsAxisBank123FakeSite() {
    val analysis = TyposquattingDetector.analyzeDomain("https://www.axisbank123.com")
    assertEquals("Axis Bank", analysis.targetBrandImpersonated)
    assertTrue(analysis.riskScore >= 70)
    assertTrue(analysis.hasDeceptiveSubdomains)
    assertTrue(analysis.redFlags.any { it.contains("Unauthorized domain impersonating Axis Bank") })
  }

  @Test
  fun typosquattingDetector_recognizesOfficialAxisBankDomain() {
    val officialAnalysis = TyposquattingDetector.analyzeDomain("https://www.axisbank.com")
    assertEquals(null, officialAnalysis.targetBrandImpersonated)
    assertEquals(0, officialAnalysis.riskScore)

    val officialIn = TyposquattingDetector.analyzeDomain("https://axisbank.co.in")
    assertEquals(null, officialIn.targetBrandImpersonated)
    assertEquals(0, officialIn.riskScore)
  }

  @Test
  fun typosquattingDetector_recognizesOfficialSbiCaliforniaAndRbiBankInDomains() {
    val sbicAnalysis = TyposquattingDetector.analyzeDomain("https://www.sbicalifornia.com")
    assertEquals(null, sbicAnalysis.targetBrandImpersonated)
    assertEquals(0, sbicAnalysis.riskScore)

    val sbicBankAnalysis = TyposquattingDetector.analyzeDomain("https://www.sbic.bank/personal-banking")
    assertEquals(null, sbicBankAnalysis.targetBrandImpersonated)
    assertEquals(0, sbicBankAnalysis.riskScore)

    val bankInAnalysis = TyposquattingDetector.analyzeDomain("https://sbi.bank.in")
    assertEquals(null, bankInAnalysis.targetBrandImpersonated)
    assertEquals(0, bankInAnalysis.riskScore)

    val yonoAnalysis = TyposquattingDetector.analyzeDomain("https://yonoretail.sbi.bank.in/registration")
    assertEquals(null, yonoAnalysis.targetBrandImpersonated)
    assertEquals(0, yonoAnalysis.riskScore)

    val axisBankIn = TyposquattingDetector.analyzeDomain("https://axis.bank.in")
    assertEquals(null, axisBankIn.targetBrandImpersonated)
    assertEquals(0, axisBankIn.riskScore)
  }

  @Test
  fun bankingPrivacyShield_identifiesBankingAndUpiPackages() {
    val service = com.example.service.ScreenLinkAccessibilityService
    assertTrue(service.isBankingOrUpiApp("com.google.android.apps.nbu.paisa.user"))
    assertTrue(service.isBankingOrUpiApp("com.phonepe.app"))
    assertTrue(service.isBankingOrUpiApp("net.one97.paytm"))
    assertTrue(service.isBankingOrUpiApp("in.org.npci.upiapp"))
    assertTrue(service.isBankingOrUpiApp("com.axis.mobile"))
    assertTrue(service.isBankingOrUpiApp("com.sbi.lotusintouch"))
    assertTrue(service.isBankingOrUpiApp("com.snapwork.hdfc"))
    assertTrue(service.isBankingOrUpiApp("com.csam.icici.bank.imobile"))
    assertTrue(service.isBankingOrUpiApp("com.chase.sig.android"))

    // Non-banking apps should not be suppressed
    org.junit.Assert.assertFalse(service.isBankingOrUpiApp("com.android.chrome"))
    org.junit.Assert.assertFalse(service.isBankingOrUpiApp("com.whatsapp"))
    org.junit.Assert.assertFalse(service.isBankingOrUpiApp("com.google.android.youtube"))
  }

  @Test
  fun bankingPrivacyShield_ignoresSystemUiAndKeyboards() {
    val service = com.example.service.ScreenLinkAccessibilityService
    // System UI, quick settings, and notifications should be treated as transient
    assertTrue(service.isTransientOrSystemPackage("com.android.systemui"))
    assertTrue(service.isTransientOrSystemPackage("com.google.android.inputmethod.latin"))
    assertTrue(service.isTransientOrSystemPackage("com.samsung.android.honeyboard"))
    assertTrue(service.isTransientOrSystemPackage("com.android.launcher3"))

    // Real user apps must not be marked as transient
    org.junit.Assert.assertFalse(service.isTransientOrSystemPackage("com.android.chrome"))
    org.junit.Assert.assertFalse(service.isTransientOrSystemPackage("com.whatsapp"))
    org.junit.Assert.assertFalse(service.isTransientOrSystemPackage("com.google.android.youtube"))
  }

  @Test
  fun typosquattingDetector_recognizesEbayAndTopLegitimateDomains() {
    val ebayAnalysis = TyposquattingDetector.analyzeDomain("https://m.ebay.com/itm/123456")
    assertEquals(null, ebayAnalysis.targetBrandImpersonated)
    assertEquals(0, ebayAnalysis.riskScore)
    assertTrue(TyposquattingDetector.isTopLegitimateDomain("m.ebay.com"))
    assertTrue(TyposquattingDetector.isTopLegitimateDomain("ebay.com"))
    assertTrue(TyposquattingDetector.isTopLegitimateDomain("google.com"))
    assertTrue(TyposquattingDetector.isTopLegitimateDomain("wikipedia.org"))
  }

  @Test
  fun threatDatabaseConnector_flagsImpersonatingSbi123Trap() {
    val sbi123Analysis = TyposquattingDetector.analyzeDomain("https://sbi123.com")
    assertEquals("State Bank of India (YONO SBI)", sbi123Analysis.targetBrandImpersonated)
    assertTrue(sbi123Analysis.riskScore >= 70)
  }

  @Test
  fun typosquattingDetector_recognizesGovernmentAndEducationalDomains() {
    val ncertAnalysis = TyposquattingDetector.analyzeDomain("https://ncert.nic.in")
    assertEquals(null, ncertAnalysis.targetBrandImpersonated)
    assertEquals(0, ncertAnalysis.riskScore)

    val govInAnalysis = TyposquattingDetector.analyzeDomain("https://india.gov.in")
    assertEquals(null, govInAnalysis.targetBrandImpersonated)
    assertEquals(0, govInAnalysis.riskScore)

    val uidaiAnalysis = TyposquattingDetector.analyzeDomain("https://uidai.gov.in/en/")
    assertEquals(null, uidaiAnalysis.targetBrandImpersonated)
    assertEquals(0, uidaiAnalysis.riskScore)

    assertTrue(TyposquattingDetector.isTopLegitimateDomain("ncert.nic.in"))
    assertTrue(TyposquattingDetector.isTopLegitimateDomain("india.gov.in"))
    assertTrue(TyposquattingDetector.isTopLegitimateDomain("gov.uk"))
    assertTrue(TyposquattingDetector.isTopLegitimateDomain("iitb.ac.in"))
  }

  @Test
  fun typosquattingDetector_recognizesYahooAsLegitimateDomain() {
    val yahooAnalysis = TyposquattingDetector.analyzeDomain("https://www.yahoo.com")
    assertEquals(null, yahooAnalysis.targetBrandImpersonated)
    assertEquals(0, yahooAnalysis.riskScore)

    val yahooMailAnalysis = TyposquattingDetector.analyzeDomain("https://mail.yahoo.com")
    assertEquals(null, yahooMailAnalysis.targetBrandImpersonated)
    assertEquals(0, yahooMailAnalysis.riskScore)

    assertTrue(TyposquattingDetector.isTopLegitimateDomain("yahoo.com"))
    assertTrue(TyposquattingDetector.isTopLegitimateDomain("www.yahoo.com"))
    assertTrue(TyposquattingDetector.isTopLegitimateDomain("mail.yahoo.com"))
  }
}


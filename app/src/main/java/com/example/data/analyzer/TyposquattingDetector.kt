package com.example.data.analyzer

data class DomainAnalysis(
  val domain: String,
  val host: String,
  val targetBrandImpersonated: String?,
  val hasHomoglyphs: Boolean,
  val hasSuspiciousTld: Boolean,
  val isRawIp: Boolean,
  val hasDeceptiveSubdomains: Boolean,
  val riskScore: Int,
  val redFlags: List<String>
)

data class BrandRegistryEntry(
  val id: String,
  val displayName: String,
  val officialDomains: List<String>,
  val keywords: List<String>
)

object TyposquattingDetector {
  val BRAND_REGISTRY = listOf(
    // Indian Banking & UPI Suites
    BrandRegistryEntry("axisbank", "Axis Bank", listOf("axisbank.com", "axisbank.co.in", "axis.bank.in"), listOf("axisbank", "axis-bank")),
    BrandRegistryEntry("sbi", "State Bank of India (YONO SBI)", listOf("sbi.co.in", "onlinesbi.sbi", "onlinesbi.com", "sbi.bank.in", "yonoretail.sbi.bank.in", "yono.sbi"), listOf("onlinesbi", "statebankofindia", "yono", "sbi")),
    BrandRegistryEntry("sbic", "SBIC Bank (State Bank of India California)", listOf("sbic.bank", "sbicalifornia.com", "sbic.com"), listOf("sbic", "sbicalifornia")),
    BrandRegistryEntry("hdfc", "HDFC Bank", listOf("hdfcbank.com", "hdfc.bank.in"), listOf("hdfcbank", "hdfc")),
    BrandRegistryEntry("icici", "ICICI Bank", listOf("icicibank.com", "icici.bank.in"), listOf("icicibank", "icici")),
    BrandRegistryEntry("kotak", "Kotak Mahindra Bank", listOf("kotak.com", "kotakcherry.com", "kotak.bank.in"), listOf("kotakbank", "kotak")),
    BrandRegistryEntry("pnb", "Punjab National Bank", listOf("pnbindia.in", "netpnb.com", "pnb.bank.in"), listOf("punjabnational", "pnbindia", "pnb")),
    BrandRegistryEntry("bob", "Bank of Baroda", listOf("bankofbaroda.in", "bobibanking.com", "bob.bank.in"), listOf("bankofbaroda", "bobibanking")),
    BrandRegistryEntry("canara", "Canara Bank", listOf("canarabank.com", "canara.bank.in"), listOf("canarabank")),
    BrandRegistryEntry("unionbank", "Union Bank of India", listOf("unionbankofindia.co.in", "unionbank.bank.in"), listOf("unionbank")),
    BrandRegistryEntry("paytm", "Paytm", listOf("paytm.com"), listOf("paytm")),
    BrandRegistryEntry("phonepe", "PhonePe", listOf("phonepe.com"), listOf("phonepe")),
    BrandRegistryEntry("gpay", "Google Pay", listOf("pay.google.com", "google.com"), listOf("googlepay", "gpay")),
    BrandRegistryEntry("bhim", "BHIM UPI", listOf("bhimupi.org.in", "npci.org.in"), listOf("bhimupi", "bhim")),
    BrandRegistryEntry("cred", "CRED", listOf("cred.club"), listOf("cred.club")),
    
    // Global Financial Institutions & Payment
    BrandRegistryEntry("paypal", "PayPal", listOf("paypal.com"), listOf("paypal")),
    BrandRegistryEntry("chase", "JPMorgan Chase", listOf("chase.com"), listOf("chase")),
    BrandRegistryEntry("bankofamerica", "Bank of America", listOf("bankofamerica.com", "bofa.com"), listOf("bankofamerica", "bofa")),
    BrandRegistryEntry("wellsfargo", "Wells Fargo", listOf("wellsfargo.com"), listOf("wellsfargo")),
    BrandRegistryEntry("citibank", "Citibank", listOf("citi.com", "citibank.com"), listOf("citibank", "citi")),
    BrandRegistryEntry("barclays", "Barclays", listOf("barclays.co.uk", "barclays.com"), listOf("barclays")),
    BrandRegistryEntry("hsbc", "HSBC", listOf("hsbc.com", "hsbc.co.in"), listOf("hsbc")),
    
    // Government & Official Institutions
    BrandRegistryEntry("gov_india", "Government of India (GoI / NIC)", listOf("india.gov.in", "nic.in", "gov.in", "ncert.nic.in", "uidai.gov.in", "incometax.gov.in", "cbse.gov.in", "passportindia.gov.in"), listOf("gov.in", "nic.in", "ncert", "uidai", "aadhaar", "digilocker", "passportindia")),
    
    // Tech & Delivery
    BrandRegistryEntry("apple", "Apple Inc.", listOf("apple.com", "icloud.com"), listOf("apple", "icloud")),
    BrandRegistryEntry("google", "Google Services", listOf("google.com"), listOf("google")),
    BrandRegistryEntry("microsoft", "Microsoft", listOf("microsoft.com", "live.com", "outlook.com"), listOf("microsoft", "outlook")),
    BrandRegistryEntry("amazon", "Amazon", listOf("amazon.com", "amazon.in"), listOf("amazon")),
    BrandRegistryEntry("netflix", "Netflix", listOf("netflix.com"), listOf("netflix")),
    BrandRegistryEntry("usps", "United States Postal Service (USPS)", listOf("usps.com"), listOf("usps")),
    BrandRegistryEntry("indiapost", "India Post", listOf("indiapost.gov.in"), listOf("indiapost")),
    BrandRegistryEntry("fedex", "FedEx Express", listOf("fedex.com"), listOf("fedex")),
    BrandRegistryEntry("ups", "United Parcel Service (UPS)", listOf("ups.com"), listOf("ups")),
    BrandRegistryEntry("dhl", "DHL Express", listOf("dhl.com"), listOf("dhl")),
    BrandRegistryEntry("whatsapp", "WhatsApp", listOf("whatsapp.com"), listOf("whatsapp")),
    BrandRegistryEntry("telegram", "Telegram", listOf("t.me", "telegram.org"), listOf("telegram")),
    BrandRegistryEntry("coinbase", "Coinbase", listOf("coinbase.com"), listOf("coinbase")),
    BrandRegistryEntry("binance", "Binance", listOf("binance.com"), listOf("binance")),
    BrandRegistryEntry("ebay", "eBay", listOf("ebay.com", "m.ebay.com", "ebay.co.uk", "ebay.in", "ebay.de"), listOf("ebay"))
  )

  val TOP_LEGITIMATE_DOMAINS = setOf(
    "ebay.com", "m.ebay.com", "ebay.co.uk", "ebay.in", "ebay.de",
    "google.com", "www.google.com", "accounts.google.com", "mail.google.com",
    "youtube.com", "www.youtube.com", "m.youtube.com",
    "amazon.com", "www.amazon.com", "amazon.in", "www.amazon.in",
    "apple.com", "www.apple.com", "icloud.com",
    "microsoft.com", "www.microsoft.com", "live.com", "outlook.com", "office.com",
    "wikipedia.org", "en.wikipedia.org", "m.wikipedia.org",
    "github.com", "www.github.com",
    "reddit.com", "www.reddit.com", "m.reddit.com",
    "twitter.com", "x.com", "linkedin.com", "instagram.com", "facebook.com",
    "netflix.com", "www.netflix.com",
    "sbic.bank", "www.sbic.bank", "sbicalifornia.com", "www.sbicalifornia.com",
    "sbi.co.in", "www.sbi.co.in", "onlinesbi.sbi", "sbi.bank.in", "yonoretail.sbi.bank.in", "yono.sbi",
    "axisbank.com", "www.axisbank.com", "axis.bank.in",
    "icicibank.com", "www.icicibank.com", "icici.bank.in",
    "hdfcbank.com", "www.hdfcbank.com", "hdfc.bank.in",
    "kotak.com", "kotak.bank.in", "pnbindia.in", "pnb.bank.in", "bankofbaroda.in", "bob.bank.in",
    "ncert.nic.in", "india.gov.in", "nic.in", "gov.in", "uidai.gov.in", "incometax.gov.in"
  )

  fun isTopLegitimateDomain(host: String): Boolean {
    val h = host.lowercase()
    if (TOP_LEGITIMATE_DOMAINS.contains(h)) return true
    val legitimateTlds = listOf(".bank.in", ".bank", ".gov.in", ".nic.in", ".gov", ".gov.uk", ".edu.in", ".ac.in", ".edu", ".org.in")
    if (legitimateTlds.any { tld -> h == tld.removePrefix(".") || h.endsWith(tld) }) return true
    return TOP_LEGITIMATE_DOMAINS.any { h.endsWith(".$it") }
  }

  private val SUSPICIOUS_TLDS = setOf(
    "xyz", "top", "tk", "ml", "ga", "cf", "gq", "buzz", "work",
    "click", "rest", "fit", "link", "live", "online", "site",
    "vip", "cfd", "monster", "fun", "icu", "shop", "support", "cc"
  )

  fun analyzeDomain(urlOrHost: String): DomainAnalysis {
    val clean = cleanUrlToHost(urlOrHost)
    val redFlags = mutableListOf<String>()
    var score = 0

    // Check for well-known verified top legitimate internet domains
    val hasHomoglyphs = clean.startsWith("xn--") || containsHomoglyphCharacters(clean)
    if (isTopLegitimateDomain(clean) && !hasHomoglyphs) {
      return DomainAnalysis(
        domain = clean,
        host = clean,
        targetBrandImpersonated = null,
        hasHomoglyphs = false,
        hasSuspiciousTld = false,
        isRawIp = false,
        hasDeceptiveSubdomains = false,
        riskScore = 0,
        redFlags = emptyList()
      )
    }

    // 1. Raw IP check
    val isRawIp = clean.matches(Regex("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d+)?$"))
    if (isRawIp) {
      score += 45
      redFlags.add("Host uses raw IP address instead of domain name ($clean)")
    }

    // 2. Homoglyph / Punycode check
    if (hasHomoglyphs) {
      score += 45
      redFlags.add("Internationalized / Punycode homoglyph detected (disguised lookalike characters)")
    }

    // 3. TLD check
    val tld = clean.substringAfterLast('.', "").lowercase()
    val hasSuspiciousTld = SUSPICIOUS_TLDS.contains(tld)
    if (hasSuspiciousTld) {
      score += 25
      redFlags.add("High-abuse registrar / suspicious top-level domain (.$tld)")
    }

    // 4. Subdomain & brand impersonation check
    var detectedBrand: String? = null
    var hasDeceptiveSubdomain = false

    val hostLower = clean.lowercase()
    
    // Check against brand registry
    for (entry in BRAND_REGISTRY) {
      val matchedKeyword = entry.keywords.firstOrNull { kw ->
        hostLower.contains(kw)
      }

      if (matchedKeyword != null) {
        // Check if host matches any official domain exactly or as a subdomain of an official domain or uses restricted .bank/.bank.in TLD
        val isOfficial = entry.officialDomains.any { off ->
          hostLower == off || hostLower.endsWith(".$off")
        } || (hostLower.endsWith(".bank") && hostLower.contains(matchedKeyword)) || (hostLower.endsWith(".bank.in") && hostLower.contains(matchedKeyword))

        if (!isOfficial) {
          detectedBrand = entry.displayName
          hasDeceptiveSubdomain = true
          score += 70
          redFlags.add(
            "CRITICAL: Unauthorized domain impersonating ${entry.displayName}! Official domain is ${entry.officialDomains.first()} (Target: '$clean')"
          )

          // Extra check for appended numbers or deceptive keywords (e.g. axisbank123, sbi-kyc, hdfc-update)
          val suffixPattern = Regex("(${Regex.escape(matchedKeyword)})[\\-_]?(?:\\d+|login|verify|online|kyc|pan|secure|auth|support|alert|update|portal|service)", RegexOption.IGNORE_CASE)
          if (suffixPattern.containsMatchIn(hostLower) || hostLower.contains("${matchedKeyword}123")) {
            score += 20
            redFlags.add("Lookalike brand permutation: Contains appended digits/scam lure terms imitating authentic login portal")
          }
          break
        }
      }
    }

    // 5. Lookalike brand permutations (e.g., paypa1, axlsbank, amaz0n, g00gle)
    if (detectedBrand == null) {
      val leetReplacements = mapOf(
        "0" to "o", "1" to "l", "5" to "s", "8" to "b", "vv" to "w"
      )
      var leetDecoded = hostLower
      leetReplacements.forEach { (k, v) -> leetDecoded = leetDecoded.replace(k, v) }
      
      for (entry in BRAND_REGISTRY) {
        val matchedKw = entry.keywords.firstOrNull { kw ->
          leetDecoded.contains(kw) && !hostLower.contains(kw)
        }
        if (matchedKw != null) {
          detectedBrand = "${entry.displayName} (Typosquatted)"
          score += 65
          redFlags.add("Typosquatting detected: deceptive character substitution imitating ${entry.displayName}")
          break
        }
      }
    }

    // 6. Generic financial terms in unregistered domain
    val isOfficialDomain = BRAND_REGISTRY.any { entry ->
      entry.officialDomains.any { off -> hostLower == off || hostLower.endsWith(".$off") } ||
      (hostLower.endsWith(".bank") && entry.keywords.any { hostLower.contains(it) }) ||
      (hostLower.endsWith(".bank.in") && entry.keywords.any { hostLower.contains(it) })
    }
    if (detectedBrand == null && !isOfficialDomain) {
      val financialTerms = listOf("bank", "banking", "netbanking", "wallet", "kyc", "otp", "aadhaar", "upi-verify")
      val hasFinTerm = financialTerms.any { hostLower.contains(it) }
      if (hasFinTerm) {
        score += 35
        redFlags.add("Contains banking/financial terminology in an unverified domain")
      }
    }

    // 7. Excessive subdomains (e.g. login.secure.verify.chase.com.evil.xyz)
    val dotCount = clean.count { it == '.' }
    if (dotCount >= 4) {
      score += 20
      redFlags.add("Nested subdomain chain ($dotCount subdomains) typically used to obscure true destination")
    }

    return DomainAnalysis(
      domain = clean,
      host = clean,
      targetBrandImpersonated = detectedBrand,
      hasHomoglyphs = hasHomoglyphs,
      hasSuspiciousTld = hasSuspiciousTld,
      isRawIp = isRawIp,
      hasDeceptiveSubdomains = hasDeceptiveSubdomain,
      riskScore = score.coerceIn(0, 100),
      redFlags = redFlags
    )
  }

  fun defangUrl(url: String): String {
    return url.replace("http://", "hxxp://")
      .replace("https://", "hxxps://")
      .replace(".", "[.]")
  }

  fun refangUrl(url: String): String {
    return url.replace("hxxps://", "https://")
      .replace("hxxp://", "http://")
      .replace("[.]", ".")
      .replace("(.)", ".")
  }

  private fun cleanUrlToHost(input: String): String {
    var str = input.trim()
    if (str.startsWith("http://", ignoreCase = true)) str = str.substring(7)
    if (str.startsWith("https://", ignoreCase = true)) str = str.substring(8)
    if (str.startsWith("hxxp://", ignoreCase = true)) str = str.substring(7)
    if (str.startsWith("hxxps://", ignoreCase = true)) str = str.substring(8)
    str = str.replace("[.]", ".")
    val slashIndex = str.indexOf('/')
    if (slashIndex != -1) str = str.substring(0, slashIndex)
    val colonIndex = str.indexOf(':')
    if (colonIndex != -1) str = str.substring(0, colonIndex)
    return str.lowercase()
  }

  private fun containsHomoglyphCharacters(text: String): Boolean {
    // Check for Cyrillic or Greek lookalikes in predominantly Latin strings
    for (ch in text) {
      val block = Character.UnicodeBlock.of(ch)
      if (block == Character.UnicodeBlock.CYRILLIC ||
          block == Character.UnicodeBlock.GREEK ||
          block == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
        return true
      }
    }
    return false
  }
}

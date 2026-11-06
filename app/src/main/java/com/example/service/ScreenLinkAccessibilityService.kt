package com.example.service

import android.accessibilityservice.AccessibilityService
import android.app.NotificationManager
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.data.analyzer.SafeAnalysisEngine
import com.example.data.model.ThreatAnalysisResult
import com.example.data.model.ThreatLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections

class ScreenLinkAccessibilityService : AccessibilityService() {

  private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  private val scannedUrlsHistory = Collections.synchronizedSet(mutableSetOf<String>())

  companion object {
    private const val TAG = "ScreenLinkService"

    private val LEGITIMATE_BANKING_PACKAGES = mapOf(
      "com.google.android.apps.nbu.paisa.user" to "Google Pay",
      "com.phonepe.app" to "PhonePe",
      "net.one97.paytm" to "Paytm",
      "in.org.npci.upiapp" to "BHIM UPI",
      "com.dreamplug.androidapp" to "CRED",
      "com.axis.mobile" to "Axis Mobile",
      "com.sbi.lotusintouch" to "YONO SBI",
      "com.sbi.upi" to "SBI UPI",
      "com.statebankofindia" to "State Bank of India",
      "com.yono.sbi" to "YONO SBI",
      "com.snapwork.hdfc" to "HDFC MobileBanking",
      "com.csam.icici.bank.imobile" to "ICICI iMobile",
      "com.msf.kbank.mobile" to "Kotak 811",
      "com.pnb.pnbone" to "PNB ONE",
      "com.bankofbaroda.mconnect" to "bob World",
      "com.canarabank.mobility" to "Canara ai1",
      "com.infrasofttech.uboi" to "Union Bank Vyom",
      "com.indusind.mpassbook" to "IndusMobile",
      "com.chase.sig.android" to "Chase Mobile",
      "com.infonow.bofa" to "Bank of America",
      "com.wf.wellsfargomobile" to "Wells Fargo Mobile",
      "com.citi.citimobile" to "Citi Mobile",
      "com.paypal.android.p2pmobile" to "PayPal"
    )

    private val LEGITIMATE_BANKING_DOMAINS = mapOf(
      "yonoretail.sbi.bank.in" to "State Bank of India (YONO SBI)",
      "sbi.bank.in" to "State Bank of India (YONO SBI)",
      "onlinesbi.sbi" to "State Bank of India (Online SBI)",
      "sbi.co.in" to "State Bank of India",
      "yono.sbi" to "State Bank of India (YONO SBI)",
      "sbic.bank" to "SBIC Bank (State Bank of India California)",
      "sbicalifornia.com" to "SBI California",
      "sbic.com" to "SBIC Bank",
      "axis.bank.in" to "Axis Bank",
      "axisbank.com" to "Axis Bank",
      "axisbank.co.in" to "Axis Bank India",
      "icici.bank.in" to "ICICI Bank",
      "icicibank.com" to "ICICI Bank",
      "hdfc.bank.in" to "HDFC Bank",
      "hdfcbank.com" to "HDFC Bank",
      "kotak.bank.in" to "Kotak Mahindra Bank",
      "kotak.com" to "Kotak Mahindra Bank",
      "pnb.bank.in" to "Punjab National Bank",
      "pnbindia.in" to "Punjab National Bank",
      "bob.bank.in" to "Bank of Baroda",
      "bankofbaroda.in" to "Bank of Baroda",
      "chase.com" to "JPMorgan Chase",
      "bankofamerica.com" to "Bank of America",
      "wellsfargo.com" to "Wells Fargo",
      "citi.com" to "Citibank",
      "citibank.com" to "Citibank",
      "paytm.com" to "Paytm",
      "phonepe.com" to "PhonePe",
      "bhimupi.org.in" to "BHIM UPI"
    )

    val WEB_BROWSER_PACKAGES = setOf(
      "com.android.chrome",
      "com.chrome.beta",
      "com.chrome.canary",
      "com.chrome.dev",
      "com.sec.android.app.sbrowser",
      "com.sec.android.app.sbrowser.beta",
      "org.mozilla.firefox",
      "com.microsoft.emmx",
      "com.opera.browser",
      "com.brave.browser"
    )

    val LURE_MESSAGING_PACKAGES = setOf(
      "com.whatsapp",
      "com.whatsapp.w4b",
      "org.telegram.messenger",
      "org.thunderdog.challegram",
      "com.google.android.apps.messaging",
      "com.samsung.android.messaging",
      "com.android.mms",
      "com.google.android.gm",
      "com.facebook.orca",
      "com.facebook.mlite",
      "com.instagram.android",
      "com.twitter.android",
      "com.discord",
      "com.slack"
    )

    @Volatile
    private var activeBankingDomainInBrowser: String? = null
    @Volatile
    private var activeBankingNameInBrowser: String? = null
    @Volatile
    private var lastBrowserPackage: String? = null

    @Volatile
    var instance: ScreenLinkAccessibilityService? = null
      private set

    private val _visibleScreenUrls = MutableStateFlow<List<String>>(emptyList())
    val visibleScreenUrls: StateFlow<List<String>> = _visibleScreenUrls.asStateFlow()

    private val _visibleScreenTextSnippet = MutableStateFlow<String>("")
    val visibleScreenTextSnippet: StateFlow<String> = _visibleScreenTextSnippet.asStateFlow()

    private val _isAutomationModeEnabled = MutableStateFlow<Boolean>(true)
    val isAutomationModeEnabled: StateFlow<Boolean> = _isAutomationModeEnabled.asStateFlow()

    private val _isBankingPrivacyShieldEnabled = MutableStateFlow<Boolean>(true)
    val isBankingPrivacyShieldEnabled: StateFlow<Boolean> = _isBankingPrivacyShieldEnabled.asStateFlow()

    private val _isBankingPrivacyActive = MutableStateFlow<Boolean>(false)
    val isBankingPrivacyActive: StateFlow<Boolean> = _isBankingPrivacyActive.asStateFlow()

    val isBankingAppCurrentlyActive: StateFlow<Boolean> = _isBankingPrivacyActive.asStateFlow()

    private val _detectedBankingAppName = MutableStateFlow<String?>(null)
    val detectedBankingAppName: StateFlow<String?> = _detectedBankingAppName.asStateFlow()

    private val _latestAutoScanResult = MutableStateFlow<ThreatAnalysisResult?>(null)
    val latestAutoScanResult: StateFlow<ThreatAnalysisResult?> = _latestAutoScanResult.asStateFlow()

    fun isBankingOrUpiApp(pkg: String): Boolean {
      if (pkg.isBlank()) return false
      val lower = pkg.lowercase()
      if (LEGITIMATE_BANKING_PACKAGES.containsKey(lower)) return true
      return lower.contains(".bank.") ||
          lower.contains(".banking.") ||
          lower.contains(".upi") ||
          lower.contains("netbanking") ||
          lower.contains("fintech") ||
          lower.contains("paisa") ||
          (lower.contains("wallet") && !lower.contains("google"))
    }

    val SYSTEM_IGNORED_PACKAGES = setOf(
      "com.android.systemui",
      "com.android.launcher3",
      "com.google.android.apps.nexuslauncher",
      "com.sec.android.app.launcher",
      "com.android.settings"
    )

    fun isTransientOrSystemPackage(pkg: String): Boolean {
      if (pkg.isBlank()) return true
      val p = pkg.lowercase()
      if (SYSTEM_IGNORED_PACKAGES.contains(p)) return true
      if (p.startsWith("com.android.systemui")) return true
      if (p.contains("inputmethod") || p.contains("keyboard") || p.contains("honeyboard") || p.contains("latin")) return true
      if (p == "android" || p == "com.example.scamshield360" || p == "com.example") return true
      return false
    }

    fun getBankingAppFriendlyName(pkg: String): String {
      val lower = pkg.lowercase()
      return LEGITIMATE_BANKING_PACKAGES[lower] ?: "Banking / UPI Application"
    }

    fun setAutomationMode(enabled: Boolean) {
      _isAutomationModeEnabled.value = enabled
    }

    fun setBankingPrivacyShield(enabled: Boolean) {
      _isBankingPrivacyShieldEnabled.value = enabled
    }

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
      val expectedServiceName = "${context.packageName}/${ScreenLinkAccessibilityService::class.java.canonicalName}"
      val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
      ) ?: return false

      val colonSplitter = TextUtils.SimpleStringSplitter(':')
      colonSplitter.setString(enabledServices)
      while (colonSplitter.hasNext()) {
        val componentName = colonSplitter.next()
        if (componentName.equals(expectedServiceName, ignoreCase = true) ||
          componentName.contains("ScreenLinkAccessibilityService")
        ) {
          return true
        }
      }
      return false
    }

    fun getCurrentlyVisibleScreenLinks(): List<String> {
      val svc = instance
      if (svc != null) {
        val freshlyScanned = svc.scanCurrentWindowDirectly()
        if (freshlyScanned.isNotEmpty()) {
          _visibleScreenUrls.value = freshlyScanned
          return freshlyScanned
        }
      }
      if (_isBankingPrivacyActive.value && activeBankingDomainInBrowser != null) {
        return listOf("https://$activeBankingDomainInBrowser")
      }
      return _visibleScreenUrls.value
    }
  }

  override fun onServiceConnected() {
    super.onServiceConnected()
    instance = this
    Log.i(TAG, "ScreenLinkAccessibilityService connected. Banking Privacy Shield: ${_isBankingPrivacyShieldEnabled.value}")
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    if (event == null) return

    val packageName = event.packageName?.toString()?.lowercase() ?: ""
    // If the event comes from system UI (notification shade, status bar, quick settings) or keyboard,
    // do not disrupt the ongoing banking privacy session!
    if (isTransientOrSystemPackage(packageName)) {
      return
    }

    val isLegitBankApp = isBankingOrUpiApp(packageName)
    val isBrowser = WEB_BROWSER_PACKAGES.contains(packageName)

    val node = rootInActiveWindow ?: return
    val collectedText = StringBuilder()
    traverseNodes(node, collectedText)
    val text = collectedText.toString()
    val textLower = text.lowercase()

    // 1. Check for specific registered banking domains (prioritize longer domain keys like "yonoretail.sbi.bank.in" first)
    val specificDomainMatch = LEGITIMATE_BANKING_DOMAINS.entries
      .filter { (domain, _) -> textLower.contains(domain) }
      .maxByOrNull { it.key.length }

    val matchedDomainEntry = specificDomainMatch ?: run {
      // 2. Fallback to RBI .bank.in or global .bank domain if not specifically mapped
      if (textLower.contains(".bank.in")) {
        java.util.AbstractMap.SimpleEntry("bank.in", "Verified Banking Portal (.bank.in)")
      } else if (textLower.contains(".bank/") || textLower.contains(".bank ") || textLower.endsWith(".bank")) {
        java.util.AbstractMap.SimpleEntry("bank", "Verified Global Financial Institution (.bank)")
      } else {
        null
      }
    }

    var isLegitBankDomain = false
    var activeAppName: String? = null

    if (isLegitBankApp) {
      activeAppName = getBankingAppFriendlyName(packageName)
      isLegitBankDomain = true
    } else if (matchedDomainEntry != null) {
      activeBankingDomainInBrowser = matchedDomainEntry.key
      activeBankingNameInBrowser = matchedDomainEntry.value
      lastBrowserPackage = packageName
      activeAppName = matchedDomainEntry.value
      isLegitBankDomain = true
    } else if (isBrowser && activeBankingDomainInBrowser != null) {
      // Sticky Banking Privacy Shield:
      // While user stays in Chrome, keep privacy active UNLESS user explicitly navigates to a distinct external non-banking site
      val urls = SafeAnalysisEngine.extractAllUrls(text)
      val navigatedToExternal = urls.any { u ->
        val host = u.substringAfter("://").substringBefore('/').lowercase()
        host.isNotBlank() &&
            !host.contains(activeBankingDomainInBrowser!!) &&
            !host.endsWith(".bank.in") &&
            !host.endsWith(".bank") &&
            !host.contains("sbi") &&
            !host.contains("yono") &&
            !LEGITIMATE_BANKING_DOMAINS.keys.any { host.contains(it) }
      }

      if (navigatedToExternal) {
        activeBankingDomainInBrowser = null
        activeBankingNameInBrowser = null
        isLegitBankDomain = false
      } else {
        isLegitBankDomain = true
        activeAppName = activeBankingNameInBrowser ?: "Verified Bank Platform"
      }
    } else if (!isBrowser && !isLegitBankApp) {
      activeBankingDomainInBrowser = null
      activeBankingNameInBrowser = null
      isLegitBankDomain = false
    }

    val currentScreenUrls = if (text.isNotBlank()) SafeAnalysisEngine.extractAllUrls(text) else emptyList()
    val resolvedScreenUrls = if (currentScreenUrls.isNotEmpty()) {
      currentScreenUrls
    } else if (isLegitBankDomain && activeBankingDomainInBrowser != null) {
      listOf("https://$activeBankingDomainInBrowser")
    } else {
      emptyList()
    }
    _visibleScreenUrls.value = resolvedScreenUrls
    _visibleScreenTextSnippet.value = text.take(250)

    // Privacy Mode Guard: If user is inside an authentic banking/UPI app or website, pause auto-scanning!
    if (_isBankingPrivacyShieldEnabled.value && (isLegitBankApp || isLegitBankDomain)) {
      if (!_isBankingPrivacyActive.value || _detectedBankingAppName.value != activeAppName) {
        _isBankingPrivacyActive.value = true
        _detectedBankingAppName.value = activeAppName
        LinkGuardianService.updateBankingPrivacyStatus(applicationContext, isPaused = true, appName = activeAppName)
        Log.i(TAG, "Financial Privacy Active: Auto-scanning paused inside verified banking environment ($activeAppName / $packageName)")
      }
      return
    } else {
      if (_isBankingPrivacyActive.value) {
        _isBankingPrivacyActive.value = false
        _detectedBankingAppName.value = null
        LinkGuardianService.updateBankingPrivacyStatus(applicationContext, isPaused = false, appName = null)
      }
    }

    if (resolvedScreenUrls.isNotEmpty()) {
      // AUTOMATION MODE:
      // Automatically scans on-screen links on its own in real-time across all apps and non-banking browser pages!
      if (_isAutomationModeEnabled.value) {
        val newUrls = resolvedScreenUrls.filter { !scannedUrlsHistory.contains(it) }
        if (newUrls.isNotEmpty()) {
          proactivelyAuditLinks(newUrls, text)
        }
      }
    }
  }

  private fun proactivelyAuditLinks(urls: List<String>, messageContext: String) {
    serviceScope.launch {
      for (url in urls) {
        if (scannedUrlsHistory.size > 25) {
          scannedUrlsHistory.clear()
        }
        scannedUrlsHistory.add(url)
        try {
          Log.i(TAG, "Automation Guard: Proactively evaluating on-screen link on its own: $url")
          val result = SafeAnalysisEngine.analyze(
            inputMessage = messageContext,
            inputUrl = url,
            isPrivacyMode = true
          )

          _latestAutoScanResult.value = result

          val isThreat = result.threatLevel == ThreatLevel.DANGER || result.threatLevel == ThreatLevel.SUSPICIOUS || result.overallScore >= 45
          LinkGuardianService.updateTelemetry(applicationContext, result.overallScore, isThreat)

          if (isThreat) {
            val alertSummary = "${result.inputUrl} (${result.overallScore}/100) • ${result.attackVector}"
            LinkGuardianService.updateThreatAlert(applicationContext, alertSummary, result.overallScore)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            NotificationHelper.createNotificationChannel(applicationContext)
            val notif = NotificationHelper.buildAutoScanAlertNotification(applicationContext, result)
            notificationManager.notify(NotificationHelper.AUTO_ALERT_NOTIFICATION_ID, notif)
          } else {
            LinkGuardianService.clearThreatAlert(applicationContext)
          }
        } catch (e: Exception) {
          Log.w(TAG, "Proactive audit failed for $url: ${e.message}")
        }
      }
    }
  }

  fun scanCurrentWindowDirectly(): List<String> {
    val node = rootInActiveWindow ?: return if (_isBankingPrivacyActive.value && activeBankingDomainInBrowser != null) {
      listOf("https://$activeBankingDomainInBrowser")
    } else emptyList()
    val collectedText = StringBuilder()
    traverseNodes(node, collectedText)
    val text = collectedText.toString()
    val extracted = SafeAnalysisEngine.extractAllUrls(text)
    if (extracted.isNotEmpty()) return extracted
    if (_isBankingPrivacyActive.value && activeBankingDomainInBrowser != null) {
      return listOf("https://$activeBankingDomainInBrowser")
    }
    return emptyList()
  }

  private fun traverseNodes(node: AccessibilityNodeInfo?, stringBuilder: StringBuilder) {
    if (node == null) return
    val nodeText = node.text?.toString()
    if (!nodeText.isNullOrBlank()) {
      stringBuilder.append(nodeText).append(" ")
    }
    val contentDesc = node.contentDescription?.toString()
    if (!contentDesc.isNullOrBlank() && contentDesc != nodeText) {
      stringBuilder.append(contentDesc).append(" ")
    }
    for (i in 0 until node.childCount) {
      traverseNodes(node.getChild(i), stringBuilder)
    }
  }

  override fun onInterrupt() {}

  override fun onDestroy() {
    super.onDestroy()
    serviceScope.cancel()
    if (instance == this) {
      instance = null
    }
  }
}

package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.AssistantOverlayActivity
import com.example.data.analyzer.DomainAnalysis
import com.example.data.analyzer.TyposquattingDetector
import com.example.data.local.AppDatabase
import com.example.data.model.ScamPreset
import com.example.data.model.ThreatAnalysisResult
import com.example.data.repository.RadarTrend
import com.example.data.repository.ScamPresetRepository
import com.example.data.repository.ScamShieldRepository
import com.example.service.LinkGuardianService
import com.example.service.ScreenLinkAccessibilityService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuizQuestion(
  val question: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String
)

class ScamShieldViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: ScamShieldRepository

  init {
    val database = AppDatabase.getDatabase(application)
    repository = ScamShieldRepository(database.threatReportDao())
  }

  val savedReports: StateFlow<List<ThreatAnalysisResult>> = repository.savedReports
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _inputText = MutableStateFlow("")
  val inputText: StateFlow<String> = _inputText.asStateFlow()

  private val _inputUrl = MutableStateFlow("")
  val inputUrl: StateFlow<String> = _inputUrl.asStateFlow()

  private val _isAnalyzing = MutableStateFlow(false)
  val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

  private val _analysisProgress = MutableStateFlow("")
  val analysisProgress: StateFlow<String> = _analysisProgress.asStateFlow()

  private val _currentResult = MutableStateFlow<ThreatAnalysisResult?>(null)
  val currentResult: StateFlow<ThreatAnalysisResult?> = _currentResult.asStateFlow()

  private val _selectedTab = MutableStateFlow(0)
  val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

  // Theme Mode: null = follow system, true = Dark, false = Light
  private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
  val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

  // Dev Mode toggle
  private val _isDevMode = MutableStateFlow(false)
  val isDevMode: StateFlow<Boolean> = _isDevMode.asStateFlow()

  // Notification Permission & Samsung Now Bar Mode
  private val _hasNotificationPermission = MutableStateFlow(false)
  val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()

  private val _isNotificationBannerDismissed = MutableStateFlow(false)
  val isNotificationBannerDismissed: StateFlow<Boolean> = _isNotificationBannerDismissed.asStateFlow()

  private val _isBackgroundGuardian = MutableStateFlow(false)
  val isBackgroundGuardian: StateFlow<Boolean> = _isBackgroundGuardian.asStateFlow()

  private val _isNowbarMeterMode = MutableStateFlow(true) // Samsung Now Bar mode enabled by default
  val isNowbarMeterMode: StateFlow<Boolean> = _isNowbarMeterMode.asStateFlow()

  // Privacy Mode: Auto-redacts personal tokens and private query params
  private val _isPrivacyMode = MutableStateFlow(true)
  val isPrivacyMode: StateFlow<Boolean> = _isPrivacyMode.asStateFlow()

  // Screen Link Accessibility Service status
  private val _isAccessibilityEnabled = MutableStateFlow(false)
  val isAccessibilityEnabled: StateFlow<Boolean> = _isAccessibilityEnabled.asStateFlow()

  private val _isAccessibilityBannerDismissed = MutableStateFlow(false)
  val isAccessibilityBannerDismissed: StateFlow<Boolean> = _isAccessibilityBannerDismissed.asStateFlow()

  val presets: List<ScamPreset> = ScamPresetRepository.PRESETS
  val radarAlerts: List<RadarTrend> = ScamPresetRepository.LIVE_SCAM_RADAR

  // Utility tool state
  private val _defangInput = MutableStateFlow("")
  val defangInput: StateFlow<String> = _defangInput.asStateFlow()

  private val _defangOutput = MutableStateFlow("")
  val defangOutput: StateFlow<String> = _defangOutput.asStateFlow()

  private val _typosquatInput = MutableStateFlow("")
  val typosquatInput: StateFlow<String> = _typosquatInput.asStateFlow()

  private val _typosquatResult = MutableStateFlow<DomainAnalysis?>(null)
  val typosquatResult: StateFlow<DomainAnalysis?> = _typosquatResult.asStateFlow()

  // Academy Quiz State
  private val _userQuizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
  val userQuizAnswers: StateFlow<Map<Int, Int>> = _userQuizAnswers.asStateFlow()

  val academyQuiz = listOf(
    QuizQuestion(
      question = "Which part of 'https://chase.com.security-verify.xyz/login' is the true registered root domain?",
      options = listOf("chase.com", "security-verify.xyz", "login", "com.security"),
      correctIndex = 1,
      explanation = "Scammers place legitimate brand names in subdomains ('chase.com.'). The actual domain you connect to is always the one preceding the top-level suffix: 'security-verify.xyz'."
    ),
    QuizQuestion(
      question = "Why do modern phishing campaigns chain 3 to 4 URL shorteners (e.g., bit.ly -> tinyurl -> target)?",
      options = listOf(
        "To make the website load faster on 5G",
        "To confuse victims by making links shorter",
        "To bypass automated perimeter email and SMS security scanners",
        "Because shortened URLs have better SEO"
      ),
      correctIndex = 2,
      explanation = "Automated email/SMS gateways only inspect the first link. Chaining multi-hop redirects tricks basic scanners while landing victims on the credential trap."
    ),
    QuizQuestion(
      question = "What is the safest action if you receive an unexpected SMS saying your bank card is locked?",
      options = listOf(
        "Click the link immediately to see if your balance is intact",
        "Reply 'STOP' or send your PIN to cancel the alert",
        "Do not click. Open your bank's official app or call the number on the back of your physical card",
        "Forward the link to 5 friends to see if their cards are locked too"
      ),
      correctIndex = 2,
      explanation = "Never use contact information provided in an unsolicited alert. Always initiate contact through known authentic channels (e.g. the phone number on your physical card)."
    )
  )

  init {
    viewModelScope.launch {
      val firstPreset = ScamPresetRepository.PRESETS.first()
      _inputText.value = firstPreset.message
      _inputUrl.value = firstPreset.url
      analyzeCurrentPayload(showDelay = false)
    }
  }

  fun refreshPermissions(context: Context) {
    _isAccessibilityEnabled.value = ScreenLinkAccessibilityService.isAccessibilityServiceEnabled(context)
  }

  fun toggleTheme() {
    _isDarkTheme.value = when (_isDarkTheme.value) {
      null -> true
      true -> false
      false -> true
    }
  }

  fun toggleDevMode() {
    _isDevMode.value = !_isDevMode.value
  }

  fun togglePrivacyMode() {
    _isPrivacyMode.value = !_isPrivacyMode.value
  }

  val isAutomationMode: StateFlow<Boolean> = ScreenLinkAccessibilityService.isAutomationModeEnabled
  val isBankingPrivacyActive: StateFlow<Boolean> = ScreenLinkAccessibilityService.isBankingPrivacyActive
  val latestAutoScanResult: StateFlow<ThreatAnalysisResult?> = ScreenLinkAccessibilityService.latestAutoScanResult

  fun toggleAutomationMode() {
    val current = ScreenLinkAccessibilityService.isAutomationModeEnabled.value
    ScreenLinkAccessibilityService.setAutomationMode(!current)
  }

  fun onNotificationPermissionResult(granted: Boolean, context: Context) {
    _hasNotificationPermission.value = granted
    if (granted) {
      _isBackgroundGuardian.value = true
      LinkGuardianService.startService(context, _isNowbarMeterMode.value)
    }
  }

  fun dismissNotificationBanner() {
    _isNotificationBannerDismissed.value = true
  }

  fun dismissAccessibilityBanner() {
    _isAccessibilityBannerDismissed.value = true
  }

  fun toggleBackgroundGuardian(context: Context) {
    val newState = !_isBackgroundGuardian.value
    _isBackgroundGuardian.value = newState
    if (newState) {
      LinkGuardianService.startService(context, _isNowbarMeterMode.value)
    } else {
      LinkGuardianService.stopService(context)
    }
  }

  fun toggleNowbarMeterMode(context: Context) {
    val newState = !_isNowbarMeterMode.value
    _isNowbarMeterMode.value = newState
    if (_isBackgroundGuardian.value) {
      LinkGuardianService.startService(context, newState)
    }
  }

  fun launchAssistantOverlayOverAnyApp(context: Context) {
    val intent = Intent(context, AssistantOverlayActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    context.startActivity(intent)
  }

  fun onInputTextChanged(text: String) {
    _inputText.value = text
  }

  fun onInputUrlChanged(url: String) {
    _inputUrl.value = url
  }

  fun selectTab(tab: Int) {
    _selectedTab.value = tab
  }

  fun loadPreset(preset: ScamPreset) {
    _inputText.value = preset.message
    _inputUrl.value = preset.url
    analyzeCurrentPayload(showDelay = true)
  }

  fun analyzeCurrentPayload(showDelay: Boolean = true) {
    val msg = _inputText.value
    val url = _inputUrl.value
    if (msg.isBlank() && url.isBlank()) return

    viewModelScope.launch {
      _isAnalyzing.value = true
      if (showDelay) {
        _analysisProgress.value = "Consulting PhishTank & multi-vendor threat intelligence..."
        delay(300)
        _analysisProgress.value = "Inspecting redirect destinations & page forms..."
        delay(350)
        _analysisProgress.value = "Synthesizing evidence & identity consistency..."
        delay(300)
      }

      val result = repository.analyze(msg, url, _isPrivacyMode.value)
      _currentResult.value = result
      _isAnalyzing.value = false
      _analysisProgress.value = ""

      val isThreat = result.overallScore >= 35
      LinkGuardianService.updateTelemetry(getApplication(), result.overallScore, isThreat)
    }
  }

  fun selectReport(result: ThreatAnalysisResult) {
    _currentResult.value = result
    _inputText.value = result.inputMessage
    _inputUrl.value = result.inputUrl
    _selectedTab.value = 0
  }

  fun deleteReport(id: String) {
    viewModelScope.launch {
      repository.deleteReport(id)
      if (_currentResult.value?.id == id) {
        _currentResult.value = null
      }
    }
  }

  fun clearAllReports() {
    viewModelScope.launch {
      repository.clearAllReports()
      _currentResult.value = null
    }
  }

  fun clearInputs() {
    _inputText.value = ""
    _inputUrl.value = ""
  }

  fun updateDefangInput(text: String) {
    _defangInput.value = text
  }

  fun defangText() {
    _defangOutput.value = TyposquattingDetector.defangUrl(_defangInput.value)
  }

  fun refangText() {
    _defangOutput.value = TyposquattingDetector.refangUrl(_defangInput.value)
  }

  fun updateTyposquatInput(text: String) {
    _typosquatInput.value = text
    if (text.isNotBlank()) {
      _typosquatResult.value = TyposquattingDetector.analyzeDomain(text)
    } else {
      _typosquatResult.value = null
    }
  }

  fun answerQuiz(questionIndex: Int, selectedOption: Int) {
    val map = _userQuizAnswers.value.toMutableMap()
    map[questionIndex] = selectedOption
    _userQuizAnswers.value = map
  }
}

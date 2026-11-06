package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ScamShieldApp
import com.example.ui.ScamShieldViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private val viewModel: ScamShieldViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    intent?.let { handleIntent(it) }

    setContent {
      val isDarkThemePreference by viewModel.isDarkTheme.collectAsStateWithLifecycle()
      val systemDark = isSystemInDarkTheme()
      val isDark = isDarkThemePreference ?: systemDark

      // Runtime Notification Permission Request on Open (Android 13+)
      val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
      ) { isGranted ->
        viewModel.onNotificationPermissionResult(isGranted, this)
      }

      LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            viewModel.onNotificationPermissionResult(true, this@MainActivity)
          } else {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        } else {
          // Pre-Android 13 permissions are granted at install time
          viewModel.onNotificationPermissionResult(true, this@MainActivity)
        }
      }

      MyApplicationTheme(darkTheme = isDark) {
        ScamShieldApp(viewModel = viewModel)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.refreshPermissions(this)
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleIntent(intent)
  }

  private fun handleIntent(intent: Intent) {
    val autoUrl = intent.getStringExtra("extra_auto_analyze_url")
    if (!autoUrl.isNullOrBlank()) {
      viewModel.onInputUrlChanged(autoUrl)
      viewModel.analyzeCurrentPayload(showDelay = true)
      viewModel.selectTab(0)
    }
  }
}

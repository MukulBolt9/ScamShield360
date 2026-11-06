package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.ui.components.AssistantOverlaySheet
import com.example.ui.theme.MyApplicationTheme

class AssistantOverlayActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val initialSharedText = intent?.getStringExtra(Intent.EXTRA_TEXT)
      ?: intent?.dataString
      ?: intent?.getStringExtra("scan_text")
      ?: ""

    setContent {
      MyApplicationTheme(darkTheme = isSystemInDarkTheme()) {
        AssistantOverlaySheet(
          initialSharedText = initialSharedText,
          onDismiss = { finish() },
          onOpenInFullApp = { url ->
            val intent = Intent(this, MainActivity::class.java).apply {
              flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
              putExtra("extra_auto_analyze_url", url)
            }
            startActivity(intent)
            finish()
          }
        )
      }
    }
  }

  override fun finish() {
    super.finish()
    // Smooth fade out without jarring window transitions
    overridePendingTransition(0, android.R.anim.fade_out)
  }
}

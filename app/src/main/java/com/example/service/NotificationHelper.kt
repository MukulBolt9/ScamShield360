package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.AssistantOverlayActivity
import com.example.MainActivity
import com.example.R

object NotificationHelper {
  const val CHANNEL_ID = "scamshield_guardian_channel"
  const val CHANNEL_NAME = "ScamShield Live Protection & Now Bar"
  const val NOTIFICATION_ID = 1001

  const val AUTO_ALERT_CHANNEL_ID = "scamshield_auto_alert_channel"
  const val AUTO_ALERT_CHANNEL_NAME = "ScamShield Real-Time Security Alerts"
  const val AUTO_ALERT_NOTIFICATION_ID = 2001

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val manager = context.getSystemService(NotificationManager::class.java)

      // Live ongoing channel optimized for Android 16 Live Updates & Samsung One UI 7 Now Bar
      val liveChannel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Live ongoing background link protection, Pixel Live Updates, and Samsung One UI 7 Now Bar status."
        setShowBadge(false)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        enableLights(true)
        lightColor = Color.parseColor("#2563EB")
      }
      manager?.createNotificationChannel(liveChannel)

      val alertChannel = NotificationChannel(
        AUTO_ALERT_CHANNEL_ID,
        AUTO_ALERT_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Real-time threat alerts integrated with ScamShield Live Update system."
        setShowBadge(true)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
      }
      manager?.createNotificationChannel(alertChannel)
    }
  }

  fun buildLiveNotification(
    context: Context,
    isNowbarMode: Boolean = true,
    scannedCount: Int,
    threatCount: Int,
    latestScore: Int,
    isBankingShieldPaused: Boolean = false,
    currentBankingAppName: String? = null,
    latestThreatAlertText: String? = null
  ): Notification {
    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val openAppPendingIntent = PendingIntent.getActivity(
      context, 0, openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Opens floating scanner popup directly over whatever external app is currently open!
    val scanIntent = Intent(context, AssistantOverlayActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val scanPendingIntent = PendingIntent.getActivity(
      context, 1, scanIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val statusTitle = when {
      isBankingShieldPaused -> "⏸️ ScamShield Paused • ${currentBankingAppName ?: "Banking Suite"}"
      latestThreatAlertText != null && latestScore >= 70 -> "🚨 Critical Threat Flagged • $latestScore/100"
      latestThreatAlertText != null -> "⚠️ Suspicious Link Flagged • $latestScore/100"
      latestScore > 0 -> "🛡️ ScamShield Live • Protected"
      else -> "🛡️ ScamShield Live • Active Protection"
    }

    val subTextStr = when {
      isBankingShieldPaused -> "Privacy Shield • Active"
      latestThreatAlertText != null && latestScore >= 70 -> "Live Alert • Danger"
      latestThreatAlertText != null -> "Live Alert • Suspicious"
      else -> "Pixel & Samsung • Live Update"
    }

    val contentLine = when {
      isBankingShieldPaused -> "Screen reading suspended to protect PINs & balances inside ${currentBankingAppName ?: "Banking Suite"}"
      latestThreatAlertText != null -> latestThreatAlertText
      else -> "Audited: $scannedCount links • Threats Blocked: $threatCount"
    }

    val bodyText = when {
      isBankingShieldPaused -> "🔒 Banking Privacy Shield ACTIVE inside ${currentBankingAppName ?: "Banking Suite"}.\nScreen reading suspended to protect PINs & balances."
      latestThreatAlertText != null -> "$latestThreatAlertText\nTap 'Scan Screen' for full evidence matrix."
      else -> "Status: Protection ACTIVE • Audited: $scannedCount • Threats Blocked: $threatCount\nTap 'Scan Screen' to inspect any on-screen link."
    }

    val tintColor = when {
      isBankingShieldPaused -> Color.parseColor("#10B981") // Emerald Green for Privacy Shield
      latestThreatAlertText != null && latestScore >= 70 -> Color.parseColor("#EF4444") // Danger Red ONLY for active critical threats
      latestThreatAlertText != null || latestScore >= 45 -> Color.parseColor("#F59E0B") // Amber Warning for Suspicious
      else -> Color.parseColor("#2563EB") // Blue for active safe monitoring
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentIntent(openAppPendingIntent)
      .setOngoing(true)
      .setOnlyAlertOnce(true)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setCategory(NotificationCompat.CATEGORY_STATUS)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setColor(tintColor)
      .setColorized(true)
      .setShowWhen(true)
      .setUsesChronometer(false)
      .setSubText(subTextStr)
      .setContentTitle(statusTitle)
      .setContentText(contentLine)
      .setProgress(100, if (isBankingShieldPaused) 0 else (if (latestScore > 0) latestScore else 100), false)
      .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
      .addAction(R.drawable.ic_launcher_foreground, "🔍 Scan Screen", scanPendingIntent)
      .addAction(R.drawable.ic_launcher_foreground, "🛡️ Open App", openAppPendingIntent)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
    }

    return builder.build()
  }

  fun buildAutoScanAlertNotification(
    context: Context,
    result: com.example.data.model.ThreatAnalysisResult
  ): Notification {
    // Legacy helper kept for backward compatibility; live updates are routed through buildLiveNotification
    val alertSummary = "${if (result.overallScore >= 70) "Critical Scam" else "Suspicious Link"}: ${result.inputUrl.take(35)}... (${result.overallScore}/100)"
    return buildLiveNotification(
      context = context,
      isNowbarMode = true,
      scannedCount = LinkGuardianService.totalScanned,
      threatCount = LinkGuardianService.totalThreats,
      latestScore = result.overallScore,
      latestThreatAlertText = alertSummary
    )
  }
}


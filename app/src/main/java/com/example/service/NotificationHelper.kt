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
  const val CHANNEL_NAME = "ScamShield Live Link Protection"
  const val NOTIFICATION_ID = 1001

  const val AUTO_ALERT_CHANNEL_ID = "scamshield_auto_alert_channel"
  const val AUTO_ALERT_CHANNEL_NAME = "ScamShield Proactive Screen Alerts"
  const val AUTO_ALERT_NOTIFICATION_ID = 2001

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val manager = context.getSystemService(NotificationManager::class.java)

      val liveChannel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Live ongoing background protection and Samsung One UI 7 Now Bar live status."
        setShowBadge(false)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        enableLights(true)
        lightColor = Color.parseColor("#2563EB")
      }
      manager?.createNotificationChannel(liveChannel)

      val alertChannel = NotificationChannel(
        AUTO_ALERT_CHANNEL_ID,
        AUTO_ALERT_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Proactive real-time heads-up alerts when suspicious links appear on screen."
        setShowBadge(true)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 200, 100, 200)
        enableLights(true)
        lightColor = Color.parseColor("#EF4444")
      }
      manager?.createNotificationChannel(alertChannel)
    }
  }

  fun buildLiveNotification(
    context: Context,
    isNowbarMode: Boolean,
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

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentIntent(openAppPendingIntent)
      .setOngoing(true)
      .setOnlyAlertOnce(true)

    val statusTitle = when {
      isBankingShieldPaused -> "⏸️ ScamShield Paused • ${currentBankingAppName ?: "Banking Suite"}"
      latestThreatAlertText != null && latestScore >= 70 -> "🚨 Critical Threat Blocked • $latestScore%"
      latestThreatAlertText != null -> "⚠️ Suspicious Link Flagged • $latestScore%"
      latestScore > 0 -> "🛡️ ScamShield Live • $latestScore%"
      else -> "🛡️ ScamShield Live • Protected"
    }

    val subTextStr = when {
      isBankingShieldPaused -> "Privacy Shield • Paused"
      latestThreatAlertText != null && latestScore >= 70 -> "Critical Threat Alert"
      latestThreatAlertText != null -> "Security Warning"
      else -> "Now Bar • Protection Active"
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

    if (isNowbarMode) {
      // SAMSUNG ONE UI 7 NOW BAR & LIVE NOTIFICATION SPECIFICATION
      builder
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setCategory(NotificationCompat.CATEGORY_STATUS)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setColor(tintColor)
        .setColorized(true)
        .setShowWhen(true)
        .setUsesChronometer(false)
        .setSubText(subTextStr)
        .setContentTitle(statusTitle)
        .setContentText(if (isBankingShieldPaused) "Screen reading paused inside ${currentBankingAppName ?: "Banking Suite"}" else "Audited: $scannedCount • Blocked: $threatCount")
        .setProgress(100, if (isBankingShieldPaused) 0 else latestScore, false)
        .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
        .addAction(R.drawable.ic_launcher_foreground, "🔍 Scan Screen", scanPendingIntent)
        .addAction(R.drawable.ic_launcher_foreground, "🛡️ Open App", openAppPendingIntent)
    } else {
      // NORMAL MINIMAL MODE: Clean, simple, non-intrusive status
      builder
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setColor(tintColor)
        .setSubText(subTextStr)
        .setContentTitle(statusTitle)
        .setContentText("Audited: $scannedCount • Blocked: $threatCount")
        .setProgress(100, if (isBankingShieldPaused) 0 else latestScore, false)
        .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
        .addAction(R.drawable.ic_launcher_foreground, "Scan Screen", scanPendingIntent)
    }

    return builder.build()
  }

  fun buildAutoScanAlertNotification(
    context: Context,
    result: com.example.data.model.ThreatAnalysisResult
  ): Notification {
    val reviewIntent = Intent(context, AssistantOverlayActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra("EXTRA_INPUT_URL", result.inputUrl)
      putExtra("EXTRA_INPUT_TEXT", result.inputMessage)
    }
    val reviewPendingIntent = PendingIntent.getActivity(
      context, 201, reviewIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val isDanger = result.threatLevel == com.example.data.model.ThreatLevel.DANGER
    val iconColor = if (isDanger) Color.parseColor("#EF4444") else Color.parseColor("#F59E0B")
    val titlePrefix = if (isDanger) "🚨 CRITICAL THREAT ON SCREEN" else "⚠️ SUSPICIOUS LINK ON SCREEN"

    // ON MODERN ANDROID (Android 14+ / 15 / 16+), UPDATE LIVE NOTIFICATION DIRECTLY TO AVOID DISRUPTIVE PUSH BANNERS
    if (Build.VERSION.SDK_INT >= 34) {
      val alertSummary = "$titlePrefix: ${result.inputUrl.take(35)}... (${result.overallScore}/100)"
      LinkGuardianService.updateThreatAlert(context, alertSummary, result.overallScore)
    }

    return NotificationCompat.Builder(context, AUTO_ALERT_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setColor(iconColor)
      .setContentTitle("$titlePrefix • ${result.overallScore}/100")
      .setContentText("${result.inputUrl.take(45)}... ${result.verdictSummary}")
      .setStyle(
        NotificationCompat.BigTextStyle().bigText(
          "Link detected: ${result.inputUrl}\n\nEvidence: ${result.verdictSummary}\nIdentity: ${result.identityConsistency?.consistencyVerdict ?: result.attackVector}\n\nTap to review full evidence matrix safely."
        )
      )
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setAutoCancel(true)
      .setContentIntent(reviewPendingIntent)
      .addAction(R.drawable.ic_launcher_foreground, "🔍 View Evidence Matrix", reviewPendingIntent)
      .build()
  }
}

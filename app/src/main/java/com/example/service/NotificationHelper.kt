package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
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

      // Live ongoing channel optimized for Google Pixel Live Updates & Samsung One UI 7 Now Bar
      val liveChannel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Live ongoing background link protection, Pixel status bar live updates, and Samsung One UI 7 Now Bar meter status."
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

    val chipText = when {
      isBankingShieldPaused -> "Privacy Shield"
      latestThreatAlertText != null && latestScore >= 70 -> "Scam Threat!"
      latestThreatAlertText != null -> "Suspicious Link"
      else -> "ScamShield Live"
    }

    val contentLine = when {
      isBankingShieldPaused -> "Screen reading suspended inside ${currentBankingAppName ?: "Banking Suite"}"
      latestThreatAlertText != null -> latestThreatAlertText
      else -> "Audited: $scannedCount links • Blocked: $threatCount threats"
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

    val progressVal = if (isBankingShieldPaused) 0 else (if (latestScore > 0) latestScore else 100)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_notification_shield)
      .setContentIntent(openAppPendingIntent)
      .setOngoing(true)
      .setOnlyAlertOnce(true)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setCategory(NotificationCompat.CATEGORY_PROGRESS)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setColor(tintColor)
      .setColorized(true)
      .setShowWhen(true)
      .setUsesChronometer(false)
      .setSubText(chipText)
      .setContentTitle(statusTitle)
      .setContentText(contentLine)
      .setProgress(100, progressVal, false)
      .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
      .addAction(R.drawable.ic_notification_shield, "🔍 Scan Screen", scanPendingIntent)
      .addAction(R.drawable.ic_notification_shield, "🛡️ Open App", openAppPendingIntent)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      builder.setLocusId(androidx.core.content.LocusIdCompat("scamshield_live_update"))
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
    }

    // SAMSUNG ONE UI 7 NOWBAR METER CUSTOM LAYOUT (ONLY applied when isNowbarMode is true)
    if (isNowbarMode) {
      try {
        val expandedViews = RemoteViews(context.packageName, R.layout.nowbar_meter_notification).apply {
          setTextViewText(R.id.nowbar_title, statusTitle)
          setTextViewText(R.id.nowbar_subtext, contentLine)
          setTextViewText(R.id.nowbar_score_badge, if (isBankingShieldPaused) "PAUSED" else "$progressVal%")
          setProgressBar(R.id.nowbar_progress_meter, 100, progressVal, false)
        }

        val collapsedViews = RemoteViews(context.packageName, R.layout.nowbar_meter_collapsed).apply {
          setTextViewText(R.id.nowbar_collapsed_title, statusTitle)
          setTextViewText(R.id.nowbar_collapsed_badge, if (isBankingShieldPaused) "PAUSED" else "$progressVal%")
        }

        builder
          .setCustomContentView(collapsedViews)
          .setCustomBigContentView(expandedViews)
          .setStyle(NotificationCompat.DecoratedCustomViewStyle())
      } catch (e: Exception) {
        // Fallback to standard Google Pixel Live Update layout if RemoteViews fails
      }
    }

    return builder.build()
  }

  fun buildAutoScanAlertNotification(
    context: Context,
    result: com.example.data.model.ThreatAnalysisResult
  ): Notification {
    val alertSummary = "${if (result.overallScore >= 70) "Critical Scam" else "Suspicious Link"}: ${result.inputUrl.take(35)}... (${result.overallScore}/100)"
    return buildLiveNotification(
      context = context,
      isNowbarMode = LinkGuardianService.isNowbarMode,
      scannedCount = LinkGuardianService.totalScanned,
      threatCount = LinkGuardianService.totalThreats,
      latestScore = result.overallScore,
      latestThreatAlertText = alertSummary
    )
  }
}

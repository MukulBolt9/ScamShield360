package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.AssistantOverlayActivity
import com.example.MainActivity
import com.example.R

object NotificationHelper {
  private const val TAG = "NotificationHelper"

  const val CHANNEL_ID = "scamshield_guardian_channel"
  const val CHANNEL_NAME = "ScamShield Live Protection & Status Chip"
  const val NOTIFICATION_ID = 1001

  const val AUTO_ALERT_CHANNEL_ID = "scamshield_auto_alert_channel"
  const val AUTO_ALERT_CHANNEL_NAME = "ScamShield Real-Time Security Alerts"
  const val AUTO_ALERT_NOTIFICATION_ID = 2001

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val manager = context.getSystemService(NotificationManager::class.java)

      // Live ongoing channel optimized for Android 16 Live Updates and Status Bar Chip
      val liveChannel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Live ongoing background link protection, Android 16 Status Bar Live Update Chip, and Samsung One UI Now Bar meter."
        setShowBadge(false)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
      }
      manager?.createNotificationChannel(liveChannel)

      val alertChannel = NotificationChannel(
        AUTO_ALERT_CHANNEL_ID,
        AUTO_ALERT_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Real-time threat alerts integrated with ScamShield Live Update system."
        setShowBadge(true)
        enableVibration(true)
        enableLights(true)
        lightColor = Color.parseColor("#EF4444")
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
      }
      manager?.createNotificationChannel(alertChannel)
    }
  }

  fun canPostPromotedNotifications(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= 36) {
      val manager = context.getSystemService(NotificationManager::class.java)
      return try {
        manager?.canPostPromotedNotifications() ?: true
      } catch (_: Throwable) {
        true
      }
    }
    return true
  }

  fun openPromotionSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= 36) {
      try {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_PROMOTION_SETTINGS).apply {
          putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return
      } catch (_: Throwable) {}
    }
    try {
      val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
    } catch (_: Throwable) {}
  }

  fun buildLiveNotification(
    context: Context,
    scannedCount: Int,
    threatCount: Int,
    latestScore: Int,
    isBankingShieldPaused: Boolean = false,
    currentBankingAppName: String? = null,
    latestThreatAlertText: String? = null,
    isSamsungNowbarEnabled: Boolean = true
  ): Notification {
    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val openAppPendingIntent = PendingIntent.getActivity(
      context, 0, openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Opens floating scanner popup directly over whatever external app is currently open
    val scanIntent = Intent(context, AssistantOverlayActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val scanPendingIntent = PendingIntent.getActivity(
      context, 1, scanIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val statusTitle = when {
      isBankingShieldPaused -> "⏸️ ScamShield Paused • ${currentBankingAppName ?: "Banking Suite"}"
      latestThreatAlertText != null && latestScore >= 70 -> "🚨 Critical Threat • $latestScore/100"
      latestThreatAlertText != null -> "⚠️ Suspicious Link • $latestScore/100"
      latestScore > 0 -> "🛡️ ScamShield Protected"
      else -> "🛡️ ScamShield Active"
    }

    // Short critical text for Android 16 Status Bar Chip (must be <= 7 characters to avoid clipping)
    val shortChipText = when {
      isBankingShieldPaused -> "Pause"
      latestThreatAlertText != null && latestScore >= 70 -> "Scam!"
      latestThreatAlertText != null -> "Alert"
      else -> "Safe"
    }

    val contentLine = when {
      isBankingShieldPaused -> "Screen reading suspended in ${currentBankingAppName ?: "Banking Suite"}"
      latestThreatAlertText != null -> latestThreatAlertText
      else -> "Audited: $scannedCount links • Blocked: $threatCount threats"
    }

    val bodyText = when {
      isBankingShieldPaused -> "🔒 Banking Privacy Shield ACTIVE inside ${currentBankingAppName ?: "Banking Suite"}.\nScreen reading suspended to protect PINs & balances."
      latestThreatAlertText != null -> "$latestThreatAlertText\nTap 'Scan Screen' for full evidence matrix."
      else -> "Status: Protection ACTIVE • Audited: $scannedCount • Threats Blocked: $threatCount\nTap 'Scan Screen' to inspect any on-screen link."
    }

    val progressVal = if (isBankingShieldPaused) 0 else (if (latestScore > 0) latestScore else 100)

    // ANDROID 16+ ARCHITECTURE (API 36+):
    // On Android 16+, Samsung One UI (One UI 8+) natively translates platform Live Updates
    // (ProgressStyle, setShortCriticalText, FLAG_PROMOTED_ONGOING) into the Samsung Now Bar
    // (Lock screen, AOD, and status bar) without requiring custom RemoteViews!
    // Google Pixel also uses this exact format for its native status bar chip.
    if (Build.VERSION.SDK_INT >= 36) {
      try {
        val progressStyle = Notification.ProgressStyle()
          .setProgress(progressVal)

        val nativeBuilder = Notification.Builder(context, CHANNEL_ID)
          .setSmallIcon(R.drawable.ic_notification_shield)
          .setContentTitle(statusTitle)
          .setContentText(contentLine)
          .setOngoing(true)
          .setOnlyAlertOnce(true)
          .setShortCriticalText(shortChipText)
          .setStyle(progressStyle)
          .setCategory(Notification.CATEGORY_PROGRESS)
          .setVisibility(Notification.VISIBILITY_PUBLIC)
          .setContentIntent(openAppPendingIntent)
          .addAction(
            Notification.Action.Builder(
              android.graphics.drawable.Icon.createWithResource(context, R.drawable.ic_notification_shield),
              "🔍 Scan Screen",
              scanPendingIntent
            ).build()
          )
          .addAction(
            Notification.Action.Builder(
              android.graphics.drawable.Icon.createWithResource(context, R.drawable.ic_notification_shield),
              "🛡️ Open App",
              openAppPendingIntent
            ).build()
          )

        nativeBuilder.extras.putBoolean("android.requestPromotedOngoing", true)
        nativeBuilder.extras.putCharSequence("android.shortCriticalText", shortChipText)

        try {
          val m = nativeBuilder.javaClass.getMethod("setRequestPromotedOngoing", Boolean::class.javaPrimitiveType)
          m.invoke(nativeBuilder, true)
        } catch (_: Throwable) {}

        val notification = nativeBuilder.build()
        // FLAG_PROMOTED_ONGOING = 262144 (0x40000)
        notification.flags = notification.flags or Notification.FLAG_PROMOTED_ONGOING

        Log.i(TAG, "Android 16 Live Update built. HasPromotableCharacteristics: ${notification.hasPromotableCharacteristics()}")
        return notification
      } catch (e: Throwable) {
        Log.w(TAG, "Native API 36 Builder failed, falling back: ${e.message}")
      }
    }

    // ANDROID 16 LOWER DEVICES (< API 36):
    // Dual approach:
    // If Samsung NowBar is enabled on Android 15/14 (One UI 7), apply the RemoteViews + DecoratedCustomViewStyle
    // template (per akexorcist.dev One UI 7 NowBar specification) so Samsung users get the lock screen meter!
    // Otherwise, build clean standard BigTextStyle notification.
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_notification_shield)
      .setContentIntent(openAppPendingIntent)
      .setOngoing(true)
      .setOnlyAlertOnce(true)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setCategory(NotificationCompat.CATEGORY_PROGRESS)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setShowWhen(true)
      .setSubText(shortChipText)
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

    builder.extras.putBoolean("android.requestPromotedOngoing", true)
    builder.extras.putCharSequence("android.shortCriticalText", shortChipText)

    if (isSamsungNowbarEnabled) {
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
        Log.w(TAG, "Samsung One UI 7 RemoteViews fallback to standard view: ${e.message}")
      }
    }

    val notification = builder.build()
    // FLAG_PROMOTED_ONGOING = 262144
    notification.flags = notification.flags or 262144

    return notification
  }

  fun buildAutoScanAlertNotification(
    context: Context,
    result: com.example.data.model.ThreatAnalysisResult
  ): Notification {
    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val openAppPendingIntent = PendingIntent.getActivity(
      context, 0, openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val scanIntent = Intent(context, AssistantOverlayActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val scanPendingIntent = PendingIntent.getActivity(
      context, 1, scanIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val title = if (result.overallScore >= 70) "🚨 Critical Scam Link Flagged!" else "⚠️ Suspicious Link Detected"
    val body = "Flagged: ${result.inputUrl}\nRisk Score: ${result.overallScore}/100 • ${result.attackVector}"

    val tintColor = if (result.overallScore >= 70) Color.parseColor("#EF4444") else Color.parseColor("#F59E0B")

    return NotificationCompat.Builder(context, AUTO_ALERT_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_notification_shield)
      .setContentTitle(title)
      .setContentText(body)
      .setStyle(NotificationCompat.BigTextStyle().bigText(body))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setDefaults(Notification.DEFAULT_ALL)
      .setAutoCancel(true)
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setColor(tintColor)
      .setColorized(true)
      .setContentIntent(scanPendingIntent)
      .addAction(R.drawable.ic_notification_shield, "🔍 Deep Audit", scanPendingIntent)
      .addAction(R.drawable.ic_notification_shield, "🛡️ Open App", openAppPendingIntent)
      .build()
  }
}

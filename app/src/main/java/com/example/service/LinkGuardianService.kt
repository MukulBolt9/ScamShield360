package com.example.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder

class LinkGuardianService : Service() {

  companion object {
    const val ACTION_START = "com.example.service.ACTION_START"
    const val ACTION_STOP = "com.example.service.ACTION_STOP"
    const val ACTION_UPDATE_STATS = "com.example.service.ACTION_UPDATE_STATS"
    const val ACTION_TOGGLE_NOWBAR = "com.example.service.ACTION_TOGGLE_NOWBAR"

    const val EXTRA_POWER_NOWBAR_MODE = "extra_power_nowbar_mode"

    @Volatile
    var isRunning = false
      private set

    @Volatile
    var isNowbarMode = false
      private set

    var totalScanned = 0
    var totalThreats = 0
    var latestScore = 0
    var isBankingShieldPaused = false
    var currentBankingAppName: String? = null
    var latestThreatAlertText: String? = null

    fun startService(context: Context, powerNowbarMode: Boolean) {
      val intent = Intent(context, LinkGuardianService::class.java).apply {
        action = ACTION_START
        putExtra(EXTRA_POWER_NOWBAR_MODE, powerNowbarMode)
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stopService(context: Context) {
      latestThreatAlertText = null
      latestScore = 0
      val intent = Intent(context, LinkGuardianService::class.java).apply {
        action = ACTION_STOP
      }
      context.startService(intent)
    }

    fun updateTelemetry(context: Context, score: Int, isThreat: Boolean) {
      totalScanned++
      if (isThreat) totalThreats++
      latestScore = score

      if (isRunning) {
        val intent = Intent(context, LinkGuardianService::class.java).apply {
          action = ACTION_UPDATE_STATS
        }
        context.startService(intent)
      }
    }

    fun updateBankingPrivacyStatus(context: Context, isPaused: Boolean, appName: String?) {
      isBankingShieldPaused = isPaused
      currentBankingAppName = appName
      if (isPaused) {
        latestThreatAlertText = null
        latestScore = 0
      }

      if (isRunning) {
        val intent = Intent(context, LinkGuardianService::class.java).apply {
          action = ACTION_UPDATE_STATS
        }
        context.startService(intent)
      }
    }

    fun updateThreatAlert(context: Context, alertSummary: String, score: Int = 85) {
      latestThreatAlertText = alertSummary
      latestScore = score
      if (isRunning) {
        val intent = Intent(context, LinkGuardianService::class.java).apply {
          action = ACTION_UPDATE_STATS
        }
        context.startService(intent)
      }
    }

    fun clearThreatAlert(context: Context) {
      latestThreatAlertText = null
      latestScore = 0
      if (isRunning) {
        val intent = Intent(context, LinkGuardianService::class.java).apply {
          action = ACTION_UPDATE_STATS
        }
        context.startService(intent)
      }
    }
  }

  override fun onCreate() {
    super.onCreate()
    NotificationHelper.createNotificationChannel(this)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_STOP -> {
        isRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        return START_NOT_STICKY
      }
      ACTION_START -> {
        isRunning = true
        isNowbarMode = intent.getBooleanExtra(EXTRA_POWER_NOWBAR_MODE, false)
        startForeground(
          NotificationHelper.NOTIFICATION_ID,
          NotificationHelper.buildLiveNotification(
            this, isNowbarMode, totalScanned, totalThreats, latestScore,
            isBankingShieldPaused, currentBankingAppName, latestThreatAlertText
          )
        )
      }
      ACTION_TOGGLE_NOWBAR -> {
        isNowbarMode = !isNowbarMode
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
          NotificationHelper.NOTIFICATION_ID,
          NotificationHelper.buildLiveNotification(
            this, isNowbarMode, totalScanned, totalThreats, latestScore,
            isBankingShieldPaused, currentBankingAppName, latestThreatAlertText
          )
        )
      }
      ACTION_UPDATE_STATS -> {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
          NotificationHelper.NOTIFICATION_ID,
          NotificationHelper.buildLiveNotification(
            this, isNowbarMode, totalScanned, totalThreats, latestScore,
            isBankingShieldPaused, currentBankingAppName, latestThreatAlertText
          )
        )
      }
    }
    return START_STICKY
  }

  override fun onBind(intent: Intent?): IBinder? = null
}

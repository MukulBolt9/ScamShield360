package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.analyzer.TyposquattingDetector
import com.example.data.model.ThreatLevel
import com.example.service.NotificationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ScamShield 360", appName)
  }

  @Test
  fun `typosquatting detector flags deceptive brand domains`() {
    val analysis = TyposquattingDetector.analyzeDomain("https://chase-security-login.xyz")
    assertTrue(analysis.riskScore > 30)
    assertTrue(analysis.hasSuspiciousTld)
  }

  @Test
  fun `defanger correctly neutralizes urls`() {
    val defanged = TyposquattingDetector.defangUrl("https://evil.com/login")
    assertEquals("hxxps://evil[.]com/login", defanged)
  }

  @Test
  fun `threat levels have clear human readable simple titles`() {
    assertEquals("Dangerous Scam: Do Not Open", ThreatLevel.DANGER.simpleTitle)
    assertEquals("Looks Safe to Open", ThreatLevel.SAFE.simpleTitle)
    assertEquals("Be Careful: Suspicious Signals", ThreatLevel.SUSPICIOUS.simpleTitle)
  }

  @Test
  fun `notification helper builds valid live nowbar notification`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val notification = NotificationHelper.buildLiveNotification(
      context = context,
      isNowbarMode = true,
      scannedCount = 10,
      threatCount = 2,
      latestScore = 80
    )
    assertNotNull(notification)
  }

  @Test
  fun `extractAllUrls finds multiple distinct links from message text`() {
    val message = "Urgent: Your parcel https://usps-parcel-fees.xyz/track is held. Also contact https://postal-help.site for support."
    val extracted = com.example.data.analyzer.SafeAnalysisEngine.extractAllUrls(message)
    assertEquals(2, extracted.size)
    assertTrue(extracted.any { it.contains("usps-parcel-fees.xyz") })
    assertTrue(extracted.any { it.contains("postal-help.site") })
  }
}

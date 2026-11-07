package com.example.data.analyzer

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class PhishTankResult(
  val isListed: Boolean,
  val inDatabase: Boolean,
  val verified: Boolean,
  val detailUrl: String?,
  val summaryText: String
)

data class UrlhausResult(
  val isListed: Boolean,
  val threatType: String?,
  val urlStatus: String?,
  val tags: List<String>,
  val referenceUrl: String?,
  val summaryText: String
)

data class ThreatCheckResult(
  val phishTank: PhishTankResult,
  val urlhaus: UrlhausResult,
  val virusTotal: VirusTotalReport,
  val isFlaggedByAny: Boolean
)

object ThreatDatabaseConnector {
  private const val TAG = "ThreatDbConnector"

  private val httpClient = OkHttpClient.Builder()
    .connectTimeout(3, TimeUnit.SECONDS)
    .readTimeout(3, TimeUnit.SECONDS)
    .callTimeout(4, TimeUnit.SECONDS)
    .build()

  suspend fun checkUrl(url: String, host: String): ThreatCheckResult = withContext(Dispatchers.IO) {
    val phishTankRes = queryPhishTank(url)
    val urlhausRes = queryUrlhaus(url, host)
    val vtRes = VirusTotalThreatProvider.fetchThreatReport(url, host)
    val flagged = phishTankRes.isListed || urlhausRes.isListed || vtRes.isFlagged

    ThreatCheckResult(
      phishTank = phishTankRes,
      urlhaus = urlhausRes,
      virusTotal = vtRes,
      isFlaggedByAny = flagged
    )
  }

  private fun queryUrlhaus(url: String, host: String): UrlhausResult {
    if (url.isBlank() && host.isBlank()) {
      return UrlhausResult(
        isListed = false,
        threatType = null,
        urlStatus = null,
        tags = emptyList(),
        referenceUrl = null,
        summaryText = "Clean / Unlisted on URLhaus (abuse.ch)"
      )
    }

    try {
      // 1. Try URL lookup first
      val requestBody = FormBody.Builder()
        .add("url", url)
        .build()

      val request = Request.Builder()
        .url("https://urlhaus-api.abuse.ch/v1/url/")
        .header("User-Agent", "ScamShield-360-Android/1.0")
        .post(requestBody)
        .build()

      val response = httpClient.newCall(request).execute()
      val jsonStr = response.body?.string()
      if (!jsonStr.isNullOrBlank()) {
        val json = JSONObject(jsonStr)
        val status = json.optString("query_status", "")
        if (status == "ok") {
          val threat = json.optString("threat", "malware_download")
          val urlStatus = json.optString("url_status", "online")
          val ref = if (json.has("urlhaus_reference")) json.optString("urlhaus_reference") else null
          val tagsArray = json.optJSONArray("tags")
          val tagsList = mutableListOf<String>()
          if (tagsArray != null) {
            for (i in 0 until tagsArray.length()) {
              tagsList.add(tagsArray.getString(i))
            }
          }
          val tagStr = if (tagsList.isNotEmpty()) " [${tagsList.joinToString()}]" else ""
          return UrlhausResult(
            isListed = true,
            threatType = threat,
            urlStatus = urlStatus,
            tags = tagsList,
            referenceUrl = ref,
            summaryText = "Flagged in URLhaus Feed: $threat ($urlStatus)$tagStr"
          )
        }
      }

      // 2. Try Host lookup if URL lookup was no_results
      if (host.isNotBlank()) {
        val hostBody = FormBody.Builder()
          .add("host", host)
          .build()

        val hostReq = Request.Builder()
          .url("https://urlhaus-api.abuse.ch/v1/host/")
          .header("User-Agent", "ScamShield-360-Android/1.0")
          .post(hostBody)
          .build()

        val hostResp = httpClient.newCall(hostReq).execute()
        val hostJsonStr = hostResp.body?.string()
        if (!hostJsonStr.isNullOrBlank()) {
          val hostJson = JSONObject(hostJsonStr)
          if (hostJson.optString("query_status", "") == "ok") {
            val urlCount = hostJson.optInt("url_count", 1)
            val ref = if (hostJson.has("urlhaus_reference")) hostJson.optString("urlhaus_reference") else null
            return UrlhausResult(
              isListed = true,
              threatType = "malware_host",
              urlStatus = "active_host",
              tags = listOf("malware_distribution"),
              referenceUrl = ref,
              summaryText = "Flagged: Domain host listed in URLhaus with $urlCount malicious URL payload(s)"
            )
          }
        }
      }
    } catch (e: Exception) {
      Log.w(TAG, "URLhaus API query skipped or offline: ${e.message}")
    }

    return UrlhausResult(
      isListed = false,
      threatType = null,
      urlStatus = null,
      tags = emptyList(),
      referenceUrl = null,
      summaryText = "Clean / Unlisted on URLhaus (abuse.ch)"
    )
  }

  private fun queryPhishTank(url: String): PhishTankResult {
    if (url.isBlank()) {
      return PhishTankResult(
        isListed = false,
        inDatabase = false,
        verified = false,
        detailUrl = null,
        summaryText = "Not Listed in PhishTank (Unlisted ≠ Safe)"
      )
    }

    try {
      val requestBody = FormBody.Builder()
        .add("url", url)
        .add("format", "json")
        .build()

      val request = Request.Builder()
        .url("https://checkurl.phishtank.com/checkurl/")
        .header("User-Agent", "phishtank/ScamShield360-Android")
        .post(requestBody)
        .build()

      val response = httpClient.newCall(request).execute()
      val jsonStr = response.body?.string()
      if (!jsonStr.isNullOrBlank()) {
        val json = JSONObject(jsonStr)
        val resultsObj = json.optJSONObject("results")
        if (resultsObj != null) {
          val inDb = resultsObj.optBoolean("in_database", false)
          val verified = resultsObj.optBoolean("verified", false)
          val valid = resultsObj.optBoolean("valid", false)
          val detail = if (resultsObj.has("phish_detail_page")) resultsObj.optString("phish_detail_page") else null
          if (inDb && (verified || valid)) {
            return PhishTankResult(
              isListed = true,
              inDatabase = true,
              verified = verified,
              detailUrl = detail,
              summaryText = "Listed in PhishTank (Confirmed Active Phishing Campaign)"
            )
          }
        }
      }
    } catch (e: Exception) {
      Log.w(TAG, "PhishTank API query skipped or offline: ${e.message}")
    }

    return PhishTankResult(
      isListed = false,
      inDatabase = false,
      verified = false,
      detailUrl = null,
      summaryText = "Not Listed in PhishTank Database"
    )
  }
}

# 🛡️ ScamShield 360 — Cybersecurity & Anti-Phishing Suite for Android

**ScamShield 360** is a modern Android security application built with **Kotlin** and **Jetpack Compose**. It features a **multi-source evidence engine** that analyzes suspicious URLs and text against real-time security databases (PhishTank, URLhaus by abuse.ch, Google Web Risk, and public DNS root servers), providing an AI-driven reasoning layer rather than relying on a single source of truth.

---

## 🚀 How to Push Code from AI Studio to GitHub

1. Click the **Export / GitHub** button in the top-right menu of Google AI Studio.
2. Select **Push to GitHub** or **Connect to GitHub Repository**.
3. Target repository: `MukulBolt9/ScamShield360`.

---

## 🛠️ How to Build the App

### Option 1: Automatic Build via GitHub Actions (Recommended)
This repository includes a pre-configured **GitHub Actions workflow** (`.github/workflows/android.yml`). 
- Whenever you push code to `main` or `master` on GitHub, GitHub Actions will automatically compile the project, run tests, and upload a ready-to-install **`app-debug.apk`** under the **Actions** tab.

### Option 2: Local Command Line Build
Ensure you have **JDK 17** (or JDK 21) installed.

```bash
# Clone the repository
git clone https://github.com/MukulBolt9/ScamShield360.git
cd ScamShield360

# Build Debug APK
gradle assembleDebug

# Run Unit Tests
gradle testDebugUnitTest
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### Option 3: Android Studio
1. Open **Android Studio** (Hedgehog 2023.1.1 or newer).
2. Select **Open** and choose the `ScamShield360` directory.
3. Wait for Gradle Sync to complete.
4. Click **Run > Run 'app'** or select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

---

## ⚡ Key Features

- **Multi-Source Evidence Matrix**: Integrates PhishTank API, URLhaus API, Google Web Risk, VirusTotal vendor aggregation, and DNS root lookup.
- **Financial Privacy Shield**: Automatically pauses accessibility screen reading inside verified banking suites and UPI apps (`yonoretail.sbi.bank.in`, YONO SBI, Axis Mobile, HDFC, ICICI, etc.) to protect PINs and account balances.
- **Universal Background Auto-Guard**: Scans on-screen links across messaging apps, SMS, and browser address bars in real time.
- **Samsung One UI 7 Now Bar & Live Notifications**: Real-time status updates in Android's notification shade.
- **Defanger Tool**: Turns malicious URLs into safe non-clickable text (`hxxps://evil[.]com`).

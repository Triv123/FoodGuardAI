<div align="center">

# 🥗 Food Guard AI

### *Know what you eat. Stay healthy.*

**Food Guard AI** is an Android app that turns your camera into a personal food-safety assistant. Scan any product barcode or nutrition label, get an instant AI-powered **safety rating**, full nutritional breakdown, banned-ingredient alerts, and a smart diet tracker — all with voice support.

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Language-Java-ED8B00?logo=java&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-24-1f6feb)
![Target SDK](https://img.shields.io/badge/targetSdk-34-1f6feb)
![License](https://img.shields.io/badge/License-MIT-blue)
![Build](https://img.shields.io/badge/build-passing-brightgreen)

</div>

---

## ✨ Features

| Feature | Description |
| --- | --- |
| 📊 **Barcode Scanning** | Live CameraX + ML Kit barcode scanner — point, scan, done. Manual entry fallback included. |
| 📄 **Label OCR Scanning** | Read nutrition labels with ML Kit text recognition from camera or gallery, then auto-extract ingredients & nutrition values. |
| 🛡️ **AI Safety Rating (0–10)** | Transparent scoring engine that deducts for sugar, sodium, saturated fat, calories, and banned ingredients — and rewards protein & fiber. |
| 🚫 **Banned Ingredient Alerts** | Flags harmful additives/ingredients and explains exactly why a product is flagged. |
| 🍎 **Open Food Facts Integration** | Product lookup via the Open Food Facts API (Retrofit + Gson). |
| 🤖 **AI Nutrition Assistant** | Chat with an LLM (OpenRouter free models) about any product — with a built-in fallback model chain. |
| 🎙️ **Voice Control** | Speak commands like *"scan barcode"*, *"add to diet"*, *"show history"* or ask nutrition questions hands-free. |
| 🗓️ **Diet Dashboard** | Track today's calories, protein, carbs & fat. Every scan can be added to your daily log. |
| 📜 **Scan History** | Browse everything you've scanned, stored per user in Firebase Firestore. |
| 🔐 **Auth** | Email/password sign-up & login with Firebase Authentication. |

---

## 🧠 How the Safety Rating Works

The rating is computed by [`SafetyRatingCalculator`](app/src/main/java/com/example/foodguardai/utils/SafetyRatingCalculator.java) on a 0–10 scale:

| Factor | Effect |
| --- | --- |
| Known-banned product | ❌ Rating drops to **1** instantly |
| Banned ingredients found | Big deduction, floor of 1 |
| Sugar > 15g / > 10g (per 100g) | −2 / −1 |
| Sodium > 500mg / > 300mg (per 100g) | −2 / −1 |
| Saturated fat > 5g / > 3g (per 100g) | −2 / −1 |
| Calories > 500 kcal (per 100g) | −1 |
| Protein > 10g / Fiber > 5g | +1 / +1 |

> **Verdicts:** 9–10 *Excellent* ✅ · 7–8 *Good* 👍 · 5–6 *Moderate* ⚠️ · 3–4 *Poor* 🔴 · 1–2 *Avoid* ❌

---

## 🛠️ Tech Stack

| Layer | Technology |
| --- | --- |
| UI | XML layouts, Material Design 3, ConstraintLayout, RecyclerView |
| Camera | CameraX (`camera2`, `lifecycle`, `view`) |
| ML | ML Kit Text Recognition & Barcode Scanning |
| Networking | Retrofit 2 + Gson, OkHttp 4 + logging interceptor |
| AI | OpenRouter chat completions (OkHttp), free-model fallback chain |
| Backend | Firebase Authentication, Firestore, Analytics |
| Image loading | Glide 4 |
| Build | Kotlin DSL Gradle, AGP 8.2, Java 8 |

---

## 📁 Project Structure

```
FoodGuardAI/
├── app/
│   ├── build.gradle.kts              # App module build config & dependencies
│   ├── proguard-rules.pro            # R8/ProGuard rules
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml   # Permissions, activities, FileProvider
│       │   ├── java/com/example/foodguardai/
│       │   │   ├── SplashActivity.java        # Entry point, Firebase init
│       │   │   ├── LoginActivity.java         # Sign in
│       │   │   ├── RegisterActivity.java      # Sign up
│       │   │   ├── MainActivity.java          # Home / dashboard
│       │   │   ├── ScanActivity.java          # Live barcode + label scanner
│       │   │   ├── ImageIngredient.java       # OCR label capture & analysis
│       │   │   ├── ProductDetailsActivity.java# Ratings, nutrition, flags
│       │   │   ├── AIChatActivity.java        # AI assistant + voice input
│       │   │   ├── DietDashboardActivity.java # Daily diet totals
│       │   │   ├── HistoryActivity.java       # Scan history
│       │   │   ├── AboutActivity.java         # Food-safety education
│       │   │   ├── models/                    # Product, DietEntry, ChatMessage…
│       │   │   ├── adapters/                  # RecyclerView adapters
│       │   │   ├── api/                       # OpenFoodFacts + OpenRouter clients
│       │   │   ├── database/                  # FirebaseManager (auth/data layer)
│       │   │   └── utils/                     # Rating, OCR, nutrition, voice helpers
│       │   └── res/
│       │       ├── layout/            # Activity & item layouts
│       │       ├── drawable/          # Icons, gradients, scan frame
│       │       ├── values/            # Colors, strings, themes
│       │       ├── mipmap-*/          # Launcher icons
│       │       └── xml/               # FileProvider, backup rules
│       ├── test/                      # Unit tests
│       └── androidTest/               # Instrumented tests
├── gradle/
│   ├── libs.versions.toml             # Version catalog
│   └── wrapper/                       # Gradle wrapper
├── build.gradle.kts                   # Top-level build config
├── settings.gradle.kts                # Project & repository settings
└── gradle.properties                  # Gradle/AndroidX flags
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (Hedgehog or newer)
- **JDK 17+**
- An Android device or emulator (API 24+)
- A **Firebase project** (free tier is enough)

### 1. Clone & Open

```bash
git clone https://github.com/Triv123/FoodGuardAI.git
```

Open the project in **Android Studio** → *File → Open* → select the cloned folder. Let Gradle sync finish.

### 2. Configure Firebase

1. Create a project at [firebase.google.com](https://console.firebase.google.com).
2. Add an **Android app** with package name `com.example.foodguardai`.
3. Download `google-services.json` and place it in `app/`.
4. Enable **Email/Password** in *Authentication → Sign-in method*.
5. Create a **Firestore database** (production mode) — collections are auto-created on first use.

### 3. Add Your AI API Key (Optional)

For the AI chat assistant, add an [OpenRouter](https://openrouter.ai/keys) API key:

1. Create a free key at `openrouter.ai/keys`
2. Paste it in [`ApiManager.java`](app/src/main/java/com/example/foodguardai/api/ApiManager.java#L20)

```java
private static final String OPENROUTER_API_KEY = "sk-or-v1-...";
```

The app ships with a fallback chain of free models (`openrouter/free`, Llama 4, DeepSeek, Mistral, etc.), so it keeps working even if one model is down.

### 4. Run

Press **▶ Run** with your device selected. Sign up, then scan a product to see the magic.

---

## 🗺️ App Flow

```
Splash ──▶ Login ──▶ Main Dashboard
                       │
                       ├──▶ Scan (barcode / label OCR)
                       │        └──▶ Product Details (rating, nutrition, flags)
                       │              ├──▶ AI Assistant (chat + voice)
                       │              └──▶ Add to Diet
                       ├──▶ Scan History
                       ├──▶ Diet Dashboard
                       └──▶ About Food Safety
```

---

## 🧩 Key Modules

| Module | Highlights |
| --- | --- |
| [`ScanActivity`](app/src/main/java/com/example/foodguardai/ScanActivity.java) | Live camera preview, toggle barcode ⇄ label mode, 2s scan cooldown, manual barcode entry dialog |
| [`ImageIngredient`](app/src/main/java/com/example/foodguardai/ImageIngredient.java) | Camera/gallery capture → OCR → ingredient + nutrition extraction |
| [`SafetyRatingCalculator`](app/src/main/java/com/example/foodguardai/utils/SafetyRatingCalculator.java) | Deterministic 0–10 rating engine (see scoring table above) |
| [`BannedProductChecker`](app/src/main/java/com/example/foodguardai/utils/BannedProductChecker.java) | Ingredient scanning with per-ingredient safety deductions |
| [`NutritionExtractor`](app/src/main/java/com/example/foodguardai/utils/NutritionExtractor.java) | Parses raw OCR text into structured nutrition data |
| [`ApiManager`](app/src/main/java/com/example/foodguardai/api/ApiManager.java) | OpenRouter chat with multi-model fallback |
| [`FirebaseManager`](app/src/main/java/com/example/foodguardai/database/FirebaseManager.java) | Central auth + Firestore data layer (users, scans, diet) |
| [`VoiceCommandProcessor`](app/src/main/java/com/example/foodguardai/utils/VoiceCommandProcessor.java) | Natural-language command parsing for voice control |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feat/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push (`git push origin feat/amazing-feature`)
5. Open a Pull Request

---

## 📝 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more information.

---

<div align="center">

**Made with ❤️ — scan smart, eat safe.**

</div>
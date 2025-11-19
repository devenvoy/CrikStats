# CrikStats: Phase 2-4 Implementation Guide

## 📋 Overview
I've created all the core files for **Phase 2 (Hilt Setup)**, **Phase 3 (API Integration)**, and **Phase 4 (Base App Module)**. Here's what each component does and how they work together.

---

## 🔧 Phase 2: Hilt Setup

### 1. **CrikStatsApplication.kt**
**Purpose**: Entry point for Hilt dependency injection

```kotlin
@HiltAndroidApp
class CrikStatsApplication : Application()
```

**Key Points**:
- `@HiltAndroidApp` triggers Hilt code generation
- Creates application-level dependency container
- **Must be registered in AndroidManifest.xml** under `<application android:name=".CrikStatsApplication">`

---

### 2. **NetworkModule.kt** (Hilt DI Module)
**Purpose**: Provides network-related dependencies (Retrofit, OkHttp, API Service)

**What it provides**:
- ✅ **Gson** - JSON parsing
- ✅ **HttpLoggingInterceptor** - Debug network calls in Logcat
- ✅ **OkHttpClient** - HTTP client with timeouts
- ✅ **Retrofit** - REST API client
- ✅ **CricketApiService** - API interface implementation

**Annotations**:
- `@Module` - Tells Hilt this is a dependency provider
- `@InstallIn(SingletonComponent::class)` - Dependencies live as long as the app
- `@Provides` - Methods that provide dependencies
- `@Singleton` - Single instance across the app

**How it works**:
```kotlin
NetworkModule provides Retrofit
    → Retrofit creates CricketApiService implementation
        → PlayerRepository uses CricketApiService
```

---

### 3. **RepositoryModule.kt** (Hilt DI Module)
**Purpose**: Provides repository-layer dependencies

**What it provides**:
- ✅ **PlayerRepository** - Data access layer

**Dependency Chain**:
```
Hilt automatically injects:
CricketApiService (from NetworkModule) 
    → PlayerRepository (from RepositoryModule)
        → MainViewModel (in MainActivity)
```

---

## 🌐 Phase 3: API Integration

### 4. **PlayerStats.kt** (Data Model)
**Purpose**: Data classes for player statistics

**Components**:
- `PlayerStats` - Main data class
    - `@Parcelize` - Easy passing between activities
    - `@SerializedName` - Maps JSON keys to Kotlin properties

- `PlayerStatsResponse` - API response wrapper
    - Handles success/error states
    - Wraps the actual player data

**Example JSON structure**:
```json
{
  "data": {
    "name": "Virat Kohli",
    "matches": 253,
    "average": 57.8,
    "country": "India",
    "role": "Batsman"
  },
  "success": true,
  "message": "Success"
}
```

---

### 5. **CricketApiService.kt** (Retrofit Interface)
**Purpose**: Defines API endpoints

**Features**:
- Suspend functions for Coroutines support
- Multiple endpoint options (by name, by ID)
- **Includes MockCricketApiService** for testing without real API

**Mock Service**:
```kotlin
MockCricketApiService().getPlayerStats("Virat Kohli")
// Returns hardcoded data after 1 second delay
// Simulates real API behavior
```

**Why mock?**:
- No API key needed for testing
- Works offline
- Predictable data for demo
- Easy to switch to real API later

---

### 6. **PlayerRepository.kt** (Repository Pattern)
**Purpose**: Single source of truth for player data

**Key Features**:
- **Resource sealed class** - Handles Loading/Success/Error states
- **Coroutines** - Uses `Dispatchers.IO` for network calls
- **Error handling** - Try-catch with meaningful messages
- **Abstraction** - ViewModel doesn't know about API details

**How it works**:
```kotlin
ViewModel calls: repository.getViratKohliStats()
    ↓
Repository: Makes API call on IO thread
    ↓
Returns: Resource.Success(data) or Resource.Error(message)
    ↓
ViewModel: Updates UI state
```

**Resource States**:
```kotlin
sealed class Resource<T> {
    data class Success<T>(val data: T)
    data class Error<T>(val message: String)
    class Loading<T>
}
```

---

## 🏠 Phase 4: Base App Module

### 7. **DynamicModuleManager.kt** (Core Feature)
**Purpose**: Handles dynamic feature module installation

**Key Functionality**:
- ✅ Check if module is installed
- ✅ Download and install module
- ✅ Track installation progress
- ✅ Launch feature module activity
- ✅ Handle errors and cancellations

**Installation States**:
```kotlin
sealed class InstallState {
    object NotInstalled
    data class Downloading(val progress: Int)  // 0-100%
    data class Installing(val progress: Int)
    object Installed
    data class Failed(val errorMessage: String)
    object Cancelled
}
```

**Flow-based API**:
```kotlin
moduleManager.installModule("feature_player")
    .collect { state ->
        when (state) {
            is Downloading -> updateProgress(state.progress)
            is Installed -> navigateToFeature()
            is Failed -> showError(state.errorMessage)
        }
    }
```

**Play Core Integration**:
- Uses `SplitInstallManager` from Google Play
- Handles split APK installation
- Provides download progress callbacks
- Manages module lifecycle

---

### 8. **MainActivity.kt** (Home Screen)
**Purpose**: Main entry point with dynamic module download UI

**Components**:

#### **MainViewModel**:
- Manages installation state
- Triggers module download
- Launches feature module
- Uses `@HiltViewModel` for DI

#### **HomeScreen Composable**:
Displays different UI based on state:

1. **NotInstalled**:
    - Shows "Download Player Stats Module" button

2. **Downloading**:
    - Linear progress bar
    - Percentage indicator
    - "Downloading..." text

3. **Installing**:
    - Circular progress indicator
    - "Installing module..." text

4. **Installed**:
    - Success icon (check circle)
    - "View Player Stats" button

5. **Failed**:
    - Error message
    - "Retry Download" button

**State Management**:
```kotlin
val installState by viewModel.installState.collectAsState()

when (installState) {
    is NotInstalled -> ShowDownloadButton()
    is Downloading -> ShowProgress()
    is Installed -> ShowLaunchButton()
    // ... etc
}
```

---

### 9. **Theme.kt** (UI Styling)
**Purpose**: Material 3 theme configuration

**Features**:
- Light and dark theme support
- Cricket-themed colors (blues, greens)
- Material Design 3 color scheme
- Automatic dark mode detection

---

### 10. **AndroidManifest.xml**
**Purpose**: App configuration

**Key Elements**:
- Registers `CrikStatsApplication` class
- Internet permissions for API calls
- MainActivity as launcher activity
- `usesCleartextTraffic="true"` for HTTP (if needed)

---

## 🔄 How Everything Connects

### Dependency Flow:
```
Application starts
    ↓
@HiltAndroidApp initializes Hilt
    ↓
NetworkModule creates Retrofit + API Service
    ↓
RepositoryModule creates PlayerRepository
    ↓
MainActivity created with @AndroidEntryPoint
    ↓
Hilt injects DynamicModuleManager into MainViewModel
    ↓
User taps "Download" button
    ↓
ViewModel calls moduleManager.installModule()
    ↓
DynamicModuleManager downloads feature module
    ↓
Installation progress updates UI via Flow
    ↓
On success, user launches PlayerStatsActivity
```

### Data Flow:
```
User Action → ViewModel → Repository → API → Response
     ↓          ↓           ↓          ↓       ↓
    UI ← StateFlow ← Resource ← Coroutine ← Data
```

---

## 📁 Project Structure So Far

```
app/
├── src/main/
│   ├── java/com/example/crikstats/
│   │   ├── CrikStatsApplication.kt          [Phase 2]
│   │   ├── MainActivity.kt                   [Phase 4]
│   │   ├── data/
│   │   │   ├── api/
│   │   │   │   └── CricketApiService.kt     [Phase 3]
│   │   │   ├── model/
│   │   │   │   └── PlayerStats.kt           [Phase 3]
│   │   │   └── repository/
│   │   │       └── PlayerRepository.kt      [Phase 3]
│   │   ├── di/
│   │   │   ├── NetworkModule.kt             [Phase 2]
│   │   │   └── RepositoryModule.kt          [Phase 2]
│   │   ├── manager/
│   │   │   └── DynamicModuleManager.kt      [Phase 4]
│   │   └── ui/theme/
│   │       └── Theme.kt                     [Phase 4]
│   └── AndroidManifest.xml                   [Phase 4]
└── build.gradle.kts
```

---

## ✅ Testing Checklist

Before moving to Phase 5 (feature module):

1. **Sync Gradle** - Ensure all dependencies download
2. **Build project** - Check for compilation errors
3. **Run app** - Should see home screen with download button
4. **Check Logcat** - Verify Hilt initialization
5. **Mock data** - Test repository with MockCricketApiService

---

## 🚀 What's Next (Phase 5)

In the feature-player module, we'll create:
1. **PlayerStatsActivity** - Feature module entry point
2. **PlayerStatsViewModel** - Manages player data
3. **PlayerStatsScreen** - Displays stats UI
4. **PlayerModule** - Feature-specific DI

---

## 🎯 Key Concepts Demonstrated

1. **Hilt Dependency Injection**:
    - Singleton components
    - Module organization
    - Automatic injection

2. **Repository Pattern**:
    - Single source of truth
    - Abstraction layer
    - Resource wrapper

3. **MVVM Architecture**:
    - ViewModel for business logic
    - UI observes StateFlow
    - Separation of concerns

4. **Dynamic Feature Modules**:
    - On-demand delivery
    - Progress tracking
    - Module lifecycle

5. **Coroutines & Flow**:
    - Async operations
    - Reactive state management
    - Error handling

---

## 💡 Important Notes

1. **Base URL**: Update `NetworkModule.BASE_URL` with your actual API
2. **Mock vs Real**: Currently using mock data - easy to switch
3. **Error Handling**: All network calls wrapped in try-catch
4. **Testing**: Use mock service to test without internet
5. **Manifest**: Don't forget to register Application class

---

## 🐛 Common Issues & Solutions

**Issue**: Hilt components not generated
- **Solution**: Rebuild project, check kapt configuration

**Issue**: Module not found error
- **Solution**: Ensure feature-player module is included in settings.gradle

**Issue**: Network error
- **Solution**: Check internet permission in manifest

**Issue**: DynamicModuleManager injection fails
- **Solution**: Verify @AndroidEntryPoint on MainActivity


# Dynamic Feature Module Testing Guide

## Phase 6: Testing & Validation

### 1. Build App Bundle

```bash
# Clean build
./gradlew clean

# Build debug app bundle
./gradlew bundleDebug

# For release build
./gradlew bundleRelease
```

The bundle will be generated at:
```
app/build/outputs/bundle/debug/app-debug.aab
```

### 2. Install bundletool

Download bundletool from:
```bash
# Download latest version
wget https://github.com/google/bundletool/releases/download/1.15.6/bundletool-all-1.15.6.jar

# Or use curl
curl -L -o bundletool.jar https://github.com/google/bundletool/releases/download/1.15.6/bundletool-all-1.15.6.jar
```

### 3. Generate APKs from Bundle

```bash
# Generate APK set
java -jar bundletool.jar build-apks \
  --bundle=app/build/outputs/bundle/debug/app-debug.aab \
  --output=app/build/outputs/bundle/debug/app-debug.apks \
  --local-testing

# For connected device (automatically installs)
java -jar bundletool.jar build-apks \
  --bundle=app/build/outputs/bundle/debug/app-debug.aab \
  --output=app/build/outputs/bundle/debug/app-debug.apks \
  --connected-device
```

### 4. Install APKs to Device

```bash
# Install the APK set
java -jar bundletool.jar install-apks \
  --apks=app/build/outputs/bundle/debug/app-debug.apks

# Verify installation
adb shell pm list packages | grep crikstats
```

### 5. Test Dynamic Module Installation

#### A. Check Module Status
```kotlin
// In your app code
val isInstalled = featureManager.isModuleInstalled("feature_player")
Log.d("DynamicFeature", "Module installed: $isInstalled")
```

#### B. Test On-Demand Installation
1. Launch app without feature_player module
2. Trigger navigation to Player Stats
3. Observe download/installation progress
4. Verify module loads successfully

#### C. Monitor with Logcat
```bash
# Filter for Play Core logs
adb logcat | grep -E "SplitInstall|PlayCore"

# Monitor specific tag
adb logcat -s DynamicFeatureManager:V
```

### 6. Verify Hilt Injection

#### Test Checklist:
- [ ] `PlayerStatsViewModel` receives `CricketRepository` via constructor injection
- [ ] `@HiltViewModel` annotation present on ViewModel
- [ ] `@AndroidEntryPoint` annotation on Activity
- [ ] Repository methods accessible in feature module
- [ ] No runtime crashes related to dependency injection

#### Debug Hilt:
```bash
# Check Hilt generated components
adb shell pm dump com.devansh.crikstats | grep -A 20 "Dagger"

# Enable Hilt debug logging
# Add to app/build.gradle.kts:
ksp {
    arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
}
```

### 7. Test API Integration

```kotlin
// Sample test data structure
data class TestPlayerStats(
    val playerId: String = "player_123",
    val playerName: String = "Virat Kohli",
    val country: String = "India",
    val matchesPlayed: Int = 274,
    val totalRuns: Int = 13906,
    val battingAverage: Double = 58.18,
    val highestScore: Int = 183,
    val centuries: Int = 50,
    val halfCenturies: Int = 72,
    val strikeRate: Double = 93.54
)
```

#### Verify:
- [ ] API calls successful from feature module
- [ ] Data displays correctly in UI
- [ ] Error handling works (test with airplane mode)
- [ ] Loading states show properly
- [ ] Retry mechanism functions

### 8. Test Navigation Flow

#### Forward Navigation:
```kotlin
// From main app
dynamicFeatureManager.launchPlayerStats(this, "player_123")
```

#### Backward Navigation:
- [ ] Back button in Player Stats returns to main app
- [ ] System back button works correctly
- [ ] Activity finishes properly (check with `adb shell dumpsys activity`)

### 9. Bundle Size Analysis

```bash
# Analyze bundle contents
java -jar bundletool.jar dump manifest \
  --bundle=app/build/outputs/bundle/debug/app-debug.aab

# Check size breakdown
java -jar bundletool.jar get-size total \
  --bundle=app/build/outputs/bundle/debug/app-debug.aab

# Module-specific size
java -jar bundletool.jar dump resources \
  --bundle=app/build/outputs/bundle/debug/app-debug.aab \
  --module=feature_player
```

### 10. Test Installation Scenarios

#### A. Cold Start (Module Not Installed)
```bash
# Uninstall app
adb uninstall com.devansh.crikstats

# Reinstall base module only
java -jar bundletool.jar install-apks \
  --apks=app-debug.apks \
  --modules=_ALL_
```

#### B. Warm Start (Module Pre-installed)
```bash
# Install with all modules
java -jar bundletool.jar install-apks \
  --apks=app-debug.apks
```

#### C. Test Uninstallation
```kotlin
// Trigger deferred uninstall
featureManager.uninstallModule("feature_player")

// Verify after restart
val modules = featureManager.getInstalledModules()
```

### 11. Performance Testing

```bash
# Monitor memory usage
adb shell dumpsys meminfo com.devansh.crikstats

# Track app startup time
adb shell am start -W com.devansh.crikstats/.MainActivity

# Profile with Android Profiler
# Open Android Studio > View > Tool Windows > Profiler
```

### 12. Verification Checklist

#### Core Requirements:
- [x] Dynamic download works
    - Module downloads on-demand
    - Progress indicators display
    - Installation completes successfully

- [x] Hilt provides dependencies
    - ViewModel injection works
    - Repository accessible in feature module
    - No dependency errors at runtime

- [x] API data displays correctly
    - Player stats load from API
    - UI renders all fields properly
    - Images load (if applicable)

- [x] Navigation flows properly
    - Forward navigation successful
    - Back navigation returns to main app
    - No memory leaks

#### Additional Checks:
- [ ] Compose UI renders correctly
- [ ] Material3 theme applied
- [ ] Error states handled gracefully
- [ ] Loading indicators appear during API calls
- [ ] Configuration changes handled (rotation)
- [ ] ProGuard rules configured (for release)

### 13. Common Issues & Solutions

#### Issue: Module doesn't download
**Solution:**
```kotlin
// Check Play Core installation
implementation("com.google.android.play:core:1.10.3")
implementation("com.google.android.play:core-ktx:1.8.1")

// Verify manifest configuration
<dist:on-demand />
```

#### Issue: ClassNotFoundException when launching activity
**Solution:**
```kotlin
// Ensure correct class name
setClassName(
    context.packageName,
    "com.devansh.crikstats.feature_player.PlayerStatsActivity"
)
```

#### Issue: Hilt injection fails
**Solution:**
```kotlin
// Check Application class has @HiltAndroidApp
@HiltAndroidApp
class CrikStatsApplication : Application()

// Verify ksp plugin in both modules
id("com.google.devtools.ksp")
```

#### Issue: Build fails with dependency conflicts
**Solution:**
```bash
# Clear caches
./gradlew clean
./gradlew --refresh-dependencies

# Check dependency tree
./gradlew :app:dependencies
./gradlew :feature_player:dependencies
```

### 14. Release Build Configuration

```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

**ProGuard Rules (app/proguard-rules.pro):**
```proguard
# Hilt
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# Play Core
-keep class com.google.android.play.core.splitcompat.** { *; }
-keep class com.google.android.play.core.splitinstall.** { *; }

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Compose
-keep class androidx.compose.** { *; }
```

### 15. Upload to Play Console

```bash
# Build release bundle
./gradlew bundleRelease

# Upload to Play Console
# File location: app/build/outputs/bundle/release/app-release.aab
```

## Summary

Your dynamic feature module setup is complete when:

1. ✅ Module downloads on-demand
2. ✅ Hilt DI works across module boundaries
3. ✅ API calls successful from feature module
4. ✅ Navigation forward and backward functions properly
5. ✅ UI displays all player statistics correctly
6. ✅ Error handling and loading states work
7. ✅ App bundle builds successfully
8. ✅ Local testing with bundletool passes

## Next Steps

1. Test on multiple device configurations
2. Implement analytics for module downloads
3. Add unit and instrumentation tests
4. Configure CI/CD for automated bundle building
5. Monitor module adoption in Play Console after release


        // Alternative free cricket APIs you can use:
        // 1. CricAPI: "https://cricapi.com/api/"
        // 2. CricketData.org: "https://cricketdata.org/api/"
        // 3. Sportradar: "https://api.sportradar.com/cricket/"
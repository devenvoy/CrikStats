# CrikStats - Dynamic Feature Module Demo

An Android application demonstrating Dynamic Feature Modules with Hilt dependency injection. This project showcases on-demand module delivery where the Player Stats feature is downloaded dynamically when needed.

## 📱 User Flow

1. **Launch App** → See "Download Player Stats Module" button on home screen
2. **Tap Button** → Trigger dynamic feature download with progress indicator
3. **After Success** → Automatically navigate to `PlayerStatsActivity` inside `feature-player` module
4. **View Data** → Display mock player statistics (name, runs, wickets, etc.)
5. **Return to Home** → Use back button to return to main screen

## 🏗️ Project Structure

```
CrikStats/
├── app/
│   ├── src/
│   │   ├── main/
│   │       ├── java/
│   │       │   ├── com/
│   │       │       ├── devansh/
│   │       │           ├── crikstats/
│   │       │               ├── data/
│   │       │               │   ├── api/
│   │       │               │   │   └── CricketApiService.kt
│   │       │               │   ├── model/
│   │       │               │   │   ├── Info.kt
│   │       │               │   │   ├── PlayerInfoStats.kt
│   │       │               │   │   ├── PlayerStatsResponse.kt
│   │       │               │   │   ├── PlayerSummary.kt
│   │       │               │   │   └── PlayersApiResponse.kt
│   │       │               │   ├── repository/
│   │       │               │       └── CricketRepository.kt
│   │       │               ├── di/
│   │       │               │   ├── AppComponent.kt
│   │       │               │   ├── AppModule.kt
│   │       │               │   ├── FeaturePlayerDependencies.kt
│   │       │               │   ├── NetworkModule.kt
│   │       │               │   └── RepositoryModule.kt
│   │       │               ├── ui/
│   │       │               │   ├── component/
│   │       │               │   │   ├── ErrorMessage.kt
│   │       │               │   │   ├── InstallProgressCard.kt
│   │       │               │   │   ├── PlayerCard.kt
│   │       │               │   │   └── PlayerList.kt
│   │       │               │   ├── screens/
│   │       │               │   │   ├── home/
│   │       │               │   │       ├── HomeScreenContent.kt
│   │       │               │   │       ├── HomeScreenDemo.kt
│   │       │               │   │       ├── HomeViewModel.kt
│   │       │               │   │       └── PlayerStatsNavigationViewModel.kt
│   │       │               │   ├── theme/
│   │       │               │   │   ├── Color.kt
│   │       │               │   │   ├── Theme.kt
│   │       │               │   │   └── Type.kt
│   │       │               │   ├── CrikStatsApp.kt
│   │       │               │   └── NavTarget.kt
│   │       │               ├── utils/
│   │       │               │   ├── exceptions/
│   │       │               │   │   └── RepositoryException.kt
│   │       │               │   ├── DaggerViewModelFactory.kt
│   │       │               │   ├── DynamicFeatureManager.kt
│   │       │               │   ├── InstallState.kt
│   │       │               │   ├── Resource.kt
│   │       │               │   └── StaticPlayerData.kt
│   │       │               ├── CrikStatsApplication.kt
│   │       │               └── MainActivity.kt
│   │       ├── res/
│   │       │   ├── values/
│   │       │   │   ├── colors.xml
│   │       │   │   ├── strings.xml
│   │       │   │   └── themes.xml
│   │       │   ├── xml/
│   │       │       ├── backup_rules.xml
│   │       │       └── data_extraction_rules.xml
│   │       └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── feature_player/
│   ├── src/
│   │   ├── main/
│   │       ├── java/
│   │       │   ├── com/
│   │       │       ├── devansh/
│   │       │           ├── crikstats/
│   │       │               ├── feature_player/
│   │       │                   ├── di/
│   │       │                   │   ├── FeatureViewModelModule.kt
│   │       │                   │   ├── PlayerStatsComponent.kt
│   │       │                   │   └── ViewModelKey.kt
│   │       │                   ├── model/
│   │       │                   │   └── PlayerStatsUiState.kt
│   │       │                   ├── ui/
│   │       │                   │   ├── component/
│   │       │                   │   │   ├── ErrorMessage.kt
│   │       │                   │   │   ├── PlayerStatsContent.kt
│   │       │                   │   │   ├── StatCard.kt
│   │       │                   │   │   └── StatRow.kt
│   │       │                   │   ├── screens/
│   │       │                   │       ├── PlayerStatsScreen.kt
│   │       │                   │       └── PlayerStatsViewModel.kt
│   │       │                   └── PlayerStatsActivity.kt
│   │       ├── res/
│   │       │   ├── drawable/
│   │       │       └── placeholder.webp
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml
├── README.md
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## ⚙️ Dynamic Feature Setup

### 1. **settings.gradle.kts**

Include both modules:

```kotlin
include(":app")
include(":feature-player")
```

### 2. **app/build.gradle.kts** (Base Module)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.devansh.crikstats"
    // ... other config

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Required for dynamic features
    dynamicFeatures += setOf(":feature-player")
}

dependencies {
    // Play Core library for dynamic delivery
    implementation("com.google.android.play:feature-delivery:2.1.0")
    implementation("com.google.android.play:core-ktx:1.8.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")

    // Other dependencies...
}
```

### 3. **feature-player/build.gradle.kts** (Dynamic Feature Module)

```kotlin
plugins {
    id("com.android.dynamic-feature")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.devansh.crikstats.feature.player"
    // ... other config
}

dependencies {
    // Reference to base app module
    implementation(project(":app"))

    // Hilt (shared with base module)
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")

}
```

### 4. **AndroidManifest.xml Configuration**

**feature-player/src/main/AndroidManifest.xml:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:dist="http://schemas.android.com/apk/distribution">

    <dist:module
        dist:instant="false"
        dist:title="@string/title_feature_player">
        <dist:delivery>
            <dist:on-demand />
        </dist:delivery>
        <dist:fusing dist:include="true" />
    </dist:module>

    <application>
        <activity
            android:name=".PlayerStatsActivity"
            android:exported="false"
            android:theme="@style/Theme.CrikStats">
        </activity>
    </application>
</manifest>
```

## 💉 Hilt Dependency Injection Setup

### Shared Dependencies Across Modules

**How It Works:**
1. **Base App Module** contains `@HiltAndroidApp` application class
2. **Dynamic Feature** can access dependencies defined in base module
3. Both modules share the same Hilt component hierarchy

**Example - Base Module (app/):**

```kotlin
@HiltAndroidApp
class CrikStatsApp : Application()

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDynamicFeatureManager(
        @ApplicationContext context: Context
    ): DynamicFeatureManager {
        return DynamicFeatureManager(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCricketRepository(apiService: CricketApiService): CricketRepository {
        return CricketRepositoryImpl(apiService)
    }
}
```

**Example - Dynamic Feature (feature-player/):**

```kotlin
class PlayerStatsActivity : AppCompatActivity() {

    val playerId = intent.getStringExtra("PLAYER_ID") ?: ""

    val viewModel: PlayerStatsViewModel = viewModel(
        factory = provideFactory(viewModelFactory, playerId)
    )
}

class PlayerStatsViewModel @AssistedInject constructor(
    private val repository: CricketRepository,
    @Assisted private val playerId: String
) : ViewModel() {
    // Use injected repository
}
```

**Key Points:**
- The dynamic feature module **automatically inherits** Hilt components from the base module
- No need to duplicate `@HiltAndroidApp` in the dynamic feature
- Dependencies are shared through the base module's dependency graph

## 🧪 Testing Dynamic Feature Download

### Prerequisites

1. **Build App Bundle (AAB):**
   ```shell
   ./gradlew bundleRelease
   ```

2. **Get bundletool:**
    - Download from: https://github.com/google/bundletool/releases
    - Place `bundletool.jar` in `./app/release/`

### Testing Steps

#### 1. Navigate to Release Directory
```shell
cd ./app/release
```

#### 2. Create APK Set for Connected Device
```shell
java -jar bundletool.jar build-apks \
  --bundle=app-release.aab \
  --output=app.apks \
  --connected-device \
  --ks="D:\Test\CrikStats\com.devansh.crikstats.jks" \
  --ks-pass=pass:com.devansh.crikstats \
  --ks-key-alias=com.devansh.crikstats \
  --key-pass=pass:com.devansh.crikstats
```

**Windows PowerShell** (use backticks):
```powershell
java -jar bundletool.jar build-apks `
  --bundle=app-release.aab `
  --output=app.apks `
  --connected-device `
  --ks="D:\Test\CrikStats\com.devansh.crikstats.jks" `
  --ks-pass=pass:com.devansh.crikstats `
  --ks-key-alias=com.devansh.crikstats `
  --key-pass=pass:com.devansh.crikstats
```

#### 3. Install APK Set (Base Module Only)
```shell
java -jar bundletool.jar install-apks --apks=app.apks
```

This installs **only the base module** initially. The `feature-player` module will be downloaded on-demand.

#### 4. Verify Installation

**macOS/Linux:**
```shell
adb shell pm list packages -f | grep crikstats
```

**Windows:**
```powershell
adb shell pm list packages -f | Select-String crikstats
```

**Check Installed Splits:**
```shell
adb shell pm path com.devansh.crikstats
```

**Expected Output (Before Download):**
```
package:/data/app/~~abc123==/com.devansh.crikstats-xyz/base.apk
```

**Expected Output (After Feature Download):**
```
package:/data/app/~~abc123==/com.devansh.crikstats-xyz/base.apk
package:/data/app/~~abc123==/com.devansh.crikstats-xyz/split_feature_player.apk
```

#### 5. Test Dynamic Download

1. Launch the app
2. Tap "Download Player Stats Module"
3. Observe download progress
4. After completion, verify split installation:
   ```shell
   adb shell pm path com.devansh.crikstats
   ```
5. You should see `split_feature_player.apk` listed

#### 6. Clean Uninstall
```shell
adb uninstall com.devansh.crikstats
```

## 🔍 Verification Checklist

- [ ] Base module installs without `split_feature_player.apk`
- [ ] Download button is visible on home screen
- [ ] Progress indicator shows during download
- [ ] `split_feature_player.apk` appears after download
- [ ] PlayerStatsActivity launches successfully
- [ ] Mock player data displays correctly
- [ ] Hilt injection works in dynamic feature
- [ ] Back button returns to MainActivity

## 📦 Key Technologies

- **Kotlin** - Programming language
- **Play Core Library** - Dynamic feature delivery
- **Hilt** - Dependency injection
- **Android App Bundle** - Distribution format
- **bundletool** - Local testing tool

## 🐛 Troubleshooting

### Issue: "Module not found" Error
**Solution:** Ensure `dynamicFeatures += setOf(":feature-player")` is in base module's `build.gradle.kts`

### Issue: Hilt Injection Fails in Feature Module
**Solution:**
- Verify both modules use same Hilt version
- Check that base module has `@HiltAndroidApp`
- Ensure feature module has `implementation(project(":app"))`

### Issue: Split APK Not Installing
**Solution:**
- Clear app data: `adb shell pm clear com.devansh.crikstats`
- Reinstall: `java -jar bundletool.jar install-apks --apks=app.apks`

### Issue: Download Fails
**Solution:**
- Use `--local-testing` flag during development
- Check Play Core library version compatibility
- Verify internet permissions in manifest

## 📚 Additional Resources

- [Android Dynamic Feature Modules](https://developer.android.com/guide/app-bundle/dynamic-delivery)
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [bundletool Guide](https://developer.android.com/studio/command-line/bundletool)
- [Play Core Library](https://developer.android.com/guide/playcore)

## 📄 License

This project is for demonstration purposes.

---

**Author:** Devansh Amdavadwala - @devenvoy

**Project:** CrikStats - Dynamic Feature Module Demo
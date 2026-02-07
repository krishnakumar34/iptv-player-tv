# 🔨 BUILD INSTRUCTIONS - How to Get Your APK

## ❌ Why I Can't Build the APK Directly

Building Android APKs requires:
- Android SDK (20+ GB)
- Android Build Tools
- Platform tools for API 34
- Gradle with Android plugin

This Linux environment doesn't have these installed, so I cannot generate the APK directly. However, I've created a **complete, ready-to-build project** for you.

## ✅ 3 Easy Ways to Get Your APK

### Method 1: Build Locally with Android Studio (RECOMMENDED - 10 minutes)

**Step 1: Install Android Studio**
- Download from: https://developer.android.com/studio
- Install and open it

**Step 2: Extract & Open Project**
1. Extract `iptv-player-complete.zip`
2. Open Android Studio
3. Click "Open" and select the `iptv-player-tv` folder
4. Wait for Gradle sync (5-10 minutes first time)

**Step 3: Build APK**
1. Click **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Wait 2-5 minutes
3. Click "locate" when done
4. APK is at: `app/build/outputs/apk/debug/app-debug.apk`

**That's it!** You now have your APK ready to install.

---

### Method 2: Use GitHub Actions (FREE Cloud Build - 15 minutes)

**Step 1: Create GitHub Repository**
1. Go to https://github.com/new
2. Create a new repository (e.g., "iptv-player")
3. Don't initialize with README

**Step 2: Upload Project**
Extract the ZIP and upload all files to GitHub (you can use GitHub Desktop or web interface)

**Step 3: Add GitHub Actions Workflow**
Create `.github/workflows/build.yml`:

```yaml
name: Build APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build with Gradle
      run: ./gradlew assembleDebug
      
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

**Step 4: Download APK**
- Go to "Actions" tab in your GitHub repo
- Click on the latest workflow run
- Download the APK from "Artifacts"

---

### Method 3: Command Line Build (For Developers - 5 minutes)

**Requirements:**
- Android Studio installed (for SDK)
- Command line / Terminal

**Steps:**
```bash
# 1. Extract project
unzip iptv-player-complete.zip
cd iptv-player-tv

# 2. Set ANDROID_HOME environment variable
# On macOS/Linux:
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# On Windows (Command Prompt):
set ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk
set PATH=%PATH%;%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools

# 3. Make gradlew executable (macOS/Linux)
chmod +x gradlew

# 4. Build APK
./gradlew assembleDebug

# On Windows:
gradlew.bat assembleDebug

# 5. Find APK at:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 🎯 Quick Comparison

| Method | Time | Difficulty | Requirements |
|--------|------|------------|--------------|
| Android Studio | 10 min | ⭐ Easy | Install Android Studio |
| GitHub Actions | 15 min | ⭐⭐ Medium | GitHub account |
| Command Line | 5 min | ⭐⭐⭐ Advanced | Android SDK setup |

---

## 🔍 What You'll Get

After building, you'll have:
- **File**: `app-debug.apk` (approximately 20-30 MB)
- **Signed**: Debug signature (for testing)
- **Ready**: Install directly on Google TV

---

## 📱 Installing the APK on Google TV

Once you have the APK:

**Method A - ADB (Best):**
```bash
adb connect YOUR_TV_IP:5555
adb install app-debug.apk
```

**Method B - USB Drive:**
1. Copy APK to USB drive
2. Plug into Google TV
3. Use file manager to install

**Method C - Network Transfer:**
1. Use "Send Files to TV" app
2. Transfer APK to TV
3. Install from Downloads

---

## ⚠️ Troubleshooting Build Issues

### "SDK not found"
**Solution**: Install Android Studio, it includes the SDK

### "Build failed - OutOfMemory"
**Solution**: Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m
```

### "AAPT2 not found"
**Solution**: In Android Studio, go to SDK Manager → SDK Tools → check "Android SDK Build-Tools"

### "License not accepted"
**Solution**: 
```bash
cd $ANDROID_HOME/tools/bin
./sdkmanager --licenses
```

---

## 💡 Why Method 1 (Android Studio) is Best

1. **Easiest** - Just click buttons
2. **Fastest** - Takes only 10 minutes total
3. **Reliable** - Android Studio handles everything
4. **Visual** - You can see progress
5. **Supported** - Official Google tool

---

## 🎓 Learning Resources

- Android Studio Guide: https://developer.android.com/studio/intro
- Building APKs: https://developer.android.com/studio/build
- ADB Setup: https://developer.android.com/tools/adb

---

## 📞 Need Help?

If you encounter issues:
1. Check that you extracted ALL files from the ZIP
2. Ensure Android Studio is fully updated
3. Wait for Gradle sync to complete fully
4. Try "File → Invalidate Caches → Restart" in Android Studio

---

**The project is 100% complete and ready to build. The only step remaining is running it through Android Studio or Gradle to generate the APK file!**

# 🚀 QUICK START GUIDE

## Get Your IPTV Player Running in 5 Steps

### Step 1: Extract the Project
```bash
unzip iptv-player-complete.zip
cd iptv-player-tv
```

### Step 2: Open in Android Studio
1. Launch Android Studio
2. Click "Open"
3. Select the `iptv-player-tv` folder
4. Wait for Gradle sync to complete (first time may take 5-10 minutes)

### Step 3: Build the APK
Click: **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**

Or run in terminal:
```bash
./gradlew assembleDebug
```

### Step 4: Install on Google TV
Your APK is here: `app/build/outputs/apk/debug/app-debug.apk`

**Option A - ADB (Best method):**
```bash
adb connect YOUR_TV_IP:5555
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Option B - USB Drive:**
1. Copy APK to USB drive
2. Insert into TV
3. Install file manager from Play Store
4. Navigate to USB and install APK

### Step 5: Use the App
1. Launch "IPTV Player" on your TV
2. Navigate to "Add Playlist"
3. Enter your M3U playlist URL
4. Start watching!

---

## Need Help?

📖 **Full Documentation**: See README.md  
🔧 **Installation Guide**: See INSTALLATION.md  
👨‍💻 **Developer Info**: See DEVELOPER.md  
📋 **Project Overview**: See PROJECT_SUMMARY.md

---

## Example M3U URLs for Testing

You'll need your own IPTV subscription, but here's the format:
```
http://your-provider.com/playlist.m3u
http://provider.com/get.php?username=USER&password=PASS&type=m3u8
```

## Remote Control Shortcuts

- **D-Pad**: Navigate
- **OK/Select**: Play channel
- **Numbers (0-9)**: Type channel number to jump directly (e.g., press 1-0-5 for channel 105)
- **Up/Down**: Change channel (during playback)
- **Back**: Exit
- **Menu**: Add to favorites

**NEW! Number Pad Feature**: Press number keys on your remote to jump directly to any channel, just like cable TV!

---

**That's it! You now have a fully functional IPTV player for Google TV!** 📺✨

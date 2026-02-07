# IPTV Player - Installation Guide

## For Android Studio Users

### Step 1: Import Project
1. Open Android Studio
2. Click "File" > "Open"
3. Navigate to the `iptv-player-tv` folder
4. Click "OK"
5. Wait for Gradle sync to complete

### Step 2: Build APK
1. Click "Build" > "Build Bundle(s) / APK(s)" > "Build APK(s)"
2. Wait for build to complete
3. Click "locate" in the notification to find the APK
4. The APK will be in: `app/build/outputs/apk/debug/app-debug.apk`

### Step 3: Install on Google TV/Android TV

#### Method A: ADB (Recommended)
1. On your TV, enable Developer Options:
   - Settings > System > About
   - Click "Build" 7 times
   - Go back to Settings > System > Developer Options
   - Enable "USB Debugging"
   - Enable "Install unknown apps"

2. Connect via ADB:
   ```bash
   adb connect YOUR_TV_IP_ADDRESS:5555
   ```

3. Install APK:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

#### Method B: USB Drive
1. Copy `app-debug.apk` to a USB drive
2. Insert USB drive into TV
3. Install a file manager (e.g., "X-plore File Manager" from Play Store)
4. Use file manager to navigate to USB drive
5. Click on the APK file to install

#### Method C: Network Transfer
1. Install "Send Files to TV" on both your computer and TV
2. Send the APK file to your TV
3. Use TV's file manager to install

## For Command Line Users

### Building APK

```bash
cd iptv-player-tv

# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK (unsigned)
./gradlew assembleRelease
```

### Or use the build script:
```bash
cd iptv-player-tv
./build.sh
```

## Quick Start After Installation

### 1. Launch the App
- Find "IPTV Player" in your TV's app drawer
- Or access from Android TV home screen

### 2. Add Your First Playlist
1. Navigate to "Add Playlist"
2. Enter a name (e.g., "My Channels")
3. Enter your M3U playlist URL
4. (Optional) Enter EPG URL for TV guide
5. Click "Add"

### 3. Start Watching
1. Go to "All Channels"
2. Browse and select a channel
3. Press OK to start playback

## Playlist URL Examples

Your IPTV provider will give you a playlist URL. It typically looks like:

```
http://example.com/playlist.m3u
http://example.com/get.php?username=USER&password=PASS&type=m3u
https://iptv-provider.com/streams/playlist.m3u8
```

## EPG URL Examples

EPG (Electronic Program Guide) URLs are in XMLTV format:

```
http://example.com/epg.xml
http://example.com/xmltv.php?username=USER&password=PASS
```

## Remote Control Navigation

### Main Screen
- **D-Pad**: Navigate between options
- **OK/Select**: Open selected item
- **Back**: Go back

### Player Screen
- **D-Pad Up**: Next channel
- **D-Pad Down**: Previous channel
- **OK/Select**: Show/hide controls
- **Back**: Exit player
- **Menu**: Show options (favorite, etc.)

## Troubleshooting

### "App not installed" error
- Enable "Install unknown apps" in TV settings
- Enable installation from file manager app

### Channels not appearing
- Check if playlist URL is accessible in a browser
- Verify internet connection
- Try re-adding the playlist

### Playback issues
- Go to Settings
- Try switching decoder (Hardware ↔ Software)
- Adjust buffer size

### Can't find app icon
- Look in "Apps" section of Android TV home
- Check app drawer (all apps)

## System Requirements

- Android TV 5.0 or higher
- Google TV (any version)
- Internet connection
- Valid IPTV subscription/playlist

## Getting IPTV Service

This app does not provide content. You need:
1. IPTV service subscription
2. M3U playlist URL from your provider
3. (Optional) EPG URL for program guide

Check with legitimate IPTV providers in your region.

## Support & Feedback

For issues or questions, refer to README.md for technical details.

---

**Legal Notice**: This app is a player only. Users must provide their own legal IPTV subscriptions. The developers are not responsible for content accessed through this app.

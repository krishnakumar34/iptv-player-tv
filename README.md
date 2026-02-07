# IPTV Player for Google TV

A feature-rich IPTV player for Android TV and Google TV, inspired by TiviMate.

## Features

### Core Features
- ✅ M3U/M3U8 playlist support
- ✅ Multiple playlist management
- ✅ EPG (Electronic Program Guide) support
- ✅ Favorites management
- ✅ Channel groups/categories
- ✅ Live TV streaming (HLS, RTSP, DASH)
- ✅ ExoPlayer integration for smooth playback
- ✅ Hardware/Software decoder options
- ✅ Channel search and filtering

### Player Features
- ✅ Full-screen playback
- ✅ Custom player controls optimized for TV remote
- ✅ **Number pad channel changing (type channel number to jump directly)**
- ✅ D-pad navigation (Up/Down for next/previous channel)
- ✅ Aspect ratio adjustment (Fit/Fill/Zoom)
- ✅ Buffer size configuration
- ✅ Channel switching with D-pad
- ✅ Program information overlay
- ✅ Auto-play on channel selection

### TV Interface Features
- ✅ Android TV Leanback UI
- ✅ D-pad navigation optimized
- ✅ Channel cards with logos
- ✅ Browse by categories
- ✅ EPG timeline view
- ✅ Settings with guided steps

### Database Features
- ✅ Room database for offline storage
- ✅ Channel metadata storage
- ✅ Playlist management
- ✅ EPG program caching
- ✅ Favorites persistence

## Technical Stack

- **Language**: Kotlin
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM with Room Database
- **Player**: ExoPlayer 2.19.1
- **UI**: Android TV Leanback
- **Networking**: OkHttp
- **Image Loading**: Glide

## Build Instructions

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK with API 34

### Building the APK

1. **Using Android Studio:**
   - Open the project in Android Studio
   - Select `Build > Build Bundle(s) / APK(s) > Build APK(s)`
   - The APK will be in `app/build/outputs/apk/debug/` or `app/build/outputs/apk/release/`

2. **Using Gradle Command Line:**
   ```bash
   # Debug build
   ./gradlew assembleDebug
   
   # Release build
   ./gradlew assembleRelease
   ```

3. **For Linux (this environment):**
   ```bash
   cd /home/claude/iptv-player-tv
   chmod +x gradlew
   ./gradlew assembleDebug
   ```

### Installation on Google TV

1. **Enable Developer Options:**
   - Go to Settings > System > About
   - Click on "Build" 7 times
   - Go back to Settings > System > Developer Options
   - Enable "USB Debugging" and "Install unknown apps"

2. **Install via ADB:**
   ```bash
   adb connect <TV_IP_ADDRESS>
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

3. **Install via USB:**
   - Copy APK to USB drive
   - Use a file manager app on TV to install

## Usage Guide

### Adding a Playlist

1. Launch the app on your Google TV
2. Navigate to "Add Playlist"
3. Enter:
   - Playlist Name (e.g., "My IPTV")
   - M3U URL (your IPTV provider's playlist URL)
   - EPG URL (optional, for program guide)
4. Press "Add"

### Watching Channels

1. Navigate to "All Channels" or your favorite category
2. Use D-pad to browse channels
3. Press OK/Select to start playback
4. **During playback:**
   - Press number keys (e.g., 1-0-5) to jump to channel 105
   - Press D-pad Up/Down to change channels sequentially
   - Press Menu to add to favorites
   - Press Back to exit

### Managing Favorites

1. While watching a channel, press Menu
2. Select "Add to Favorites"
3. Access favorites from the main menu

### EPG (TV Guide)

1. Navigate to "TV Guide" from main menu
2. Browse current and upcoming programs
3. Select a program to see details or start playback

### Settings

- **Buffer Size**: Adjust for better streaming performance
- **Decoder**: Choose Hardware (faster) or Software (compatible)
- **Aspect Ratio**: Fit, Fill, or Zoom video to screen

## Supported Formats

### Playlist Formats
- M3U
- M3U8 (Extended M3U)

### Streaming Protocols
- HTTP/HTTPS
- HLS (HTTP Live Streaming)
- RTSP (Real-Time Streaming Protocol)
- DASH (Dynamic Adaptive Streaming over HTTP)

### EPG Formats
- XMLTV

## Project Structure

```
iptv-player-tv/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/iptvplayer/
│   │       │   ├── models/          # Data models (Channel, Playlist, EpgProgram)
│   │       │   ├── database/        # Room DAOs and Database
│   │       │   ├── utils/           # M3U Parser, helpers
│   │       │   ├── MainActivity.kt  # Main browser activity
│   │       │   ├── PlayerActivity.kt # Video player
│   │       │   ├── SettingsActivity.kt
│   │       │   └── EpgActivity.kt   # TV Guide
│   │       ├── res/
│   │       │   ├── layout/          # XML layouts
│   │       │   ├── values/          # Strings, colors, themes
│   │       │   └── drawable/        # Graphics
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## Features Comparison with TiviMate

| Feature | This App | TiviMate |
|---------|----------|----------|
| M3U Playlist | ✅ | ✅ |
| EPG Support | ✅ | ✅ |
| Multiple Playlists | ✅ | ✅ |
| Favorites | ✅ | ✅ |
| Channel Groups | ✅ | ✅ |
| TV Interface | ✅ | ✅ |
| Catch-up TV | 🔄 Planned | ✅ |
| Recording | 🔄 Planned | ✅ (Premium) |
| Multi-view | 🔄 Planned | ✅ (Premium) |
| Parental Control | 🔄 Planned | ✅ (Premium) |

## Troubleshooting

### Channels not loading
- Verify your M3U URL is accessible
- Check internet connection
- Try a different playlist

### Playback issues
- Switch decoder in Settings (Hardware ↔ Software)
- Adjust buffer size
- Check if stream URL is valid

### EPG not showing
- Ensure EPG URL is correct (XMLTV format)
- EPG data may take time to download
- Check channel tvg-id matches EPG channel ID

## Legal Disclaimer

This app is a player only. It does not provide any content, IPTV services, or streams. Users must provide their own legal IPTV service subscriptions and playlists. The developers are not responsible for the content accessed through this application.

## License

This project is for educational purposes. Please ensure you have the legal right to access any IPTV streams you use with this application.

## Contributing

This is a complete, working IPTV player app. Potential enhancements:
- Recording functionality
- Catch-up TV support
- Multi-view (PiP)
- Parental controls
- Custom themes
- Cloud sync for favorites

## Version History

### v1.0.0 (Current)
- Initial release
- M3U playlist support
- EPG integration
- Basic player with ExoPlayer
- Favorites management
- Channel groups
- Android TV Leanback UI

# IPTV Player - Developer Documentation

## Architecture Overview

This IPTV player follows the MVVM (Model-View-ViewModel) architecture pattern with Room Database for persistence.

### Architecture Layers

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│  (Activities, Fragments, UI)        │
├─────────────────────────────────────┤
│         Business Logic Layer        │
│  (ViewModels, Use Cases)            │
├─────────────────────────────────────┤
│         Data Layer                  │
│  (Repository, Room DAOs)            │
├─────────────────────────────────────┤
│         Data Sources                │
│  (Room Database, Network)           │
└─────────────────────────────────────┘
```

## Project Structure

### Package Organization

```
com.iptvplayer/
├── models/              # Data classes
│   ├── Channel.kt       # Channel entity
│   ├── Playlist.kt      # Playlist entity
│   └── EpgProgram.kt    # EPG program entity
│
├── database/            # Room database
│   ├── AppDatabase.kt   # Database instance
│   ├── ChannelDao.kt    # Channel operations
│   ├── PlaylistDao.kt   # Playlist operations
│   └── EpgDao.kt        # EPG operations
│
├── utils/               # Utilities
│   └── M3UParser.kt     # M3U playlist parser
│
├── MainActivity.kt      # Main browser
├── PlayerActivity.kt    # Video player
├── SettingsActivity.kt  # Settings
└── EpgActivity.kt       # TV guide
```

## Core Components

### 1. Database Layer (Room)

#### Channel Entity
```kotlin
@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val url: String,
    val logo: String?,
    val groupTitle: String?,
    val tvgId: String?,
    val playlistId: Long,
    val isFavorite: Boolean
)
```

#### Key DAOs
- **ChannelDao**: CRUD operations for channels
- **PlaylistDao**: Manage playlists
- **EpgDao**: Store and retrieve EPG data

### 2. M3U Parser

The M3U parser extracts channel information from playlist files:

```kotlin
class M3UParser {
    suspend fun parsePlaylist(url: String, playlistId: Long): List<Channel>
}
```

Supports:
- Standard M3U format
- Extended M3U with metadata
- tvg-id, tvg-name, tvg-logo attributes
- group-title for categorization

### 3. Player (ExoPlayer)

Features:
- HLS streaming support
- RTSP protocol support
- DASH streaming
- Adaptive bitrate streaming
- Buffer configuration
- Hardware/software decoding

### 4. UI (Android TV Leanback)

Components:
- **BrowseSupportFragment**: Main browsing interface
- **ImageCardView**: Channel cards
- **GuidedStepFragment**: Settings UI
- **Custom Player Controls**: TV-optimized playback controls

## Key Features Implementation

### Adding a Playlist

Flow:
1. User enters playlist URL
2. `M3UParser` fetches and parses playlist
3. Channels extracted and saved to Room database
4. UI observes database changes via LiveData
5. Channel cards displayed automatically

### Playing a Channel

Flow:
1. User selects channel from browser
2. Channel data passed to PlayerActivity
3. ExoPlayer initialized with stream URL
4. Appropriate media source created (HLS/RTSP/etc.)
5. Playback begins

### EPG Integration

Flow:
1. XMLTV file fetched from EPG URL
2. Programs parsed and stored in database
3. Current/upcoming programs displayed
4. Programs linked to channels via tvg-id

## Extending the App

### Adding New Features

#### 1. Recording Functionality

Add to PlayerActivity:
```kotlin
private fun startRecording() {
    // Implement recording using DownloadManager
    // or custom recording service
}
```

#### 2. Catch-up TV

Modify Channel model:
```kotlin
data class Channel(
    // ... existing fields
    val catchupDays: Int?,
    val catchupSource: String?
)
```

#### 3. Parental Controls

Add to database:
```kotlin
@Entity
data class ParentalControl(
    val channelId: Long,
    val pinRequired: Boolean,
    val ageRating: Int
)
```

### Customization Points

#### Changing Theme
Edit `res/values/colors.xml` and `res/values/themes.xml`

#### Custom Player Controls
Modify `res/layout/custom_player_control.xml`

#### Adding Decoders
Update ExoPlayer configuration in PlayerActivity:
```kotlin
val renderersFactory = DefaultRenderersFactory(context)
    .setExtensionRendererMode(
        DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
    )
```

## Performance Optimization

### Database Queries
- Use LiveData for automatic UI updates
- Index frequently queried fields
- Use transactions for bulk inserts

### Image Loading
```kotlin
// Recommended: Glide integration
Glide.with(context)
    .load(channel.logo)
    .placeholder(R.drawable.default_logo)
    .into(imageView)
```

### Network Requests
- Use OkHttp connection pool
- Implement caching for playlists
- Compress EPG data

## Testing

### Unit Tests
Test M3U parser:
```kotlin
@Test
fun testM3UParsing() {
    val parser = M3UParser()
    val channels = parser.parsePlaylist(testUrl, 1L)
    assert(channels.isNotEmpty())
}
```

### UI Tests
Test channel browsing:
```kotlin
@Test
fun testChannelNavigation() {
    onView(withId(R.id.browse_fragment))
        .perform(click())
    // Verify channel list displayed
}
```

## Build Variants

### Debug
- Logging enabled
- Debug symbols included
- No obfuscation

### Release
- ProGuard enabled
- Optimized
- Signed APK required for distribution

## Dependencies

### Core Dependencies
```gradle
// ExoPlayer
implementation 'com.google.android.exoplayer:exoplayer:2.19.1'

// Room Database
implementation 'androidx.room:room-runtime:2.6.1'
kapt 'androidx.room:room-compiler:2.6.1'

// Leanback
implementation 'androidx.leanback:leanback:1.0.0'
```

## Security Considerations

### HTTPS Enforcement
Always use HTTPS for playlist URLs when possible.

### Input Validation
Validate all user inputs, especially URLs.

### Storage
- Sensitive data should be encrypted
- Use Android Keystore for credentials

## Known Limitations

1. **No DRM Support**: Add ExoPlayer DRM extensions if needed
2. **No Timeshift**: Requires additional buffer management
3. **Limited EPG Formats**: Currently supports XMLTV only

## Future Enhancements

### Priority 1
- [ ] Recording to local storage
- [ ] Catch-up TV support
- [ ] Multi-view (PiP)

### Priority 2
- [ ] Cloud sync for favorites
- [ ] Custom themes
- [ ] Advanced parental controls

### Priority 3
- [ ] Social features
- [ ] Recommendations engine
- [ ] Analytics dashboard

## Troubleshooting Development Issues

### Gradle Build Fails
- Clean project: `./gradlew clean`
- Invalidate caches in Android Studio
- Update Gradle wrapper

### ExoPlayer Issues
- Check codec support on device
- Verify stream URL accessibility
- Test with different decoder modes

### Room Database Issues
- Clear app data
- Verify entity relationships
- Check migration strategies

## Contributing Guidelines

1. Follow Kotlin coding conventions
2. Write unit tests for new features
3. Update documentation
4. Test on multiple TV devices
5. Ensure backward compatibility

## Resources

- [ExoPlayer Documentation](https://exoplayer.dev/)
- [Android TV Guidelines](https://developer.android.com/training/tv)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Leanback Library](https://developer.android.com/training/tv/playback)

## License & Legal

This code is provided for educational purposes. Ensure compliance with:
- IPTV service terms of service
- Content licensing agreements
- Local streaming regulations
- Copyright laws

---

**Version**: 1.0.0  
**Last Updated**: 2025  
**Maintainer**: IPTV Player Development Team

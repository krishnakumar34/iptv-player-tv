# 📺 Supported Streaming Formats

## ✅ ALL Live TV Formats Supported!

Your IPTV Player now supports **ALL major streaming protocols** used in live TV:

### 🎯 Fully Supported Formats:

#### 1. **HLS (HTTP Live Streaming)** ✅
- Extension: `.m3u8`
- Quality: Adaptive bitrate
- Most common IPTV format
- Examples:
  ```
  http://example.com/stream.m3u8
  https://live.provider.com/channel/hls/playlist.m3u8
  ```

#### 2. **RTSP (Real-Time Streaming Protocol)** ✅
- Prefix: `rtsp://`
- Quality: Fixed or adaptive
- Used by many IPTV providers
- Examples:
  ```
  rtsp://server.com:554/live/channel1
  rtsp://192.168.1.100:8554/stream
  ```

#### 3. **HTTP/HTTPS (Progressive Download)** ✅
- Extensions: `.mp4`, `.ts`, `.mkv`, `.avi`, `.flv`
- Quality: Fixed
- Direct stream URLs
- Examples:
  ```
  http://server.com/stream.ts
  https://cdn.provider.com/live/channel.mp4
  ```

#### 4. **DASH (Dynamic Adaptive Streaming)** ✅
- Extension: `.mpd`
- Quality: Adaptive bitrate
- Modern streaming format
- Examples:
  ```
  http://example.com/manifest.mpd
  https://live.server.com/stream.mpd
  ```

#### 5. **SmoothStreaming (Microsoft)** ✅
- Extension: `Manifest`
- Quality: Adaptive bitrate
- Used by some providers
- Examples:
  ```
  http://server.com/stream/Manifest
  ```

#### 6. **UDP Multicast** ✅
- Prefix: `udp://`
- Quality: Fixed
- LAN-based IPTV
- Examples:
  ```
  udp://@239.255.1.1:1234
  ```

---

## 🔧 How It Works

The app uses **ExoPlayer** with all format extensions:

```kotlin
// Auto-detects and plays ALL formats
exoplayer-core          // Core functionality
exoplayer-hls           // .m3u8 streams
exoplayer-dash          // .mpd streams
exoplayer-rtsp          // rtsp:// streams
exoplayer-smoothstreaming // SmoothStreaming
```

**No manual configuration needed!** The player automatically:
- ✅ Detects stream format
- ✅ Chooses correct decoder
- ✅ Handles buffering
- ✅ Manages adaptive bitrate
- ✅ Switches quality automatically

---

## 📋 Testing Different Formats

### Test HLS:
```
http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8
```

### Test DASH:
```
https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd
```

### Test HTTP Progressive:
```
http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4
```

---

## 🎛️ Codec Support

### Video Codecs:
- ✅ H.264 (AVC)
- ✅ H.265 (HEVC)
- ✅ VP8
- ✅ VP9
- ✅ AV1

### Audio Codecs:
- ✅ AAC
- ✅ MP3
- ✅ Opus
- ✅ Vorbis
- ✅ AC3
- ✅ E-AC3

### Container Formats:
- ✅ MPEG-TS (.ts)
- ✅ MP4
- ✅ MKV
- ✅ WebM
- ✅ FLV

---

## 🚀 Performance Features

### Adaptive Bitrate Streaming:
- Automatically switches quality based on connection
- Smooth playback without buffering
- Works with HLS, DASH, SmoothStreaming

### Buffering Optimization:
- Configurable buffer size
- Preloading for smooth playback
- Network-aware buffering

### Hardware Acceleration:
- GPU-accelerated decoding
- Lower battery consumption
- Better performance on 4K streams

---

## ⚙️ Advanced Settings

The player supports:
- ✅ Hardware vs Software decoder selection
- ✅ Buffer size configuration
- ✅ Aspect ratio adjustment
- ✅ Audio track selection (multi-audio streams)
- ✅ Subtitle support

---

## 🆚 Format Comparison

| Format | Adaptive | Quality | Latency | Use Case |
|--------|----------|---------|---------|----------|
| HLS | Yes | High | Medium | Most IPTV |
| DASH | Yes | High | Medium | Modern IPTV |
| RTSP | No | Medium | Low | IP Cameras, Live TV |
| HTTP | No | Varies | High | Simple streams |
| UDP | No | High | Very Low | LAN IPTV |

---

## 🐛 Troubleshooting

### Stream Won't Play?

**Check 1: URL Format**
- HLS must end with `.m3u8`
- RTSP must start with `rtsp://`
- HTTP must start with `http://` or `https://`

**Check 2: Network Access**
- Some streams require VPN
- Firewall may block RTSP/UDP
- Check internet connection

**Check 3: Codec Support**
- Try switching decoder (Hardware ↔ Software)
- Some exotic codecs may not work
- Update to latest app version

### Poor Quality?

**Solution 1: Check Connection**
- Requires stable 5+ Mbps for HD
- Use wired connection if possible
- Close other network apps

**Solution 2: Adjust Buffer**
- Go to Settings
- Increase buffer size
- Restart stream

### Stuttering/Buffering?

**Solution 1: Network**
- Test internet speed
- Try different WiFi channel
- Move closer to router

**Solution 2: Device**
- Close background apps
- Restart TV
- Clear app cache

---

## 📱 M3U Playlist Support

Your M3U playlists can contain **any mix** of formats:

```m3u
#EXTM3U
#EXTINF:-1,Channel 1 (HLS)
http://server.com/channel1.m3u8

#EXTINF:-1,Channel 2 (RTSP)
rtsp://server.com/channel2

#EXTINF:-1,Channel 3 (HTTP)
http://server.com/channel3.ts

#EXTINF:-1,Channel 4 (DASH)
http://server.com/channel4.mpd
```

All will work perfectly! ✅

---

## 🎉 Bottom Line

**You can play ANYTHING:**
- ✅ HLS streams (.m3u8)
- ✅ RTSP streams (rtsp://)
- ✅ HTTP streams (.ts, .mp4, etc.)
- ✅ DASH streams (.mpd)
- ✅ SmoothStreaming
- ✅ UDP multicast
- ✅ Any combination in your M3U

**No configuration needed - just add your playlist and watch!** 📺🚀

package com.iptvplayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.iptvplayer.database.AppDatabase
import com.iptvplayer.models.Channel
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerView: StyledPlayerView
    private var player: ExoPlayer? = null
    private lateinit var database: AppDatabase
    
    private var channelId: Long = -1
    private var channelUrl: String = ""
    private var channelName: String = ""
    
    // Channel list and navigation
    private var allChannels: List<Channel> = emptyList()
    private var currentChannelIndex: Int = 0
    
    // Number input for direct channel access
    private lateinit var channelNumberOverlay: TextView
    private var numberInputBuffer = StringBuilder()
    private val numberInputHandler = Handler(Looper.getMainLooper())
    private var numberInputRunnable: Runnable? = null
    private val NUMBER_INPUT_DELAY = 2000L // 2 seconds delay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        database = AppDatabase.getDatabase(this)
        playerView = findViewById(R.id.player_view)
        channelNumberOverlay = findViewById(R.id.channel_number_overlay)

        channelId = intent.getLongExtra("CHANNEL_ID", -1)
        channelUrl = intent.getStringExtra("CHANNEL_URL") ?: ""
        channelName = intent.getStringExtra("CHANNEL_NAME") ?: ""

        loadChannelList()
        setupPlayer()
    }
    
    private fun loadChannelList() {
        lifecycleScope.launch {
            database.channelDao().getAllChannels().observe(this@PlayerActivity) { channels ->
                allChannels = channels
                // Find current channel index
                currentChannelIndex = channels.indexOfFirst { it.id == channelId }
                if (currentChannelIndex == -1) currentChannelIndex = 0
            }
        }
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer
            
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            // Show loading indicator
                        }
                        Player.STATE_READY -> {
                            // Hide loading indicator
                        }
                        Player.STATE_ENDED -> {
                            // Handle playback end
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(
                        this@PlayerActivity,
                        "Playback error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("IPTV-Player/1.0")
                .setAllowCrossProtocolRedirects(true)

            val mediaSource = when {
                channelUrl.contains(".m3u8") -> {
                    HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(channelUrl))
                }
                else -> {
                    val mediaItem = MediaItem.fromUri(channelUrl)
                    exoPlayer.prepare()
                    return@also
                }
            }

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        // Hide system UI for immersive experience
        hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                changeChannelRelative(-1)
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                changeChannelRelative(1)
                true
            }
            KeyEvent.KEYCODE_MENU -> {
                toggleFavorite()
                true
            }
            KeyEvent.KEYCODE_BACK -> {
                finish()
                true
            }
            // Number keys 0-9
            KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_5,
            KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_8,
            KeyEvent.KEYCODE_9 -> {
                handleNumberInput(keyCode)
                true
            }
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                // If number input is active, confirm immediately
                if (numberInputBuffer.isNotEmpty()) {
                    cancelNumberInputTimer()
                    jumpToChannelNumber()
                    true
                } else {
                    super.onKeyDown(keyCode, event)
                }
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
    
    private fun handleNumberInput(keyCode: Int) {
        val digit = when (keyCode) {
            KeyEvent.KEYCODE_0 -> "0"
            KeyEvent.KEYCODE_1 -> "1"
            KeyEvent.KEYCODE_2 -> "2"
            KeyEvent.KEYCODE_3 -> "3"
            KeyEvent.KEYCODE_4 -> "4"
            KeyEvent.KEYCODE_5 -> "5"
            KeyEvent.KEYCODE_6 -> "6"
            KeyEvent.KEYCODE_7 -> "7"
            KeyEvent.KEYCODE_8 -> "8"
            KeyEvent.KEYCODE_9 -> "9"
            else -> return
        }
        
        // Cancel previous timer
        cancelNumberInputTimer()
        
        // Add digit to buffer (max 4 digits)
        if (numberInputBuffer.length < 4) {
            numberInputBuffer.append(digit)
            updateNumberOverlay()
            
            // Set timer to auto-jump after delay
            numberInputRunnable = Runnable {
                jumpToChannelNumber()
            }
            numberInputHandler.postDelayed(numberInputRunnable!!, NUMBER_INPUT_DELAY)
        }
    }
    
    private fun updateNumberOverlay() {
        if (numberInputBuffer.isNotEmpty()) {
            channelNumberOverlay.text = "Channel: $numberInputBuffer"
            channelNumberOverlay.visibility = View.VISIBLE
        } else {
            channelNumberOverlay.visibility = View.GONE
        }
    }
    
    private fun cancelNumberInputTimer() {
        numberInputRunnable?.let {
            numberInputHandler.removeCallbacks(it)
            numberInputRunnable = null
        }
    }
    
    private fun jumpToChannelNumber() {
        if (numberInputBuffer.isEmpty()) return
        
        val channelNumber = numberInputBuffer.toString().toIntOrNull()
        numberInputBuffer.clear()
        channelNumberOverlay.visibility = View.GONE
        
        if (channelNumber != null && channelNumber > 0 && channelNumber <= allChannels.size) {
            val targetIndex = channelNumber - 1 // Convert to 0-based index
            val targetChannel = allChannels[targetIndex]
            switchToChannel(targetChannel, targetIndex)
        } else {
            Toast.makeText(this, "Channel $channelNumber not found", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun changeChannelRelative(direction: Int) {
        if (allChannels.isEmpty()) return
        
        currentChannelIndex = (currentChannelIndex + direction + allChannels.size) % allChannels.size
        val targetChannel = allChannels[currentChannelIndex]
        switchToChannel(targetChannel, currentChannelIndex)
    }
    
    private fun switchToChannel(channel: Channel, index: Int) {
        currentChannelIndex = index
        channelId = channel.id
        channelUrl = channel.url
        channelName = channel.name
        
        // Show channel info
        val channelNumber = index + 1
        Toast.makeText(
            this,
            "Channel $channelNumber: ${channel.name}",
            Toast.LENGTH_SHORT
        ).show()
        
        // Switch stream
        playChannel(channel.url)
    }
    
    private fun playChannel(url: String) {
        player?.stop()
        
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("IPTV-Player/1.0")
            .setAllowCrossProtocolRedirects(true)

        val mediaSource = when {
            url.contains(".m3u8") -> {
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(url))
            }
            else -> {
                null
            }
        }

        mediaSource?.let {
            player?.setMediaSource(it)
            player?.prepare()
            player?.playWhenReady = true
        } ?: run {
            val mediaItem = MediaItem.fromUri(url)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.playWhenReady = true
        }
    }

    private fun toggleFavorite() {
        if (channelId != -1L) {
            lifecycleScope.launch {
                val channel = database.channelDao().getChannelById(channelId)
                channel?.let {
                    database.channelDao().updateFavorite(channelId, !it.isFavorite)
                    val message = if (!it.isFavorite) "Added to favorites" else "Removed from favorites"
                    Toast.makeText(this@PlayerActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        player?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelNumberInputTimer()
        player?.release()
        player = null
    }
}

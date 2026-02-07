package com.iptvplayer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.iptvplayer.database.AppDatabase
import com.iptvplayer.models.Channel
import com.iptvplayer.models.Playlist
import com.iptvplayer.utils.M3UParser
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var browseSupportFragment: BrowseSupportFragment
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)

        setupUI()
        observeData()
    }

    private fun setupUI() {
        browseSupportFragment = supportFragmentManager
            .findFragmentById(R.id.browse_fragment) as BrowseSupportFragment

        browseSupportFragment.apply {
            title = getString(R.string.browse_title)
            headersState = BrowseSupportFragment.HEADERS_ENABLED
            isHeadersTransitionOnBackEnabled = true
            brandColor = resources.getColor(R.color.primary, null)
            onItemViewClickedListener = ItemViewClickedListener()
        }

        createHeaders()
    }

    private fun createHeaders() {
        val headersList = mutableListOf<HeaderItem>()
        
        headersList.add(HeaderItem(0, getString(R.string.all_channels)))
        headersList.add(HeaderItem(1, getString(R.string.favorites)))
        headersList.add(HeaderItem(2, getString(R.string.add_playlist)))
        headersList.add(HeaderItem(3, getString(R.string.settings)))
        headersList.add(HeaderItem(4, getString(R.string.epg)))

        headersList.forEach { header ->
            rowsAdapter.add(ListRow(header, ArrayObjectAdapter(ChannelPresenter())))
        }

        browseSupportFragment.adapter = rowsAdapter
    }

    private fun observeData() {
        database.channelDao().getAllChannels().observe(this) { channels ->
            updateChannelRow(0, channels)
        }

        database.channelDao().getFavoriteChannels().observe(this) { favorites ->
            updateChannelRow(1, favorites)
        }
    }

    private fun updateChannelRow(rowIndex: Int, channels: List<Channel>) {
        val listRow = rowsAdapter.get(rowIndex) as? ListRow
        listRow?.let {
            val adapter = it.adapter as ArrayObjectAdapter
            adapter.clear()
            channels.forEach { channel ->
                adapter.add(channel)
            }
        }
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            when (item) {
                is Channel -> {
                    val intent = Intent(this@MainActivity, PlayerActivity::class.java).apply {
                        putExtra("CHANNEL_ID", item.id)
                        putExtra("CHANNEL_NAME", item.name)
                        putExtra("CHANNEL_URL", item.url)
                    }
                    startActivity(intent)
                }
            }

            when ((row as? ListRow)?.headerItem?.id) {
                2L -> showAddPlaylistDialog()
                3L -> startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                4L -> startActivity(Intent(this@MainActivity, EpgActivity::class.java))
            }
        }
    }

    private fun showAddPlaylistDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        val layout = layoutInflater.inflate(R.layout.dialog_add_playlist, null)
        val urlInput = layout.findViewById<android.widget.EditText>(R.id.playlist_url)
        val nameInput = layout.findViewById<android.widget.EditText>(R.id.playlist_name)

        builder.setView(layout)
            .setTitle(R.string.add_playlist)
            .setPositiveButton(R.string.add) { _, _ ->
                val url = urlInput.text.toString()
                val name = nameInput.text.toString()
                if (url.isNotEmpty() && name.isNotEmpty()) {
                    addPlaylist(name, url)
                } else {
                    Toast.makeText(this, "Please enter both URL and name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun addPlaylist(name: String, url: String) {
        lifecycleScope.launch {
            try {
                val playlist = Playlist(name = name, url = url)
                val playlistId = database.playlistDao().insertPlaylist(playlist)
                
                val parser = M3UParser()
                val channels = parser.parsePlaylist(url, playlistId)
                database.channelDao().insertChannels(channels)
                
                Toast.makeText(this@MainActivity, "Playlist added successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error adding playlist: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class ChannelPresenter : Presenter() {
        override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context).apply {
                isFocusable = true
                isFocusableInTouchMode = true
                setMainImageDimensions(313, 176)
            }
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val channel = item as Channel
            val cardView = viewHolder.view as ImageCardView
            
            cardView.titleText = channel.name
            cardView.contentText = channel.groupTitle ?: ""
            
            // Load channel logo if available
            channel.logo?.let { logoUrl ->
                // Use Glide or similar to load image
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }
}

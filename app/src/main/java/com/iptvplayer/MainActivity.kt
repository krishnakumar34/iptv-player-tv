package com.iptvplayer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.iptvplayer.database.AppDatabase
import com.iptvplayer.models.Channel
import com.iptvplayer.models.Playlist
import com.iptvplayer.utils.M3UParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : FragmentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var browseSupportFragment: BrowseSupportFragment
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    // Define IDs for our rows to avoid confusion
    companion object {
        const val ROW_ALL_CHANNELS = 0L
        const val ROW_FAVORITES = 1L
        const val ROW_ADD_PLAYLIST = 2L
        const val ROW_SETTINGS = 3L
        const val ROW_EPG = 4L
    }

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
            
            // Safe color retrieval for all Android versions
            brandColor = ContextCompat.getColor(this@MainActivity, R.color.primary)
            
            onItemViewClickedListener = ItemViewClickedListener()
        }

        createHeaders()
    }

    private fun createHeaders() {
        rowsAdapter.clear()

        // 1. Create the Headers
        val headerAll = HeaderItem(ROW_ALL_CHANNELS, getString(R.string.all_channels))
        val headerFav = HeaderItem(ROW_FAVORITES, getString(R.string.favorites))
        val headerAdd = HeaderItem(ROW_ADD_PLAYLIST, getString(R.string.add_playlist))
        val headerSet = HeaderItem(ROW_SETTINGS, getString(R.string.settings))
        val headerEpg = HeaderItem(ROW_EPG, getString(R.string.epg))

        // 2. Add empty adapters for dynamic data (Channels & Favorites)
        rowsAdapter.add(ListRow(headerAll, ArrayObjectAdapter(ChannelPresenter())))
        rowsAdapter.add(ListRow(headerFav, ArrayObjectAdapter(ChannelPresenter())))

        // 3. Add "Dummy" items for the Action rows so they are clickable
        // Without this, the row is empty and you cannot select it on TV!
        
        // Add Playlist Row
        val addAdapter = ArrayObjectAdapter(ChannelPresenter())
        addAdapter.add(Channel(id = -1, name = "Click to Add Playlist", url = "", logo = "", groupTitle = "Action"))
        rowsAdapter.add(ListRow(headerAdd, addAdapter))

        // Settings Row
        val setAdapter = ArrayObjectAdapter(ChannelPresenter())
        setAdapter.add(Channel(id = -2, name = "Open Settings", url = "", logo = "", groupTitle = "Action"))
        rowsAdapter.add(ListRow(headerSet, setAdapter))

        // EPG Row
        val epgAdapter = ArrayObjectAdapter(ChannelPresenter())
        epgAdapter.add(Channel(id = -3, name = "View TV Guide", url = "", logo = "", groupTitle = "Action"))
        rowsAdapter.add(ListRow(headerEpg, epgAdapter))

        browseSupportFragment.adapter = rowsAdapter
    }

    private fun observeData() {
        // Observe All Channels
        database.channelDao().getAllChannels().observe(this) { channels ->
            updateChannelRow(ROW_ALL_CHANNELS.toInt(), channels)
        }

        // Observe Favorites
        database.channelDao().getFavoriteChannels().observe(this) { favorites ->
            updateChannelRow(ROW_FAVORITES.toInt(), favorites)
        }
    }

    private fun updateChannelRow(rowIndex: Int, channels: List<Channel>) {
        if (rowIndex >= rowsAdapter.size()) return

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
            // Check which ROW we are in using the Header ID
            val headerId = (row as? ListRow)?.headerItem?.id ?: return

            when (headerId) {
                ROW_ADD_PLAYLIST -> {
                    showAddPlaylistDialog()
                    return // Stop here
                }
                ROW_SETTINGS -> {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                    return // Stop here
                }
                ROW_EPG -> {
                    startActivity(Intent(this@MainActivity, EpgActivity::class.java))
                    return // Stop here
                }
            }

            // If we are here, it means we clicked a CHANNEL in the first two rows
            if (item is Channel) {
                val intent = Intent(this@MainActivity, PlayerActivity::class.java).apply {
                    putExtra("CHANNEL_ID", item.id)
                    putExtra("CHANNEL_NAME", item.name)
                    putExtra("CHANNEL_URL", item.url)
                }
                startActivity(intent)
            }
        }
    }

    private fun showAddPlaylistDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        
        // Make sure 'dialog_add_playlist.xml' exists in your layout folder!
        val layout = inflater.inflate(R.layout.dialog_add_playlist, null)
        
        val urlInput = layout.findViewById<EditText>(R.id.playlist_url)
        val nameInput = layout.findViewById<EditText>(R.id.playlist_name)

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
            .create()
            .show()
    }

    private fun addPlaylist(name: String, url: String) {
        // Run database operations in the background (IO Thread)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val playlist = Playlist(name = name, url = url)
                val playlistId = database.playlistDao().insertPlaylist(playlist)
                
                val parser = M3UParser()
                val channels = parser.parsePlaylist(url, playlistId)
                database.channelDao().insertChannels(channels)
                
                // Show success message on the Main UI Thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Playlist added! ${channels.size} channels found.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Show error on the Main UI Thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error adding playlist: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private class ChannelPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
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
            
            // Only try to load an image if the URL is valid
            if (!channel.logo.isNullOrEmpty()) {
                // Use Glide here if you have it installed:
                // Glide.with(viewHolder.view.context).load(channel.logo).into(cardView.mainImageView)
            } else {
                cardView.mainImage = null // Clear previous image
                // Optionally set a default icon for "Add Playlist"
                if (channel.id == -1L) {
                     cardView.setMainImage(ContextCompat.getDrawable(viewHolder.view.context, android.R.drawable.ic_input_add))
                }
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }
}

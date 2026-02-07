package com.iptvplayer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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
    
    private val HEADER_ALL_CHANNELS = 0L
    private val HEADER_FAVORITES = 1L
    private val HEADER_GROUPS_START = 100L
    private val HEADER_ADD_PLAYLIST = 9998L
    private val HEADER_SETTINGS = 9999L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)
        setupUI()
        loadData()
    }

    private fun setupUI() {
        browseSupportFragment = supportFragmentManager
            .findFragmentById(R.id.browse_fragment) as BrowseSupportFragment

        browseSupportFragment.apply {
            title = getString(R.string.browse_title)
            headersState = BrowseSupportFragment.HEADERS_ENABLED
            isHeadersTransitionOnBackEnabled = true
            brandColor = resources.getColor(R.color.primary, null)
            searchAffordanceColor = resources.getColor(R.color.accent, null)
            onItemViewClickedListener = ItemViewClickedListener()
        }

        browseSupportFragment.adapter = rowsAdapter
    }

    private fun loadData() {
        // Observe all channels
        database.channelDao().getAllChannels().observe(this) { channels ->
            updateUI(channels)
        }
    }

    private fun updateUI(allChannels: List<Channel>) {
        rowsAdapter.clear()

        // Add "All Channels" row
        val allChannelsAdapter = ArrayObjectAdapter(ChannelPresenter())
        allChannels.forEach { allChannelsAdapter.add(it) }
        rowsAdapter.add(ListRow(
            HeaderItem(HEADER_ALL_CHANNELS, getString(R.string.all_channels)),
            allChannelsAdapter
        ))

        // Add "Favorites" row
        val favorites = allChannels.filter { it.isFavorite }
        if (favorites.isNotEmpty()) {
            val favoritesAdapter = ArrayObjectAdapter(ChannelPresenter())
            favorites.forEach { favoritesAdapter.add(it) }
            rowsAdapter.add(ListRow(
                HeaderItem(HEADER_FAVORITES, getString(R.string.favorites)),
                favoritesAdapter
            ))
        }

        // Add channel groups
        val groups = allChannels.mapNotNull { it.groupTitle }.distinct().sorted()
        groups.forEachIndexed { index, groupName ->
            val groupChannels = allChannels.filter { it.groupTitle == groupName }
            val groupAdapter = ArrayObjectAdapter(ChannelPresenter())
            groupChannels.forEach { groupAdapter.add(it) }
            rowsAdapter.add(ListRow(
                HeaderItem(HEADER_GROUPS_START + index, groupName),
                groupAdapter
            ))
        }

        // Add action rows
        addActionRow(HEADER_ADD_PLAYLIST, getString(R.string.add_playlist), "Add M3U/M3U8 Playlist")
        addActionRow(HEADER_SETTINGS, getString(R.string.settings), "Configure Player Settings")
    }

    private fun addActionRow(id: Long, title: String, description: String) {
        val actionAdapter = ArrayObjectAdapter(ActionPresenter())
        actionAdapter.add(ActionItem(title, description))
        rowsAdapter.add(ListRow(HeaderItem(id, title), actionAdapter))
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            when (item) {
                is Channel -> playChannel(item)
                is ActionItem -> handleAction((row as ListRow).headerItem.id)
            }
        }
    }

    private fun playChannel(channel: Channel) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("CHANNEL_ID", channel.id)
            putExtra("CHANNEL_NAME", channel.name)
            putExtra("CHANNEL_URL", channel.url)
        }
        startActivity(intent)
    }

    private fun handleAction(headerId: Long) {
        when (headerId) {
            HEADER_ADD_PLAYLIST -> showAddPlaylistDialog()
            HEADER_SETTINGS -> startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun showAddPlaylistDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_playlist, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.playlist_name)
        val urlInput = dialogView.findViewById<EditText>(R.id.playlist_url)
        val epgInput = dialogView.findViewById<EditText>(R.id.epg_url)

        AlertDialog.Builder(this)
            .setTitle(R.string.add_playlist)
            .setView(dialogView)
            .setPositiveButton(R.string.add) { _, _ ->
                val name = nameInput.text.toString().trim()
                val url = urlInput.text.toString().trim()
                val epgUrl = epgInput.text.toString().trim()

                if (name.isEmpty() || url.isEmpty()) {
                    Toast.makeText(this, "Please enter playlist name and URL", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                addPlaylist(name, url, epgUrl.ifEmpty { null })
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun addPlaylist(name: String, url: String, epgUrl: String?) {
        lifecycleScope.launch {
            try {
                Toast.makeText(this@MainActivity, "Loading playlist...", Toast.LENGTH_SHORT).show()

                withContext(Dispatchers.IO) {
                    // Save playlist
                    val playlist = Playlist(name = name, url = url, epgUrl = epgUrl)
                    val playlistId = database.playlistDao().insertPlaylist(playlist)

                    // Parse and save channels
                    val parser = M3UParser()
                    val channels = parser.parsePlaylist(url, playlistId)
                    database.channelDao().insertChannels(channels)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Playlist added successfully (${name})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    // Channel Presenter for grid cards
    private inner class ChannelPresenter : Presenter() {
        override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context).apply {
                isFocusable = true
                isFocusableInTouchMode = true
                setMainImageDimensions(313, 176)
                setBackgroundColor(resources.getColor(R.color.card_background, null))
            }
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val channel = item as Channel
            val cardView = viewHolder.view as ImageCardView

            cardView.titleText = channel.name
            cardView.contentText = channel.groupTitle ?: "Live TV"

            // Load channel logo
            channel.logo?.let { logoUrl ->
                if (logoUrl.isNotEmpty()) {
                    try {
                        Glide.with(this@MainActivity)
                            .load(logoUrl)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_gallery)
                            .into(cardView.mainImageView)
                    } catch (e: Exception) {
                        cardView.mainImage = resources.getDrawable(android.R.drawable.ic_menu_gallery, null)
                    }
                }
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }

    // Action Presenter for settings/add playlist
    private inner class ActionPresenter : Presenter() {
        override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context).apply {
                isFocusable = true
                isFocusableInTouchMode = true
                setMainImageDimensions(313, 176)
            }
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val action = item as ActionItem
            val cardView = viewHolder.view as ImageCardView

            cardView.titleText = action.title
            cardView.contentText = action.description
            cardView.mainImage = resources.getDrawable(android.R.drawable.ic_menu_preferences, null)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }

    data class ActionItem(val title: String, val description: String)
}

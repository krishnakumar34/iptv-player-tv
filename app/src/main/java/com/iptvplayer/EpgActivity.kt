package com.iptvplayer

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.iptvplayer.database.AppDatabase
import com.iptvplayer.models.EpgProgram
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EpgActivity : FragmentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var browseSupportFragment: BrowseSupportFragment
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)

        setupUI()
        loadEpgData()
    }

    private fun setupUI() {
        browseSupportFragment = supportFragmentManager
            .findFragmentById(R.id.browse_fragment) as BrowseSupportFragment

        browseSupportFragment.apply {
            title = getString(R.string.epg)
            headersState = BrowseSupportFragment.HEADERS_ENABLED
            isHeadersTransitionOnBackEnabled = true
            brandColor = resources.getColor(R.color.primary, null)
        }

        browseSupportFragment.adapter = rowsAdapter
    }

    private fun loadEpgData() {
        lifecycleScope.launch {
            // Load channels and their programs
            database.channelDao().getAllChannels().observe(this@EpgActivity) { channels ->
                rowsAdapter.clear()
                
                channels.forEach { channel ->
                    channel.tvgId?.let { tvgId ->
                        val currentTime = System.currentTimeMillis()
                        database.epgDao().getProgramsForChannel(tvgId, currentTime)
                            .observe(this@EpgActivity) { programs ->
                                updateEpgRow(channel.name, programs)
                            }
                    }
                }
            }
        }
    }

    private fun updateEpgRow(channelName: String, programs: List<EpgProgram>) {
        val headerItem = HeaderItem(channelName)
        val adapter = ArrayObjectAdapter(EpgPresenter())
        
        programs.forEach { program ->
            adapter.add(program)
        }
        
        rowsAdapter.add(ListRow(headerItem, adapter))
    }

    private class EpgPresenter : Presenter() {
        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        override fun onCreateViewHolder(parent: android.view.ViewGroup): ViewHolder {
            val cardView = ImageCardView(parent.context).apply {
                isFocusable = true
                isFocusableInTouchMode = true
                setMainImageDimensions(313, 176)
            }
            return ViewHolder(cardView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val program = item as EpgProgram
            val cardView = viewHolder.view as ImageCardView
            
            val startTime = dateFormat.format(Date(program.startTime))
            val endTime = dateFormat.format(Date(program.endTime))
            
            cardView.titleText = program.title
            cardView.contentText = "$startTime - $endTime"
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
    }
}

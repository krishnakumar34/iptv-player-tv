package com.iptvplayer

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction

class SettingsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (savedInstanceState == null) {
            GuidedStepSupportFragment.addAsRoot(this, SettingsFragment(), android.R.id.content)
        }
    }

    class SettingsFragment : GuidedStepSupportFragment() {

        override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
            return GuidanceStylist.Guidance(
                getString(R.string.settings),
                "Configure your IPTV player",
                "",
                null
            )
        }

        override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
            addAction(actions, ACTION_BUFFER_SIZE, "Buffer Size", "Adjust video buffer")
            addAction(actions, ACTION_DECODER, "Decoder", "Hardware / Software")
            addAction(actions, ACTION_ASPECT_RATIO, "Aspect Ratio", "Fit / Fill / Zoom")
            addAction(actions, ACTION_CLEAR_CACHE, "Clear Cache", "Remove cached data")
            addAction(actions, ACTION_ABOUT, "About", "Version 1.0.0")
        }

        private fun addAction(
            actions: MutableList<GuidedAction>,
            id: Long,
            title: String,
            desc: String
        ) {
            actions.add(
                GuidedAction.Builder(context)
                    .id(id)
                    .title(title)
                    .description(desc)
                    .build()
            )
        }

        override fun onGuidedActionClicked(action: GuidedAction) {
            when (action.id) {
                ACTION_BUFFER_SIZE -> {
                    // Show buffer size options
                }
                ACTION_DECODER -> {
                    // Show decoder options
                }
                ACTION_ASPECT_RATIO -> {
                    // Show aspect ratio options
                }
                ACTION_CLEAR_CACHE -> {
                    // Clear cache
                }
            }
        }

        companion object {
            private const val ACTION_BUFFER_SIZE = 1L
            private const val ACTION_DECODER = 2L
            private const val ACTION_ASPECT_RATIO = 3L
            private const val ACTION_CLEAR_CACHE = 4L
            private const val ACTION_ABOUT = 5L
        }
    }
}

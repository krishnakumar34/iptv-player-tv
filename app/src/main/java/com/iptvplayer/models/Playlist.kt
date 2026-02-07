package com.iptvplayer.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val url: String,
    val epgUrl: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

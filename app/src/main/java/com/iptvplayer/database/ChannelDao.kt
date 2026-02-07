package com.iptvplayer.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.iptvplayer.models.Channel

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels ORDER BY position ASC")
    fun getAllChannels(): LiveData<List<Channel>>

    @Query("SELECT * FROM channels WHERE playlistId = :playlistId ORDER BY position ASC")
    fun getChannelsByPlaylist(playlistId: Long): LiveData<List<Channel>>

    @Query("SELECT * FROM channels WHERE isFavorite = 1 ORDER BY position ASC")
    fun getFavoriteChannels(): LiveData<List<Channel>>

    @Query("SELECT * FROM channels WHERE groupTitle = :group ORDER BY position ASC")
    fun getChannelsByGroup(group: String): LiveData<List<Channel>>

    @Query("SELECT DISTINCT groupTitle FROM channels WHERE groupTitle IS NOT NULL")
    fun getAllGroups(): LiveData<List<String>>

    @Query("SELECT * FROM channels WHERE id = :channelId")
    suspend fun getChannelById(channelId: Long): Channel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: Channel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<Channel>)

    @Update
    suspend fun updateChannel(channel: Channel)

    @Delete
    suspend fun deleteChannel(channel: Channel)

    @Query("DELETE FROM channels WHERE playlistId = :playlistId")
    suspend fun deleteChannelsByPlaylist(playlistId: Long)

    @Query("UPDATE channels SET isFavorite = :isFavorite WHERE id = :channelId")
    suspend fun updateFavorite(channelId: Long, isFavorite: Boolean)
}

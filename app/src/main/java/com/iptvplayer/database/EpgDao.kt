package com.iptvplayer.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.iptvplayer.models.EpgProgram

@Dao
interface EpgDao {
    @Query("SELECT * FROM epg_programs WHERE channelId = :channelId AND endTime > :currentTime ORDER BY startTime ASC")
    fun getProgramsForChannel(channelId: String, currentTime: Long): LiveData<List<EpgProgram>>

    @Query("SELECT * FROM epg_programs WHERE channelId = :channelId AND startTime <= :currentTime AND endTime > :currentTime")
    suspend fun getCurrentProgram(channelId: String, currentTime: Long): EpgProgram?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgram(program: EpgProgram)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrograms(programs: List<EpgProgram>)

    @Query("DELETE FROM epg_programs WHERE endTime < :time")
    suspend fun deleteOldPrograms(time: Long)

    @Query("DELETE FROM epg_programs")
    suspend fun deleteAllPrograms()
}

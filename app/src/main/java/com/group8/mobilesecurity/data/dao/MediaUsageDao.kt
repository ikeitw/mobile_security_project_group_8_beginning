package com.group8.mobilesecurity.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.group8.mobilesecurity.data.model.MediaUsage

@Dao
interface MediaUsageDao {
    @Insert
    suspend fun insert(usage: MediaUsage)

    @Query("SELECT * FROM media_usage ORDER BY timestamp DESC LIMIT 100")
    suspend fun recentUsages(): List<MediaUsage>

    @Query("DELETE FROM media_usage")
    suspend fun clearAll()
}

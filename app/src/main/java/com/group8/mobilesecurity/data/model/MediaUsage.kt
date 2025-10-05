package com.group8.mobilesecurity.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_usage")
data class MediaUsage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,          // "camera" or "microphone"
    val timestamp: Long       // epoch millis
)

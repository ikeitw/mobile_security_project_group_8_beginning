package com.group8.mobilesecurity.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.group8.mobilesecurity.data.dao.MediaUsageDao
import com.group8.mobilesecurity.data.dao.UserDao
import com.group8.mobilesecurity.data.model.MediaUsage
import com.group8.mobilesecurity.data.model.User

@Database(
    entities = [User::class, MediaUsage::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mediaUsageDao(): MediaUsageDao   // <-- expose DAO
}

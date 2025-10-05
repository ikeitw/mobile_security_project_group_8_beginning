package com.group8.mobilesecurity.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.group8.mobilesecurity.data.dao.UserDao
import com.group8.mobilesecurity.data.model.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

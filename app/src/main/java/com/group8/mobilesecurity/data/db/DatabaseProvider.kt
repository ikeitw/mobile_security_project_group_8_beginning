package com.group8.mobilesecurity.data.db

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile private var db: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            db ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "mobile_security_db"
            )
                .fallbackToDestructiveMigration()   // <— avoids migration crash during dev
                .build().also { db = it }
        }
    }
}

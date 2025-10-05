package com.group8.mobilesecurity.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.group8.mobilesecurity.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUser(username: String): User?
}

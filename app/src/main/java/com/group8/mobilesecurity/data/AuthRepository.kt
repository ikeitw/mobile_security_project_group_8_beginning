package com.group8.mobilesecurity.data

import android.content.Context
import com.group8.mobilesecurity.data.db.DatabaseProvider
import com.group8.mobilesecurity.data.model.User
import com.group8.mobilesecurity.utils.Crypto

class AuthRepository(private val context: Context) {
    private val dao = DatabaseProvider.getDatabase(context).userDao()

    suspend fun registerUser(username: String, plainPassword: String): Result<Unit> {
        return try {
            val enc = Crypto.weakEncrypt(plainPassword)
            dao.insertUser(User(username = username, password = enc))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(username: String, plainPassword: String): Result<Boolean> {
        return try {
            val stored = dao.getUser(username)
            if (stored == null) {
                Result.success(false)
            } else {
                val decrypted = Crypto.weakDecrypt(stored.password)
                Result.success(decrypted == plainPassword)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // For demo/testing: returns stored encrypted value (so you can show attacker can read it).
    suspend fun getEncryptedPassword(username: String): String? {
        return dao.getUser(username)?.password
    }

    suspend fun getDecryptedPassword(username: String): String? {
        return dao.getUser(username)?.let { Crypto.weakDecrypt(it.password) }
    }
}

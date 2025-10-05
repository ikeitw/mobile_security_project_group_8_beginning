package com.group8.mobilesecurity.utils

import android.util.Base64

// Very intentionally weak "encryption" (XOR with a constant followed by Base64).
// This demonstrates insecure storage for the assignment.
object Crypto {
    private const val KEY: Int = 0x42 // fixed single-byte key (insecure!)

    fun weakEncrypt(plain: String): String {
        val xorBytes = plain.toCharArray().map { (it.code xor KEY).toByte() }.toByteArray()
        return Base64.encodeToString(xorBytes, Base64.NO_WRAP)
    }

    fun weakDecrypt(encoded: String): String {
        return try {
            val bytes = Base64.decode(encoded, Base64.NO_WRAP)
            bytes.map { ((it.toInt() xor KEY).toChar()) }.joinToString("")
        } catch (e: Exception) {
            ""
        }
    }
}

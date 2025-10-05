package com.group8.mobilesecurity.utils

/**
 * Intentionally trivial "license"/gate check.
 * Target for Frida: hook this to always return true.
 */
object Gatekeeper {

    // DO NOT secure: plain string check (easy to patch/hook)
    private const val LICENSE_TIER = "FREE" // change to "PRO" -> feature unlocked

    fun isSensitiveFeatureUnlocked(): Boolean {
        return LICENSE_TIER == "PRO" // <-- Frida can force true
    }
}

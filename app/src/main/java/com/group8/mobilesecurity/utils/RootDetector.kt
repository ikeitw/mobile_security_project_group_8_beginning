package com.group8.mobilesecurity.utils

import android.os.Build
import android.os.Debug
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object RootDetector {

    // 1) su binary on common paths
    private val suPaths = listOf(
        "/system/bin/su", "/system/xbin/su", "/sbin/su",
        "/system/su", "/system/bin/.ext/su", "/system/usr/we-need-root/su"
    )

    private fun hasSuBinary(): Boolean = suPaths.any { File(it).exists() }

    // 2) Build tags (test-keys often on rooted/custom ROMs)
    private fun hasTestKeys(): Boolean = Build.TAGS?.contains("test-keys") == true

    // 3) Try running "su -c id"
    private fun canExecuteSu(): Boolean {
        return try {
            val p = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val exit = p.waitFor()
            exit == 0
        } catch (_: Exception) {
            false
        }
    }

    // 4) Debugger attached (often true during dynamic analysis)
    private fun isDebugger(): Boolean = Debug.isDebuggerConnected() || Debug.waitingForDebugger()

    // 5) Quick Frida heuristic: look for frida-server process or the default port (27042)
    private fun fridaRunning(): Boolean {
        try {
            // check process list
            val p = Runtime.getRuntime().exec("ps")
            BufferedReader(InputStreamReader(p.inputStream)).use { r ->
                var line: String?
                while (r.readLine().also { line = it } != null) {
                    if (line!!.contains("frida", ignoreCase = true)) return true
                }
            }
        } catch (_: Exception) { /* ignore */ }

        // check tcp ports (frida default 27042)
        try {
            File("/proc/net/tcp").takeIf { it.exists() }?.readText()?.let { txt ->
                if (txt.contains("000069A2", ignoreCase = true)) return true // 0x69A2 = 27042 little-endian
            }
        } catch (_: Exception) { /* ignore */ }

        return false
    }

    data class Result(
        val rooted: Boolean,
        val debugger: Boolean,
        val frida: Boolean,
        val details: List<String>
    )

    fun check(): Result {
        val details = mutableListOf<String>()
        val su = hasSuBinary().also { if (it) details += "su binary present" }
        val testKeys = hasTestKeys().also { if (it) details += "build tags contain test-keys" }
        val canSu = canExecuteSu().also { if (it) details += "su executed successfully" }
        val dbg = isDebugger().also { if (it) details += "debugger attached" }
        val frida = fridaRunning().also { if (it) details += "frida likely running" }

        return Result(
            rooted = su || testKeys || canSu,
            debugger = dbg,
            frida = frida,
            details = if (details.isEmpty()) listOf("No indicators found") else details
        )
    }
}

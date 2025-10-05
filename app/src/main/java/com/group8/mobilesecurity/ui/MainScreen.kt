package com.group8.mobilesecurity.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var current by remember { mutableStateOf("network") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MobileSecurityLab") },
                actions = {
                    TextButton(onClick = { current = "network" }) { Text("Network") }
                    TextButton(onClick = { current = "auth" }) { Text("Auth") }
                    TextButton(onClick = { current = "media" }) { Text("Media") }
                    TextButton(onClick = { current = "security" }) { Text("Security") } // <-- add
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(8.dp)) {
            when (current) {
                "network" -> NetworkScreen()
                "auth" -> AuthScreen()
                "security" -> SecurityScreen()
                "media" -> MediaScreen()
            }
        }
    }
}

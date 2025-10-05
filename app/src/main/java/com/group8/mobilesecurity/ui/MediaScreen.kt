package com.group8.mobilesecurity.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.group8.mobilesecurity.data.db.AppDatabase
import com.group8.mobilesecurity.data.db.DatabaseProvider
import com.group8.mobilesecurity.data.model.MediaUsage
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun MediaScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { DatabaseProvider.getDatabase(ctx) }
    var usages by remember { mutableStateOf<List<MediaUsage>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    val cameraPermission = Manifest.permission.CAMERA
    val micPermission = Manifest.permission.RECORD_AUDIO

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            scope.launch { recordAndReload(db, "camera") { usages = it } }
            Toast.makeText(ctx, "Camera permitted (simulated usage recorded)", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(ctx, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val micLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            scope.launch { recordAndReload(db, "microphone") { usages = it } }
            Toast.makeText(ctx, "Microphone permitted (simulated usage recorded)", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(ctx, "Microphone permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) { usages = db.mediaUsageDao().recentUsages() }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Sensor Usage (camera/mic) — stored locally", style = MaterialTheme.typography.titleLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { cameraLauncher.launch(cameraPermission) }) {
                Text("Use Camera (simulate)")
            }
            Button(onClick = { micLauncher.launch(micPermission) }) {
                Text("Use Microphone (simulate)")
            }
            OutlinedButton(onClick = {
                scope.launch {
                    db.mediaUsageDao().clearAll()
                    usages = emptyList()
                }
            }) { Text("Clear history") }
        }

        HorizontalDivider()

        Text("Recent usage (most recent first):", style = MaterialTheme.typography.titleMedium)
        if (usages.isEmpty()) {
            Text("No usage recorded yet.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(usages) { u ->
                    Text("${u.type.uppercase(Locale.getDefault())} — ${Date(u.timestamp)}")
                }
            }
        }
    }
}

private suspend fun recordAndReload(
    db: AppDatabase,
    type: String,
    cb: (List<MediaUsage>) -> Unit
) {
    val dao = db.mediaUsageDao()
    dao.insert(MediaUsage(type = type, timestamp = System.currentTimeMillis()))
    cb(dao.recentUsages())
}


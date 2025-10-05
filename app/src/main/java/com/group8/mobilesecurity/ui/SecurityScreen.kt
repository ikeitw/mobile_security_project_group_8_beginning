package com.group8.mobilesecurity.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.group8.mobilesecurity.utils.Gatekeeper
import com.group8.mobilesecurity.utils.RootDetector

@Composable
fun SecurityScreen() {
    val ctx = LocalContext.current
    var result by remember { mutableStateOf(RootDetector.check()) }

    Card(Modifier.fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Security Checks", style = MaterialTheme.typography.titleLarge)

            Text("Root detected: ${result.rooted}")
            Text("Debugger attached: ${result.debugger}")
            Text("Frida running: ${result.frida}")
            Text("Details: ${result.details.joinToString()}")

            Spacer(Modifier.height(8.dp))

            // Sensitive feature – blocked if root or Frida found (until bypassed)
            val blocked = result.rooted || result.frida

            Button(
                onClick = {
                    if (blocked) {
                        Toast.makeText(ctx, "Blocked on rooted / instrumented device", Toast.LENGTH_SHORT).show()
                    } else if (!Gatekeeper.isSensitiveFeatureUnlocked()) {
                        Toast.makeText(ctx, "Feature locked: upgrade required", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(ctx, "Sensitive feature executed ✅", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !blocked
            ) {
                Text(if (blocked) "Blocked (root/Frida)" else "Run Sensitive Feature")
            }

            OutlinedButton(
                onClick = { result = RootDetector.check() }
            ) { Text("Re-run checks") }

            Text(
                "Notes:\n• Root/Frida detection is basic and for demo only.\n" +
                        "• Gatekeeper uses a weak check (FREE vs PRO) so you can bypass with Frida.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

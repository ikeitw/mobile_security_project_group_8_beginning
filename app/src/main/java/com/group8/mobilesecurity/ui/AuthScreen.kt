package com.group8.mobilesecurity.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.group8.mobilesecurity.data.AuthRepository

@Composable
fun AuthScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { AuthRepository(ctx) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var showDecrypted by remember { mutableStateOf<String?>(null) }
    var isPwdHidden by remember { mutableStateOf(true) }

    Card(Modifier.fillMaxWidth().padding(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Auth (INSECURE) — Register / Login", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (isPwdHidden) PasswordVisualTransformation() else VisualTransformation.None,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        Toast.makeText(ctx, "Enter username and password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        val res = repo.registerUser(username.trim(), password)
                        status = if (res.isSuccess) "Registered $username" else "Register failed: ${res.exceptionOrNull()?.message}"
                        // show stored encrypted password for demo (insecure)
                        showDecrypted = repo.getDecryptedPassword(username.trim())
                    }
                }) { Text("Register") }

                Button(onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        Toast.makeText(ctx, "Enter username and password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        val res = repo.loginUser(username.trim(), password)
                        status = when {
                            res.isFailure -> "Error: ${res.exceptionOrNull()?.message}"
                            res.getOrNull() == true -> "Login success"
                            else -> "Login failed"
                        }
                        if (res.isSuccess) {
                            // also show stored encrypted/decrypted values as demonstration
                            showDecrypted = repo.getDecryptedPassword(username.trim())
                        }
                    }
                }) { Text("Login") }

                OutlinedButton(onClick = { isPwdHidden = !isPwdHidden }) {
                    Text(if (isPwdHidden) "Show" else "Hide")
                }
            }

            status?.let { Text(it) }

            Spacer(Modifier.height(8.dp))
            Text("Demo: decrypted stored password (insecure): ${showDecrypted ?: "-"}", style = MaterialTheme.typography.bodySmall)
            Text("Note: Passwords are encrypted with a weak XOR+Base64 scheme (intentional). Do not copy this to production.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        }
    }
}

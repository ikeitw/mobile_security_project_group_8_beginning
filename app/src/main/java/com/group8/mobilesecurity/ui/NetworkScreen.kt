package com.group8.mobilesecurity.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.group8.mobilesecurity.data.model.PostDto
import com.group8.mobilesecurity.data.model.UserDto
import com.group8.mobilesecurity.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun NetworkScreen() {
    val scope = rememberCoroutineScope()

    // Normal call state
    var posts by remember { mutableStateOf<List<PostDto>>(emptyList()) }
    var loadingPosts by remember { mutableStateOf(false) }
    var postsError by remember { mutableStateOf<String?>(null) }

    // IDOR call state
    var userIdText by remember { mutableStateOf("1") } // default "my" id
    var user by remember { mutableStateOf<UserDto?>(null) }
    var loadingUser by remember { mutableStateOf(false) }
    var userError by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

        Text("Mobile Security – API Demos", style = MaterialTheme.typography.titleLarge)

        // --- Normal API card ---
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Normal Request: Fetch Posts", style = MaterialTheme.typography.titleMedium)
                Text("This call is safe and just lists public posts. You can intercept it with a proxy; it shouldn't impact security-critical behavior.")
                Button(
                    onClick = {
                        loadingPosts = true; postsError = null
                        scope.launch {
                            try {
                                posts = RetrofitClient.api.getPosts().take(5) // keep UI short
                            } catch (e: Exception) {
                                postsError = e.message
                            } finally { loadingPosts = false }
                        }
                    }
                ) { Text(if (loadingPosts) "Loading..." else "Load 5 Posts") }

                if (postsError != null) Text("Error: $postsError", color = MaterialTheme.colorScheme.error)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(posts) { p ->
                        Text("• #${p.id} (${p.userId}) ${p.title}")
                    }
                }
            }
        }

        // --- IDOR demo card ---
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("IDOR Demo: Fetch Profile by userId", style = MaterialTheme.typography.titleMedium)
                Text("The app requests a profile by userId. Changing the id (via UI, Burp, or Frida) returns another user’s profile → classic IDOR behavior.")

                OutlinedTextField(
                    value = userIdText,
                    onValueChange = { userIdText = it.filter { ch -> ch.isDigit() }.take(2) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("User ID") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Button(
                    onClick = {
                        val id = userIdText.toIntOrNull() ?: 1
                        loadingUser = true; userError = null
                        scope.launch {
                            try {
                                user = RetrofitClient.api.getUser(id)
                            } catch (e: Exception) {
                                userError = e.message
                            } finally { loadingUser = false }
                        }
                    }
                ) { Text(if (loadingUser) "Loading..." else "Fetch Profile") }

                if (userError != null) Text("Error: $userError", color = MaterialTheme.colorScheme.error)

                user?.let {
                    Text("Name: ${it.name}")
                    Text("Email: ${it.email}")
                    Text("Company: ${it.company.name}")
                    Text("City: ${it.address.city}")
                }
            }
        }
    }
}

package com.group8.mobilesecurity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.group8.mobilesecurity.ui.NetworkScreen
import com.group8.mobilesecurity.ui.theme.MobileSecurityProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileSecurityProjectTheme {
                NetworkScreen()
            }
        }
    }
}

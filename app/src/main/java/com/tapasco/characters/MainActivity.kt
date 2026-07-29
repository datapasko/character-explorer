package com.tapasco.characters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tapasco.characters.presentation.navigation.AppNavigation
import com.tapasco.characters.ui.theme.CharactersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CharactersTheme {
                AppNavigation()
            }
        }
    }
}

package com.tapasco.characters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tapasco.characters.presentation.navigation.AppNavigation
import com.tapasco.characters.ui.theme.CharactersTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = koinViewModel()
            val savedIsDarkTheme by appViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val systemUsesDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = savedIsDarkTheme ?: systemUsesDarkTheme

            CharactersTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChange = appViewModel::setDarkTheme,
                )
            }
        }
    }
}

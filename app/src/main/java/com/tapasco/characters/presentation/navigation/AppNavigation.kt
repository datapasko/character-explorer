package com.tapasco.characters.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tapasco.characters.presentation.characters.CharactersScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val canNavigateBack = navController.previousBackStackEntry != null

    val isDetailScreen =
        currentBackStackEntry
            ?.destination
            ?.hasRoute<Routes.CharacterDetail>() == true

    var detailSubtitle by remember { mutableStateOf("Characters") }

    Scaffold(
        topBar = {
            AppTopBar(
                canNavigateBack = canNavigateBack,
                subtitle = if (isDetailScreen) detailSubtitle else "Characters",
                isDarkTheme = isDarkTheme,
                onDarkThemeChange = onDarkThemeChange,
                onBack = {
                    navController.popBackStack()
                },
            )
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.Characters,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable<Routes.Characters> {
                CharactersScreen(
                    onCharacterClick = { id ->
                        navController.navigate(Routes.CharacterDetail(id))
                    },
                )
            }

            composable<Routes.CharacterDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<Routes.CharacterDetail>()
                Routes.CharacterDetail(
                    id = route.id,
                )
            }
        }
    }
}

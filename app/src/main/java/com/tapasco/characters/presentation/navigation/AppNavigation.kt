package com.tapasco.characters.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tapasco.characters.presentation.characters.CharactersScreen
import com.tapasco.characters.presentation.detail.CharacterDetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val canNavigateBack =
        currentBackStackEntry != null && navController.previousBackStackEntry != null

    Scaffold(
        topBar = {
            AppTopBar(
                canNavigateBack = canNavigateBack,
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
                CharacterDetailScreen(
                    characterId = route.id,
                )
            }
        }
    }
}

package com.tapasco.characters.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val isDarkTheme: Flow<Boolean?>
    val favoriteCharacterIds: Flow<Set<Int>>

    suspend fun setDarkTheme(isDarkTheme: Boolean)

    suspend fun toggleFavoriteCharacter(characterId: Int)
}

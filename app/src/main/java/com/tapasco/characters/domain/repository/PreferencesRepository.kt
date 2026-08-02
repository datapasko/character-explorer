package com.tapasco.characters.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val favoriteCharacterIds: Flow<Set<Int>>

    suspend fun toggleFavoriteCharacter(characterId: Int)
}

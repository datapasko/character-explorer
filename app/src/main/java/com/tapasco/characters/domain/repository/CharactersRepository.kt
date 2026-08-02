package com.tapasco.characters.domain.repository

import androidx.paging.PagingData
import com.tapasco.characters.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
    fun getCharacters(
        name: String? = null,
        status: String? = null,
    ): Flow<PagingData<Character>>

    fun observeCharacter(characterId: Int): Flow<Character?>

    suspend fun refreshCharacter(
        characterId: Int,
        forceRefresh: Boolean = false,
    ): Result<Unit>
}

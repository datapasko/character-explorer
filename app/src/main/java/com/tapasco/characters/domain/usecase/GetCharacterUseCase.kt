package com.tapasco.characters.domain.usecase

import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetCharacterUseCase(
    private val repository: CharactersRepository,
) {
    operator fun invoke(characterId: Int): Flow<Character?> = if (characterId > 0) {
        repository.observeCharacter(characterId)
    } else {
        flowOf(null)
    }
}

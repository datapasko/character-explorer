package com.tapasco.characters.domain.usecase

import com.tapasco.characters.domain.repository.CharactersRepository

class RefreshCharacterUseCase(
    private val repository: CharactersRepository,
) {
    suspend operator fun invoke(
        characterId: Int,
        forceRefresh: Boolean = false,
    ): Result<Unit> = if (characterId > 0) {
        repository.refreshCharacter(
            characterId = characterId,
            forceRefresh = forceRefresh,
        )
    } else {
        Result.failure(
            IllegalArgumentException("Character id must be greater than zero"),
        )
    }
}

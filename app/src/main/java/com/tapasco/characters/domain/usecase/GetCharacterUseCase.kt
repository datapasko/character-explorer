package com.tapasco.characters.domain.usecase

import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.repository.CharactersRepository

class GetCharacterUseCase(
    private val repository: CharactersRepository,
) {
    suspend operator fun invoke(characterId: Int): Result<Character> = if (characterId > 0) {
        repository.getCharacter(characterId)
    } else {
        Result.failure(
            IllegalArgumentException("Character id must be greater than zero"),
        )
    }
}

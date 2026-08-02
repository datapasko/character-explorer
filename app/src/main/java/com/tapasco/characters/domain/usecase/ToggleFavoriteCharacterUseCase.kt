package com.tapasco.characters.domain.usecase

import com.tapasco.characters.domain.repository.PreferencesRepository

class ToggleFavoriteCharacterUseCase(
    private val repository: PreferencesRepository,
) {
    suspend operator fun invoke(characterId: Int) {
        repository.toggleFavoriteCharacter(characterId)
    }
}

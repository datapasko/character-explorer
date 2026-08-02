package com.tapasco.characters.domain.usecase

import com.tapasco.characters.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteCharacterIdsUseCase(
    private val repository: PreferencesRepository,
) {
    operator fun invoke(): Flow<Set<Int>> = repository.favoriteCharacterIds
}

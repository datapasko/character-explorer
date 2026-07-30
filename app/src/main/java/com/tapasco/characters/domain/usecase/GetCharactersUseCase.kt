package com.tapasco.characters.domain.usecase

import androidx.paging.PagingData
import com.tapasco.characters.core.utils.normalizedQuery
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow

class GetCharactersUseCase(
    private val repository: CharactersRepository,
) {
    operator fun invoke(
        name: String? = null,
        status: String? = null,
    ): Flow<PagingData<Character>> = repository.getCharacters(
        name = name.normalizedQuery(),
        status = status.normalizedQuery(),
    )
}

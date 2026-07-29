package com.tapasco.characters.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tapasco.characters.data.mapper.toDomain
import com.tapasco.characters.data.remote.api.RickAndMortyApi
import com.tapasco.characters.data.remote.paging.CharactersPagingSource
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow

private const val PAGE_SIZE = 20
private const val PREFETCH_ITEMS = 5

class CharactersRepositoryImpl(
    private val api: RickAndMortyApi,
) : CharactersRepository {
    override fun getCharacters(
        name: String?,
        status: String?,
    ): Flow<PagingData<Character>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_ITEMS,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = {
            CharactersPagingSource(
                api = api,
                name = name,
                status = status,
            )
        },
    ).flow

    override suspend fun getCharacter(characterId: Int): Character = api.getCharacter(characterId).toDomain()
}

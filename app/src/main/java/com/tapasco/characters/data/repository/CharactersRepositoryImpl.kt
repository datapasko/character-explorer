package com.tapasco.characters.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.tapasco.characters.core.utils.TimeProvider
import com.tapasco.characters.core.utils.runSuspendCatching
import com.tapasco.characters.data.local.CharactersDatabase
import com.tapasco.characters.data.mapper.toDomain
import com.tapasco.characters.data.mapper.toEntity
import com.tapasco.characters.data.remote.api.RickAndMortyApi
import com.tapasco.characters.data.remote.paging.CharactersRemoteMediator
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.repository.CharactersRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val PAGE_SIZE = 20
private const val PREFETCH_ITEMS = 5
private const val DETAIL_CACHE_TIMEOUT_MILLIS = 24 * 60 * 60 * 1_000L

class CharactersRepositoryImpl(
    private val api: RickAndMortyApi,
    private val database: CharactersDatabase,
    private val timeProvider: TimeProvider,
    private val ioDispatcher: CoroutineDispatcher,
) : CharactersRepository {
    private val characterDao = database.characterDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(
        name: String?,
        status: String?,
    ): Flow<PagingData<Character>> {
        val queryKey = characterQueryKey(name = name, status = status)

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_ITEMS,
                enablePlaceholders = false,
            ),
            remoteMediator = CharactersRemoteMediator(
                queryKey = queryKey,
                name = name,
                status = status,
                database = database,
                api = api,
                timeProvider = timeProvider,
            ),
            pagingSourceFactory = { characterDao.pagingSource(queryKey) },
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override fun observeCharacter(characterId: Int): Flow<Character?> = characterDao
        .observeCharacter(characterId)
        .map { entity -> entity?.toDomain() }

    override suspend fun refreshCharacter(
        characterId: Int,
        forceRefresh: Boolean,
    ): Result<Unit> = withContext(ioDispatcher) {
        runSuspendCatching {
            val cachedCharacter = characterDao.getCharacter(characterId)
            val cacheIsFresh = cachedCharacter != null &&
                timeProvider.currentTimeMillis() - cachedCharacter.updatedAt < DETAIL_CACHE_TIMEOUT_MILLIS

            if (!forceRefresh && cacheIsFresh) {
                return@runSuspendCatching
            }

            characterDao.upsertCharacter(
                api.getCharacter(characterId).toEntity(
                    updatedAt = timeProvider.currentTimeMillis(),
                ),
            )
        }
    }
}

internal fun characterQueryKey(
    name: String?,
    status: String?,
): String = "${name.orEmpty().trim().lowercase()}|${status.orEmpty().trim().lowercase()}"

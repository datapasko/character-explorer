package com.tapasco.characters.data.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.tapasco.characters.core.utils.TimeProvider
import com.tapasco.characters.data.local.CharactersDatabase
import com.tapasco.characters.data.local.entity.CharacterEntity
import com.tapasco.characters.data.local.entity.CharacterQueryEntity
import com.tapasco.characters.data.local.entity.CharacterQueryResultEntity
import com.tapasco.characters.data.mapper.toEntity
import com.tapasco.characters.data.remote.api.RickAndMortyApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharactersRemoteMediator(
    private val queryKey: String,
    private val name: String?,
    private val status: String?,
    private val database: CharactersDatabase,
    private val api: RickAndMortyApi,
    private val timeProvider: TimeProvider,
) : RemoteMediator<Int, CharacterEntity>() {
    private val characterDao = database.characterDao()
    private val queryDao = database.characterQueryDao()

    override suspend fun initialize(): InitializeAction {
        val query = queryDao.getQuery(queryKey)
        val isFresh = query != null &&
            timeProvider.currentTimeMillis() - query.updatedAt < CACHE_TIMEOUT_MILLIS

        return if (isFresh) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>,
    ): MediatorResult {
        if (loadType == LoadType.PREPEND) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        val storedQuery = queryDao.getQuery(queryKey)
        val page = when (loadType) {
            LoadType.REFRESH -> FIRST_PAGE
            LoadType.APPEND -> {
                if (storedQuery?.endReached != false) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                storedQuery.nextPage ?: return MediatorResult.Success(
                    endOfPaginationReached = true,
                )
            }
            LoadType.PREPEND -> error("Handled above")
        }

        return try {
            val response = try {
                api.getCharacters(page = page, name = name, status = status)
            } catch (exception: HttpException) {
                if (exception.code() == HTTP_NOT_FOUND) null else throw exception
            }
            val now = timeProvider.currentTimeMillis()
            val characters = response?.results.orEmpty().map { character ->
                character.toEntity(updatedAt = now)
            }
            val endReached = response?.info?.next == null

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    queryDao.deleteResults(queryKey)
                }

                characterDao.upsertCharacters(characters)
                queryDao.upsertQuery(
                    CharacterQueryEntity(
                        queryKey = queryKey,
                        nextPage = if (endReached) null else page + 1,
                        endReached = endReached,
                        updatedAt = if (loadType == LoadType.REFRESH) {
                            now
                        } else {
                            storedQuery?.updatedAt ?: now
                        },
                    ),
                )
                queryDao.insertResults(
                    characters.mapIndexed { index, character ->
                        CharacterQueryResultEntity(
                            queryKey = queryKey,
                            characterId = character.id,
                            position = ((page - 1) * state.config.pageSize) + index,
                        )
                    },
                )
            }

            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }
}

private const val FIRST_PAGE = 1
private const val HTTP_NOT_FOUND = 404
private const val CACHE_TIMEOUT_MILLIS = 24 * 60 * 60 * 1_000L

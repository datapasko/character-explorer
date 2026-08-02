package com.tapasco.characters.data.repository

import com.tapasco.characters.core.utils.TimeProvider
import com.tapasco.characters.core.utils.runSuspendCatching
import com.tapasco.characters.data.local.dao.EpisodeDao
import com.tapasco.characters.data.local.entity.EpisodeEntity
import com.tapasco.characters.data.mapper.toDomain
import com.tapasco.characters.data.mapper.toEntity
import com.tapasco.characters.data.remote.api.RickAndMortyApi
import com.tapasco.characters.domain.model.Episode
import com.tapasco.characters.domain.repository.EpisodesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

private const val EPISODE_CACHE_TIMEOUT_MILLIS = 24 * 60 * 60 * 1_000L

class EpisodesRepositoryImpl(
    private val api: RickAndMortyApi,
    private val episodeDao: EpisodeDao,
    private val timeProvider: TimeProvider,
    private val ioDispatcher: CoroutineDispatcher,
) : EpisodesRepository {
    override suspend fun getEpisodes(
        episodeIds: List<Int>,
    ): Result<List<Episode>> = withContext(ioDispatcher) {
        val uniqueEpisodeIds = episodeIds.distinct()
        if (uniqueEpisodeIds.isEmpty()) {
            return@withContext Result.success(emptyList())
        }

        val cachedEpisodes = episodeDao.getEpisodes(uniqueEpisodeIds)
        val now = timeProvider.currentTimeMillis()
        val cacheIsCompleteAndFresh = cachedEpisodes.size == uniqueEpisodeIds.size &&
            cachedEpisodes.all { episode ->
                now - episode.updatedAt < EPISODE_CACHE_TIMEOUT_MILLIS
            }

        if (cacheIsCompleteAndFresh) {
            return@withContext Result.success(cachedEpisodes.toDomainInOrder(uniqueEpisodeIds))
        }

        val refreshResult = runSuspendCatching {
            val remoteEpisodes = when (uniqueEpisodeIds.size) {
                1 -> listOf(api.getEpisode(uniqueEpisodeIds.first()))
                else -> api.getEpisodes(uniqueEpisodeIds.joinToString(separator = ","))
            }
            episodeDao.upsertEpisodes(
                remoteEpisodes.map { episode -> episode.toEntity(updatedAt = now) },
            )
        }

        if (refreshResult.isFailure && cachedEpisodes.size != uniqueEpisodeIds.size) {
            return@withContext Result.failure(checkNotNull(refreshResult.exceptionOrNull()))
        }

        Result.success(
            episodeDao.getEpisodes(uniqueEpisodeIds).toDomainInOrder(uniqueEpisodeIds),
        )
    }
}

private fun List<EpisodeEntity>.toDomainInOrder(episodeIds: List<Int>): List<Episode> {
    val episodesById = associateBy { episode -> episode.id }
    return episodeIds.mapNotNull { episodeId -> episodesById[episodeId]?.toDomain() }
}

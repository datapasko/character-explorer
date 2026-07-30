package com.tapasco.characters.data.repository

import com.tapasco.characters.core.utils.runSuspendCatching
import com.tapasco.characters.data.mapper.toDomain
import com.tapasco.characters.data.remote.api.RickAndMortyApi
import com.tapasco.characters.domain.model.Episode
import com.tapasco.characters.domain.repository.EpisodesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class EpisodesRepositoryImpl(
    private val api: RickAndMortyApi,
    private val ioDispatcher: CoroutineDispatcher,
) : EpisodesRepository {
    override suspend fun getEpisodes(
        episodeIds: List<Int>,
    ): Result<List<Episode>> = withContext(ioDispatcher) {
        runSuspendCatching {
            val uniqueEpisodeIds = episodeIds.distinct()

            when (uniqueEpisodeIds.size) {
                0 -> emptyList()
                1 -> listOf(
                    api.getEpisode(uniqueEpisodeIds.first()).toDomain(),
                )

                else -> api.getEpisodes(
                    episodeIds = uniqueEpisodeIds.joinToString(separator = ","),
                ).map { episode ->
                    episode.toDomain()
                }
            }
        }
    }
}

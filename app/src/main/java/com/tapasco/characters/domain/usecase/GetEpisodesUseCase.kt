package com.tapasco.characters.domain.usecase

import com.tapasco.characters.domain.model.Episode
import com.tapasco.characters.domain.repository.EpisodesRepository

class GetEpisodesUseCase(
    private val repository: EpisodesRepository,
) {
    suspend operator fun invoke(episodeUrls: List<String>): Result<List<Episode>> {
        val episodeIds = episodeUrls
            .mapNotNull { episodeUrl ->
                episodeUrl
                    .substringAfterLast('/')
                    .toIntOrNull()
            }
            .distinct()

        return repository.getEpisodes(episodeIds)
    }
}

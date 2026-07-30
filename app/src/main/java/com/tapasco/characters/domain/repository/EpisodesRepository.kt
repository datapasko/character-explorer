package com.tapasco.characters.domain.repository

import com.tapasco.characters.domain.model.Episode

interface EpisodesRepository {
    suspend fun getEpisodes(episodeIds: List<Int>): Result<List<Episode>>
}

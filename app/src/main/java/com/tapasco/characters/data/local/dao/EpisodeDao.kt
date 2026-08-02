package com.tapasco.characters.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tapasco.characters.data.local.entity.EpisodeEntity

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE id IN (:episodeIds)")
    suspend fun getEpisodes(episodeIds: List<Int>): List<EpisodeEntity>

    @Upsert
    suspend fun upsertEpisodes(episodes: List<EpisodeEntity>)
}

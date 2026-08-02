package com.tapasco.characters.data.mapper

import com.tapasco.characters.data.local.entity.EpisodeEntity
import com.tapasco.characters.data.remote.dto.EpisodeDto
import com.tapasco.characters.domain.model.Episode

fun EpisodeDto.toEntity(updatedAt: Long): EpisodeEntity = EpisodeEntity(
    id = id,
    name = name,
    airDate = airDate,
    code = episode,
    updatedAt = updatedAt,
)

fun EpisodeEntity.toDomain(): Episode = Episode(
    id = id,
    name = name,
    airDate = airDate,
    code = code,
)

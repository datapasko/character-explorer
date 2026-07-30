package com.tapasco.characters.data.mapper

import com.tapasco.characters.data.remote.dto.EpisodeDto
import com.tapasco.characters.domain.model.Episode

fun EpisodeDto.toDomain(): Episode = Episode(
    id = id,
    name = name,
    airDate = airDate,
    code = episode,
)

package com.tapasco.characters.data.mapper

import com.tapasco.characters.data.remote.dto.CharacterDto
import com.tapasco.characters.data.remote.dto.LocationReferenceDto
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.model.CharacterLocation

fun CharacterDto.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = origin.toDomain(),
    location = location.toDomain(),
    imageUrl = image,
    episodeUrls = episodes,
    createdAt = created,
)

private fun LocationReferenceDto.toDomain(): CharacterLocation = CharacterLocation(
    name = name,
    url = url,
)

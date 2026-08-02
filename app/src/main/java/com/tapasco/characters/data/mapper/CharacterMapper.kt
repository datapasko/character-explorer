package com.tapasco.characters.data.mapper

import com.tapasco.characters.data.local.entity.CharacterEntity
import com.tapasco.characters.data.remote.dto.CharacterDto
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.model.CharacterLocation
import com.tapasco.characters.domain.model.GenderCharacter
import com.tapasco.characters.domain.model.StatusCharacter
import java.util.Locale

fun CharacterDto.toEntity(updatedAt: Long): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    status = status.toStatusCharacter(),
    species = species,
    type = type,
    gender = gender.toGenderCharacter(),
    originName = origin.name,
    originUrl = origin.url,
    locationName = location.name,
    locationUrl = location.url,
    imageUrl = image,
    episodeUrls = episodes,
    createdAt = created,
    updatedAt = updatedAt,
)

fun CharacterEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = CharacterLocation(name = originName, url = originUrl),
    location = CharacterLocation(name = locationName, url = locationUrl),
    imageUrl = imageUrl,
    episodeUrls = episodeUrls,
    createdAt = createdAt,
)

private fun String.toStatusCharacter(): StatusCharacter = when (lowercase(Locale.ROOT)) {
    "alive" -> StatusCharacter.ALIVE
    "dead" -> StatusCharacter.DEAD
    else -> StatusCharacter.UNKNOWN
}

private fun String.toGenderCharacter(): GenderCharacter = when (lowercase(Locale.ROOT)) {
    "female" -> GenderCharacter.FEMALE
    "male" -> GenderCharacter.MALE
    "genderless" -> GenderCharacter.GENDERLESS
    else -> GenderCharacter.UNKNOWN
}

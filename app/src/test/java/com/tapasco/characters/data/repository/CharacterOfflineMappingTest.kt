package com.tapasco.characters.data.repository

import com.tapasco.characters.data.mapper.toDomain
import com.tapasco.characters.data.mapper.toEntity
import com.tapasco.characters.data.remote.dto.CharacterDto
import com.tapasco.characters.data.remote.dto.LocationReferenceDto
import com.tapasco.characters.domain.model.GenderCharacter
import com.tapasco.characters.domain.model.StatusCharacter
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterOfflineMappingTest {
    @Test
    fun networkCharacter_roundTripsThroughLocalEntity() {
        val dto = characterDto()

        val character = dto.toEntity(updatedAt = 123L).toDomain()

        assertEquals(dto.id, character.id)
        assertEquals(dto.name, character.name)
        assertEquals(StatusCharacter.ALIVE, character.status)
        assertEquals(GenderCharacter.MALE, character.gender)
        assertEquals(dto.origin.name, character.origin.name)
        assertEquals(dto.location.name, character.location.name)
        assertEquals(dto.episodes, character.episodeUrls)
    }

    @Test
    fun unrecognizedStatusAndGender_mapToUnknown() {
        val entity = characterDto().copy(
            status = "not-a-status",
            gender = "not-a-gender",
        ).toEntity(updatedAt = 123L)

        assertEquals(StatusCharacter.UNKNOWN, entity.status)
        assertEquals(GenderCharacter.UNKNOWN, entity.gender)
    }

    @Test
    fun queryKey_normalizesFilters() {
        assertEquals("rick|alive", characterQueryKey(name = "  Rick ", status = " ALIVE "))
        assertEquals("|", characterQueryKey(name = null, status = null))
    }
}

private fun characterDto() = CharacterDto(
    id = 1,
    name = "Rick Sanchez",
    status = "Alive",
    species = "Human",
    type = "",
    gender = "Male",
    origin = LocationReferenceDto(name = "Earth (C-137)", url = "origin-url"),
    location = LocationReferenceDto(name = "Citadel of Ricks", url = "location-url"),
    image = "image-url",
    episodes = listOf("https://rickandmortyapi.com/api/episode/1"),
    url = "character-url",
    created = "2017-11-04T18:48:46.250Z",
)

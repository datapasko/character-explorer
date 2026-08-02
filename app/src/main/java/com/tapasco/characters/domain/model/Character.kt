package com.tapasco.characters.domain.model

data class Character(
    val id: Int,
    val name: String,
    val status: StatusCharacter,
    val species: String,
    val type: String,
    val gender: GenderCharacter,
    val origin: CharacterLocation,
    val location: CharacterLocation,
    val imageUrl: String,
    val episodeUrls: List<String>,
    val createdAt: String,
)

enum class StatusCharacter {
    ALIVE,
    DEAD,
    UNKNOWN,
}

enum class GenderCharacter {
    FEMALE,
    MALE,
    GENDERLESS,
    UNKNOWN,
}

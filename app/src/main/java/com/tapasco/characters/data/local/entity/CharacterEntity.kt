package com.tapasco.characters.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tapasco.characters.domain.model.GenderCharacter
import com.tapasco.characters.domain.model.StatusCharacter

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: StatusCharacter,
    val species: String,
    val type: String,
    val gender: GenderCharacter,
    val originName: String,
    val originUrl: String,
    val locationName: String,
    val locationUrl: String,
    val imageUrl: String,
    val episodeUrls: List<String>,
    val createdAt: String,
    val updatedAt: Long,
)

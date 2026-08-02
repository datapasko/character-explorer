package com.tapasco.characters.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_queries")
data class CharacterQueryEntity(
    @PrimaryKey val queryKey: String,
    val nextPage: Int?,
    val endReached: Boolean,
    val updatedAt: Long,
)

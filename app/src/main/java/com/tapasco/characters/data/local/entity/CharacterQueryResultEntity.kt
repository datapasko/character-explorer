package com.tapasco.characters.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "character_query_results",
    primaryKeys = ["queryKey", "characterId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterQueryEntity::class,
            parentColumns = ["queryKey"],
            childColumns = ["queryKey"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("characterId")],
)
data class CharacterQueryResultEntity(
    val queryKey: String,
    val characterId: Int,
    val position: Int,
)

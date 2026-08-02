package com.tapasco.characters.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tapasco.characters.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query(
        """
        SELECT characters.* FROM characters
        INNER JOIN character_query_results
            ON characters.id = character_query_results.characterId
        WHERE character_query_results.queryKey = :queryKey
        ORDER BY character_query_results.position ASC
        """,
    )
    fun pagingSource(queryKey: String): PagingSource<Int, CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :characterId")
    fun observeCharacter(characterId: Int): Flow<CharacterEntity?>

    @Query("SELECT * FROM characters WHERE id = :characterId")
    suspend fun getCharacter(characterId: Int): CharacterEntity?

    @Upsert
    suspend fun upsertCharacters(characters: List<CharacterEntity>)

    @Upsert
    suspend fun upsertCharacter(character: CharacterEntity)
}

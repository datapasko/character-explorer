package com.tapasco.characters.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.tapasco.characters.data.local.entity.CharacterQueryEntity
import com.tapasco.characters.data.local.entity.CharacterQueryResultEntity

@Dao
interface CharacterQueryDao {
    @Query("SELECT * FROM character_queries WHERE queryKey = :queryKey")
    suspend fun getQuery(queryKey: String): CharacterQueryEntity?

    @Upsert
    suspend fun upsertQuery(query: CharacterQueryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<CharacterQueryResultEntity>)

    @Query("DELETE FROM character_query_results WHERE queryKey = :queryKey")
    suspend fun deleteResults(queryKey: String)
}

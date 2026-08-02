package com.tapasco.characters.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tapasco.characters.data.local.dao.CharacterDao
import com.tapasco.characters.data.local.dao.CharacterQueryDao
import com.tapasco.characters.data.local.dao.EpisodeDao
import com.tapasco.characters.data.local.entity.CharacterEntity
import com.tapasco.characters.data.local.entity.CharacterQueryEntity
import com.tapasco.characters.data.local.entity.CharacterQueryResultEntity
import com.tapasco.characters.data.local.entity.EpisodeEntity

@Database(
    entities = [
        CharacterEntity::class,
        CharacterQueryEntity::class,
        CharacterQueryResultEntity::class,
        EpisodeEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(DatabaseConverters::class)
abstract class CharactersDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao

    abstract fun characterQueryDao(): CharacterQueryDao

    abstract fun episodeDao(): EpisodeDao
}

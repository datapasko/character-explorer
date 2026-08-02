package com.tapasco.characters.data.local

import androidx.room.TypeConverter
import com.tapasco.characters.domain.model.GenderCharacter
import com.tapasco.characters.domain.model.StatusCharacter
import kotlin.enums.enumEntries

class DatabaseConverters {
    @TypeConverter
    fun statusToString(status: StatusCharacter): String = status.name

    @TypeConverter
    fun stringToStatus(value: String): StatusCharacter = enumEntries<StatusCharacter>()
        .firstOrNull { it.name == value }
        ?: StatusCharacter.UNKNOWN

    @TypeConverter
    fun genderToString(gender: GenderCharacter): String = gender.name

    @TypeConverter
    fun stringToGender(value: String): GenderCharacter = enumEntries<GenderCharacter>()
        .firstOrNull { it.name == value }
        ?: GenderCharacter.UNKNOWN

    @TypeConverter
    fun episodeUrlsToString(urls: List<String>): String = urls.joinToString(separator = "\n")

    @TypeConverter
    fun stringToEpisodeUrls(value: String): List<String> = if (value.isEmpty()) {
        emptyList()
    } else {
        value.lines()
    }
}

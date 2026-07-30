package com.tapasco.characters.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tapasco.characters.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences",
)

class PreferencesRepositoryImpl(
    context: Context,
) : PreferencesRepository {
    private val dataStore = context.applicationContext.preferencesDataStore
    private val preferences = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    override val isDarkTheme: Flow<Boolean?> = preferences
        .map { preferences ->
            preferences[IS_DARK_THEME]
        }

    override val favoriteCharacterIds: Flow<Set<Int>> = preferences
        .map { preferences ->
            preferences[FAVORITE_CHARACTER_IDS]
                .orEmpty()
                .mapNotNull(String::toIntOrNull)
                .toSet()
        }

    override suspend fun setDarkTheme(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_THEME] = isDarkTheme
        }
    }

    override suspend fun toggleFavoriteCharacter(characterId: Int) {
        dataStore.edit { preferences ->
            val favoriteId = characterId.toString()
            val updatedFavorites = preferences[FAVORITE_CHARACTER_IDS]
                .orEmpty()
                .toMutableSet()

            if (!updatedFavorites.add(favoriteId)) {
                updatedFavorites.remove(favoriteId)
            }

            preferences[FAVORITE_CHARACTER_IDS] = updatedFavorites.toSet()
        }
    }

    private companion object {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val FAVORITE_CHARACTER_IDS = stringSetPreferencesKey("favorite_character_ids")
    }
}

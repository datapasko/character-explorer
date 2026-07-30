package com.tapasco.characters.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val isDarkTheme: Flow<Boolean?>

    suspend fun setDarkTheme(isDarkTheme: Boolean)
}

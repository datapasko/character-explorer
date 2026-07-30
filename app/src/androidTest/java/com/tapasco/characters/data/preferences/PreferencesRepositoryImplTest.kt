package com.tapasco.characters.data.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesRepositoryImplTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun toggleFavoriteCharacter_persistsFavoriteAcrossRepositoryInstances() = runBlocking {
        val repository = PreferencesRepositoryImpl(context)
        ensureNotFavorite(repository, TEST_CHARACTER_ID)

        try {
            repository.toggleFavoriteCharacter(TEST_CHARACTER_ID)

            val recreatedRepository = PreferencesRepositoryImpl(context)
            assertTrue(TEST_CHARACTER_ID in recreatedRepository.favoriteCharacterIds.first())
        } finally {
            if (TEST_CHARACTER_ID in repository.favoriteCharacterIds.first()) {
                repository.toggleFavoriteCharacter(TEST_CHARACTER_ID)
            }
        }

        assertFalse(TEST_CHARACTER_ID in repository.favoriteCharacterIds.first())
    }

    private suspend fun ensureNotFavorite(
        repository: PreferencesRepositoryImpl,
        characterId: Int,
    ) {
        if (characterId in repository.favoriteCharacterIds.first()) {
            repository.toggleFavoriteCharacter(characterId)
        }
    }

    private companion object {
        const val TEST_CHARACTER_ID = Int.MAX_VALUE
    }
}

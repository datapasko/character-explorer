package com.tapasco.characters.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tapasco.characters.core.utils.TimeProvider
import com.tapasco.characters.data.local.CharactersDatabase
import com.tapasco.characters.data.remote.api.RickAndMortyApi
import com.tapasco.characters.data.remote.dto.CharacterDto
import com.tapasco.characters.data.remote.dto.CharactersResponseDto
import com.tapasco.characters.data.remote.dto.EpisodeDto
import com.tapasco.characters.data.remote.dto.LocationReferenceDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharactersOfflineRepositoryTest {
    private lateinit var database: CharactersDatabase
    private lateinit var api: FakeCharactersApi
    private lateinit var repository: CharactersRepositoryImpl
    private lateinit var timeProvider: MutableTimeProvider

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            CharactersDatabase::class.java,
        ).build()
        api = FakeCharactersApi()
        timeProvider = MutableTimeProvider(currentTimeMillis = 10_000L)
        repository = CharactersRepositoryImpl(
            api = api,
            database = database,
            timeProvider = timeProvider,
            ioDispatcher = Dispatchers.IO,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun refreshCharacter_writesNetworkResultToLocalSourceOfTruth() = runTest {
        repository.refreshCharacter(characterId = 1).getOrThrow()

        val cachedCharacter = repository.observeCharacter(characterId = 1).first()

        assertEquals("Rick Sanchez", cachedCharacter?.name)
        assertEquals(1, api.characterRequests)
    }

    @Test
    fun cachedCharacter_remainsAvailableWhenForcedRefreshFails() = runTest {
        repository.refreshCharacter(characterId = 1).getOrThrow()
        api.failure = IllegalStateException("Offline")

        val refreshResult = repository.refreshCharacter(
            characterId = 1,
            forceRefresh = true,
        )

        assertTrue(refreshResult.isFailure)
        assertEquals("Rick Sanchez", repository.observeCharacter(1).first()?.name)
    }

    @Test
    fun freshCharacter_skipsUnnecessaryNetworkRefresh() = runTest {
        repository.refreshCharacter(characterId = 1).getOrThrow()

        repository.refreshCharacter(characterId = 1).getOrThrow()

        assertEquals(1, api.characterRequests)
    }

    @Test
    fun expiredCharacter_isRefreshedFromNetwork() = runTest {
        repository.refreshCharacter(characterId = 1).getOrThrow()
        timeProvider.currentTimeMillis += 25 * 60 * 60 * 1_000L

        repository.refreshCharacter(characterId = 1).getOrThrow()

        assertEquals(2, api.characterRequests)
    }
}

private class FakeCharactersApi : RickAndMortyApi {
    var characterRequests = 0
    var failure: Throwable? = null

    override suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
    ): CharactersResponseDto = error("Not needed for detail cache tests")

    override suspend fun getCharacter(characterId: Int): CharacterDto {
        characterRequests++
        failure?.let { throw it }
        return characterDto(characterId)
    }

    override suspend fun getEpisode(episodeId: Int): EpisodeDto = error("Not needed")

    override suspend fun getEpisodes(episodeIds: String): List<EpisodeDto> = error("Not needed")
}

private fun characterDto(id: Int) = CharacterDto(
    id = id,
    name = "Rick Sanchez",
    status = "Alive",
    species = "Human",
    type = "",
    gender = "Male",
    origin = LocationReferenceDto(name = "Earth (C-137)", url = "origin-url"),
    location = LocationReferenceDto(name = "Citadel of Ricks", url = "location-url"),
    image = "image-url",
    episodes = listOf("https://rickandmortyapi.com/api/episode/1"),
    url = "character-url",
    created = "2017-11-04T18:48:46.250Z",
)

private class MutableTimeProvider(
    var currentTimeMillis: Long,
) : TimeProvider {
    override fun currentTimeMillis(): Long = currentTimeMillis
}

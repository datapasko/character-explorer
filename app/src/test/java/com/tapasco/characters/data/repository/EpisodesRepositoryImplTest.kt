package com.tapasco.characters.data.repository

import com.tapasco.characters.data.remote.api.RickAndMortyApi
import com.tapasco.characters.data.remote.dto.CharacterDto
import com.tapasco.characters.data.remote.dto.CharactersResponseDto
import com.tapasco.characters.data.remote.dto.EpisodeDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodesRepositoryImplTest {

    @Test
    fun oneId_usesSingleEpisodeEndpoint() = runTest {
        val api = FakeRickAndMortyApi()
        val repository = EpisodesRepositoryImpl(
            api = api,
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val episodes = repository.getEpisodes(listOf(10)).getOrThrow()

        assertEquals(10, api.requestedEpisodeId)
        assertNull(api.requestedEpisodeIds)
        assertEquals("Close Rick-counters of the Rick Kind", episodes.single().name)
    }

    @Test
    fun multipleIds_useMultipleEpisodeEndpoint() = runTest {
        val api = FakeRickAndMortyApi()
        val repository = EpisodesRepositoryImpl(
            api = api,
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        repository.getEpisodes(listOf(10, 28, 10)).getOrThrow()

        assertNull(api.requestedEpisodeId)
        assertEquals("10,28", api.requestedEpisodeIds)
    }

    @Test
    fun apiFailure_isReturnedAsResultFailure() = runTest {
        val expectedException = IllegalStateException("Network error")
        val repository = EpisodesRepositoryImpl(
            api = FakeRickAndMortyApi(failure = expectedException),
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = repository.getEpisodes(listOf(10))

        assertSame(expectedException, result.exceptionOrNull())
    }
}

private class FakeRickAndMortyApi(
    private val failure: Exception? = null,
) : RickAndMortyApi {
    var requestedEpisodeId: Int? = null
    var requestedEpisodeIds: String? = null

    override suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
    ): CharactersResponseDto = error("Not needed for EpisodesRepositoryImpl tests")

    override suspend fun getCharacter(characterId: Int): CharacterDto = error("Not needed for EpisodesRepositoryImpl tests")

    override suspend fun getEpisode(episodeId: Int): EpisodeDto {
        failure?.let { throw it }
        requestedEpisodeId = episodeId
        return episodeDto(id = episodeId)
    }

    override suspend fun getEpisodes(episodeIds: String): List<EpisodeDto> {
        requestedEpisodeIds = episodeIds
        return episodeIds.split(',').map { id ->
            episodeDto(id = id.toInt())
        }
    }
}

private fun episodeDto(id: Int) = EpisodeDto(
    id = id,
    name = if (id == 10) {
        "Close Rick-counters of the Rick Kind"
    } else {
        "Episode $id"
    },
    airDate = "April 7, 2014",
    episode = "S01E10",
)

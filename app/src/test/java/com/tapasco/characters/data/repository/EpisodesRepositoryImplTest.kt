package com.tapasco.characters.data.repository

import com.tapasco.characters.core.utils.TimeProvider
import com.tapasco.characters.data.local.dao.EpisodeDao
import com.tapasco.characters.data.local.entity.EpisodeEntity
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
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodesRepositoryImplTest {

    @Test
    fun oneId_usesSingleEpisodeEndpoint() = runTest {
        val api = FakeRickAndMortyApi()
        val repository = EpisodesRepositoryImpl(
            api = api,
            episodeDao = FakeEpisodeDao(),
            timeProvider = FixedTimeProvider,
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
            episodeDao = FakeEpisodeDao(),
            timeProvider = FixedTimeProvider,
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
            episodeDao = FakeEpisodeDao(),
            timeProvider = FixedTimeProvider,
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = repository.getEpisodes(listOf(10))

        assertSame(expectedException, result.exceptionOrNull())
    }

    @Test
    fun completeFreshCache_skipsNetworkAndPreservesRequestedOrder() = runTest {
        val api = FakeRickAndMortyApi()
        val episodeDao = FakeEpisodeDao(
            initialEpisodes = listOf(
                episodeEntity(id = 10, updatedAt = FixedTimeProvider.currentTimeMillis()),
                episodeEntity(id = 28, updatedAt = FixedTimeProvider.currentTimeMillis()),
            ),
        )
        val repository = EpisodesRepositoryImpl(
            api = api,
            episodeDao = episodeDao,
            timeProvider = FixedTimeProvider,
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val episodes = repository.getEpisodes(listOf(28, 10, 28)).getOrThrow()

        assertEquals(listOf(28, 10), episodes.map { episode -> episode.id })
        assertEquals(0, api.episodeRequestCount)
    }

    @Test
    fun completeStaleCache_isReturnedWhenRefreshFails() = runTest {
        val expectedException = IllegalStateException("Offline")
        val api = FakeRickAndMortyApi(failure = expectedException)
        val repository = EpisodesRepositoryImpl(
            api = api,
            episodeDao = FakeEpisodeDao(
                initialEpisodes = listOf(episodeEntity(id = 10, updatedAt = 0L)),
            ),
            timeProvider = FixedTimeProvider,
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = repository.getEpisodes(listOf(10))

        assertTrue(result.isSuccess)
        assertEquals(listOf(10), result.getOrThrow().map { episode -> episode.id })
        assertEquals(1, api.episodeRequestCount)
    }

    @Test
    fun emptyRequest_returnsEmptyListWithoutReadingNetwork() = runTest {
        val api = FakeRickAndMortyApi()
        val repository = EpisodesRepositoryImpl(
            api = api,
            episodeDao = FakeEpisodeDao(),
            timeProvider = FixedTimeProvider,
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val episodes = repository.getEpisodes(emptyList()).getOrThrow()

        assertTrue(episodes.isEmpty())
        assertEquals(0, api.episodeRequestCount)
    }
}

private class FakeEpisodeDao(
    initialEpisodes: List<EpisodeEntity> = emptyList(),
) : EpisodeDao {
    private val episodes = initialEpisodes.associateBy { episode -> episode.id }.toMutableMap()

    override suspend fun getEpisodes(episodeIds: List<Int>): List<EpisodeEntity> = episodes.values
        .filter { episode -> episode.id in episodeIds }
        .sortedBy { episode -> episode.id }

    override suspend fun upsertEpisodes(episodes: List<EpisodeEntity>) {
        episodes.forEach { episode -> this.episodes[episode.id] = episode }
    }
}

private object FixedTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = 100_000_000L
}

private class FakeRickAndMortyApi(
    private val failure: Exception? = null,
) : RickAndMortyApi {
    var requestedEpisodeId: Int? = null
    var requestedEpisodeIds: String? = null
    var episodeRequestCount = 0

    override suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
    ): CharactersResponseDto = error("Not needed for EpisodesRepositoryImpl tests")

    override suspend fun getCharacter(characterId: Int): CharacterDto = error("Not needed for EpisodesRepositoryImpl tests")

    override suspend fun getEpisode(episodeId: Int): EpisodeDto {
        episodeRequestCount++
        failure?.let { throw it }
        requestedEpisodeId = episodeId
        return episodeDto(id = episodeId)
    }

    override suspend fun getEpisodes(episodeIds: String): List<EpisodeDto> {
        episodeRequestCount++
        failure?.let { throw it }
        requestedEpisodeIds = episodeIds
        return episodeIds.split(',').map { id ->
            episodeDto(id = id.toInt())
        }
    }
}

private fun episodeEntity(
    id: Int,
    updatedAt: Long,
) = EpisodeEntity(
    id = id,
    name = "Episode $id",
    airDate = "April 7, 2014",
    code = "S01E10",
    updatedAt = updatedAt,
)

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

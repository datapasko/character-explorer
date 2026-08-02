package com.tapasco.characters.data.remote.paging

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tapasco.characters.core.utils.TimeProvider
import com.tapasco.characters.data.local.CharactersDatabase
import com.tapasco.characters.data.local.entity.CharacterEntity
import com.tapasco.characters.data.remote.api.RickAndMortyApi
import com.tapasco.characters.data.remote.dto.CharacterDto
import com.tapasco.characters.data.remote.dto.CharactersResponseDto
import com.tapasco.characters.data.remote.dto.EpisodeDto
import com.tapasco.characters.data.remote.dto.LocationReferenceDto
import com.tapasco.characters.data.remote.dto.PageInfoDto
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class CharactersRemoteMediatorTest {
    private lateinit var database: CharactersDatabase
    private lateinit var api: MediatorFakeApi
    private lateinit var mediator: CharactersRemoteMediator
    private lateinit var timeProvider: MutableMediatorTimeProvider

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            CharactersDatabase::class.java,
        ).build()
        api = MediatorFakeApi()
        timeProvider = MutableMediatorTimeProvider(currentTimeMillis = 10_000L)
        mediator = CharactersRemoteMediator(
            queryKey = "|",
            name = null,
            status = null,
            database = database,
            api = api,
            timeProvider = timeProvider,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun refresh_storesCharactersAndQueryMetadataInRoom() = runTest {
        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals("Rick Sanchez", database.characterDao().getCharacter(1)?.name)
        assertEquals(2, database.characterQueryDao().getQuery("|")?.nextPage)
        assertEquals(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH, mediator.initialize())
    }

    @Test
    fun failedRefresh_keepsPreviouslyCachedData() = runTest {
        mediator.load(LoadType.REFRESH, emptyPagingState())
        api.failure = IOException("Offline")

        val result = mediator.load(LoadType.REFRESH, emptyPagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals("Rick Sanchez", database.characterDao().getCharacter(1)?.name)
    }

    @Test
    fun append_usesStoredNextPageAndMarksEndOfPagination() = runTest {
        mediator.load(LoadType.REFRESH, emptyPagingState())

        val result = mediator.load(LoadType.APPEND, emptyPagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        assertEquals(listOf(1, 2), api.requestedPages)
        assertEquals("Morty Smith", database.characterDao().getCharacter(2)?.name)
        assertEquals(null, database.characterQueryDao().getQuery("|")?.nextPage)
        assertTrue(database.characterQueryDao().getQuery("|")?.endReached == true)
    }

    @Test
    fun initialize_launchesRefreshWhenCachedQueryHasExpired() = runTest {
        mediator.load(LoadType.REFRESH, emptyPagingState())
        timeProvider.currentTimeMillis += 25 * 60 * 60 * 1_000L

        val action = mediator.initialize()

        assertEquals(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH, action)
    }
}

private fun emptyPagingState() = PagingState<Int, CharacterEntity>(
    pages = emptyList(),
    anchorPosition = null,
    config = PagingConfig(pageSize = 20),
    leadingPlaceholderCount = 0,
)

private class MediatorFakeApi : RickAndMortyApi {
    var failure: Throwable? = null
    val requestedPages = mutableListOf<Int>()

    override suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
    ): CharactersResponseDto {
        requestedPages += page
        failure?.let { throw it }
        return CharactersResponseDto(
            info = PageInfoDto(
                count = 2,
                pages = 2,
                next = if (page == 1) {
                    "https://rickandmortyapi.com/api/character?page=2"
                } else {
                    null
                },
                previous = if (page == 1) null else "https://rickandmortyapi.com/api/character?page=1",
            ),
            results = listOf(characterDto(id = page)),
        )
    }

    override suspend fun getCharacter(characterId: Int): CharacterDto = error("Not needed")

    override suspend fun getEpisode(episodeId: Int): EpisodeDto = error("Not needed")

    override suspend fun getEpisodes(episodeIds: String): List<EpisodeDto> = error("Not needed")
}

private fun characterDto(id: Int) = CharacterDto(
    id = id,
    name = if (id == 1) "Rick Sanchez" else "Morty Smith",
    status = "Alive",
    species = "Human",
    type = "",
    gender = "Male",
    origin = LocationReferenceDto(name = "Earth (C-137)", url = "origin-url"),
    location = LocationReferenceDto(name = "Citadel of Ricks", url = "location-url"),
    image = "image-url",
    episodes = emptyList(),
    url = "character-url",
    created = "2017-11-04T18:48:46.250Z",
)

private class MutableMediatorTimeProvider(
    var currentTimeMillis: Long,
) : TimeProvider {
    override fun currentTimeMillis(): Long = currentTimeMillis
}

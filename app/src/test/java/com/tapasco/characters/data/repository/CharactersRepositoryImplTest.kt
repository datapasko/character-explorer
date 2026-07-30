package com.tapasco.characters.data.repository

import androidx.paging.testing.asSnapshot
import com.tapasco.characters.data.remote.api.RickAndMortyApi
import com.tapasco.characters.data.remote.dto.CharacterDto
import com.tapasco.characters.data.remote.dto.CharactersResponseDto
import com.tapasco.characters.data.remote.dto.EpisodeDto
import com.tapasco.characters.data.remote.dto.LocationReferenceDto
import com.tapasco.characters.data.remote.dto.PageInfoDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class CharactersRepositoryImplTest {

    @Test
    fun getCharacter_mapsResponseAndForwardsId() = runTest {
        val characterDto = characterDto(id = 137)
        val api = FakeCharactersApi(characterResponse = characterDto)
        val repository = CharactersRepositoryImpl(
            api = api,
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val character = repository.getCharacter(characterDto.id).getOrThrow()

        assertEquals(characterDto.id, api.requestedCharacterId)
        assertEquals(characterDto.id, character.id)
        assertEquals(characterDto.name, character.name)
        assertEquals(characterDto.origin.name, character.origin.name)
        assertEquals(characterDto.location.name, character.location.name)
        assertEquals(characterDto.image, character.imageUrl)
        assertEquals(characterDto.episodes, character.episodeUrls)
    }

    @Test
    fun getCharacter_apiFailureIsReturnedAsResultFailure() = runTest {
        val expectedException = IllegalStateException("Network error")
        val repository = CharactersRepositoryImpl(
            api = FakeCharactersApi(characterFailure = expectedException),
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = repository.getCharacter(characterId = 1)

        assertSame(expectedException, result.exceptionOrNull())
    }

    @Test
    fun getCharacter_cancellationIsRethrown() = runTest {
        val expectedException = CancellationException("Request cancelled")
        val repository = CharactersRepositoryImpl(
            api = FakeCharactersApi(characterFailure = expectedException),
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        try {
            repository.getCharacter(characterId = 1)
            fail("CancellationException should be rethrown")
        } catch (actualException: CancellationException) {
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun getCharacters_forwardsFiltersMapsResultsAndLoadsNextPage() = runTest {
        val firstPageCharacters = (1..20).map(::characterDto)
        val secondPageCharacter = characterDto(id = 21)
        val api = FakeCharactersApi(
            pages = mapOf(
                1 to charactersResponse(
                    characters = firstPageCharacters,
                    nextPage = 2,
                ),
                2 to charactersResponse(
                    characters = listOf(secondPageCharacter),
                    nextPage = null,
                ),
            ),
        )
        val repository = CharactersRepositoryImpl(
            api = api,
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val characters = repository
            .getCharacters(
                name = "Rick",
                status = "alive",
            )
            .asSnapshot {
                scrollTo(index = 20)
            }

        assertEquals(listOf(1, 2), api.characterPageRequests.map { it.page })
        assertEquals(
            listOf("Rick", "Rick"),
            api.characterPageRequests.map { it.name },
        )
        assertEquals(
            listOf("alive", "alive"),
            api.characterPageRequests.map { it.status },
        )
        assertEquals(21, characters.size)
        assertEquals(firstPageCharacters.first().name, characters.first().name)
        assertEquals(secondPageCharacter.name, characters.last().name)
    }
}

private class FakeCharactersApi(
    private val characterResponse: CharacterDto = characterDto(),
    private val characterFailure: Throwable? = null,
    private val pages: Map<Int, CharactersResponseDto> = emptyMap(),
) : RickAndMortyApi {
    var requestedCharacterId: Int? = null
    val characterPageRequests = mutableListOf<CharacterPageRequest>()

    override suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
    ): CharactersResponseDto {
        characterPageRequests += CharacterPageRequest(
            page = page,
            name = name,
            status = status,
        )
        return checkNotNull(pages[page]) {
            "No response configured for page $page"
        }
    }

    override suspend fun getCharacter(characterId: Int): CharacterDto {
        requestedCharacterId = characterId
        characterFailure?.let { throw it }
        return characterResponse
    }

    override suspend fun getEpisode(episodeId: Int): EpisodeDto = error("Not needed for CharactersRepositoryImpl tests")

    override suspend fun getEpisodes(episodeIds: String): List<EpisodeDto> = error("Not needed for CharactersRepositoryImpl tests")
}

private data class CharacterPageRequest(
    val page: Int,
    val name: String?,
    val status: String?,
)

private fun charactersResponse(
    characters: List<CharacterDto>,
    nextPage: Int?,
) = CharactersResponseDto(
    info = PageInfoDto(
        count = characters.size,
        pages = if (nextPage == null) 1 else nextPage,
        next = nextPage?.let { page ->
            "https://rickandmortyapi.com/api/character?page=$page"
        },
        previous = null,
    ),
    results = characters,
)

private fun characterDto(
    id: Int = 1,
) = CharacterDto(
    id = id,
    name = "Character $id",
    status = "Alive",
    species = "Human",
    type = "",
    gender = "Male",
    origin = LocationReferenceDto(
        name = "Earth (C-137)",
        url = "https://rickandmortyapi.com/api/location/1",
    ),
    location = LocationReferenceDto(
        name = "Citadel of Ricks",
        url = "https://rickandmortyapi.com/api/location/3",
    ),
    image = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
    episodes = listOf(
        "https://rickandmortyapi.com/api/episode/1",
    ),
    url = "https://rickandmortyapi.com/api/character/$id",
    created = "2017-11-04T18:48:46.250Z",
)

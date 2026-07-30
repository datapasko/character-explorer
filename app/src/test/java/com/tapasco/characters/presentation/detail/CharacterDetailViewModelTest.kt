package com.tapasco.characters.presentation.detail

import androidx.paging.PagingData
import com.tapasco.characters.MainDispatcherRule
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.model.CharacterLocation
import com.tapasco.characters.domain.model.Episode
import com.tapasco.characters.domain.repository.CharactersRepository
import com.tapasco.characters.domain.repository.EpisodesRepository
import com.tapasco.characters.domain.usecase.GetCharacterUseCase
import com.tapasco.characters.domain.usecase.GetEpisodesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun init_loadsCharacterById() = runTest {
        val character = testCharacter(id = 137)
        val episode = testEpisode()
        val repository = FakeCharactersRepository(
            responses = ArrayDeque(listOf(Result.success(character))),
        )
        val episodesRepository = FakeEpisodesRepository(
            responses = ArrayDeque(listOf(Result.success(listOf(episode)))),
        )

        val viewModel = createViewModel(
            characterId = character.id,
            repository = repository,
            episodesRepository = episodesRepository,
        )
        assertEquals(CharacterDetailState(), viewModel.state.value)

        runCurrent()

        assertEquals(listOf(137), repository.requestedIds)
        assertEquals(
            CharacterDetailState(
                isLoading = false,
                character = character,
                episodes = listOf(episode),
            ),
            viewModel.state.value,
        )
        assertEquals(listOf(listOf(1)), episodesRepository.requestedIds)
    }

    @Test
    fun failedRequest_exposesErrorState() = runTest {
        val repository = FakeCharactersRepository(
            responses = ArrayDeque(
                listOf(Result.failure<Character>(IllegalStateException("Network error"))),
            ),
        )
        val viewModel = createViewModel(repository = repository)

        runCurrent()

        assertEquals(
            CharacterDetailState(
                isLoading = false,
                hasError = true,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun retry_loadsCharacterAgain() = runTest {
        val character = testCharacter()
        val repository = FakeCharactersRepository(
            responses = ArrayDeque(
                listOf(
                    Result.failure(IllegalStateException("Network error")),
                    Result.success(character),
                ),
            ),
        )
        val viewModel = createViewModel(repository = repository)
        runCurrent()
        assertEquals(
            CharacterDetailState(
                isLoading = false,
                hasError = true,
            ),
            viewModel.state.value,
        )

        viewModel.retry()
        runCurrent()

        assertEquals(listOf(character.id, character.id), repository.requestedIds)
        assertEquals(
            CharacterDetailState(
                isLoading = false,
                character = character,
                episodes = listOf(testEpisode()),
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun failedEpisodesRequest_keepsCharacterAndExposesSectionError() = runTest {
        val character = testCharacter()
        val repository = FakeCharactersRepository(
            responses = ArrayDeque(listOf(Result.success(character))),
        )
        val episodesRepository = FakeEpisodesRepository(
            responses = ArrayDeque(
                listOf(Result.failure(IllegalStateException("Network error"))),
            ),
        )
        val viewModel = createViewModel(
            repository = repository,
            episodesRepository = episodesRepository,
        )

        runCurrent()

        assertEquals(
            CharacterDetailState(
                isLoading = false,
                character = character,
                hasEpisodesError = true,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun retryEpisodes_afterFailureLoadsEpisodesAgain() = runTest {
        val character = testCharacter()
        val episode = testEpisode()
        val repository = FakeCharactersRepository(
            responses = ArrayDeque(listOf(Result.success(character))),
        )
        val episodesRepository = FakeEpisodesRepository(
            responses = ArrayDeque(
                listOf(
                    Result.failure(IllegalStateException("Network error")),
                    Result.success(listOf(episode)),
                ),
            ),
        )
        val viewModel = createViewModel(
            repository = repository,
            episodesRepository = episodesRepository,
        )
        runCurrent()

        viewModel.retryEpisodes()
        runCurrent()

        assertEquals(
            listOf(listOf(episode.id), listOf(episode.id)),
            episodesRepository.requestedIds,
        )
        assertEquals(
            CharacterDetailState(
                isLoading = false,
                character = character,
                episodes = listOf(episode),
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun characterWithoutEpisodes_doesNotRequestEpisodes() = runTest {
        val character = testCharacter().copy(episodeUrls = emptyList())
        val repository = FakeCharactersRepository(
            responses = ArrayDeque(listOf(Result.success(character))),
        )
        val episodesRepository = FakeEpisodesRepository(
            responses = ArrayDeque(),
        )
        val viewModel = createViewModel(
            repository = repository,
            episodesRepository = episodesRepository,
        )

        runCurrent()

        assertEquals(emptyList<List<Int>>(), episodesRepository.requestedIds)
        assertEquals(
            CharacterDetailState(
                isLoading = false,
                character = character,
            ),
            viewModel.state.value,
        )
    }

    private fun createViewModel(
        characterId: Int = 1,
        repository: FakeCharactersRepository,
        episodesRepository: FakeEpisodesRepository = FakeEpisodesRepository(
            responses = ArrayDeque(
                listOf(Result.success(listOf(testEpisode()))),
            ),
        ),
    ) = CharacterDetailViewModel(
        characterId = characterId,
        getCharacterUseCase = GetCharacterUseCase(repository),
        getEpisodesUseCase = GetEpisodesUseCase(episodesRepository),
    )
}

private class FakeCharactersRepository(
    private val responses: ArrayDeque<Result<Character>>,
) : CharactersRepository {
    val requestedIds = mutableListOf<Int>()

    override fun getCharacters(
        name: String?,
        status: String?,
    ): Flow<PagingData<Character>> = flowOf(PagingData.empty())

    override suspend fun getCharacter(characterId: Int): Result<Character> {
        requestedIds += characterId
        return responses.removeFirst()
    }
}

private class FakeEpisodesRepository(
    private val responses: ArrayDeque<Result<List<Episode>>>,
) : EpisodesRepository {
    val requestedIds = mutableListOf<List<Int>>()

    override suspend fun getEpisodes(
        episodeIds: List<Int>,
    ): Result<List<Episode>> {
        requestedIds += episodeIds
        return responses.removeFirst()
    }
}

private fun testCharacter(
    id: Int = 1,
) = Character(
    id = id,
    name = "Rick Sanchez",
    status = "Alive",
    species = "Human",
    type = "",
    gender = "Male",
    origin = CharacterLocation(name = "Earth (C-137)", url = ""),
    location = CharacterLocation(name = "Citadel of Ricks", url = ""),
    imageUrl = "https://example.com/rick.png",
    episodeUrls = listOf("https://rickandmortyapi.com/api/episode/1"),
    createdAt = "2017-11-04",
)

private fun testEpisode() = Episode(
    id = 1,
    name = "Pilot",
    airDate = "December 2, 2013",
    code = "S01E01",
)

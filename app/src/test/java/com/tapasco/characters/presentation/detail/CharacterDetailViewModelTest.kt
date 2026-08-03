package com.tapasco.characters.presentation.detail

import androidx.paging.PagingData
import com.tapasco.characters.MainDispatcherRule
import com.tapasco.characters.domain.model.Character
import com.tapasco.characters.domain.model.CharacterLocation
import com.tapasco.characters.domain.model.Episode
import com.tapasco.characters.domain.model.GenderCharacter
import com.tapasco.characters.domain.model.StatusCharacter
import com.tapasco.characters.domain.repository.CharactersRepository
import com.tapasco.characters.domain.repository.EpisodesRepository
import com.tapasco.characters.domain.usecase.GetCharacterUseCase
import com.tapasco.characters.domain.usecase.GetEpisodesUseCase
import com.tapasco.characters.domain.usecase.RefreshCharacterUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
    fun loadCharacter_loadsCharacterById() = runTest {
        val character = testCharacter(id = 137)
        val episode = testEpisode()
        val repository = FakeCharactersRepository(
            responses = ArrayDeque(listOf(Result.success(character))),
        )
        val episodesRepository = FakeEpisodesRepository(
            responses = ArrayDeque(listOf(Result.success(listOf(episode)))),
        )

        val viewModel = createViewModel(
            repository = repository,
            episodesRepository = episodesRepository,
        )
        assertEquals(CharacterDetailState(), viewModel.state.value)
        assertEquals(emptyList<Int>(), repository.observedIds)
        assertEquals(emptyList<Int>(), repository.requestedIds)

        viewModel.loadCharacter(character.id)
        runCurrent()

        assertEquals(listOf(137), repository.observedIds)
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

        viewModel.loadCharacter(1)
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
        viewModel.loadCharacter(character.id)
        runCurrent()
        assertEquals(
            CharacterDetailState(
                isLoading = false,
                hasError = true,
            ),
            viewModel.state.value,
        )

        viewModel.retry(character.id)
        runCurrent()

        assertEquals(listOf(character.id, character.id), repository.requestedIds)
        assertEquals(listOf(false, true), repository.forceRefreshRequests)
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

        viewModel.loadCharacter(character.id)
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
        viewModel.loadCharacter(character.id)
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

        viewModel.loadCharacter(character.id)
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

    @Test
    fun cachedCharacter_remainsVisibleWhenBackgroundRefreshFails() = runTest {
        val character = testCharacter()
        val repository = FakeCharactersRepository(
            responses = ArrayDeque(
                listOf(Result.failure(IllegalStateException("Offline"))),
            ),
            initialCharacter = character,
        )
        val viewModel = createViewModel(repository = repository)

        viewModel.loadCharacter(character.id)
        runCurrent()

        assertEquals(
            CharacterDetailState(
                isLoading = false,
                character = character,
                episodes = listOf(testEpisode()),
            ),
            viewModel.state.value,
        )
        assertEquals(listOf(character.id), repository.requestedIds)
    }

    private fun createViewModel(
        repository: FakeCharactersRepository,
        episodesRepository: FakeEpisodesRepository = FakeEpisodesRepository(
            responses = ArrayDeque(
                listOf(Result.success(listOf(testEpisode()))),
            ),
        ),
    ) = CharacterDetailViewModel(
        getCharacterUseCase = GetCharacterUseCase(repository),
        refreshCharacterUseCase = RefreshCharacterUseCase(repository),
        getEpisodesUseCase = GetEpisodesUseCase(episodesRepository),
    )
}

private class FakeCharactersRepository(
    private val responses: ArrayDeque<Result<Character>>,
    initialCharacter: Character? = null,
) : CharactersRepository {
    val observedIds = mutableListOf<Int>()
    val requestedIds = mutableListOf<Int>()
    val forceRefreshRequests = mutableListOf<Boolean>()
    private val character = MutableStateFlow(initialCharacter)

    override fun getCharacters(
        name: String?,
        status: String?,
    ): Flow<PagingData<Character>> = flowOf(PagingData.empty())

    override fun observeCharacter(characterId: Int): Flow<Character?> {
        observedIds += characterId
        return character
    }

    override suspend fun refreshCharacter(
        characterId: Int,
        forceRefresh: Boolean,
    ): Result<Unit> {
        requestedIds += characterId
        forceRefreshRequests += forceRefresh
        return responses.removeFirst().map { refreshedCharacter ->
            character.value = refreshedCharacter
        }
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
    status = StatusCharacter.ALIVE,
    species = "Human",
    type = "",
    gender = GenderCharacter.MALE,
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
